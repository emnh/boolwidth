package sadiasrc.graph;

import sadiasrc.exceptions.InvalidPositionException;

import java.util.ArrayList;

public class IndexEdge<V extends IVertex> extends IndexGraphElement implements
		IEdge<V> {
	V a, b;

	public IndexEdge(IGraph<?, ?> owner, int index, V a, V b) {
		super(owner, index);
		this.a = a;
		this.b = b;
	}

	
	public ArrayList<V> endVertices() {
		ArrayList<V> ends = new ArrayList<V>();
		ends.add(IndexEdge.this.a);
		ends.add(IndexEdge.this.b);
		return ends;
	}

	@Override
	public boolean equals(IEdge<V> e) {
		if (e.getClass() == this.getClass()) {
			return equals((IndexEdge<V>) e);
		}
		return false;
	}

	public boolean equals(IndexEdge<V> e) {
		if (this.a.equals(e.a) && this.b.equals(e.b)) {
			return true;
		}
		if (this.a.equals(e.b) && this.b.equals(e.a)) {
			return true;
		}
		return false;
	}

	public V opposite(V v) throws InvalidPositionException {
		if (v == this.a) {
			return this.b;
		} else if (v == this.b) {
			return this.a;
		} else {
			throw new InvalidPositionException("The Vertex " + v
					+ " does not belong to the Edge " + this);
		}
	}

	@Override
	public String toString() {
		return "[" + this.a.toString() + "-" + this.b.toString() + ":" + id()
				+ "]";
	}
}
