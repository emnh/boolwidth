package boolwidth.greedysearch.spanning;

import graph.BasicGraphAlgorithms;
import graph.Vertex;
import interfaces.IGraph;

import java.util.Collection;

/**
 * Created by emh on 11/17/2014.
 */
public class SpanningTreeCostSymDiffDecompose extends SpanningTreeDecompose {

    public SpanningTreeCostSymDiffDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Collection<Vertex<Integer>> getIncidentVertices(Vertex<Integer> v) {
        return getGraph().incidentVertices(v);
    }

    @Override
    public double getCost(Vertex<Integer> a, Vertex<Integer> b) {
        double cost = hoods.get(a.id()).symmetricDifference(hoods.get(b.id())).size();
        return cost;
    }
}
