package sadiasrc.graph;

import sadiasrc.util.IIndexedSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import sadiasrc.util.IndexedSet;
import sadiasrc.exceptions.InvalidPositionException;

public class AdjacencyListGraph<V extends IVertex, E extends IEdge<V>>
		implements IGraph<V, E>, Iterable<V> {

	protected IIndexedSet<V> vList; // container for vertices
	protected IIndexedSet<E> eList; // container for edges
	protected ArrayList<ArrayList<E>> adjacencyList; // for each vertex, keep

    // duplicates edge list as neighbor list for minor constant speedup in case edge not needed
    protected ArrayList<ArrayList<V>> neighbors;
	protected int index;

	// adjacent edges

	/** Default constructor that creates an empty graph */
	public AdjacencyListGraph() {
		this.vList = new IndexedSet<V>();
		this.eList = new IndexedSet<E>();
		this.adjacencyList = new ArrayList<ArrayList<E>>();
        this.neighbors = new ArrayList<ArrayList<V>>();
	}

	public AdjacencyListGraph(int capacity) {
		this.vList = new IndexedSet<V>(capacity);
		this.eList = new IndexedSet<E>(capacity);
		this.adjacencyList = new ArrayList<ArrayList<E>>(capacity);
        this.neighbors = new ArrayList<ArrayList<V>>(capacity);
	}

	
	public int nextIndex()
	{
		return index++;
	}
	
	/** Test whether two vertices are adjacent. Running time: O(n) */
	public boolean areAdjacent(V u, V v) throws InvalidPositionException {
		checkVertex(u);
		checkVertex(v);
		// search the incidence list of the vertex with smaller degree
		Iterable<E> iterToSearch;
		if (degree(u) < degree(v)) {
			iterToSearch = incidentEdges(u);
		} else {
			iterToSearch = incidentEdges(v);
		}
		for (E e : iterToSearch) {
			ArrayList<V> vs = e.endVertices();
			// if there exists an edge whose endpoints are u and v
			if (vs.get(0) == u && vs.get(1) == v || vs.get(0) == v
					&& vs.get(1) == u) {
				return true;
			}
		}
		return false;
	}

	/** Test whether two vertices are adjacent. Running time: O(n) */
	public boolean areAdjacent(int U, int V) throws InvalidPositionException {
		V u = getVertex(U);
		V v = getVertex(V);
		checkVertex(u);
		checkVertex(v);
		// search the incidence list of the vertex with smaller degree
		Iterable<E> iterToSearch;
		if (degree(u) < degree(v)) {
			iterToSearch = incidentEdges(u);
		} else {
			iterToSearch = incidentEdges(v);
		}
		for (E e : iterToSearch) {
			ArrayList<V> vs = e.endVertices();
			// if there exists an edge whose endpoints are u and v
			if (vs.get(0) == u && vs.get(1) == v || vs.get(0) == v
					&& vs.get(1) == u) {
				return true;
			}
		}
		return false;
	}

	/** Determines whether a given edge is valid. Running time: O(1) */
	protected void checkEdge(E e) throws InvalidPositionException {
		if (e == null || !this.eList.contains(e)) {
			throw new InvalidPositionException("The Edge " + e
					+ " is not an edge of this Graph.");
		}
	}

	/** Determines whether a given vertex is valid. Running time: O(1) */
	protected void checkVertex(V v) throws InvalidPositionException {
		//System.out.println("Vlist"+vList);
		if (v == null || !this.vList.contains(v)) {
			throw new InvalidPositionException("The Vertex " + v
					+ " is not a vertex of this Graph");
		}
	}

	@Override
	public boolean contains(V v) {
		return this.vList.contains(v);
	}

	public boolean containsEdge(E e) {
		return this.eList.contains(e);
	}

	@SuppressWarnings("unchecked")
	public V createVertex() {
		V v = vertices().iterator().next();
		return (V) v.createInstance(this);
	}

	/** Return the degree of a given vertex. Running time: O(1) */
	public int degree(V v) {
		checkVertex(v);
		return this.adjacencyList.get(this.vList.indexOf(v)).size();
	}
	

	/** Return an iterator over the edges of the graph */
	public Iterable<E> edges() {
		return this.eList;

	}

	/**
	 * Return the endvertices of a edge in an array of length 2. Running time:
	 * O(1)
	 */
	public ArrayList<V> endVertices(E e) throws InvalidPositionException {
		checkEdge(e);
		return e.endVertices();
	}

	protected int getIndex(V v) {
		return this.vList.indexOf(v);
	}

	/**
	 * Returns the vertex with given id.
	 *
	 * @param i
	 *            id of the vertex to find.
	 * @return the vertex with the given id if such a vertex exist, null
	 *         otherwise
	 */
	public V getVertex(int i) {
		if (i > this.vList.size()) {
			return null;
		}
		return this.vList.get(i);
	}

	// Auxiliary methods

	/**
	 * Return an iterator over the edges incident to a vertex. Running time:
	 * O(1)
	 */
	public Iterable<E> incidentEdges(V v) throws InvalidPositionException {
		checkVertex(v);
		int i = getIndex(v);
		if (i < 0) {
			throw new InvalidPositionException();
		} else {
			return this.adjacencyList.get(i);
		}
	}

	/**
	 * Return an iterator over the vertices incident to a vertex. Running time:
	 * O(n)
	 */
	public Collection<V> incidentVertices(V v) throws InvalidPositionException {
		/*ArrayList<V> vertices = new ArrayList<V>();
		for (E edge : incidentEdges(v)) {
			vertices.add(edge.opposite(v));
		}
		return vertices;
		*/
        return this.neighbors.get(getIndex(v));
	}

	/**
	 * Insert and return a new edge with a given element between two vertices
	 * Running time: O(1)
	 */
	public boolean insertEdge(E e) throws InvalidPositionException {
		for (V v : e.endVertices()) {
			checkVertex(v);
		}
		if (!areAdjacent(e.endVertices().get(0), e.endVertices().get(0))) {
			for (V v : e.endVertices()) {
				this.adjacencyList.get(getIndex(v)).add(e);
                this.neighbors.get(getIndex(v)).add(e.opposite(v));
			}
		}

		this.eList.add(e);
		return true;
	}

	public boolean insertVertex(V v) {
		if (this.vList.add(v)) {
			ArrayList<E> al = new ArrayList<E>();
			this.adjacencyList.add(al);
            ArrayList<V> nl = new ArrayList<V>();
            this.neighbors.add(nl);
			return true;
		}
		return false;
	}

	public int[] intAdjacencyMatrix() {
		assert numVertices() <= 30; // 31?

		int[] m = new int[numVertices()];
		Arrays.fill(m, 0);
		// int rowidx = 0;
		for (V v : vertices()) {
			// assert v.id() == rowidx;
			// int row = 0;
			for (V n : incidentVertices(v)) {
				m[getIndex(v)] |= 1 << getIndex(n);
			}
			// m[rowidx] = row;
			// rowidx++;
		}

		return m;
	}

	public boolean[][] adjacencyMatrix() {

		boolean[][] adj = new boolean[numVertices()][numVertices()];
		for (V v : vertices()) {
			for (V n : incidentVertices(v)) {
				adj[getIndex(v)][getIndex(n)] = true;
				
			}
		}
		return adj;
	}

	@Override
	public boolean isEmpty() {
		return numVertices() == 0;
	}

	@Override
	public Iterator<V> iterator() {
		return this.vList.iterator();
	}

	/** Return number of edges */
	public int numEdges() {
		return this.eList.size();
	}

	/** Return number of vertices */
	public int numVertices() {
		return this.vList.size();
	}

	/** Return the other endvertex of an incident edge. Running time: O(1) */
	public V opposite(V v, E e) throws InvalidPositionException {
		checkVertex(v);
		checkEdge(e);
		return e.opposite(v);
	}

	/** Remove an edge and return its element. Running time: O(n) */
	public boolean removeEdge(E e) throws InvalidPositionException {
		checkEdge(e);
		this.adjacencyList.get(getIndex(e.endVertices().get(0))).remove(e);
		this.adjacencyList.get(getIndex(e.endVertices().get(1))).remove(e);
		this.eList.remove(e);
		return true;
	}

	/** Remove an edge. Running time: O(m) */
	protected boolean removeEdgeOneWay(E e, V removed)
			throws InvalidPositionException {
		return this.adjacencyList.get(getIndex(opposite(removed, e))).remove(e);
		// TODO: check consistency
	}

	/**
	 * Remove a vertex and all its incident edges and return the element stored
	 * at the removed vertex. Running time: O(n^2)
	 */
	public boolean removeVertex(V v) throws InvalidPositionException {
		checkVertex(v);
		// TODO: optimize.
		// if we iterate cleverly without iterator we don't need a copy
		for (E e : incidentEdges(v)) {
			removeEdgeOneWay(e, v);
			this.eList.remove(v);
		}
		this.adjacencyList.remove(incidentEdges(v));
		this.vList.remove(v);

		return true;
	}

	public String toDimacs() {
		StringBuffer s = new StringBuffer();
		Formatter f = new Formatter(s);
		f.format("p edge %d %d\n", numVertices(), numEdges());
		for (E e : edges()) {
			f.format("e %s %s\n", getIndex(e.endVertices().get(0)) + 1,
					getIndex(e.endVertices().get(1)) + 1);
		}
		return s.toString();
	}

	public String toGraphViz(String label) {
		StringBuffer s = new StringBuffer();
		Formatter f = new Formatter(s);
		f.format("graph {\n");
		label = label.replaceAll("\n", "\\n");
		// what to do if nodes overlap
		f.format("overlap = scale;\n");
		// curve edges around nodes
		f.format("splines = true;\n");
		// node shape
		f.format("node [shape=circle];\n");
		// graph title
		f.format("label = \"%s\";\n", label);
		toGraphVizNodes(f);
		toGraphVizEdges(f);
		f.format("}\n");
		return s.toString();
	}

	protected void toGraphVizEdges(Formatter f) {
		for (E e : edges()) {
			f.format("\"%s\" -- \"%s\";\n", e.endVertices().get(0).hashCode(),
					e.endVertices().get(1).hashCode());
		}
	}

	protected void toGraphVizNodes(Formatter f) {
		// int i = 0;
		for (V n : vertices()) {
			// f.format("%s [ label = \"%d\" ];\n", n.hashCode(), ++i);
			f.format("%s [ label = \"%d,d=%d\",height=%.2f ];\n", n.hashCode(),
					getIndex(n), degree(n), 0.1 * degree(n));
		}
	}

	/**
	 * Returns a string representation of the vertex and edge lists, separated
	 * by a newline.
	 */
	@Override
	public String toString() {
		return this.vList.toString() + "\n" + this.eList.toString();
	}

	/** Return an iterator over the vertices of the graph */
	public Iterable<V> vertices() {
		return this.vList;
	}

	@Override
	public boolean contains(E e) {
		return eList.contains(e);
	}

	@Override
	public Collection<V> neighbours(V v) {
		/*ArrayList<V> ns = new ArrayList<V>();
		//for(E e : incidentEdges(v))
		for (V n : incidentVertices(v))
			ns.add(n);*/
		return incidentVertices(v);
	}
	

	@Override
	public String graphname() {
		// TODO Auto-generated method stub
		String name="G_"+numVertices()+"_"+numEdges();
		return name;
	}

	
}
