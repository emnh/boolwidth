package boolwidth.heuristics;

public class LocalSearch {

}

// package boolwidth.heuristics;
//
// import interfaces.IGraph;
//
// import java.util.*;
//
// import boolwidth.CutBool;
//
// import util.Util;
//
// /**
// * This class decomposes a graph. Splitting the graph according to the method
// * used.
// */
// public class LocalSearch<V, E> {
//
// public static final int SEARCHSTEPS = 1000;
//
// /**
// * Returns a decomposition of the given graph as a binary tree. First cut
// * puts every third node to the left and the rest to the right as children
// * of the root. The nodes of each child of the root is split 1/2 + 1/2.
// * Running time: O(?)
// */
// public LSDecomposition<V, E> localSearch(IGraph.D<V, E> g) {
//
// LSDecomposition.D<V, E> decomposition = new LSDecomposition.D<V, E>(g);
// CutBoolComparator<V, E> cmp = new CutBoolComparator<V, E>(
// decomposition, new RandomHeuristic<V, E>());
//
// /*
// * class StackFrame { DNode<V> left; DNode<V> right; }
// */
//
// Stack<VertexSplit<V>> s = new Stack<VertexSplit<V>>();
//
// s.add(decomposition.root());
//
// HashMap<VertexSplit<V>, Integer> depths = new HashMap<VertexSplit<V>,
// Integer>();
// int depth;
// // set root depth = 0
// for (VertexSplit<V> n : s) {
// depths.put(n, 0);
// }
// while (!s.isEmpty()) {
// VertexSplit<V> split = s.pop();
// depth = depths.get(split);
//
// if (split.element().size() == 1)
// continue;
//
// // first split is 1/3, the rest 1/2
// int left_size_initial = (depth == 0) ? Util.divRoundUp(split
// .element().size(), 3) : split.element().size() / 2;
//
// // split randomly at first
// decomposition.splitRandom(split, left_size_initial);
//
// // local search
// // arbitrary formula. decreasing with depth
// int searchsteps = SEARCHSTEPS / (depth + 1);
// VertexSplit<V> newsplit = decomposition.findBetterCut(split, cmp,
// CutBool.BOUND_UNINITIALIZED,
// searchsteps, false);
//
// decomposition.replace(split, newsplit);
// split = newsplit;
//
// // depth from root
// depths.put(decomposition.left(split), depth + 1);
// depths.put(decomposition.right(split), depth + 1);
//
// // push onto stack
// s.add(decomposition.left(split));
// s.add(decomposition.right(split));
//
// }
// return decomposition;
// }
// }