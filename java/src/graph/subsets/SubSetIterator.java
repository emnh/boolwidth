package graph.subsets;

import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.ISetPosition;

import java.util.Iterator;

public class SubSetIterator<V extends ISetPosition> implements
		Iterator<PosSubSet<V>>, Iterable<PosSubSet<V>> {
	PosSubSet<V> set;
	boolean oneSize;
	boolean isFirst;
	int size;
	int firstFree;
	int firstBit;
	int nextFree;

	public SubSetIterator(Iterable<V> set) {
		PosSet<V> al = new PosSet<V>(set);
		this.set = new PosSubSet<V>(al);
		this.oneSize = false;
		this.firstFree = 0;
		this.isFirst = true;
	}

	// TODO: does not work for k==0 or k==size()
	public SubSetIterator(Iterable<V> set, int k) {
		PosSet<V> al = new PosSet<V>(set);
		this.set = new PosSubSet<V>(al);
		this.oneSize = true;
		this.size = k;
		this.firstFree = k;
		this.firstBit = 0;
		if (k == 0) {
			this.firstBit = this.set.groundSetSize();
		}
		this.nextFree = k;
		for (int i = 0; i < k; i++) {
			this.set.set(i, true);
		}
		this.isFirst = true;
	}

	/**
	 * Warning: will modify set
	 * 
	 * @param set
	 */
	public SubSetIterator(PosSubSet<V> set) {
		this.set = set;
		this.oneSize = false;
		this.firstFree = 0;
		this.isFirst = true;
	}

	/**
	 * Warning: will modify set
	 * 
	 * @param set
	 * @param k
	 */
	public SubSetIterator(PosSubSet<V> set, int k) {
		this.set = set;
		this.oneSize = true;
		this.size = k;
		this.firstFree = k;
		this.firstBit = 0;
		if (k == 0) {
			this.firstBit = this.set.groundSetSize();
		}
		this.nextFree = k;
		for (int i = 0; i < k; i++) {
			this.set.set(i, true);
		}
		this.isFirst = true;
	}

	public boolean hasNext() {
		if (this.oneSize) {
			return this.nextFree < this.set.groundSetSize();
		} else {
			return this.firstFree < this.set.groundSetSize();
		}
	}

	@Override
	public Iterator<PosSubSet<V>> iterator() {
		return this;
	}

	public PosSubSet<V> next() {
		if (this.isFirst) {
			this.isFirst = false;
			return this.set.clone();
		}
		if (this.oneSize) {
			return next(this.size);
		} else {
			return nextAny();
		}
	}

	protected PosSubSet<V> next(int k) {
		this.set.set(this.nextFree, true);
		this.set.set(this.nextFree - 1, false);
		int bits = this.nextFree - this.firstBit;
		for (int i = 0; i < bits - 1; i++) {
			this.set.set(this.firstBit + i, false);
			this.set.set(i, true);
		}
		this.firstFree = bits - 1;
		if (bits == 1) {
			this.firstBit++;
			while (this.nextFree < this.set.groundSetSize()
					&& this.set.get(this.nextFree)) {
				this.nextFree++;
			}
		} else {
			this.firstBit = 0;
			this.nextFree = bits - 1;
		}
		return this.set.clone();
	}

	protected PosSubSet<V> nextAny() {
		this.set.set(this.firstFree, true);
		for (int i = 0; i < this.firstFree; i++) {
			this.set.set(i, false);
		}
		this.firstFree = 0;
		while (this.firstFree < this.set.groundSetSize()
				&& this.set.get(this.firstFree)) {
			this.firstFree++;
		}
		return this.set.clone();
	}

	// this method is not supported.
	public void remove() {
		// TODO Auto-generated method stub
	}
}
