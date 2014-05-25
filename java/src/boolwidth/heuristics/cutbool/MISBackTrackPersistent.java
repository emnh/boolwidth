package boolwidth.heuristics.cutbool;

import com.github.krukow.clj_ds.PersistentSet;
import com.github.krukow.clj_lang.PersistentHashSet;
import graph.AdjacencyListGraph;
import graph.SubsetGraph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by emh on 5/14/2014.
 */
class PersistentMISState<V, E> {
    public AdjacencyListGraph<Vertex<V>, V, E> graph;
    //public HashSet<Vertex<V>> R_in;
    //public HashSet<Vertex<V>> P_tried;

    public PersistentHashSet<Vertex<V>> P_any;
    public PersistentHashSet<Vertex<V>> X_out;

    public PersistentMISState(AdjacencyListGraph<Vertex<V>, V, E> graph) {
        this.graph = graph;
        //R_in = new HashSet<>();
        //P_any = PersistentHashSet.
        P_any = PersistentHashSet.create(graph.verticesCollection());
        //P_any = new PersistentHashSet<>(graph.verticesCollection());
        X_out = PersistentHashSet.create();
    }

    public PersistentMISState(PersistentMISState<V, E> b) {
        graph = b.graph;
        //R_in = b.R_in;
        P_any = b.P_any;
        X_out = b.X_out;
    }
}

public class MISBackTrackPersistent {


    /*public String fmtVList(Collection<Vertex<V>> vertices) {
        return "";
    }*/

    public static <V, E> long unionIterate(PersistentMISState<V, E> state) {
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
                PersistentMISState<V, E> newState = new PersistentMISState<>(state);
                newState.graph = subgraph.newGraph;
                newState.P_any = PersistentHashSet.EMPTY; // subgraph.newGraph.verticesCollection());
                newState.X_out = PersistentHashSet.EMPTY;

                for (Vertex<V> v : state.P_any) {
                    Vertex<V> newV = subgraph.mapVertex(graph.mapVertex(v));
                    if (newV != null) {
                        newState.P_any = newState.P_any.cons(newV);
                    }
                }
                for (Vertex<V> v : state.X_out) {
                    Vertex<V> newV = subgraph.mapVertex(graph.mapVertex(v));
                    if (newV != null) {
                        newState.X_out = newState.X_out.cons(newV);
                    }
                }
                count *= unionIterate(newState);
            }
            return count;
        }

        Vertex<V> v = null;
        int maxdegree = 0;
        for (Vertex<V> v2 : state.P_any) {
            if (state.graph.degree(v2) >= maxdegree) {
                v = v2;
                maxdegree = state.graph.degree(v2);
            }
        }
        /*
        try {
            //v = state.P_any.iterator().next();
        } catch (Exception e) {
            System.out.println("hasnext: " + state.P_any.iterator().hasNext());
            System.out.println("P_any: " + state.P_any.toString());
            //System.out.println("X_out: " + state.X_out.toString());
            throw e;
        }*/

        //System.out.printf("v: %s\n", v.toString());

        PersistentMISState<V, E> newState = new PersistentMISState<>(state);
        newState.P_any = newState.P_any.disjoin(v);
        newState.X_out = newState.X_out.disjoin(v);
        for (Vertex<V> neighbor : state.graph.incidentVerticesCollection(v)) {
            newState.P_any = newState.P_any.disjoin(neighbor);
            newState.X_out = newState.X_out.disjoin(neighbor);
        }

        //System.out.println("v: " + v);
        //System.out.println("branching on NG(v)");
        count += unionIterate(newState);

        PersistentMISState<V, E> newState2 = new PersistentMISState<>(state);
        newState2.P_any = newState2.P_any.disjoin(v);
        newState2.X_out = newState2.X_out.cons(v);
        count += unionIterate(newState2);

        return count;
    }

    public static <V, E> long countNeighborhoods(AdjacencyListGraph<Vertex<V>, V, E> g) {
        PersistentMISState<V, E> state = new PersistentMISState<>(g);
        return unionIterate(state);
    }
}
