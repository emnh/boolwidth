package boolwidth.cutbool;

import graph.BiGraph;
import graph.PosSet;
import graph.Vertex;

import java.util.Arrays;

/**
 * Created by emh on 5/10/2014.
 */

class State {
    int rowCount;
    int colCount;
    int[][] mat;
    int[] sample;
    int estimate;
    int position;

    public State() {
    }

    public State(State b) {
        this.rowCount = b.rowCount;
        this.colCount = b.colCount;
        this.mat = b.mat;
        this.sample = b.sample;
        this.estimate = b.estimate;
        this.position = b.position;
    }
}

public class CBBacktrackEstimate {

    static final int QVAL = -1; // question mark (free position in sample)

    public static int isPartialHood(State state, int[] sample) {
        int[][] subsetRows = new int[state.rowCount][];
        int subsetRowCount = 0;
        for (int rowIndex = 0; rowIndex < state.rowCount; rowIndex++) {
            boolean allColsOK = true;
            for (int colIndex = 0; colIndex < state.colCount; colIndex++) {
                allColsOK = allColsOK && (sample[colIndex] == QVAL || state.mat[rowIndex][colIndex] <= sample[colIndex]);
            }
            if (allColsOK) {
                subsetRows[subsetRowCount++] = state.mat[rowIndex];
            }
        }
        int[] subsetUnion = new int[state.colCount];
        for (int colIndex = 0; colIndex < state.colCount; colIndex++) {
            subsetUnion[colIndex] = 0;
        }
        for (int rowIndex = 0; rowIndex < subsetRowCount; rowIndex++) {
            for (int colIndex = 0; colIndex < state.colCount; colIndex++) {
                subsetUnion[colIndex] = subsetUnion[colIndex] | subsetRows[rowIndex][colIndex];
            }
        }
        boolean subsetUnionEqualsSample = true;
        for (int colIndex = 0; colIndex < state.colCount; colIndex++) {
            subsetUnionEqualsSample = subsetUnionEqualsSample && (sample[colIndex] == QVAL || subsetUnion[colIndex] == sample[colIndex]);
        }
        return subsetUnionEqualsSample ? 1 : 0;
    }

    public static void printSample(int[] sample) {
        for (int i = 0; i < sample.length; i++) {
            System.out.printf("%d", sample[i]);
        }
        System.out.println("");
    }

    public static long union_sample(State state) {
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
            State newstate;
            switch(isPartialHood0 + isPartialHood1) {
                case 0:
                    estimate = 1;
                    break;
                case 1:
                    newstate = new State(state);
                    if (isPartialHood0 == 1) {
                        newstate.sample = newsample0;
                    } else {
                        newstate.sample = newsample1;
                    }
                    estimate = union_sample(newstate);
                    break;
                case 2:
                    newstate = new State(state);
                    int randval = (int) (Math.random() * 2);
                    if (randval == 0) {
                        newstate.sample = newsample0;
                    } else {
                        newstate.sample = newsample1;
                    }
                    estimate = union_sample(newstate) * 2;
                    break;
            }
        }
        return estimate;
    }

    public static <V, E> long estimateNeighborhoods(BiGraph<V, E> g, int sampleCount) {
        final PosSet<Vertex<V>> rights = new PosSet<Vertex<V>>(g.rightVertices());

        State state = new State();
        state.rowCount = g.numLeftVertices();
        state.colCount = g.numRightVertices();
        state.mat = g.getAdjacencyMatrix();
        state.sample = new int[state.colCount];
        Arrays.fill(state.sample, QVAL);

        long sum = 0;
        for (int i = 0; i < sampleCount; i++) {
            long est = union_sample(state);
            sum += est;
        }
        long average = sum / sampleCount;
        return average;
    }
}
