package graph;

import interfaces.IPosSet;
import interfaces.ISetPosition;
import interfaces.ISubSet;

import java.util.Iterator;
import java.util.Set;

// TODO: make groundSet immutable

public class PosSubSet<TVertex extends ISetPosition> extends
AbstractPosSet<TVertex> implements Cloneable, Iterable<TVertex>,
ISubSet<PosSubSet<TVertex>, TVertex> {

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
		this.words = new long[(set.size() - 1 >> 6) + 1];
		this.groundSet = set;
	}

	public PosSubSet(IPosSet<TVertex> set, Iterable<TVertex> subset) {
		this(set);
		for (TVertex v : subset) {
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
		return set(getPos(v), true);
	}

	// set given bit to false;
	protected void clear(int bitIndex, int wordIndex) {
		// clear the bit
		this.words[wordIndex] &= ~(1L << bitIndex);
	}

	@Override
	public PosSubSet<TVertex> clone() {
		return new PosSubSet<TVertex>(this.groundSet, this.words.clone());
	}

	//	@Override
	//	protected PosSubSet<TVertex> clone() {
	//		return new PosSubSet<TVertex>(this.groundSet, this.words.clone());
	//	}

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
		return getSubSet().iterator();
	}

	public boolean remove(TVertex v) {
		this.modified = true;
		return set(getPos(v), false);
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
		return new PosSubSet<TVertex>(this.groundSet, newWords);
	}

	public PosSubSet<TVertex> union(Set<TVertex> set) {
		if (set instanceof PosSubSet<?>) {
			return union((PosSubSet<TVertex>) set);
		} else {
			throw new UnsupportedOperationException("not implemented");
		}
	}
}

