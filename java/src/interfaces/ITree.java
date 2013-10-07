package interfaces;

import exceptions.BoundaryViolationException;
import exceptions.EmptyTreeException;
import exceptions.InvalidPositionException;
import graph.Vertex;

/**
 * An interface for a tree where nodes can have an arbitrary number of children.
 */

public interface ITree<TVertex extends Vertex<V>, V, E> extends
		IGraph<TVertex, V, E> {

	// must override this method to ensure no cycles
	// public Edge<V,E> insertEdge(TVertex u, TVertex v, E o) throws
	// InvalidPositionException;

	/** Returns an iterator over the children of a given node. */
	public Iterable<TVertex> children(TVertex v)
			throws InvalidPositionException;

	/** Returns whether the tree is empty. */
	public boolean isEmpty();

	/** Returns whether a given node is external. */
	public boolean isExternal(TVertex v) throws InvalidPositionException;

	/** Returns whether a given node is internal. */
	public boolean isInternal(TVertex v) throws InvalidPositionException;

	/** Returns whether a given node is the root of the tree. */
	public boolean isRoot(TVertex v) throws InvalidPositionException;

	/** Returns the parent of a given node. */
	public TVertex parent(TVertex v) throws InvalidPositionException,
			BoundaryViolationException;

	/** Returns the root of the tree. */
	public TVertex root() throws EmptyTreeException;
}