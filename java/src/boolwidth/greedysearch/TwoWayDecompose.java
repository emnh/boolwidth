package boolwidth.greedysearch;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.CaterpillarDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by emh on 11/2/2014.
 */

public class TwoWayDecompose extends CaterpillarDecompose {

    public TwoWayDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    public long getACutBool(Collection<Integer> vertexIDs) {
        return getApproximateCutBool(vertexIDs);
    }
}
