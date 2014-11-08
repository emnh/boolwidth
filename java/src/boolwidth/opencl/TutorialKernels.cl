__kernel void count_hoods(
// __global const ulong* randomShuffles,
__global const ulong* randoms, __global const ulong* bitsets,
int colCount, int rowCount, int colWordCount, __global ulong* out, __global ulong* results,
int sampleCount,
__local ulong* totalSubsets0,
__local ulong* totalSubsets1,
__local ulong* sample, __local ulong* sampleMask, __local ulong* total)
{
    int gid = get_global_id(0);
    if (gid >= sampleCount)
        return;

    const ulong one = 1L;

    ulong estimate = 1;
    for (int j = 0; j < colWordCount; j++) {
        sampleMask[j] = 0L;
        sample[j] = randoms[gid*colWordCount + j];
    }
    int totalCount = rowCount;
    int totalCount0 = rowCount;
    int totalCount1 = rowCount;
    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
        for (int j = 0; j < colWordCount; j++) {
            totalSubsets0[rowIndex*colWordCount+j] = bitsets[rowIndex*colWordCount+j];
            totalSubsets1[rowIndex*colWordCount+j] = bitsets[rowIndex*colWordCount+j];
        }
    }
    __local ulong* totalSubsets = totalSubsets0;
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
        }
        totalCount1 = 0;
        for (int rowIndex = 0; rowIndex < totalCount; rowIndex++) {
            bool isSubset = true;
            for (int j = 0; j < colWordCount; j++) {
                ulong result = (totalSubsets[rowIndex*colWordCount+j] & sampleMask[j]);
                isSubset = isSubset && ((result & (sample[j] & sampleMask[j])) == result);
            }
            if (isSubset) {
                for (int j = 0; j < colWordCount; j++) {
                    total[j] |= totalSubsets[rowIndex*colWordCount+j];
                    totalSubsets1[totalCount1*colWordCount+j] = totalSubsets[rowIndex*colWordCount+j];
                }
                totalCount1++;
            }
        }
        bool isPartialHood = true;
        if (totalCount == 0) isPartialHood = false;
        for (int j = 0; j < colWordCount; j++) {
            ulong total_masked = total[j] & sampleMask[j];
            ulong sampleMasked = sample[j] & sampleMask[j];
            isPartialHood = isPartialHood && (total_masked == sampleMasked);
        }
        if (isPartialHood) {
            validCount1 = 1;
        }

        // Is partial hood 0 okay?
        sample[wordIndex] &= ~(one << bitIndex);
        for (int j = 0; j < colWordCount; j++) {
            total[j] = 0;
        }
        totalCount0 = 0;
        for (int rowIndex = 0; rowIndex < totalCount; rowIndex++) {
            bool isSubset = true;
            for (int j = 0; j < colWordCount; j++) {
                ulong result = (totalSubsets[rowIndex*colWordCount+j] & sampleMask[j]);
                isSubset = isSubset && ((result & (sample[j] & sampleMask[j])) == result);
            }
            if (isSubset) {
                for (int j = 0; j < colWordCount; j++) {
                    total[j] |= totalSubsets[rowIndex*colWordCount+j];
                    totalSubsets0[totalCount0*colWordCount+j] = totalSubsets[rowIndex*colWordCount+j];
                }
                totalCount0++;
            }
        }
        isPartialHood = true;
        if (totalCount == 0) isPartialHood = false;
        for (int j = 0; j < colWordCount; j++) {
            ulong total_masked = total[j] & sampleMask[j];
            ulong sampleMasked = sample[j] & sampleMask[j];
            isPartialHood = isPartialHood && (total_masked == sampleMasked);
        }
        if (isPartialHood) {
            validCount0 = 1;
        }

        int validCount = validCount0 + validCount1;
        if (validCount == 2) {
            estimate *= 2;
            if (oldBit) {
                totalSubsets = totalSubsets1;
                totalCount = totalCount1;
                sample[wordIndex] |= (one << bitIndex);
            } else {
                totalSubsets = totalSubsets0;
                totalCount = totalCount0;
                sample[wordIndex] &= ~(one << bitIndex);
            }
        } else if (validCount == 1) {
            if (validCount1 == 1) {
                totalSubsets = totalSubsets1;
                totalCount = totalCount1;
                sample[wordIndex] |= (one << bitIndex);
            } else if (validCount0 == 1) {
                totalSubsets = totalSubsets0;
                totalCount = totalCount0;
                sample[wordIndex] &= ~(one << bitIndex);
            }
        } else if (validCount == 0) {
            break;
        }
    }
    out[gid] = estimate;
}