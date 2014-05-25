package boolwidth.heuristics;

public interface PartialDecompositionHeuristic<V, E> {

	/**
	 * Search for best partial decomposition
	 */
	public long runHeuristic(LSDecomposition<V, E> decomposition,
			VertexSplit<V> top, long upper_bound);

}
