package boolwidth.greedysearch;

import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by emh on 11/2/2014.
 */
public class RandomDecomposition extends BaseDecomposition {

    public RandomDecomposition(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public long getCutValueForTrickle(ImmutableBinaryTree ibt, HashSet<Integer> lefts) {
        return lefts.size();
        //return getFunkyCutBool(ibt, lefts);
        //return getCutBool(lefts);
    }

    @Override
    protected ImmutableBinaryTree trickleReRoot(ImmutableBinaryTree ibt, SimpleNode parent) {
        return ibt;
    }

    @Override
    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        for (Vertex<Integer> v : vertices) {
            ibt = trickle(ibt, null, ibt.getRoot(), v);
        }
        return ibt;
    }



}
