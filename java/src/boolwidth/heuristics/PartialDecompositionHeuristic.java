package boolwidth.heuristics;

public interface PartialDecompositionHeuristic<V, E> {

	/**
	 * Search for best partial decomposition
	 */
	public int runHeuristic(LSDecomposition<V, E> decomposition,
			VertexSplit<V> top, int upper_bound);

}
