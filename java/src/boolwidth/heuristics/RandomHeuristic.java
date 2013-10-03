package boolwidth.heuristics;

public class RandomHeuristic<V, E> implements
		PartialDecompositionHeuristic<V, E> {

	/**
	 * splits one level randomly
	 */
	@Override
	public int runHeuristic(LSDecomposition<V, E> decomposition,
			VertexSplit<V> node, int upperBound) {
		int retval = 0;

		VertexSplit<V> newnode = node.clone();

		if (newnode.size() > 1) {
			decomposition.splitRandom(newnode, newnode.size() / 2);
		}
		retval = decomposition.boolWidth(newnode);

		return retval;
	}

}