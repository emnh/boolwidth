package sadiasrc.util;

import sadiasrc.graph.IndexVertex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class HashIndexSet implements IIndexedSet<IndexVertex> {

	public HashMap<Integer, IndexVertex> map;
	
	public HashIndexSet()
	{
		map = new HashMap<Integer, IndexVertex>();
	}
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if(o instanceof IndexVertex)
		{
			IndexVertex v = (IndexVertex) o;
			return get(v.id())==v;
		}
		else
			return false;
	}

	@Override
	public Iterator<IndexVertex> iterator() {
		return map.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(IndexVertex e) {
		map.put(e.id(), e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		
		return null!=map.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return map.keySet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends IndexVertex> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IndexVertex get(int i) {
		return map.get(i);
	}

	@Override
	public int indexOf(Object e) {
		if(e instanceof IndexVertex)
		{
			IndexVertex v = (IndexVertex) e;
			return v.id();
		}
		return -1;
	}

	@Override
	/*
	 * Make sure map.get(id) returns tempv
	 * return true if map changed, otherwise false
	 */
	public boolean replace(int id, IndexVertex tempv) {
		if(map.get(id)==tempv)
			return false;
		if(map.containsValue(tempv))
			map.remove(tempv);
		map.put(id, tempv);
		return true;
	}
	
	@Override
	public String toString() {
		return map.values().toString();
	}

}
