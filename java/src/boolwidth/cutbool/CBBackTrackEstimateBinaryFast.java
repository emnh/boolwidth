package boolwidth.cutbool;

import com.github.krukow.clj_lang.PersistentHashSet;
import com.github.krukow.clj_lang.PersistentVector;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import graph.BiGraph;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import graph.subsets.Position;

import java.util.*;

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
    PersistentVector<Integer> sample;
    BiGraph<V, E> bigraph;
    BiMap<Integer, Integer> rightIDMap = HashBiMap.create();
    PersistentHashSet<Integer> leftInvalid = PersistentHashSet.create();
    Random rnd;

    PosSet<Vertex<V>> groundSet;
    HashMap<Vertex<V>, PosSubSet<Vertex<V>>> neighboursOfLeft;
    HashMap<Vertex<V>, PosSubSet<Vertex<V>>> neighboursOfRight;
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
        this.bigraph = b.bigraph;
        this.rightIDMap = b.rightIDMap;
        this.neighboursOfRight = b.neighboursOfRight;
        this.neighboursOfLeft = b.neighboursOfLeft;
        this.leftInvalid = b.leftInvalid;
        this.rnd = b.rnd;
    }
    static final int QVAL = -1; // question mark (free position in sample)

    public static <V, E> String formatSample(CBBackTrackEstimateBinaryFast<V, E> state) {
        String s = "[";
        for (int i = 0; i < state.sample.size(); i++) {
            if (state.sample.nth(i) == 1) {
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
        int[] qpos = new int[state.sample.size()];
        int qcount = 0;
        long estimate = 1;
        for (int i = 0; i < state.sample.size(); i++) {
            if (state.sample.nth(i) == QVAL) {
                qpos[qcount] = i;
                qcount++;
            }
        }
        //System.out.printf("qcount: %d\n", qcount);
        if (qcount == 0) {
            //System.out.printf("sample: %s, neighborCounts: %s\n", formatSample(state), state.neighbourCounts);
            estimate = 1;
        } else {
            int newposrand = state.rnd.nextInt(qcount);
            int newposition = qpos[newposrand];
            PersistentVector<Integer> newsample0 = state.sample;
            PersistentVector<Integer> newsample1 = state.sample;
            newsample0 = newsample0.assocN(newposition, 0);
            newsample1 = newsample1.assocN(newposition, 1);

            CBBackTrackEstimateBinaryFast<V, E> newstate0 = new CBBackTrackEstimateBinaryFast<>(state);
            CBBackTrackEstimateBinaryFast<V, E> newstate1 = new CBBackTrackEstimateBinaryFast<>(state);
            newstate0.sample = newsample0;
            newstate1.sample = newsample1;
            int isPartialHood0 = 1;
            int isPartialHood1 = 1;

            PosSubSet<Vertex<V>> leftInvalidSS = new PosSubSet<>(state.groundSet);
            for (Integer i : newstate1.leftInvalid) {
                leftInvalidSS.add(state.bigraph.getVertex(i));
            }

            // for 1, only need to check that this position is good
            int validCount = state.neighboursOfRight.get(state.bigraph.getVertex(state.rightIDMap.inverse().get(newposition))).subtract(leftInvalidSS).size();
            if (validCount == 0) {
                isPartialHood1 = 0;
            }

            // invalidate left neighbors of vertex at right newposition in the 0 case
            //state.bigraph.incidentVertices(state.bigraph.getVertex(state.rightIDMap.inverse().get(newposition)));
            Collection<Vertex<V>> neighbours = state.neighboursOfRight.get(state.bigraph.getVertex(state.rightIDMap.inverse().get(newposition)));

            PosSubSet<Vertex<V>> invalidNeighbours = new PosSubSet<>(state.groundSet);
            for (Vertex<V> v : neighbours) {
                newstate0.leftInvalid = newstate0.leftInvalid.cons(v.id());
                leftInvalidSS.add(v);
                invalidNeighbours = invalidNeighbours.union(state.neighboursOfLeft.get(v));
            }

            //for (int i = 0; i < newsample0.size(); i++) {
            for (Vertex<V> v : invalidNeighbours) {
                int i = state.rightIDMap.get(v.id());
                if (newsample0.nth(i) == 1) {//&& invalidNeighbours.contains(state.bigraph.getVertex(state.rightIDMap.inverse().get(i)))) {
                    validCount = state.neighboursOfRight.get(v).subtract(leftInvalidSS).size();
                    if (validCount == 0) {
                        isPartialHood0 = 0;
                        break;
                    }
                }
            }

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
                    if (state.rnd.nextBoolean()) {
                        newstate = newstate0;
                    } else {
                        newstate = newstate1;
                    }
                    estimate = union_sample(newstate) * 2;
                    //estimate = union_sample(newstate0) + union_sample(newstate1);
                    break;
            }
        }
        return estimate;
    }

    public static <V, E> long estimateNeighborhoods(BiGraph<V, E> g, int sampleCount) {

        CBBackTrackEstimateBinaryFast<V, E> state = new CBBackTrackEstimateBinaryFast<V, E>();
        state.rowCount = g.numLeftVertices();
        state.colCount = g.numRightVertices();
        state.sample = PersistentVector.create();
        state.groundSet = new PosSet<>(g.vertices());
        state.neighboursOfLeft = new HashMap<>();
        state.neighboursOfRight = new HashMap<>();
        state.bigraph = g;
        state.rnd = new java.util.Random();

        for (Vertex<V> node : g.leftVertices()) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(state.groundSet, g.incidentVertices(node));
            state.neighboursOfLeft.put(node, neighbors);
        }
        for (Vertex<V> node : g.rightVertices()) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(state.groundSet, g.incidentVertices(node));
            state.neighboursOfRight.put(node, neighbors);
        }
        for (int c = 0; c < state.colCount; c++) {
            state.sample = state.sample.cons(QVAL);
        }

        int idx = 0;
        for (Vertex<V> rightNode : g.rightVertices()) {
            state.rightIDMap.put(rightNode.id(), idx);
            idx += 1;
        }

        //System.out.printf("size: %d ,", state.neighbourSubtracts.size())

        long sum = 0;
        //sampleCount = 1;
        for (int i = 0; i < sampleCount; i++) {
            long est = union_sample(state);
            sum += est;
        }
        long average = sum / sampleCount;
        return average;
    }
}
