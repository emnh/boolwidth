package boolwidth.greedysearch.spanning;

import graph.BasicGraphAlgorithms;
import graph.Vertex;
import interfaces.IGraph;
import sadiasrc.graph.BasicGraphAlgorithm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by emh on 11/17/2014.
 */
public class SpanningTreeAllDecompose extends SpanningTreeDecompose {

    public SpanningTreeAllDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Collection<Vertex<Integer>> getIncidentVertices(Vertex<Integer> v) {
        return BasicGraphAlgorithms.getAllVertices(getGraph());
    }
}
