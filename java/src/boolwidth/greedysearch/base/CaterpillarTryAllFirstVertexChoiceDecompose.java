package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by emh on 11/9/2014.
 */
public class CaterpillarTryAllFirstVertexChoiceDecompose extends BaseDecompose {

    public CaterpillarTryAllFirstVertexChoiceDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split rootSplit0 = createSplit(0, this, getGraph().vertices());

        long minBW = Long.MAX_VALUE;
        ImmutableBinaryTree minIBT = null;

        int i = 0;
        ArrayList<Vertex<Integer>> vertices = BasicGraphAlgorithms.BFSAll(getGraph(), getGraph().vertices().iterator().next());
        vertices = BasicGraphAlgorithms.BFSAll(getGraph(), vertices.iterator().next());
        //getGraph().vertices().forEach((v) -> vertices.add(v));
        //Collections.shuffle(vertices);

        for (Vertex<Integer> first : vertices) {
            Split split = rootSplit0.decomposeAdvanceFixed(first);
            ImmutableBinaryTree ibt = new ImmutableBinaryTree();
            ibt = ibt.addRoot();
            SimpleNode last = ibt.getRoot();
            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();
            while (!split.done()) {
                split = split.decomposeAdvance();
                ibt = ibt.addChild(last, split.getLastMoved().id());
                last = ibt.getReference();
            }
            long bw = getBooleanWidth(ibt, minBW);
            System.out.printf("i: %d/%d, minBW: %.2f, BW: %.2f\n",
                    i, getGraph().numVertices(), getLogBooleanWidth(minBW), getLogBooleanWidth(bw));
            if (bw != UPPER_BOUND_EXCEEDED && bw < minBW) {
                minBW = bw;
                minIBT = ibt;
            }
            i++;
        }
        return minIBT;
    }
}
