package sadiasrc.graph;

import sadiasrc.exceptions.EmptyTreeException;

/**
 * An interface for a tree where nodes can have an arbitrary number of children.
 */

public interface ITree<V extends IVertex, E extends IEdge<? extends V>> extends
		IForest<V, E> {

	// must override this method to ensure no cycles
	// public boolean insertEdge(E e);

	/** Returns the root of the tree. */
	public V root() throws EmptyTreeException;
}