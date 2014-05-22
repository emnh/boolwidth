package boolwidth.heuristics.cutbool;

import graph.AdjacencyListGraph;
import graph.SubsetGraph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by emh on 5/14/2014.
 */
class MISState<V, E> {
    public AdjacencyListGraph<Vertex<V>, V, E> graph;
    //public HashSet<Vertex<V>> R_in;
    //public HashSet<Vertex<V>> P_tried;
    public HashSet<Vertex<V>> P_any;
    public HashSet<Vertex<V>> X_out;

    public MISState(AdjacencyListGraph<Vertex<V>, V, E> graph) {
        this.graph = graph;
        //R_in = new HashSet<>();
        P_any = new HashSet<>(graph.verticesCollection());
        X_out = new HashSet<>();
    }

    public MISState(MISState<V, E> b) {
        graph = b.graph;
        //R_in = b.R_in;
        P_any = b.P_any;
        X_out = b.X_out;
    }
}

public class MISBackTrack {


    /*public String fmtVList(Collection<Vertex<V>> vertices) {
        return "";
    }*/

    public static <V, E> long unionIterate(MISState<V, E> state) {
        //System.out.println("P_any: " + state.P_any.toString());
        //System.out.println("X_out: " + state.X_out.toString());

        //HashSet<Vertex<V>> term = new HashSet<>(state.P_any);
        //term.removeAll(state.X_out);
        if (state.P_any.isEmpty() && state.X_out.isEmpty()) {
            return 1;
        }
        for (Vertex<V> x : state.X_out) {
            HashSet<Vertex<V>> neighbors = new HashSet<>(state.graph.incidentVerticesCollection(x));
            neighbors.retainAll(state.P_any);
            if (neighbors.isEmpty()) {
                return 0;
            }
        }
        long count = 0;

        ArrayList<Vertex<V>> component = new ArrayList<Vertex<V>>();
        component.addAll(state.P_any);
        component.addAll(state.X_out);
        SubsetGraph<Vertex<V>, V, E> graph = new SubsetGraph(state.graph, component);
        ArrayList<SubsetGraph<Vertex<V>, V, E>> components = graph.newGraph.connectedComponents();
        if (components.size() > 1) {
            //System.out.printf("components: %d\n", components.size());
            count = 1;
            for (SubsetGraph<Vertex<V>, V, E> subgraph : components) {
                MISState<V, E> newState = new MISState<>(state);
                newState.graph = subgraph.newGraph;
                newState.P_any = new HashSet<Vertex<V>>(); // subgraph.newGraph.verticesCollection());
                newState.X_out = new HashSet<Vertex<V>>();

                for (Vertex<V> v : state.P_any) {
                    Vertex<V> newV = subgraph.mapVertex(graph.mapVertex(v));
                    if (newV != null) newState.P_any.add(newV);
                }
                for (Vertex<V> v : state.X_out) {
                    Vertex<V> newV = subgraph.mapVertex(graph.mapVertex(v));
                    if (newV != null) newState.X_out.add(newV);
                }
                count *= unionIterate(newState);
            }
            return count;
        }

        Vertex<V> v = state.P_any.iterator().next();

        //state.R_in.add(v);
        HashSet<Vertex<V>> neighbors = new HashSet<>(state.graph.incidentVerticesCollection(v));

        MISState<V, E> newState = new MISState<>(state);
        newState.P_any = new HashSet<>(state.P_any);
        newState.X_out = new HashSet<>(state.X_out);
        newState.P_any.remove(v);
        newState.X_out.remove(v);
        newState.P_any.removeAll(neighbors);
        newState.X_out.removeAll(neighbors);
        //System.out.println("v: " + v);
        //System.out.println("branching on NG(v)");
        count += unionIterate(newState);

        MISState<V, E> newState2 = new MISState<>(state);
        newState2.P_any = new HashSet<>(state.P_any);
        newState2.X_out = new HashSet<>(state.X_out);
        newState2.P_any.remove(v);
        newState2.X_out.add(v);
        count += unionIterate(newState2);

        return count;
    }

    public static <V, E> long countNeighborhoods(AdjacencyListGraph<Vertex<V>, V, E> g) {
        MISState<V, E> state = new MISState<>(g);
        return unionIterate(state);
    }
}
