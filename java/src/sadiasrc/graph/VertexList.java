package sadiasrc.graph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VertexList<V extends IndexVertex> extends AbstractMap<V,Integer>
{
	ArrayList<V> list;

	VertexList()
	{
		list = new ArrayList<V>();
	}

	public VertexList(int capacity) {
		list = new ArrayList<V>(capacity);
	}

	@Override
	public Set<java.util.Map.Entry<V, Integer>> entrySet() {
		HashSet<Entry<V,Integer>> hs = new HashSet<Entry<V, Integer>>();
		for(int i=0; i<list.size();i++)
			if(list.get(i)!=null)
				hs.add(new SimpleEntry<V,Integer>(list.get(i),i));
		return hs;
	}


//
//	@Override
//	public boolean containsKey(Object key) {
//		int index = indexOf(key);
//
//		if(index>list.size() || index<0)
//			return false;
//		if(list.get(index)==null)
//			return false;
//		return true;
//	}
//
//	@Override
//	public boolean containsValue(Object value) {
//		return list.contains(value);
//	}
//
//	private int indexOf( Object key)
//	{
//		if(! (key instanceof Integer))
//			return -1;
//		return (Integer)key;
//
//	}
//
//	@Override
//	public V get(Object key) {
//		return list.get(indexOf(key));
//	}
//
//	@Override
//	public V put(Integer key, V value) {
//		V old = list.get(key);
//		list.set(key, value);
//		return old;
//	}
//
//
//	@Override
//	public Set<Integer> keySet() {
//		HashSet<Integer> hs = new HashSet<Integer>();
//		for(int i=0; i<list.size();i++)
//			if(list.get(i)!=null)
//				hs.add(i);
//		return hs;
//	}
//
//	@Override
//	public Collection<V> values() {
//		return list;
//	}
//
//
//
//	@Override
//	public V remove(Object key) {
//		return list.set(indexOf(key), null);
//	}
}
