package graph;

import exceptions.InvalidPositionException;
import graph.subsets.Position;

import java.util.ArrayList;

public class Edge<TVertex extends Vertex<V>, V, E> extends Position<E> {

	/** Default generics parameterization **/
	public static class D<V, E> extends Edge<Vertex<V>, V, E> {

		public D(E element, Vertex<V> a, Vertex<V> b, int id) {
			super(element, a, b, id);
		}

	}

	TVertex a;
	TVertex b;

    // For serialization only
    @Deprecated
    public Edge()
    {

    }

	public Edge(E element, TVertex a, TVertex b, int id) {
		super(element, id);
		this.a = a;
		this.b = b;
	}

	@SuppressWarnings("serial")
	public ArrayList<TVertex> endVertices() {
		// TVertex[] ends = (TVertex[]) new Object[2];
		// This is perhaps more correct, but Array.newInstance is very slow
		// TVertex[] ends = (TVertex[]) Array.newInstance(a.getClass(), 2);
		// ends[0] = a;
		// ends[1] = b;
		return new ArrayList<TVertex>() {
			{
				add(Edge.this.a);
				add(Edge.this.b);
			}
		};
	}

	public boolean equals(Edge<TVertex, V, E> e) {
		if (this.elem == null || e.element() == null) {
			return this.elem == e.element();
		}
		if (!this.elem.equals(e.element())) {
			return false;
		}
		if (this.a.equals(e.a) && this.b.equals(e.b)) {
			return true;
		}
		if (this.a.equals(e.b) && this.b.equals(e.a)) {
			return true;
		}
		return false;
	}

	public TVertex left() {
		return this.a;
	}

	public TVertex opposite(TVertex v) throws InvalidPositionException {
		if (v == this.a) {
			return this.b;
		} else if (v == this.b) {
			return this.a;
		} else {
			throw new InvalidPositionException("No such vertex exists");
		}
	}

	public TVertex right() {
		return this.b;
	}

	@Override
	public String toString() {
		String elemstr = this.elem == null ? "-" : "-" + this.elem.toString()
				+ "-";
		return this.a.toString() + elemstr + this.b.toString();
	}
}
