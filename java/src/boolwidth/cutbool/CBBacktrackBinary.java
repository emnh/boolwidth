package boolwidth.cutbool;

import graph.*;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by emh on 5/10/2014.
 */

public class CBBacktrackBinary<V> {

    int rowCount;
    int colCount;
    int estimate;
    int position;
    int[] sample;

    PosSet<Vertex<V>> groundSet;
    ArrayList<PosSubSet<Vertex<V>>> bmat;
    //PosSubSet<Vertex<V>> binSample = new PosSubSet<Vertex<V>>(state.groundSet);
    //PosSubSet<Vertex<V>> mask = new PosSubSet<Vertex<V>>(state.groundSet);

    public CBBacktrackBinary() {

    }

    public CBBacktrackBinary(CBBacktrackBinary<V> b) {
        this.rowCount = b.rowCount;
        this.colCount = b.colCount;
        this.sample = b.sample;
        this.estimate = b.estimate;
        this.position = b.position;
        this.groundSet = b.groundSet;
        this.bmat = b.bmat;
    }

    static final int QVAL = -1; // question mark (free position in sample)

    public static <V> int isPartialHood(CBBacktrackBinary<V> state, int[] sample) {
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

    public static void printSample(int[] sample) {
        for (int i = 0; i < sample.length; i++) {
            System.out.printf("%d", sample[i]);
        }
        System.out.println("");
    }

    public static <V> long union_sample(CBBacktrackBinary<V> state) {
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
            estimate = 1;
        } else {
            int newposrand = (int) (Math.random() * qcount);
            int newposition = qpos[newposrand];
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
            int isPartialHood0 = isPartialHood(state, newsample0);
            int isPartialHood1 = isPartialHood(state, newsample1);
            CBBacktrackBinary<V> newstate;
            switch(isPartialHood0 + isPartialHood1) {
                case 0:
                    estimate = 1;
                    break;
                case 1:
                    newstate = new CBBacktrackBinary<V>(state);
                    if (isPartialHood0 == 1) {
                        newstate.sample = newsample0;
                    } else {
                        newstate.sample = newsample1;
                    }
                    estimate = union_sample(newstate);
                    break;
                case 2:
                    newstate = new CBBacktrackBinary<V>(state);
                    newstate.sample = newsample0;
                    estimate = union_sample(newstate);
                    newstate.sample = newsample1;
                    estimate += union_sample(newstate);
                    break;
            }
        }
        return estimate;
    }

    // essential operations:
    // remove left node: remove all left nodes intersecting its neighborhood, that is, neighbors of neighbors
    // include left node: remove just neighborhood
    // is connected: check if bigraph is connected and split into two bigraphs otherwise

    public static <V, E> long countNeighborhoods(BiGraph<V, E> g) {

        CBBacktrackBinary<V> state = new CBBacktrackBinary<V>();
        state.rowCount = g.numLeftVertices();
        state.colCount = g.numRightVertices();
        state.sample = new int[state.colCount];
        state.groundSet = new PosSet<Vertex<V>>(g.rightVertices());
        state.bmat = new ArrayList<PosSubSet<Vertex<V>>>();
        for (Vertex<V> node : g.leftVertices()) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(state.groundSet, g.incidentVertices(node));
            if (neighbors.size() > 0) {
                state.bmat.add(neighbors);
            }
        }
        Arrays.fill(state.sample, QVAL);
        return union_sample(state);
    }
}
