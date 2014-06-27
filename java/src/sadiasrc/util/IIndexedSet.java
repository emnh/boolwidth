package sadiasrc.util;

import java.util.Set;

public interface IIndexedSet<E> extends Set<E> {

	/**
	 * @param i
	 * @return The element with specified index, if no such element return null;
	 */
	public E get(int i);

	/**
	 * @param e
	 * @return The element with index equal to the specified index, -1 if
	 *         element is not in the set.
	 */
	public int indexOf(Object e);

	public boolean replace(int id, E tempv);
}
