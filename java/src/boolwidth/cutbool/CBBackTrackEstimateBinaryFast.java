package boolwidth.cutbool;

import com.github.krukow.clj_lang.PersistentVector;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import graph.BiGraph;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import graph.subsets.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emh on 5/10/2014.
 */

class PositionInteger {
    public int position;
    public int value;

    public PositionInteger(int position, int value) {
        this.position = position;
        this.value = value;
    }

    public String toString() {
        return String.format("%d: %d", position, value);
    }
}

public class CBBackTrackEstimateBinaryFast<V, E> {

    int rowCount;
    int colCount;
    int estimate;
    int position;
    int[] sample;
    BiGraph<V, E> bigraph;
    BiMap<Integer, Integer> rightIDMap = HashBiMap.create();

    PersistentVector<Integer> neighbourCounts = PersistentVector.create();
    ArrayList<ArrayList<PositionInteger>> neighbourSubtracts;

    PosSet<Vertex<V>> groundSet;
    ArrayList<PosSubSet<Vertex<V>>> bmat;
    //PosSubSet<Vertex<V>> binSample = new PosSubSet<Vertex<V>>(state.groundSet);
    //PosSubSet<Vertex<V>> mask = new PosSubSet<Vertex<V>>(state.groundSet);

    public CBBackTrackEstimateBinaryFast() {

    }

    public CBBackTrackEstimateBinaryFast(CBBackTrackEstimateBinaryFast<V, E> b) {
        this.rowCount = b.rowCount;
        this.colCount = b.colCount;
        this.sample = b.sample;
        this.estimate = b.estimate;
        this.position = b.position;
        this.groundSet = b.groundSet;
        this.bmat = b.bmat;
        this.neighbourCounts = b.neighbourCounts;
        this.neighbourSubtracts = b.neighbourSubtracts;
        this.bigraph = b.bigraph;
        this.rightIDMap = b.rightIDMap;
    }

    static final int QVAL = -1; // question mark (free position in sample)

    public static <V, E> int isPartialHood(CBBackTrackEstimateBinaryFast<V, E> state, int[] sample) {
        int[][] subsetRows = new int[state.rowCount][];
        int subsetRowCount = 0;

        // init binSample, TODO: maintain as part of recursion instead
        PosSubSet<Vertex<V>> binSample = new PosSubSet<Vertex<V>>(state.groundSet);
        PosSubSet<Vertex<V>> mask = new PosSubSet<Vertex<V>>(state.groundSet);
        for (int colIndex = 0; colIndex < state.colCount; colIndex++) {
            mask.set(colIndex, sample[colIndex] != QVAL);
            if (sample[colIndex] == QVAL) {
                binSample.set(colIndex, false);
            } else {
                binSample.set(colIndex, sample[colIndex] == 1);
            }
        }

        PosSubSet<Vertex<V>> total = new PosSubSet<Vertex<V>>(state.groundSet);
        for (int rowIndex = 0; rowIndex < state.bmat.size(); rowIndex++) {
            PosSubSet<Vertex<V>> cur = state.bmat.get(rowIndex);

            if (cur.intersect(mask).isSubset(binSample)) {
                total = total.union(cur);
            }
        }
        boolean subsetUnionEqualsSample = total.intersect(mask).equals(binSample.intersect(mask));
        return subsetUnionEqualsSample ? 1 : 0;
    }

    public static <V, E> String formatSample(CBBackTrackEstimateBinaryFast<V, E> state) {
        String s = "[";
        for (int i = 0; i < state.sample.length; i++) {
            if (state.sample[i] == 1) {
                s += String.format("%d, ", state.rightIDMap.inverse().get(i));
            } else {
                //s += ", ";
            }
        }
        s += "]";
        return s;
    }

