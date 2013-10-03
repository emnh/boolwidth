package graph;

import interfaces.IPosSet;
import interfaces.ISetPosition;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: make new vertexset-id inside vertex to avoid hashmap (?)

@SuppressWarnings("serial")
public class PosSet<TVertex extends ISetPosition> extends ArrayList<TVertex>
		implements IPosSet<TVertex> {

	HashMap<TVertex, Integer> ids;

	private static final int DEFAULT_SIZE = 32;

	public PosSet() {
		this(DEFAULT_SIZE);
	}

	public PosSet(int n) {
		super(n);
		this.ids = new HashMap<TVertex, Integer>(n);
	}

	/**
	 * O(n)
	 * 
	 * @param it
	 */
	public PosSet(Iterable<TVertex> it) {
		this(DEFAULT_SIZE);

		for (TVertex v : it) {
			add(v);
		}
	}

	/**
	 * O(1)
	 */
	@Override
	public boolean add(TVertex v) {
		this.ids.put(v, size());
		super.add(v);
		return true;
	}

	/**
	 * O(1)
	 */
	@Override
	public boolean contains(Object o) {
		// if (o instanceof Vertex) {
		// TVertex v = (TVertex) o;
		// int i = getId(v);
		// if (i < size() && i >= 0)
		// return get(i) == v;
		// }
		// return false;
		return this.ids.containsKey(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graph.IPosSet#getId(int)
	 */
	public int getId(TVertex i) {
		if (this.ids.get(i) == null) {
			return -1;
		}
		return this.ids.get(i);
	}

	@Override
	public TVertex getVertex(int v) {
		return get(v);
	}

	/**
	 * O(1)
	 */
	public boolean remove(TVertex v) {
		TVertex temp = getVertex(size() - 1);
		int index = getId(v);
		set(index, temp);
		super.remove(size() - 1);
		this.ids.remove(index);
		return true;
	}
}
