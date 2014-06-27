package sadiasrc.decomposition;

import sadiasrc.exceptions.InvalidPositionException;
import sadiasrc.graph.IEdge;
import sadiasrc.graph.IGraph;
import sadiasrc.graph.IGraphSubSet;
import sadiasrc.graph.ITree;
import sadiasrc.graph.IVertex;

public interface IDecomposition<DV extends IVertex, DE extends IEdge<DV>, V extends IVertex, E extends IEdge<V>, G extends IGraph<V, E>>
		extends ITree<DV, DE> {

	/**
	 * @param root
	 * @return The subgraph associated with the subtree rooted at root.
	 * @throws InvalidPositionException
	 *             if root is not a node of this decomposition-tree
	 */
	public IGraphSubSet<V, E> getSubSet(DV root)
			throws InvalidPositionException;

	/**
	 * @return the graph that this decomposition id decomposing
	 */
	public G graph();

	/**
	 * @return true if this decomposition satisfy its given invariant, false
	 *         otherwise
	 */
	public boolean isComplete();
}
