__kernel void count_hoods(
// __global const ulong* randomShuffles
__global const ulong* randoms, __global const ulong* bitsets,
int colCount, int rowCount, int colWordCount, __global ulong* out, __global ulong* results,
int sampleCount,
__local ulong* sample, __local ulong* sampleMask, __local ulong* total)
{
    int gid = get_global_id(0);
    int gid2 = get_global_id(1);
    if (colWordCount > 16)
        return;
    if (gid >= sampleCount)
        //gid = gid % sampleCount;
        return;

    const ulong one = 1L;

    ulong estimate = 1;
    for (int j = 0; j < colWordCount; j++) {
        sampleMask[j] = 0L;
        sample[j] = randoms[gid*colWordCount + j];
        //barrier(CLK_GLOBAL_MEM_FENCE);
        //out[gid] = randoms[gid*colWordCount + j];
    }
    for (int bitc = 0; bitc < colCount; bitc++) {
        //int bit = randomShuffles[gid * colCount + bitc];
        int bit = bitc;
        int wordIndex = bit >> 6L;
        int bitIndex = bit & 63L;
        sampleMask[wordIndex] |= (one << bitIndex);
        int validCount0 = 0;
        int validCount1 = 0;
        bool oldBit = ((sample[wordIndex] & (one << bitIndex)) != 0);

        // Is partial hood 1 okay?
        sample[wordIndex] |= (one << bitIndex);
        for (int j = 0; j < colWordCount; j++) {
            total[j] = 0;
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            bool isSubset = true;
            for (int j = 0; j < colWordCount; j++) {
                ulong result = (bitsets[rowIndex*colWordCount+j] & sampleMask[j]);
                isSubset = isSubset && ((result & (sample[j] & sampleMask[j])) == result);
                //barrier(CLK_LOCAL_MEM_FENCE);
            }
            if (isSubset) {
                for (int j = 0; j < colWordCount; j++) {
                    total[j] |= bitsets[rowIndex*colWordCount+j];
                }
            }
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        bool isPartialHood = true;
        for (int j = 0; j < colWordCount; j++) {
            ulong total_masked = total[j] & sampleMask[j];
            ulong sampleMasked = sample[j] & sampleMask[j];
            isPartialHood = isPartialHood && (total_masked == sampleMasked);
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        if (isPartialHood) {
            validCount1 = 1;
        }

        // Is partial hood 0 okay?
        sample[wordIndex] &= ~(one << bitIndex);
        for (int j = 0; j < colWordCount; j++) {
            total[j] = 0;
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            bool isSubset = true;
            for (int j = 0; j < colWordCount; j++) {
                ulong result = (bitsets[rowIndex*colWordCount+j] & sampleMask[j]);
                isSubset = isSubset && ((result & (sample[j] & sampleMask[j])) == result);
                //barrier(CLK_LOCAL_MEM_FENCE);
            }
            if (isSubset) {
                for (int j = 0; j < colWordCount; j++) {
                    total[j] |= bitsets[rowIndex*colWordCount+j];
                }
            }
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        isPartialHood = true;
        for (int j = 0; j < colWordCount; j++) {
            ulong total_masked = total[j] & sampleMask[j];
            ulong sampleMasked = sample[j] & sampleMask[j];
            isPartialHood = isPartialHood && (total_masked == sampleMasked);
            //barrier(CLK_LOCAL_MEM_FENCE);
        }
        if (isPartialHood) {
            validCount0 = 1;
        }

        int validCount = validCount0 + validCount1;
        if (validCount == 2) {
            estimate *= 2;
            //if ((randoms[gid*colWordCount+wordIndex] & (1L << bitIndex)) != 0) {
            if (oldBit) {
                sample[wordIndex] |= (one << bitIndex);
            } else {
                sample[wordIndex] &= ~(one << bitIndex);
            }
        } else if (validCount == 1) {
            if (validCount1 == 1) {
                sample[wordIndex] |= (one << bitIndex);
            } else if (validCount0 == 1) {
                sample[wordIndex] &= ~(one << bitIndex);
            }
        } else if (validCount == 0) {
            break;
        }
    }
    results[0] += estimate;
    results[1] += 1;
    out[gid] = estimate; // bitsets[i*colWordCount];
    //out[gid] = gid2;
    //barrier(CLK_GLOBAL_MEM_FENCE);
}