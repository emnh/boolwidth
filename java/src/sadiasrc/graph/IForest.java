package sadiasrc.graph;

import sadiasrc.exceptions.BoundaryViolationException;
import sadiasrc.exceptions.InvalidPositionException;

/**
 * An interface for a tree where nodes can have an arbitrary number of children.
 */

public interface IForest<V extends IVertex, E extends IEdge<? extends V>>
		extends IGraph<V, E> {

	// must override this method to ensure no cycles
	// public boolean insertEdge(E e);


	/** Add a child to the given node. Return true if Forest changed */
	public boolean addChild(V child, V parent,E edge);

	/** Adds a root node to an empty tree. */
	public boolean addRoot(V v);

	/** Returns an iterator over the children of a given node. */
	public Iterable<V> children(V v) throws InvalidPositionException;

	/** Returns whether a given node is external. */
	public boolean isExternal(V v) throws InvalidPositionException;

	/** Returns whether a given node is internal. */
	public boolean isInternal(V v) throws InvalidPositionException;

	/** Returns whether a given node is the root of the tree. */
	public boolean isRoot(V v) throws InvalidPositionException;

	/** Returns whether a given node has parent. */
	public boolean hasParent(V v) throws InvalidPositionException;
	
	/** Returns the parent of a given node. */
	public V parent(V v) throws InvalidPositionException,
			BoundaryViolationException;

	/** Returns the root of the tree. */
	public Iterable<V> roots();
}
