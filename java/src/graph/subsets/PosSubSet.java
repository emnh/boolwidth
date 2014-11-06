package graph.subsets;

import graph.Vertex;
import interfaces.IPosSet;
import interfaces.ISetPosition;
import interfaces.ISubSet;

import java.util.Iterator;
import java.util.Set;

// TODO: make groundSet immutable

public class PosSubSet<TVertex extends ISetPosition> extends
        AbstractPosSet<TVertex> implements Cloneable, Iterable<TVertex>,
ISubSet<PosSubSet<TVertex>, TVertex> {

    // TODO: optimize bit search using Java BitSet methods
    class BitSetIterator implements Iterator<TVertex> {

        //for (int i = subset.nextSetBit(0); i >= 0; i = subset.nextSetBit(i+1)) {
        private int i = 0;
        private int size = groundSetSize();

        public BitSetIterator() {
            // Ready next one
            while (!get(i) && i < size) {
                i++;
            }
        }

        public boolean hasNext() {
            return i < size;
        }

        public TVertex next() {
            TVertex ret = null;
            if (get(i)) {
                ret = groundSet.getVertex(i);
                // Ready next one
                do {
                    i++;
                } while (!get(i) && i < size);
            }
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }


    // Just for serialization
    @Deprecated
    public PosSubSet() {

    }

	public IPosSet<TVertex> groundSet;

	public long[] words;

	public transient boolean modified = true;
	public transient PosSet<TVertex> subsetcache = null;

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

	public PosSubSet(IPosSet<TVertex> set) {
		this.words = new long[(set.size() / Long.SIZE) + 1];
		this.groundSet = set;
	}

	public PosSubSet(IPosSet<TVertex> set, Iterable<TVertex> subset) {
		this(set);
		for (TVertex v : subset) {
            assert set.contains(v) : "not a subset";
			add(v);
		}
	}

	public PosSubSet(IPosSet<TVertex> groundSet, long[] w) {
		this.groundSet = groundSet;
		this.words = w;
	}

	@Override
	public boolean add(TVertex v) {
		this.modified = true;
		return set(v.id(), true);
	}

	// set given bit to false;
	protected void clear(int bitIndex, int wordIndex) {
		// clear the bit
		this.words[wordIndex] &= ~(1L << bitIndex);
	}

	@Override
	public PosSubSet<TVertex> clone() {
		return new PosSubSet<>(this.groundSet, this.words.clone());
	}

    public void cloneInPlace(PosSubSet<TVertex> o) {
        this.groundSet = o.groundSet;
        this.words = o.words.clone();
    }

	public int compareTo(PosSubSet<TVertex> set) {
		long[] sw = set.getWords();
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != sw[i]) {
				if (this.words[i] > sw[i]) {
					return -1;
				} else {
					return 1;
				}
			}
		}
		return 0;
	}

	public int compareTo(Set<TVertex> set) {
		if (set instanceof PosSubSet<?>) {
			return compareTo((PosSubSet<TVertex>) set);
		} else {
			throw new UnsupportedOperationException("not implemented");
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (!(obj instanceof PosSubSet)) {
			throw new UnsupportedOperationException("not implemented");
			//return false;
		}
		PosSubSet<TVertex> set = (PosSubSet) obj;
		if (this.groundSet != set.groundSet) {
			//System.out.println("groundset differs");
			// TODO: implement and use groundSet.equals?
			throw new UnsupportedOperationException("groundset differs");
			//return false;
		}

		// Check words in use by both BitSets
		long[] sw = set.getWords();
		for (int i = 0; i < this.words.length; i++) {
			if (this.words[i] != sw[i]) {
				return false;
			}
		}
		return true;
	}

	// get a bit value
	public boolean get(int bit) {
		checkRange(bit);
		// find right word
		int wordIndex = bit >> 6;
		// find right bit
		int bitIndex = bit & 63;

		return (this.words[wordIndex] & 1L << bitIndex) != 0;
	}

	public int getId(TVertex v) {
		// make sure we're not modifying the subset if other subsets are using
		// it as their ground set
		if (this.subsetcache != null) {
			assert !this.modified;
		}
		return getSubSet().getId(v);
	}

	public int getPos(TVertex v) {
		int pos = this.groundSet.getId(v);
		if (!(this.groundSet.getVertex(pos) == v)) {
			throw new IndexOutOfBoundsException("Not a proper SubSet");
		}
		return pos;
	}

	// returns the groundSet
	public IPosSet<TVertex> getSet() {
		return this.groundSet;
	}

	// returns the elements set to true;
	public PosSet<TVertex> getSubSet() {
		if (this.modified) {
			this.subsetcache = new PosSet<TVertex>();
			for (int i = 0; i < this.groundSet.size(); i++) {
				if (get(i)) {
					this.subsetcache.add(this.groundSet.getVertex(i));
				}
			}
		}

		return this.subsetcache;
	}

	public TVertex getVertex(int i) {
		return this.groundSet.getVertex(i);
	}

	public long[] getWords() {
		return this.words;
	}

	public int groundSetSize() {
		return this.groundSet.size();
	}

	@Override
	public int hashCode() {
		long h = 1234;
		for (int i = this.words.length; --i >= 0;) {
			h ^= this.words[i] * (i + 1);
		}

		return (int) (h >> 32 ^ h);
	}

	@Override
	public boolean isEmpty() {
		for (long word : this.words) {
			if (word != 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterator<TVertex> iterator() {
        return new BitSetIterator();
		//return getSubSet().iterator();
	}

    /*@Override
    public boolean remove(Object v) {
        if (v instanceof Vertex) {
            return set(((TVertex) v).id(), false);
        }
        return false;
    }*/

	public boolean remove(TVertex v) {
		this.modified = true;
		return set(v.id(), false);
	}

	// set a bit to given value
	public boolean set(int bit, boolean value) {
		checkRange(bit);
		// find right word
		int wordIndex = bit >> 6;
		// find right bit
		int bitIndex = bit & 63;

		boolean was_set = (this.words[wordIndex] & 1L << bitIndex) != 0;

		if (value) {
			set(bitIndex, wordIndex);
		} else {
			clear(bitIndex, wordIndex);
		}

		return was_set;
	}

	// set given bit to true;
	protected void set(int bitIndex, int wordIndex) {
		// set the bit
		this.words[wordIndex] |= 1L << bitIndex;
	}

	public void setBits(int wordIndex, long bits) {
		this.words[wordIndex] |= bits;
	}

	// iterates over all subsets of the set
	public Iterable<PosSubSet<TVertex>> setIterable() {
		SubSetIterator<TVertex> it = new SubSetIterator<TVertex>(this.clone());
		return it;
	}

	// iterates over all subsets of size k
	public Iterable<PosSubSet<TVertex>> setIterable(int k) {
		SubSetIterator<TVertex> it = new SubSetIterator<TVertex>(this.clone(),
				k);
		return it;
	}

	// iterates over all subsets of the set
	public Iterator<PosSubSet<TVertex>> setIterator() {
		SubSetIterator<TVertex> it = new SubSetIterator<TVertex>(this.clone());
		return it;
	}

	// iterates over all subsets of size k
	public Iterator<PosSubSet<TVertex>> setIterator(int k) {
		SubSetIterator<TVertex> it = new SubSetIterator<TVertex>(this.clone(),
				k);
		return it;
	}

	// public Object clone() {
	// return new PosSubSet<T>(groundSet,words.clone());
	// }

	// TODO: optimize: keep size as field (?)
	@Override
	public int size() {
		int size = 0;
		for (long word : this.words) {
			size += Long.bitCount(word);
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\"");
		boolean isFirst = true;
		for (int i = 0; i < groundSetSize(); i++) {
			if (get(i)) {
				if (!isFirst) {
					sb.append(",");
				}
				sb.append(this.groundSet.getVertex(i).toString());
				isFirst = false;
			}
		}
		return sb.append("\"").toString();
	}

    public PosSubSet<TVertex> subtract(PosSubSet<TVertex> set) {
        if ((Set<?>) this.groundSet != set.getSet()) {
            throw new IndexOutOfBoundsException(
                    "Different ground sets can not be compared");
        }

        long[] oldWords = set.getWords();
        long[] newWords = new long[this.words.length];

        // Perform logical AND NOT on words
        for (int i = 0; i < this.words.length; i++) {
            newWords[i] = this.words[i] & ~oldWords[i];
        }
        return new PosSubSet<>(this.groundSet, newWords);
    }

    public void subtractInPlace(PosSubSet<TVertex> set) {
        if ((Set<?>) this.groundSet != set.getSet()) {
            throw new IndexOutOfBoundsException(
                    "Different ground sets can not be compared");
        }

        long[] oldWords = set.getWords();

        // Perform logical AND NOT on words
        for (int i = 0; i < this.words.length; i++) {
            this.words[i] &= ~oldWords[i];
        }
    }

    public PosSubSet<TVertex> inverse() {
        long[] newWords = new long[this.words.length];

        // Perform logical NOT on words
        for (int i = 0; i < this.words.length; i++) {
            newWords[i] = ~this.words[i];
        }
        return new PosSubSet<>(this.groundSet, newWords);
    }

	public PosSubSet<TVertex> union(PosSubSet<TVertex> set) {
		if ((Set<?>) this.groundSet != set.getSet()) {
			throw new IndexOutOfBoundsException(
			"Different ground sets can not be compared");
		}

		long[] oldWords = set.getWords();
		long[] newWords = new long[this.words.length];

		// Perform logical OR on words
		for (int i = 0; i < this.words.length; i++) {
			newWords[i] = this.words[i] | oldWords[i];
		}
		return new PosSubSet<>(this.groundSet, newWords);
	}

    public PosSubSet<TVertex> intersection(PosSubSet<TVertex> set) {
        if ((Set<?>) this.groundSet != set.getSet()) {
            throw new IndexOutOfBoundsException(
                    "Different ground sets can not be compared");
        }

        long[] oldWords = set.getWords();
        long[] newWords = new long[this.words.length];

        // Perform logical AND on words
        for (int i = 0; i < this.words.length; i++) {
            newWords[i] = this.words[i] & oldWords[i];
        }
        return new PosSubSet<TVertex>(this.groundSet, newWords);
    }

    public boolean intersects(PosSubSet<TVertex> set) {
        return !this.intersection(set).isEmpty();
    }


    /*public String toString() {
        String s = "";
        for (TVertex v : this) {
            s += "," + v.toString();
        }
        return s;
    }*/

    public PosSubSet<TVertex> intersect(PosSubSet<TVertex> set) {
        if ((Set<?>) this.groundSet != set.getSet()) {
            throw new IndexOutOfBoundsException(
                    "Different ground sets can not be compared");
        }

        long[] oldWords = set.getWords();
        long[] newWords = new long[this.words.length];

        // Perform logical AND on words
        for (int i = 0; i < this.words.length; i++) {
            newWords[i] = this.words[i] & oldWords[i];
        }
        return new PosSubSet<TVertex>(this.groundSet, newWords);
    }

    public boolean isSubset(PosSubSet<TVertex> set) {
        return this.intersect(set).equals(this);
    }

    @Override
    public TVertex first() {
        for (int i = 0; i < this.groundSet.size(); i++) {
            if (get(i)) {
                return this.groundSet.getVertex(i);
            }
        }
        return null;
    }

    public boolean contains(TVertex v) {
        return get(v.id());
    }

    @Override
    public boolean contains(Object v) {
        if (v instanceof Vertex) {
            return get(((Vertex) v).id());
        }
        return false;
    }
}