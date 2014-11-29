package boolwidth.greedysearch.experimental;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.CaterpillarDecompose;
import boolwidth.greedysearch.base.Util;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.VertexLabel;
import interfaces.IGraph;

import java.util.ArrayList;

/**
 * Created by emh on 11/26/2014.
 */
public class SimpleBFSDecompose extends BaseDecompose {

    public SimpleBFSDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        ArrayList<Vertex<Integer>> ordering;
        long mincb = Long.MAX_VALUE;
        ImmutableBinaryTree minIBT = null;
        vertices = BasicGraphAlgorithms.BFSAll(getGraph(), vertices.iterator().next());
        for (Vertex<Integer> start : vertices) {
            ordering = BasicGraphAlgorithms.BFSGrid(getGraph(), start);
            int i = 0;
            for (Vertex<Integer> v : ordering) {
                VertexLabel.setOrder(v, Integer.toString(i));
                i += 1;
            }
            final ImmutableBinaryTree ibt = CaterpillarDecompose.getCaterpillarIBTFromOrdering(ordering);
            final long mincb2 = mincb;
            Long cb = Util.timedExecution(() -> getBooleanWidth(ibt, mincb2), 15000);
            if (cb != null) {
                System.out.printf("cb: %.2f\n", getLogBooleanWidth(cb));
                if (cb != UPPER_BOUND_EXCEEDED && cb < mincb) {
                    mincb = cb;
                    minIBT = ibt;
                }
            }
        }

        return minIBT;
    }
}