// @SuppressWarnings("unchecked")
// public NodeSubSet<T> intersection(NodeSubSet<T> set) {
// if(groundSet != set.groundSet)
// throw new
// IndexOutOfBoundsException("Different ground sets can not be compared");
// NodeSubSet<T> copy = (NodeSubSet)this.clone();
//
// // Perform logical OR on words
// for (int i = 0; i < words.length; i++)
// copy.words[i] = words[i] & set.words[i];
// return copy;
// }
//
// @SuppressWarnings("unchecked")
// public NodeSubSet<T> xor(NodeSubSet<T> set) {
// if(groundSet != set.groundSet)
// throw new
// IndexOutOfBoundsException("Different ground sets can not be compared");
// NodeSubSet<T> copy = (NodeSubSet)this.clone();
// // Perform logical XOR on words
// for (int i = 0; i < words.length; i++)
// copy.words[i] = words[i] ^ set.words[i];
// return copy;
// }
//
//
// public String toString() {
// StringBuilder b = new StringBuilder(2*numBits + 1);
// b.append('{');
//
// for (int i = 0; i < numBits; i++) {
// if(i>0)
// b.append(",");
// if(get(i) == true)
// b.append(1);
// else
// b.append(0);
// }
//
// b.append('}');
// return b.toString();
// }
//
// public Set<T> getSubSet() {
// Set<T> subSet = new TreeSet<T>();
// for(int i=0; i<numBits; i++)
// if(get(i))subSet.add(list[i]);
// return subSet;
// }
// }
//
//
//
//
//
//
// /**
// * Sets the bit at the specified index to the complement of its
// * current value.
// *
// * @param bitIndex the index of the bit to flip
// * @throws IndexOutOfBoundsException if the specified index is negative
// * @since 1.4
// public void flip(int bitIndex) {
// if (bitIndex < 0)
// throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
//
// int wordIndex = wordIndex(bitIndex);
//
// words[wordIndex] ^= (1L << bitIndex);
// }*/
//
// /**
// * Sets each bit from the specified {@code fromIndex} (inclusive) to the
// * specified {@code toIndex} (exclusive) to the complement of its current
// * value.
// *
// * @param fromIndex index of the first bit to flip
// * @param toIndex index after the last bit to flip
// * @throws IndexOutOfBoundsException if {@code fromIndex} is negative,
// * or {@code toIndex} is negative, or {@code fromIndex} is
// * larger than {@code toIndex}
// * @since 1.4
//
// public void flip(int fromIndex, int toIndex) {
// checkRange(fromIndex, toIndex);
//
// if (fromIndex == toIndex)
// return;
//
// int startWordIndex = wordIndex(fromIndex);
// int endWordIndex = wordIndex(toIndex - 1);
// expandTo(endWordIndex);
//
// long firstWordMask = WORD_MASK << fromIndex;
// long lastWordMask = WORD_MASK >>> -toIndex;
// if (startWordIndex == endWordIndex) {
// // Case 1: One word
// words[startWordIndex] ^= (firstWordMask & lastWordMask);
// } else {
// // Case 2: Multiple words
// // Handle first word
// words[startWordIndex] ^= firstWordMask;
//
// // Handle intermediate words, if any
// for (int i = startWordIndex+1; i < endWordIndex; i++)
// words[i] ^= WORD_MASK;
//
// // Handle last word
// words[endWordIndex] ^= lastWordMask;
// }
// }*/
//
// /**
// * Returns a new {@code BitSet} composed of bits from this {@code BitSet}
// * from {@code fromIndex} (inclusive) to {@code toIndex} (exclusive).
// *
// * @param fromIndex index of the first bit to include
// * @param toIndex index after the last bit to include
// * @return a new {@code BitSet} from a range of this {@code BitSet}
// * @throws IndexOutOfBoundsException if {@code fromIndex} is negative,
// * or {@code toIndex} is negative, or {@code fromIndex} is
// * larger than {@code toIndex}
// * @since 1.4
//
// public BitSet get(int fromIndex, int toIndex) {
// checkRange(fromIndex, toIndex);
//
// checkInvariants();
//
// int len = length();
//
// // If no set bits in range return empty bitset
// if (len <= fromIndex || fromIndex == toIndex)
// return new BitSet(0);
//
// // An optimization
// if (toIndex > len)
// toIndex = len;
//
// BitSet result = new BitSet(toIndex - fromIndex);
// int targetWords = wordIndex(toIndex - fromIndex - 1) + 1;
// int sourceIndex = wordIndex(fromIndex);
// boolean wordAligned = ((fromIndex & BIT_INDEX_MASK) == 0);
//
// // Process all words but the last word
// for (int i = 0; i < targetWords - 1; i++, sourceIndex++)
// result.words[i] = wordAligned ? words[sourceIndex] :
// (words[sourceIndex] >>> fromIndex) |
// (words[sourceIndex+1] << -fromIndex);
//
// // Process the last word
// long lastWordMask = WORD_MASK >>> -toIndex;
// result.words[targetWords - 1] =
// ((toIndex-1) & BIT_INDEX_MASK) < (fromIndex & BIT_INDEX_MASK)
// ? // straddles source words
// ((words[sourceIndex] >>> fromIndex) |
// (words[sourceIndex+1] & lastWordMask) << -fromIndex)
// :
// ((words[sourceIndex] & lastWordMask) >>> fromIndex);
//
// // Set wordsInUse correctly
// result.wordsInUse = targetWords;
// result.recalculateWordsInUse();
// result.checkInvariants();
//
// return result;
// }*/
//
// /**
// * Returns the index of the first bit that is set to {@code true}
// * that occurs on or after the specified starting index. If no such
// * bit exists then {@code -1} is returned.
// *
// * <p>To iterate over the {@code true} bits in a {@code BitSet},
// * use the following loop:
// *
// * <pre> {@code
// * for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
// * // operate on index i here
// * }}</pre>
// *
// * @param fromIndex the index to start checking from (inclusive)
// * @return the index of the next set bit, or {@code -1} if there
// * is no such bit
// * @throws IndexOutOfBoundsException if the specified index is negative
// * @since 1.4
//
// public int nextSetBit(int fromIndex) {
// if (fromIndex < 0)
// throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
//
// checkInvariants();
//
// int u = wordIndex(fromIndex);
// if (u >= wordsInUse)
// return -1;
//
// long word = words[u] & (WORD_MASK << fromIndex);
//
// while (true) {
// if (word != 0)
// return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
// if (++u == wordsInUse)
// return -1;
// word = words[u];
// }
// }*/
//
// /**
// * Returns the index of the first bit that is set to {@code false}
// * that occurs on or after the specified starting index.
// *
// * @param fromIndex the index to start checking from (inclusive)
// * @return the index of the next clear bit
// * @throws IndexOutOfBoundsException if the specified index is negative
// * @since 1.4
//
// public int nextClearBit(int fromIndex) {
// // Neither spec nor implementation handle bitsets of maximal length.
// // See 4816253.
// if (fromIndex < 0)
// throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
//
// checkInvariants();
//
// int u = wordIndex(fromIndex);
// if (u >= wordsInUse)
// return fromIndex;
//
// long word = ~words[u] & (WORD_MASK << fromIndex);
//
// while (true) {
// if (word != 0)
// return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(word);
// if (++u == wordsInUse)
// return wordsInUse * BITS_PER_WORD;
// word = ~words[u];
// }
// }*/
//
// /**
// * Returns true if the specified {@code BitSet} has any bits set to
// * {@code true} that are also set to {@code true} in this {@code BitSet}.
// *
// * @param set {@code BitSet} to intersect with
// * @return boolean indicating whether this {@code BitSet} intersects
// * the specified {@code BitSet}
// * @since 1.4
//
// public boolean intersects(BitSet set) {
// for (int i = Math.min(wordsInUse, set.wordsInUse) - 1; i >= 0; i--)
// if ((words[i] & set.words[i]) != 0)
// return true;
// return false;
// }*/
//
// /**
// * Returns the number of bits set to {@code true} in this {@code BitSet}.
// *
// * @return the number of bits set to {@code true} in this {@code BitSet}
// * @since 1.4
// public int cardinality() {
// int sum = 0;
// for (int i = 0; i < wordsInUse; i++)
// sum += Long.bitCount(words[i]);
// return sum;
// }*/
//
// /**
// * Performs a logical <b>AND</b> of this target bit set with the
// * argument bit set. This bit set is modified so that each bit in it
// * has the value {@code true} if and only if it both initially
// * had the value {@code true} and the corresponding bit in the
// * bit set argument also had the value {@code true}.
// *
// * @param set a bit set
// public void and(BitSet set) {
// if (this == set)
// return;
//
// while (wordsInUse > set.wordsInUse)
// words[--wordsInUse] = 0;
//
// // Perform logical AND on words in common
// for (int i = 0; i < wordsInUse; i++)
// words[i] &= set.words[i];
// }*/
//
// /**
// * Performs a logical <b>XOR</b> of this bit set with the bit set
// * argument. This bit set is modified so that a bit in it has the
// * value {@code true} if and only if one of the following
// * statements holds:
// * <ul>
// * <li>The bit initially has the value {@code true}, and the
// * corresponding bit in the argument has the value {@code false}.
// * <li>The bit initially has the value {@code false}, and the
// * corresponding bit in the argument has the value {@code true}.
// * </ul>
// *
// * @param set a bit set
// */
//
//
// /**
// * Clears all of the bits in this {@code BitSet} whose corresponding
// * bit is set in the specified {@code BitSet}.
// *
// * @param set the {@code BitSet} with which to mask this
// * {@code BitSet}
// * @since 1.2
// public void andNot(BitSet set) {
// // Perform logical (a & !b) on words in common
// for (int i = Math.min(wordsInUse, set.wordsInUse) - 1; i >= 0; i--)
// words[i] &= ~set.words[i];
//
// recalculateWordsInUse();
// checkInvariants();
// }*/
