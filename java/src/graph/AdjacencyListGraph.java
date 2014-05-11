package graph;

import exceptions.InvalidPositionException;
import interfaces.IAttributeStorage;
import interfaces.IGraph;
import interfaces.IPosition;
import interfaces.IVertexFactory;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;

public class AdjacencyListGraph<TVertex extends Vertex<V>, V, E> extends
AbstractSet<TVertex> implements IGraph<TVertex, V, E>,
Iterable<TVertex>, IAttributeStorage, Cloneable {

	protected ArrayList<TVertex> vList; // container for vertices

	protected ArrayList<Edge<TVertex, V, E>> eList; // container for edges
	// for each
	protected ArrayList<ArrayList<Edge<TVertex, V, E>>> adjacencyList;
	protected IVertexFactory<TVertex, V> vertexFactory;

	// TODO: make decorator
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();

	/** Default generics parameterization **/
	public static class D<V, E> extends AdjacencyListGraph<Vertex<V>, V, E>
	implements IGraph.D<V, E> {

		public D() {
			super(new Vertex.Factory<V>());
		}
	}

	/**
	 * 
	 */
	@Deprecated
	public AdjacencyListGraph() {
		throw new UnsupportedOperationException("remove this and fix errors");
	}

	/** Default constructor that creates an empty graph */
	public AdjacencyListGraph(IVertexFactory<TVertex, V> factory) {
		this.vList = new ArrayList<TVertex>();
		this.eList = new ArrayList<Edge<TVertex, V, E>>();
		this.adjacencyList = new ArrayList<ArrayList<Edge<TVertex, V, E>>>();
		this.vertexFactory = factory;
	}

	/** Test whether two vertices are adjacent. Running time: O(n) */
	public boolean areAdjacent(TVertex u, TVertex v)
	throws InvalidPositionException {
		checkVertex(u);
		checkVertex(v);
		// search the incidence list of the vertex with smaller degree
		Iterable<Edge<TVertex, V, E>> iterToSearch;
		if (degree(u) < degree(v)) {
			iterToSearch = incidentEdges(u);
		} else {
			iterToSearch = incidentEdges(v);
		}
		for (Edge<TVertex, V, E> e : iterToSearch) {
			// if there exists an edge whose endpoints are u and v
			if (e.left() == u && e.right() == v || e.left() == v
					&& e.right() == u) {
				return true;
			}
		}

		return false;
	}

	/** Determines whether a given edge is valid. Running time: O(1) */
	protected void checkEdge(Edge<TVertex, V, E> e)
	throws InvalidPositionException {
		if (e == null || !e.equals(this.eList.get(e.id()))) {
			throw new InvalidPositionException("Edge is invalid");
		}
	}

	protected void checkPosition(Edge<TVertex, V, E> p)
	throws InvalidPositionException {
		checkPosition((IPosition<?>) p);
		if (p.id() >= this.eList.size()) {
			throw new InvalidPositionException("Position is invalid");
		}
		checkEdge(p);
	}

	/** Determines whether a given position is valid. Running time: O(1) */
	// @SuppressWarnings("unchecked")
	protected void checkPosition(IPosition<?> p)
	throws InvalidPositionException {
		if (p == null) {
			throw new InvalidPositionException("Position is invalid");
		}
	}

	protected void checkPosition(TVertex p) throws InvalidPositionException {
		checkPosition((IPosition<?>) p);
		checkVertex(p);
	}

	/** Determines whether a given vertex is valid. Running time: O(1) */
	protected void checkVertex(TVertex v) throws InvalidPositionException {
		if (v == null || !v.equals(this.vList.get(v.id()))) {
			throw new InvalidPositionException("Vertex is invalid");
		}
	}

	public AdjacencyListGraph<TVertex, V, E> copy() {
		if (this.vList.isEmpty()) {
			return new AdjacencyListGraph<TVertex, V, E>(this.vertexFactory);
		} else {
			throw new UnsupportedOperationException("non-empty graph copying not implemented");
		}
	}

	/**
	 * Create new vertex
	 * 
	 * @param v
	 *            vertex element
	 * @param id
	 * @return
	 */
	public TVertex createVertex(V v, int id) {
		return this.vertexFactory.createNew(v, id);
	}

	/** Return the degree of a given vertex. Running time: O(1) */
	public int degree(TVertex v) {
		checkVertex(v);
		return this.adjacencyList.get(v.id()).size();
	}

	/**
	 * Return an iterator over the elements of all the edges. Running time: O(n)
	 */
	@Deprecated
	public Iterable<E> edgeElements() {
		ArrayList<E> al = new ArrayList<E>(this.vList.size());
		for (Edge<TVertex, V, E> e : this.eList) {
			al.add(e.elem);
		}
		return al;
	}

	/** Return an iterator over the edges of the graph */
	public Iterable<Edge<TVertex, V, E>> edges() {
		return this.eList;

	}

	/**
	 * Return the endvertices of a edge in an array of length 2. Running time:
	 * O(1)
	 */
	public ArrayList<TVertex> endVertices(Edge<TVertex, V, E> e)
	throws InvalidPositionException {
		checkEdge(e);
		return e.endVertices();
	}

	/** Running time: O(n) */
	protected void fixIds(ArrayList<? extends IPosition<?>> al) {
		for (int i = 0; i < al.size(); i++) {
			al.get(i).setId(i);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttr(String key) {
		if (this.attributes.containsKey(key)) {
			return (T) this.attributes.get(key);
		} else {
			// TODO: get default from configuration
			return null;
		}
	}

	@Override
	public int getId(TVertex v) {
		return v.id();
	}

	/**
	 * ID for new vertex
	 * 
	 * @return
	 */
	public int getNextID() {
		return this.vList.size();
	}

	@Override
	public TVertex getVertex(int i) {
		return this.vList.get(i);
	}

	@Override
	public boolean hasAttr(String key) {
		return this.attributes.containsKey(key);
	}

	/**
	 * Return an iterator over the edges incident to a vertex. Running time:
	 * O(1)
	 */
	public Iterable<Edge<TVertex, V, E>> incidentEdges(TVertex v)
	throws InvalidPositionException {
		checkVertex(v);
		return this.adjacencyList.get(v.id());
	}

	/**
	 * Return an iterator over the vertices incident to a vertex. Running time:
	 * O(n)
	 */
	public Iterable<TVertex> incidentVertices(TVertex v)
	throws InvalidPositionException {
		ArrayList<TVertex> vertices = new ArrayList<TVertex>();
		for (Edge<TVertex, V, E> edge : incidentEdges(v)) {
			vertices.add(edge.opposite(v));
		}
		return vertices;
	}

	// Auxiliary methods

	/**
	 * Insert and return a new edge with a given element between two vertices
	 * Running time: O(1)
	 */
	public Edge<TVertex, V, E> insertEdge(TVertex v, TVertex w, E o)
	throws InvalidPositionException {
		checkVertex(v);
		checkVertex(w);
		Edge<TVertex, V, E> ee = new Edge<TVertex, V, E>(o, v, w, this.eList
				.size());
		this.adjacencyList.get(v.id()).add(ee);
		this.adjacencyList.get(w.id()).add(ee);
		this.eList.add(ee);
		return ee;
	}

	protected TVertex insertVertex(TVertex v) {
		if (this.vList.size() != v.id()) {
			throw new InvalidPositionException("incorrect id");
		}
		this.vList.add(v);
		ArrayList<Edge<TVertex, V, E>> al = new ArrayList<Edge<TVertex, V, E>>();
		this.adjacencyList.add(al);
		return v;
	}

	/**
	 * Insert and return a new vertex with a given element. Running time: O(1)
	 */
	@Deprecated
	public TVertex insertVertex(V v) {
		// TVertex vv = new TVertex(v, vList.size());
		TVertex vv = createVertex(v, this.vList.size());
		return insertVertex(vv);
	}

	/** Default constructor that creates an empty graph */
	// constructing generic factory from vertex class was too slow
	// public AdjacencyListGraph(Class<?> vertexcls) {
	// vList = new ArrayList<TVertex>();
	// eList = new ArrayList<Edge<TVertex, V, E>>();
	// adjacencyList = new ArrayList<ArrayList<Edge<TVertex, V, E>>>();
	// vertexFactory = new GenericFactory<TVertex>(
	// vertexcls,
	// Object.class,
	// int.class);
	// }

	public int[] intBitsAdjacencyMatrix() {
		assert numVertices() <= 30; // 31?

		int[] m = new int[numVertices()];
		Arrays.fill(m, 0);
		// int rowidx = 0;
		for (TVertex v : vertices()) {
			// assert v.id() == rowidx;
			// int row = 0;
			for (TVertex n : incidentVertices(v)) {
				m[v.id()] |= 1 << n.id();
			}
			// m[rowidx] = row;
			// rowidx++;
		}

		return m;
	}

	@Override
	public Iterator<TVertex> iterator() {
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
	public TVertex opposite(TVertex v, Edge<TVertex, V, E> e)
	throws InvalidPositionException {
		checkVertex(v);
		checkEdge(e);
		return e.opposite(v);
	}

	public boolean remove(TVertex v) {
		if (this.vList.contains(v)) {
			return false;
		} else {
			removeVertex(v);
			return true;
		}
	}

	/** Remove an edge and return its element. Running time: O(n) */
	public E removeEdge(Edge<TVertex, V, E> e) throws InvalidPositionException {
		checkEdge(e);
		// System.out.printf("LR: %s %s\n", e.left(), e.right());
		// checkVertex(e.left());
		// checkVertex(e.right());
		this.adjacencyList.get(e.left().id()).remove(e);
		this.adjacencyList.get(e.right().id()).remove(e);
		int last = this.eList.size() - 1;
		Edge<TVertex, V, E> temp = this.eList.remove(last);
		if (e.id() == last) {
			return temp.element();
		}
		temp.setId(e.id());
		this.eList.set(e.id(), temp);
		return e.element();
	}

	/** Remove an edge and return its element. Running time: O(m) */
	protected E removeEdgeOneWay(Edge<TVertex, V, E> e, TVertex removed)
	throws InvalidPositionException {
		checkEdge(e);
		TVertex other = opposite(removed, e);

		this.adjacencyList.get(other.id()).remove(e);
		int last = this.eList.size() - 1;
		Edge<TVertex, V, E> temp = this.eList.remove(last);
		if (e.id() == last) {
			return temp.element();
		}
		temp.setId(e.id());
		this.eList.set(e.id(), temp);
		return e.element();
		// TODO: check consistency
	}

	/**
	 * Remove a vertex and all its incident edges and return the element stored
	 * at the removed vertex. Running time: O(n^2)
	 */
	public V removeVertex(TVertex v) throws InvalidPositionException {
		checkVertex(v);
		// TODO: optimize.
		// if we iterate cleverly without iterator we don't need a copy
		for (Edge<TVertex, V, E> e : incidentEdges(v)) {
			removeEdgeOneWay(e, v);
		}

		int last = this.vList.size() - 1;
		TVertex tempv = this.vList.remove(last);
		ArrayList<Edge<TVertex, V, E>> templ = this.adjacencyList.remove(last);
		if (v.id() != last) {
			this.vList.set(v.id(), tempv);
			this.adjacencyList.set(v.id(), templ);
			tempv.setId(v.id());
		}
		return v.element();
	}

	/**
	 * Replaces the element of edge p with element o. Returns the former
	 * element. Running time: O(1)
	 */
	public E replace(Edge<TVertex, V, E> p, E o)
	throws InvalidPositionException {
		checkEdge(p);
		E temp = p.element();
		p.setElement(o);
		return temp;
	}

	/**
	 * Replaces the element of vertex p with element o. Returns the former
	 * element. Running time: O(1).
	 */
	public V replace(TVertex p, V o) throws InvalidPositionException {
		checkVertex(p);
		V temp = p.element();
		p.setElement(o);

		return temp;
	}

	// TODO: accept configuration with types and defaults
	@Override
	public void setAttr(String key, Object value) {
		this.attributes.put(key, value);
	}

	@Override
	public int size() {
		return numVertices();
	}

	public V test() {
		return null;
	}

	public String toDimacs() {
		StringBuffer s = new StringBuffer();
		Formatter f = new Formatter(s);
		f.format("p edge %d %d\n", numVertices(), numEdges());
		for (Edge<TVertex, V, E> e : edges()) {
			f.format("e %s %s\n", e.left().id() + 1, e.right().id() + 1);
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
		for (Edge<TVertex, V, E> e : edges()) {
			f.format("\"%s\" -- \"%s\";\n", e.left().hashCode(), e.right()
					.hashCode());
		}
	}

	protected void toGraphVizNodes(Formatter f) {
		// int i = 0;
		for (TVertex n : vertices()) {
			// f.format("%s [ label = \"%d\" ];\n", n.hashCode(), ++i);
			f.format("%s [ label = \"%d,d=%d\",height=%.2f ];\n", n.hashCode(),
					n.id(), degree(n), 0.1 * degree(n));
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

	/**
	 * Return an iterator over the elements of all the vertices. Running time:
	 * O(n)
	 */
	@Deprecated
	public Iterable<V> vertexElements() {
		ArrayList<V> al = new ArrayList<V>(this.vList.size());
		for (TVertex e : this.vList) {
			al.add(e.elem);
		}
		return al;

	}

	/** Return an iterator over the vertices of the graph */
	public Iterable<TVertex> vertices() {
		return this.vList;
	}

}
