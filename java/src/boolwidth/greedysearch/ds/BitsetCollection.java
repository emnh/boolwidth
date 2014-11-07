package boolwidth.greedysearch.ds;

import java.util.ArrayList;

/**
 * Created by emh on 11/7/2014.
 * A collection class for immutable integer bitsets.
 */

public class BitsetCollection {

    private int maxSizeBits = 0;

    /**
     * Checks that fromIndex ... toIndex is a valid range of bit indices.
     */
    private static void checkRange(int bitIndex) {
        checkRange(bitIndex, bitIndex);
    }

    /**
     * Checks that fromIndex ... toIndex is a valid range of bit indices.
     */
    private static void checkRange(int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        }
        if (toIndex < 0) {
            throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex
                    + " > toIndex: " + toIndex);
        }
    }

    public BSCBitSet createBSCBitSet() {
        return new BSCBitSet(bitToWordLength(maxSizeBits));
    }

    public BSCBitSet createBSCBitSet(int[] members) {
        BSCBitSet bs = new BSCBitSet(bitToWordLength(maxSizeBits));
        return this.addAll(bs, members);
    }

    public boolean get(BSCBitSet bs, int i) {
        long[] words = bs.getWords();
        int wordIndex = i >> 6;
        int bitIndex = i & 63;
        words[wordIndex] |= 1L << bitIndex;
        return (words[wordIndex] & 1L << bitIndex) != 0;
    }

    public BSCBitSet add(BSCBitSet bs, int i) {
        long[] words = bs.getWords().clone(); //new long[bitToWordLength(maxSizeBits)];
        int wordIndex = i >> 6;
        int bitIndex = i & 63;
        words[wordIndex] |= 1L << bitIndex;
        return new BSCBitSet(words);
    }

    public BSCBitSet addAll(BSCBitSet bs, int[] is) {
        long[] words = bs.getWords().clone(); //new long[bitToWordLength(maxSizeBits)];
        for (int i : is) {
            int wordIndex = i >> 6;
            int bitIndex = i & 63;
            words[wordIndex] |= 1L << bitIndex;
        }
        return new BSCBitSet(words);
    }

    public BSCBitSet remove(BSCBitSet bs, int i) {
        long[] words = bs.getWords().clone(); //new long[bitToWordLength(maxSizeBits)];
        int wordIndex = i >> 6;
        int bitIndex = i & 63;
        words[wordIndex] &= ~(1L << bitIndex);
        return new BSCBitSet(words);
    }

    public BSCBitSet removeAll(BSCBitSet bs, int[] is) {
        long[] words = bs.getWords().clone(); //new long[bitToWordLength(maxSizeBits)];
        for (int i : is) {
            int wordIndex = i >> 6;
            int bitIndex = i & 63;
            words[wordIndex] &= ~(1L << bitIndex);
        }
        return new BSCBitSet(words);
    }

    public static int bitToWordLength(long numBits)
    {
        return ((((int)(numBits - 1) >> 6)) + 1);
    }

    public <T> ArrayList<T> getMembers(Iterable<T> memberSet, BSCBitSet bs) {
        ArrayList<T> ret = new ArrayList<>();
        int i = 0;
        for (T member : memberSet) {
            if (this.get(bs, i)) ret.add(member);
            i += 1;
        }
        return ret;
    }
}
