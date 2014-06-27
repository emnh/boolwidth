package sadiasrc.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The runningtimes in this class assumes O(1) runningtime
 * for contains and get methods in the map.
 *
 * @author mva021
 *
 * @param <E>
 */
public class IndexedSet<E> extends AbstractSet<E> implements IIndexedSet<E>,Cloneable
{
	protected Map<E, Integer> map;
	protected ArrayList<E> list;

	public IndexedSet() {
		this.map = new HashMap<E, Integer>();
		this.list = new ArrayList<E>();
	}

	public IndexedSet(int capacity) {
		capacity = Math.min(capacity, 16);
		this.map = new HashMap<E, Integer>(capacity);
		this.list = new ArrayList<E>(capacity);
	}

	public IndexedSet(Iterable<? extends E> elems) {
		this();
		for (E e : elems) {
			add(e);
		}
	}

	//O(1)
	@Override
	public boolean add(E e) {
		if (this.map.containsKey(e)) {
			return false;
		}
		this.map.put(e, list.size());
		this.list.add(e);
		return true;
	}


	@Override
	public void clear() {
		this.map.clear();
		this.list.clear();
	}

	//O(n)
	@Override
	// Shallow clone
	public Object clone() {
		return new IndexedSet<E>(this);
	}

	//O(1)
	@Override
	public boolean contains(Object o) {
		return this.map.containsKey(o);
	}

	//O(1)
	//returns the element at position i
	@Override
	public E get(int i) {
		if(i<0 || i>=list.size())
			throw new IndexOutOfBoundsException("Can't get "+i+". There is only "+list.size()+" elements in this list.");
		return this.list.get(i);
	}

	/*
	 * O(1)
	 * (non-Javadoc)
	 *
	 * @see interfaces.IIndexdSet#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf(Object e) {
		Integer i = this.map.get(e);
		if (i == null) {
			return -1;
		}
		return i;
	}

	public boolean intersects(Iterable<E> elements) {
		for (E e : elements) {
			if (contains(e)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new IndexedSetIterator<E>(this);
	}

	@Override
	public boolean remove(Object o) {
		//System.out.println("Removing "+o+" from "+this);
		if(o==null || size() == 0) return false;
		Integer i = this.map.remove(o);
		if (i == null || i<0) {
			return false;
		}
		int last = this.list.size() - 1;
		if (i == last) {
			this.list.remove(last);
			return true;
		}
		this.list.set(i, this.list.remove(last));
		this.map.put(this.list.get(i), i);
		return true;
	}

    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for(Object o : c)
        	modified |= remove(o);

        return modified;
    }


	@Override
	public boolean replace(int id, E e) {
		if (contains(e) || id > size()) {
			return false;
		}
		this.list.set(id, e);
		this.map.put(e, id);
		return true;

	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.list.toArray(a);
	}
}
