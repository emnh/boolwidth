package sadiasrc.util;

import java.util.Iterator;

public class IndexedSetIterator<E> implements Iterator<E> {

	IndexedSet<E> set;
	boolean rem;
	int index;
	public IndexedSetIterator(IndexedSet<E> set) {
		this.set = set;
		index = 0;
		rem = false;
	}
	@Override
	public boolean hasNext() {
		return index<set.size();
	}

	@Override
	public E next() {
		rem=false;
		return set.get(index++);
	}

	@Override
	public void remove() {
		if(index==0)
			throw new IllegalStateException("the next method has not yet been called");
		if(rem)
			throw new IllegalStateException("the remove method has already been called after the last call to the next method");
		set.remove(set.get(--index));
		rem = true;
	}

}
