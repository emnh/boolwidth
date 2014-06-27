package sadiasrc.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sadiasrc.util.IndexedSet;

/*
 * This class keeps track of a subset of vertices, for instance
 * the left and right side of a BiGraph.
 * Not that this is not the same as a SubSet, and should not be
 * used if lots of modifications are to be done on this set.
 * The most important operations are indexOf(v) and contains(v)
 * wish both should be O(1) operations.
 * The map is used for this, and should hence not be a HashMap.
 *
 * In addition we want iterate methods to be efficient, hence we
 * have an ArrayList for this purpose.
 *
 * Most methods are general and have been implemented in IndexedSet
 */
public class VertexSet<V extends IndexVertex> extends IndexedSet<V> implements Comparable<VertexSet<V>>{

	public VertexSet() {
		this.map = new HashMap<V,Integer>();//new VertexList<V>(); //maps the index of a vertex in the list
		this.list = new ArrayList<V>();	//the elements
	}

	public VertexSet(int capacity) {
		this.map = new HashMap<V,Integer>(capacity);//new VertexList<V>(capacity);
		this.list = new ArrayList<V>();
	}

	public VertexSet(Iterable<V> it) {
		super(it);
	}

	//not an important method
	@Override
	public int compareTo(VertexSet<V> o) {
		if(this.size()<o.size())
			return -1;
		if(this.size()>o.size())
			return 1;
		int m=0;
		while(m<size())
		{
			int cur = this.get(m).compareTo(o.get(m));
			if(cur!=0)
				return cur;
			m++;
		}
		return 0;
	}

	public VertexSet<IndexVertex> union(VertexSet<IndexVertex> neighbors) {
		VertexSet<IndexVertex> ns = new VertexSet<IndexVertex>(neighbors);
		ns.addAll(this);
		return ns;
	}

	public void db() {
		if(map.entrySet().size()!=list.size())
			System.out.println("Warning! Flawed set.");
	}
	@Override
    public Object clone() {
        return new VertexSet<IndexVertex>((Iterable)this);
    } 
}
