package boolwidth.greedysearch.ds;

/**
 * Created by emh on 11/7/2014.
 */
public class BSCBitSet {
    private long[] words;

    public BSCBitSet(int length) {
        if (length == 0) throw new IndexOutOfBoundsException("cannot create zero length BSCBitSet");
        this.words = new long[length];
    }

    public BSCBitSet(long[] words) {
        this.words = words;
    }

    public long[] getWords() {
        return words;
    }
}