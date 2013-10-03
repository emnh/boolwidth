package graph;

import exceptions.InvalidPositionException;

/**
 * A realization of a graph according to adjacency list structure where the
 * element of the vertices is an integer and the edge element is a string.
 */

public class IntegerGraph extends AdjacencyListGraph<Vertex<Integer>, Integer, String> {

	public IntegerGraph() {
		this(64);
	}

	// nodes are integers numbered 0,1,..,n-1
	public IntegerGraph(int maxIndex) {
		super(new Vertex.Factory<Integer>());
		for (int a = 0; a < maxIndex; a++) {
			insertVertex(a);
		}
	}

	private void check(int pos) {
		if (pos < 0) {
			throw new InvalidPositionException("Negative id not allowed");
		}
		while (pos >= numVertices()) {
			insertVertex(numVertices());
		}
	}

	public Edge<Vertex<Integer>, Integer, String> insertEdge(int v, int w)
	throws InvalidPositionException {
		check(v);
		check(w);
		Vertex<Integer> a = this.vList.get(v);
		Vertex<Integer> b = this.vList.get(w);
		if (a == b || areAdjacent(a, b)) {
			return null;
		}
		return super.insertEdge(a, b, "-");
	}

	@Override
	public String toString() {
		String s = "" + numVertices() + " nodes and " + numEdges()
		+ " edges.\n";
		for (Vertex<Integer> v : this.vList) {
			s += v.element() + ": ";
			boolean first = true;
			for (Vertex<Integer> n : incidentVertices(v)) {
				if (!first) {
					s += ",";
				}
				s += n.element();
				first = false;
			}
			s += "\n";
		}
		return s;
	}
}
