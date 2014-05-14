package boolwidth.heuristics.cutbool;

import graph.AdjacencyListGraph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by emh on 5/14/2014.
 */
class MISState<V, E> {
    public AdjacencyListGraph<Vertex<V>, V, E> g;
    public HashSet<Vertex<V>> R_in;
    //public HashSet<Vertex<V>> P_tried;
    public HashSet<Vertex<V>> P_any;
    public HashSet<Vertex<V>> X_out;

    public MISState(AdjacencyListGraph<Vertex<V>, V, E> g) {
        this.g = g;
        R_in = new HashSet<>();
        P_any = new HashSet<>(g.verticesCollection());
        X_out = new HashSet<>();
    }

    public MISState(MISState<V, E> b) {
        g = b.g;
        R_in = b.R_in;
        P_any = b.P_any;
        X_out = b.X_out;
    }
}

public class MISBackTrack {


    /*public String fmtVList(Collection<Vertex<V>> vertices) {
        return "";
    }*/

    public static <V, E> long unionIterate(MISState<V, E> state) {
        System.out.println("P_any: " + state.P_any.toString());
        System.out.println("X_out: " + state.X_out.toString());

        //HashSet<Vertex<V>> term = new HashSet<>(state.P_any);
        //term.removeAll(state.X_out);
        if (state.P_any.isEmpty() && state.X_out.isEmpty()) {
            return 1;
        }
        for (Vertex<V> x : state.X_out) {
            HashSet<Vertex<V>> neighbors = new HashSet<>(state.g.incidentVerticesCollection(x));
            neighbors.retainAll(state.P_any);
            if (neighbors.isEmpty()) {
                return 0;
            }
        }
        long count = 0;

        for (Vertex<V> v : new ArrayList<>(state.P_any)) {
            state.R_in.add(v);
            HashSet<Vertex<V>> neighbors = new HashSet<>(state.g.incidentVerticesCollection(v));

            MISState<V, E> newState = new MISState<>(state);
            newState.P_any = new HashSet<>(state.P_any);
            newState.X_out = new HashSet<>(state.X_out);
            newState.P_any.removeAll(neighbors);
            newState.X_out.removeAll(neighbors);
            if (newState.P_any.size() < state.P_any.size() || newState.X_out.size() < state.X_out.size()) {
                System.out.println("v: " + v);
                //System.out.println("branching on NG(v)");
                count += unionIterate(newState);
            } else {
                count += 1;
            }

            /*MISState<V, E> newState2 = new MISState<>(state);
            newState2.P_any = new HashSet<>(state.P_any);
            newState2.X_out = new HashSet<>(state.X_out);
            newState2.P_any.remove(v);
            newState2.X_out.add(v);
            count += unionIterate(newState2);
*/
            state.P_any.remove(v);
            state.X_out.remove(v);
        }

        return count;
    }

    public static <V, E> long countNeighborhoods(AdjacencyListGraph<Vertex<V>, V, E> g) {
        MISState<V, E> state = new MISState<>(g);
        return unionIterate(state);
    }
}
