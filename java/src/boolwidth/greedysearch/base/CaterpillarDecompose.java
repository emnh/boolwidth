package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.symdiff.SplitSymDiff;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;

/**
 * Created by emh on 11/9/2014.
 */
public class CaterpillarDecompose extends BaseDecompose {

    public CaterpillarDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    public static ImmutableBinaryTree getCaterpillarIBTFromOrdering(ArrayList<Vertex<Integer>> vertexOrdering) {
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        SimpleNode last = ibt.getRoot();
        for (Vertex<Integer> v : vertexOrdering) {
            ibt = ibt.addChild(last, v.id());
            last = ibt.getReference();
        }
        return ibt;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split split = createSplit(0, this, getGraph().vertices());
        ArrayList<Vertex<Integer>> ordering = new ArrayList<>();

        while (!split.done()) {
            split = split.decomposeAdvance();
            ordering.add(split.getLastMoved());
        }
        return getCaterpillarIBTFromOrdering(ordering);
    }
}
