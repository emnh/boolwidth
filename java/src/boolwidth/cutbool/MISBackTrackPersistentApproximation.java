package boolwidth.cutbool;

import com.github.krukow.clj_lang.PersistentHashSet;
import graph.AdjacencyListGraph;
import graph.Vertex;

import java.util.HashSet;


/**
 * Created by emh on 5/14/2014.
 */
class PersistentMISStateApproximation<V, E> {
    public AdjacencyListGraph<Vertex<V>, V, E> graph;
    //public HashSet<Vertex<V>> R_in;
    //public HashSet<Vertex<V>> P_tried;

    public PersistentHashSet<Vertex<V>> P_any;
    public PersistentHashSet<Vertex<V>> X_out;
    public int depth = 1;

    public PersistentMISStateApproximation(AdjacencyListGraph<Vertex<V>, V, E> graph) {
        this.graph = graph;
        //R_in = new HashSet<>();
        //P_any = PersistentHashSet.
        P_any = PersistentHashSet.create(graph.verticesCollection());
        //P_any = new PersistentHashSet<>(graph.verticesCollection());
        X_out = PersistentHashSet.create();
    }

    public PersistentMISStateApproximation(PersistentMISStateApproximation<V, E> b) {
        graph = b.graph;
        //R_in = b.R_in;
        P_any = b.P_any;
        X_out = b.X_out;
    }
}

public class MISBackTrackPersistentApproximation {

    /*public String fmtVList(Collection<Vertex<V>> vertices) {
        return "";
    }*/

    public static <V, E> long unionIterate(PersistentMISStateApproximation<V, E> state) {
        //System.out.println("P_any: " + state.P_any.toString());
        //System.out.println("X_out: " + state.X_out.toString());

        //HashSet<Vertex<V>> term = new HashSet<>(state.P_any);
        //term.removeAll(state.X_out);
        if (state.P_any.isEmpty() && state.X_out.isEmpty()) {
            return state.depth;
        }
        for (Vertex<V> x : state.X_out) {
            HashSet<Vertex<V>> neighbors = new HashSet<>(state.graph.incidentVerticesCollection(x));
            neighbors.retainAll(state.P_any);
            if (neighbors.isEmpty()) {
                return 0;
            }
        }
        long count = 0;

        Vertex<V> v = null;
        int rndVertex = 0; // (int) (Math.random() * state.P_any.size());
        //System.out.printf("rndvertex: %d\n", rndVertex);
        int i = 0;
        for (Vertex<V> v2 : state.P_any) {
            if (i == rndVertex) {
                v = v2;
                break;
            }
            i++;
        }

        //System.out.printf("v: %s\n", v.toString());

        boolean firstBranch = Math.random() >= 0.5;
        boolean secondBranch = !firstBranch;
        boolean firstBranchDone = false;
        boolean secondBranchDone = false;

        while (count == 0 && (!firstBranchDone || !secondBranchDone)) {
            if (firstBranch && !firstBranchDone) {
                //System.out.println("first branch");
                PersistentMISStateApproximation<V, E> newState = new PersistentMISStateApproximation<>(state);
                newState.P_any = newState.P_any.disjoin(v);
                newState.X_out = newState.X_out.disjoin(v);
                for (Vertex<V> neighbor : state.graph.incidentVerticesCollection(v)) {
                    newState.P_any = newState.P_any.disjoin(neighbor);
                    newState.X_out = newState.X_out.disjoin(neighbor);
                }

                //System.out.println("v: " + v);
                //System.out.println("branching on NG(v)");
                newState.depth = state.depth + 1;
                count += unionIterate(newState);
                return count;
                //secondBranch = (count == 0);
                //firstBranchDone = true;
            }
            if (secondBranch && !secondBranchDone) {
                //System.out.println("second branch");
                PersistentMISStateApproximation<V, E> newState2 = new PersistentMISStateApproximation<>(state);
                newState2.depth = state.depth + 1;
                newState2.P_any = newState2.P_any.disjoin(v);
                newState2.X_out = newState2.X_out.cons(v);
                count += unionIterate(newState2);
                return count;
                //firstBranch = (count == 0);
                //secondBranchDone = true;
            }
        }
        if (firstBranchDone && secondBranchDone) {
            //System.out.printf("miss at depth: %d\n", state.depth);
            count--;
        }
        return count;
    }

    public static <V, E> long countNeighborhoods(AdjacencyListGraph<Vertex<V>, V, E> g, int sampleCount) {
        PersistentMISStateApproximation<V, E> state = new PersistentMISStateApproximation<>(g);
        long sum = 0;
        int successCount = 0;
        for (int i = 0; i < sampleCount; i++) {
            long count = unionIterate(state);
            if (count > 0) {
                successCount++;
                sum += count;
            }
        }
        //System.out.printf("success count: %d\n", successCount);
        return (long) Math.pow(2, (double) sum / successCount);
        //return sum / successCount;
    }
}
