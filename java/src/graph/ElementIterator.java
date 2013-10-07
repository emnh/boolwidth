package graph;

import interfaces.IPosition;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple iterator class for lists. The elements of a list are returned by
 * this iterator. No copy of the list is made, so any changes to the list are
 * reflected in the iterator.
 */
public class ElementIterator<E> implements Iterator<E> {
	protected Iterator<IPosition<E>> list; // the underlying list

	/** Creates an element iterator over the given list. */
	public ElementIterator(Iterable<IPosition<E>> list) {
		this.list = list.iterator();
	}

	/** Returns whether the iterator has a next object. */
	public boolean hasNext() {
		return this.list.hasNext();
	}

	/** Returns the next object in the iterator. */
	public E next() throws NoSuchElementException {
		if (!hasNext()) {
			throw new NoSuchElementException("No next element");
		}
		return this.list.next().element();
	}

	/**
	 * Throws an {@link UnsupportedOperationException} in all cases, because
	 * removal is not a supported operation in this iterator.
	 */
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("remove");
	}
}