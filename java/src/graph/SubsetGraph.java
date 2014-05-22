package graph;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by emh on 5/22/14.
 */
public class SubsetGraph<TVertex extends Vertex<V>, V, E> {
    public AdjacencyListGraph<TVertex, V, E> oldGraph;
    public AdjacencyListGraph<TVertex, V, E> newGraph;
    private int[] oldIdToNewId;

    public SubsetGraph(AdjacencyListGraph<TVertex, V, E> oldgraph, ArrayList<TVertex> component) {
        this.oldGraph = oldgraph;
        subsetGraph(component);
    }

    /**
     * Return null if doesn't exist in subgraph.
     * @param old
     * @return
     */
    public TVertex mapVertex(TVertex old) {
        int newId = oldIdToNewId[oldGraph.getId(old)];
        if (newId == -1) return null;
        return newGraph.getVertex(newId);
    }

    public AdjacencyListGraph<TVertex, V, E> subsetGraph(ArrayList<TVertex> component) {
        oldIdToNewId = new int[oldGraph.numVertices()];
        Arrays.fill(oldIdToNewId, -1);
        newGraph = new AdjacencyListGraph<TVertex, V, E>(oldGraph.vertexFactory);
        for (TVertex v : component) {
            int nextId = newGraph.getNextID();
            TVertex v2 = newGraph.createVertex(v.element(), nextId);
            newGraph.insertVertex(v2);
            oldIdToNewId[oldGraph.getId(v)] = nextId;
        }
        for (TVertex v1 : component) {
            for (TVertex v2 : oldGraph.incidentVertices(v1)) {
                // TODO: insert old edge element instead of null
                TVertex newV1 = mapVertex(v1);
                TVertex newV2 = mapVertex(v2);
                if (newV2 != null) {
                    newGraph.insertEdge(newV1, newV2, null);
                }
            }
        }
        return newGraph;
    }

}
