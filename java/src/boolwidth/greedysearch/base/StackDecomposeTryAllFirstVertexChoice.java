package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by emh on 11/9/2014.
 */
public class StackDecomposeTryAllFirstVertexChoice extends StackDecompose {

    public StackDecomposeTryAllFirstVertexChoice(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    /*@Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new Split(depth, decomposition, rights);
    }*/

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        Split rootSplit0 = createSplit(0, this, vertices);

        long minBW = Long.MAX_VALUE;
        ImmutableBinaryTree minIBT = null;
        Multimap<Split, Split> minSplitChildren = null;

        int i = 0;
        for (Vertex<Integer> v : vertices) {
            i++;
            Split rootSplit = rootSplit0.decomposeAdvanceFixed(v);
            Stack<StackDecomposeSplitStackItem> splits = new Stack<>();
            Multimap<Split, Split> splitChildren = ArrayListMultimap.create();

            // TODO: reuse split tree for next iterations
            splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
            decomposeSplits(splits, splitChildren);

            // TODO: don't construct IBT just to getBooleanWidth
            ImmutableBinaryTree ibt = getImmutableBinaryTree(splitChildren);

            long bw = getBooleanWidth(ibt);
            System.out.printf("i: %d/%d, minBW: %.2f, BW: %.2f\n",
                    i, vertices.size(), getLogBooleanWidth(minBW), getLogBooleanWidth(bw));
            if (bw < minBW) {
                minBW = bw;
                minIBT = ibt;
                minSplitChildren = splitChildren;
            }
        }

        // local search
        long start = System.currentTimeMillis();
        Stack<StackDecomposeSplitStackItem> splits = new Stack<>();
        while (System.currentTimeMillis() - start < LOCAL_SEARCH_TIME) {
            Split rootSplit = minSplitChildren.get(null).iterator().next();
            splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
            localSearch(splits, minSplitChildren);
        }
        ImmutableBinaryTree lsIBT = getImmutableBinaryTree(minSplitChildren);

        // local search is dumb enough that it can increase boolean width, because it considers local cuts only
        if (getBooleanWidth(lsIBT) < getBooleanWidth(minIBT)) {
            minIBT = lsIBT;
        }

        return minIBT;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        this.getGraph().vertices().forEach((node) -> list.add(node));
        return decompose(list);
    }
}