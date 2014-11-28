package boolwidth.greedysearch.experimental;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.growNeighbourHood.SplitGrowNeighbourhood;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;

/**
 * Created by emh on 11/2/2014.
 */

public class ExperimentalDecompose extends BaseDecompose {

    public final int SEARCH_TIME = 10000;

    public ExperimentalDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return new SplitGrowNeighbourhood(depth, decomposition, lefts, rights);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        final long target = 512;

        ArrayList<Split> splits = new ArrayList<>();
        Split topSplit = createSplit(0, this, getGraph().vertices());
        Split split = topSplit;
        int blockCount = 10;
        int block = 0;
        ArrayList<Vertex<Integer>> previous = new ArrayList<>();
        while (split.getRights().size() > 0) {
            long cb = 0;
            while (cb < target && split.getRights().size() > 0) {
                topSplit = topSplit.decomposeAdvance();
                split = split.decomposeAdvanceFixed(topSplit.getLastMoved());
                cb = getCutBool(split.getLefts(), true);
                previous.add(split.getLastMoved());
                long fullCB = getCutBool(previous, true);
                System.out.printf("cb: %.2f, full: %.2f, left/right: %d/%d, last moved: %s\n",
                        getLogBooleanWidth(cb), getLogBooleanWidth(fullCB), split.getLefts().size(), split.getRights().size(), split.getLastMoved());
            }
            block++;
            System.out.printf("block: %d\n", block);
            splits.add(split);
            split = createSplit(0, this, split.getRights());
            System.out.println("");
        }

        return null;
    }
}
