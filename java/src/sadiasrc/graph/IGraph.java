package sadiasrc.graph;

import sadiasrc.exceptions.InvalidPositionException;

/**
 * An interface for a graph.
 */
/**
 * @author MaVa
 *
 * @param <V>
 * @param <E>
 */
public interface IGraph<V extends IVertex, E extends IEdge<? extends V>> {

	public String graphname();
	/** Tests whether two vertices are adjacent */
	public boolean areAdjacent(V u, V v) throws InvalidPositionException;

	public boolean contains(E e);

	public boolean contains(V v);

	/** Returns the edges of the graph as an iterable collection */
	public Iterable<E> edges();

	/** Returns the edges incident on a vertex as an iterable collection */
	public Iterable<E> incidentEdges(V v) throws InvalidPositionException;

	/** Inserts and return a new edge with a given element between two vertices */
	public boolean insertEdge(E e) throws InvalidPositionException;

	/** Creates a new vertex */
	public boolean insertVertex(V v);

	/** Returns the int adjacency matrix of the graph */
	// TODO: change to boolean[][];
	public int[] intAdjacencyMatrix();

	/** Returns whether the tree is empty. */
	public boolean isEmpty();

	public Iterable<V> neighbours(V v);
	

	/** Returns the number of edges of the graph */
	public int numEdges();

	/** Returns the number of vertices of the graph */
	public int numVertices();

	/** Returns the other endvertex of an incident edge */
	public V opposite(V v, E e) throws InvalidPositionException;

	/** Removes an edge and return true if this graph changed */
	public boolean removeEdge(E e) throws InvalidPositionException;

	/**
	 * Removes a vertex and all its incident edges.
	 * 
	 * @param v
	 *            the vertex to remove
	 * @return true if this graph changed
	 */
	public boolean removeVertex(V v);

	/** Returns the vertices of the graph as an iterable collection */
	public Iterable<V> vertices();
	
	
	/**
	 * @param v a vertex of the graph
	 * @return the number of neighbours of v
	 */
	public int degree(V v);
}
