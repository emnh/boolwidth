package interfaces;

import exceptions.InvalidPositionException;
import graph.Edge;
import graph.Vertex;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An interface for a graph.
 */
// TODO: implement IAttributeStorage
public interface IGraph<TVertex extends Vertex<V>, V, E> extends
		IPosSet<TVertex>, IAttributeStorage {
	/** Default generics parameterization **/
	public interface D<V, E> extends IGraph<Vertex<V>, V, E> {
	}

    // like clone, but public
    public IGraph<TVertex, V, E> copy();

	/** Tests whether two vertices are adjacent */
	public boolean areAdjacent(TVertex u, TVertex v)
			throws InvalidPositionException;

	/** Returns an iterator of the elements stored in edges of the graph. */
	public Iterable<E> edgeElements();

	/** Returns the edges of the graph as an iterable collection */
	public Iterable<Edge<TVertex, V, E>> edges();

	/** Returns the endvertices of a vertex as an array of length 2 */
	public ArrayList<TVertex> endVertices(Edge<TVertex, V, E> e)
			throws InvalidPositionException;

	/** Returns the edges incident on a vertex as an iterable collection */
	public Iterable<Edge<TVertex, V, E>> incidentEdges(TVertex v)
			throws InvalidPositionException;

	/** Inserts and return a new edge with a given element between two vertices */
	public Edge<TVertex, V, E> insertEdge(TVertex u, TVertex v, E o)
			throws InvalidPositionException;

	/** Inserts and return a new vertex with a given element */
	public TVertex insertVertex(V o);

	/** Returns the int adjacency matrix of the graph */
	public int[] intBitsAdjacencyMatrix();

	/** Returns the number of edges of the graph */
	public int numEdges();

	/** Returns the number of vertices of the graph */
	public int numVertices();

	/** Returns the other endvertex of an incident edge */
	public TVertex opposite(TVertex v, Edge<TVertex, V, E> e)
			throws InvalidPositionException;

	/** Removes an edge and return its element */
	public E removeEdge(Edge<TVertex, V, E> e) throws InvalidPositionException;

	/**
	 * Removes a vertex and all its incident edges and returns the element
	 * stored at the removed vertex
	 */
	public V removeVertex(TVertex v) throws InvalidPositionException;

	/**
	 * Replaces the element of a given edge with a new element and returns the
	 * old element
	 */
	public E replace(Edge<TVertex, V, E> p, E o)
			throws InvalidPositionException;

	/**
	 * Replaces the element of a given vertex with a new element and returns the
	 * old element
	 */
	public V replace(TVertex p, V o) throws InvalidPositionException;

	public String toDimacs();

	public String toGraphViz(String label);

	/** Returns an iterator of the elements stored in vertices of the graph. */
	public Iterable<V> vertexElements();

	/** Returns the vertices of the graph as an iterable collection */
	public Iterable<TVertex> vertices();

    /** Returns neighbours **/
    public Collection<TVertex> incidentVertices(TVertex v) throws InvalidPositionException;
}
