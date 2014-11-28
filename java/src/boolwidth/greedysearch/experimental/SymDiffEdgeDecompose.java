package boolwidth.greedysearch.experimental;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.CaterpillarDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.VertexLabel;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by emh on 11/3/2014.
 */
//public class SymDiffDecompose extends StackDecompose {
//public class SymDiffDecompose extends StackDecomposeTryAllFirstVertexChoice {
//public class SymDiffDecompose extends CaterpillarToFullTryAllFirstVertexChoiceDecompose {
public class SymDiffEdgeDecompose extends CaterpillarDecompose {

    public SymDiffEdgeDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return new SplitSymDiffEdge(depth, decomposition, lefts, rights);
    }

    @Override
    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {

        ArrayList<Vertex<Integer>> ordering = new ArrayList<>();

        ArrayList<Vertex<Integer>> lowVertices = new ArrayList<>();
        vertices.sort(Comparator.comparingInt((v) -> getGraph().incidentVertices(v).size()));
        for (Vertex<Integer> v : vertices) {
            if (getGraph().incidentVertices(v).size() <= 3) {
                lowVertices.add(v);
            }
        }

        ImmutableBinaryTree minIBT = null;
        long minCB = Long.MAX_VALUE;
        for (Vertex<Integer> u  : lowVertices) {
            for (Vertex<Integer> v  : lowVertices) {
                if (u != v) {
                    ordering = new ArrayList<>();
                    ArrayList<Vertex<Integer>> path = BasicGraphAlgorithms.getShortestPath(getGraph(), new HashSet<>(vertices), u, v);
                    System.out.printf("u, v, path: %s, %s, %s\n", u, v, path);
                    Split split = createSplit(0, this, vertices);
                    for (Vertex<Integer> p : path) {
                        split = split.decomposeAdvanceFixed(p);
                        ordering.add(split.getLastMoved());
                    }
                    while (!split.done()) {
                        split = split.decomposeAdvance();
                        ordering.add(split.getLastMoved());
                    }
                    ImmutableBinaryTree ibt = getCaterpillarIBTFromOrdering(ordering);
                    if (getBooleanWidth(ibt, minCB) < minCB) {
                        minCB = getBooleanWidth(ibt, minCB);
                        minIBT = ibt;
                    }
                }
            }
        }

        //System.out.printf("ordering: %s\n", Util.labels(ordering));
        int i = 0;
        for (Vertex<Integer> v : ordering) {
            VertexLabel.setOrder(v, Integer.toString(i));
            i += 1;
        }
        return minIBT;
    }
}