package boolwidth.cutbool.ccmis_trial;

import graph.AdjacencyListGraph;
import graph.Edge;
import graph.PosSubSet;
import graph.Vertex;

import java.util.Collection;

/**
 * Created by emh on 6/12/14.
 */

public class IndexGraph extends AdjacencyListGraph<IndexVertex, Integer, String> {

    public IndexGraph(AdjacencyListGraph<? extends Vertex<Integer>, Integer, String> graph) {
        //IVertexFactory<IndexVertex, Integer> factory = ;
        super(new IndexVertex.Factory());

        //System.out.println("creating new indexgraph");

        // add left and right vertices
        for (Vertex<Integer> v : graph.vertices()) {
            int newID = getNextID();
            if (v.id() != newID) {
                throw new UnsupportedOperationException("expected IDs to be same");
            };
            IndexVertex newVertexLeft = createVertex(v.element(), newID);
            IndexVertex iv = insertVertex(newVertexLeft);
            //System.out.printf("inserting %s\n", iv);

        }

        // add edges going between left and right
        for (Edge<? extends Vertex<Integer>, Integer, String> e : graph.edges()) {
            int a = e.endVertices().get(0).id();
            int b = e.endVertices().get(1).id();
            IndexVertex va = this.vList.get(a);
            IndexVertex vb = this.vList.get(b);
            Edge<IndexVertex, Integer, String> ie = insertEdge(va, vb, e.element());
            //System.out.printf("inserting %d-%d, %d-%d: %s\n", a, b, va.id(), vb.id(), ie);
        }
    }

}