    public static <V, E> long union_sample(CBBackTrackEstimateBinaryFast<V, E> state) {
        // find question marks in sample
        int[] qpos = new int[state.sample.length];
        int qcount = 0;
        long estimate = 1;
        for (int i = 0; i < state.sample.length; i++) {
            if (state.sample[i] == QVAL) {
                qpos[qcount] = i;
                qcount++;
            }
        }
        //System.out.printf("qcount: %d\n", qcount);
        if (qcount == 0) {
            System.out.printf("sample: %s, neighborCounts: %s\n", formatSample(state), state.neighbourCounts);
            estimate = 1;
        } else {
            //int newposrand = (int) (Math.random() * qcount);
            int newposition = qpos[0];
            int[] newsample0 = new int[state.sample.length];
            int[] newsample1 = new int[state.sample.length];
            //newsample0 = Arrays.copyOf(state.sample, state.sample.length);
            //newsample1 = Arrays.copyOf(state.sample, state.sample.length);
            for (int i = 0; i < state.sample.length; i++) {
                newsample0[i] = state.sample[i];
                newsample1[i] = state.sample[i];
            }
            newsample0[newposition] = 0;
            newsample1[newposition] = 1;
            //printSample(state.sample);
            //printSample(newsample0);
            //printSample(newsample1);

            CBBackTrackEstimateBinaryFast<V, E> newstate0 = new CBBackTrackEstimateBinaryFast<>(state);
            CBBackTrackEstimateBinaryFast<V, E> newstate1 = new CBBackTrackEstimateBinaryFast<>(state);

            // find left neighbors of vertex at right newposition
            // foreach left neighbor subtract 1 from each right neighbor
            PersistentVector<Integer> neighbourCounts = state.neighbourCounts;
            ArrayList<PositionInteger> subtract = state.neighbourSubtracts.get(newposition);
            for (PositionInteger pi : subtract) {
                //System.out.printf("position %d:%d\n", pi.position, pi.value);
                neighbourCounts = neighbourCounts.assocN(pi.position, neighbourCounts.get(pi.position) - pi.value);
            }
            newstate0.neighbourCounts = neighbourCounts;

            int isPartialHood0 = 1;
            int isPartialHood1 = 1;
            for (int i = 0; i < neighbourCounts.size(); i++) {
                if (newsample0[i] == 1 && neighbourCounts.get(i) < 1) {
                    isPartialHood0 = 0;
                }
            }
            // for 1, only need to check that this position is good
            if (state.neighbourCounts.get(newposition) < 1) {
                isPartialHood1 = 0;
            }

            newstate0.sample = newsample0;
            newstate1.sample = newsample1;

            //int isPartialHood0 = isPartialHood(state, newsample0);
            //int isPartialHood1 = isPartialHood(state, newsample1);
            CBBackTrackEstimateBinaryFast<V, E> newstate;
            switch(isPartialHood0 + isPartialHood1) {
                case 0:
                    estimate = 1;
                    break;
                case 1:
                    if (isPartialHood0 == 1) {
                        newstate = newstate0;
                    } else {
                        newstate = newstate1;
                    }
                    estimate = union_sample(newstate);
                    break;
                case 2:
                    /*int randval = (int) (Math.random() * 2);
                    if (randval == 0) {
                        newstate = newstate0;
                    } else {
                        newstate = newstate1;
                    }*/
                    //estimate = union_sample(newstate) * 2;
                    estimate = union_sample(newstate0) + union_sample(newstate1);
                    break;
            }
        }
        return estimate;
    }

    public static <V, E> long estimateNeighborhoods(BiGraph<V, E> g, int sampleCount) {

        CBBackTrackEstimateBinaryFast<V, E> state = new CBBackTrackEstimateBinaryFast<V, E>();
        state.rowCount = g.numLeftVertices();
        state.colCount = g.numRightVertices();
        state.sample = new int[state.colCount];
        state.groundSet = new PosSet<Vertex<V>>(g.vertices());
        state.bmat = new ArrayList<PosSubSet<Vertex<V>>>();
        for (Vertex<V> node : g.leftVertices()) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(state.groundSet, g.incidentVertices(node));
            if (neighbors.size() > 0) {
                state.bmat.add(neighbors);
            }
        }
        Arrays.fill(state.sample, QVAL);

        int idx = 0;
        for (Vertex<V> rightNode : g.rightVertices()) {
            state.rightIDMap.put(rightNode.id(), idx);
            idx += 1;
        }

        state.neighbourSubtracts = new ArrayList<>();
        for (Vertex<V> rightNode : g.rightVertices()) {
            int count = g.degree(rightNode);
            state.neighbourCounts = state.neighbourCounts.cons(count);

            // find left neighbors of vertex at right newposition
            // foreach left neighbor subtract 1 from each right neighbor
            ArrayList<PositionInteger> list = new ArrayList<>();
            state.neighbourSubtracts.add(list);
            HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();

            for (Vertex<V> leftNode : g.incidentVertices(rightNode)) {
                for (Vertex<V> rightNeighbor : g.incidentVertices(leftNode)) {
                    // inner loop is O(n^3), shouldn't we be able to unwrap this to O(n^2)?
                    int j = state.rightIDMap.get(rightNeighbor.id());
                    //state.neighbourSubtracts.get(i);
                    int value = hmap.getOrDefault(j, 0);
                    hmap.put(j, value + 1);
                }
            }
            for (Map.Entry<Integer, Integer> e : hmap.entrySet()) {
                list.add(new PositionInteger(e.getKey(), e.getValue()));
            }
            System.out.printf("%d: %s\n", hmap.get(rightNode.id()), list);
        }
        //System.out.printf("size: %d ,", state.neighbourSubtracts.size())

        long sum = 0;
        sampleCount = 1;
        for (int i = 0; i < sampleCount; i++) {
            long est = union_sample(state);
            sum += est;
        }
        long average = sum / sampleCount;
        return average;
    }
}
