package interfaces;

import exceptions.BoundaryViolationException;
import exceptions.InvalidPositionException;
import exceptions.NonEmptyTreeException;
import graph.BinNode;

/**
 * An interface for a binary tree, where each node can have zero, one, or two
 * children.
 * 
 * @author Roberto Tamassia, Michael Goodrich
 */
public interface IBinaryTree<TVertex extends BinNode<TVertex, N>, N, E> extends
		ITree<TVertex, N, E> {

	/** Adds a left child storing element e to node p. Returns the child node. */
	public TVertex addLeft(TVertex p, N e) throws InvalidPositionException;

	/** Adds a right child storing element e to node p. Returns the child node. */
	public TVertex addRight(TVertex p, N e) throws InvalidPositionException;

	/** Adds a root node to an empty tree. */
	public TVertex addRoot(N e) throws NonEmptyTreeException;

	/** Attaches two trees to be subtrees of an external node. */
	public void attach(TVertex root, IBinaryTree<TVertex, N, E> t1,
			IBinaryTree<TVertex, N, E> t2) throws InvalidPositionException;

	/** Returns whether a node has a left child. */
	public boolean hasLeft(TVertex p) throws InvalidPositionException;

	/** Returns whether a node has a right child. */
	public boolean hasRight(TVertex p) throws InvalidPositionException;

	/** Returns the left child of a node. */
	public TVertex left(TVertex p) throws InvalidPositionException,
			BoundaryViolationException;

	/** Removes a node. */
	public boolean remove(TVertex n) throws InvalidPositionException;

	/** Returns the right child of a node. */
	public TVertex right(TVertex p) throws InvalidPositionException,
			BoundaryViolationException;

	public TVertex root();

	/** Returns the sibling of a node. */
	public TVertex sibling(TVertex n) throws InvalidPositionException;

	/** Returns number of nodes in the tree */
	public int size();
}
