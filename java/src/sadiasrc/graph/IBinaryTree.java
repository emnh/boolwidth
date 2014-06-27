package sadiasrc.graph;

import sadiasrc.exceptions.BoundaryViolationException;
import sadiasrc.exceptions.InvalidPositionException;

/**
 * An interface for a binary tree, where each node can have zero, one, or two
 * children.
 * 
 * @author Roberto Tamassia, Michael Goodrich
 */
public interface IBinaryTree<V extends IVertex, E extends IEdge<? extends V>>
		extends ITree<V, E> {
	/** Adds v as a left child to node p. Returns the child node. */
	public boolean addLeft(V p, V child, E e) throws InvalidPositionException;

	/** Adds a right child storing element e to node p. Returns the child node. */
	public boolean addRight(V p, V child, E e) throws InvalidPositionException;

	/** Attaches two trees to be subtrees of an external node. */
	public void attach(V root, IBinaryTree<V, E> t1, IBinaryTree<V, E> t2)
			throws InvalidPositionException;

	/** Returns whether a node has a left child. */
	public boolean hasLeft(V p) throws InvalidPositionException;

	/** Returns whether a node has a right child. */
	public boolean hasRight(V p) throws InvalidPositionException;

	/** Returns the left child of a node. */
	public V left(V p) throws InvalidPositionException,
			BoundaryViolationException;

	/** Removes a node. */
	public boolean remove(V n) throws InvalidPositionException;

	/** Returns the right child of a node. */
	public V right(V p) throws InvalidPositionException,
			BoundaryViolationException;

	/** Returns the sibling of a node. */
	public V sibling(V n) throws InvalidPositionException;
}
