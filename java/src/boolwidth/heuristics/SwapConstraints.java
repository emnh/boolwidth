package boolwidth.heuristics;

import util.Util;

/**
 * Created by emh on 6/27/14.
 */
public class SwapConstraints {

    public static <V> boolean isValid(VertexSplit<V> node) {
        int minsize = minSplitSize(node);
        return node.getLeft().size() >= minsize && node.getRight().size() >= minsize;
    }

    public static <V> boolean isValidSwap(VertexSplit<V> node, int fromleft,
                                          int fromright) {
        // make sure each side is not less than 1/3
        int newleftsize = node.getLeft().size() + fromright - fromleft;
        int newrightsize = node.getRight().size() + fromleft - fromright;
        if (newleftsize < minSplitSize(node)) {
            return false;
        }
        if (newrightsize < minSplitSize(node)) {
            return false;
        }
        return fromleft != 0 || fromright != 0;
    }

    /**
     * Compute max that can be moved from left without violating size
     * constraints
     *
     * @param <V>
     * @param node
     * @return
     */
    public static <V> int maxFromLeft(VertexSplit<V> node) {
        return Math.max(node.getLeft().size() - minSplitSize(node), 0);
    }

    public static <V> int maxFromRight(VertexSplit<V> node) {
        return Math.max(node.getRight().size() - minSplitSize(node), 0);
    }

    public static <V> int minSplitSize(VertexSplit<V> node) {
        return Math.max(Util.divRoundUp(node.size(), 3), 1);
    }
}

