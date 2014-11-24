package control;

import io.DiskGraph;
import nl.uu.cs.treewidth.algorithm.*;
import nl.uu.cs.treewidth.input.DgfReader;
import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.input.InputException;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.timing.Stopwatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

/**
 * Created by emh on 11/18/2014.
 */
public class TreeWidthUpperBound {

    public static int getUpperBound(String fileName) {
        NGraph<GraphInput.InputData> g = null;
        GraphInput input = new DgfReader(fileName);
        try {
            g = input.get();
        } catch( InputException e ) {
            System.out.printf("no such file: %s\n", fileName);
            return 0;
        }

        Stopwatch sw = new Stopwatch();
        sw.start();
        int algoNumber = 4;

        int[] upperBounds = new int[algoNumber];
        Arrays.fill(upperBounds, Integer.MAX_VALUE);
        int upperBoundIndex = 0;
        long[] time = new long[algoNumber];

        try {
            Permutation<GraphInput.InputData> p = new LexBFS<>(); //The upperbound algorithm
            PermutationToTreeDecomposition<GraphInput.InputData> pttd = new PermutationToTreeDecomposition<GraphInput.InputData>(p);
            pttd.setInput(g);
            pttd.run();
            upperBounds[0] = pttd.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in PTTD");
        } catch (StackOverflowError e) {
            System.out.println("treewidthlib bug in PTTD");
        }
        sw.stop();
        time[0] = sw.getTime();
        System.out.printf("ub-%d (%s): %d (%d ms)\n", upperBoundIndex, fileName, upperBounds[upperBoundIndex], time[upperBoundIndex]);
        upperBoundIndex++;

        sw.start();
        try {
            GreedyFillIn<GraphInput.InputData> ubAlgo = new GreedyFillIn<>();
            ubAlgo.setInput(g);
            ubAlgo.run();
            upperBounds[1] = ubAlgo.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in GreedyFillIn");
        }
        sw.stop();
        time[1] = sw.getTime();
        System.out.printf("ub-%d (%s): %d (%d ms)\n", upperBoundIndex, fileName, upperBounds[upperBoundIndex], time[upperBoundIndex]);
        upperBoundIndex++;

        sw.start();
        try {
            GreedyDegree<GraphInput.InputData> ubAlgo = new GreedyDegree<>();
            ubAlgo.setInput(g);
            ubAlgo.run();
            upperBounds[2] = ubAlgo.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in GreedyDegree");
        }
        sw.stop();
        time[2] = sw.getTime();
        System.out.printf("ub-%d (%s): %d (%d ms)\n", upperBoundIndex, fileName, upperBounds[upperBoundIndex], time[upperBoundIndex]);
        upperBoundIndex++;

        /*sw.start();
        try {
            TreewidthDP<GraphInput.InputData> ubAlgo2 = new TreewidthDP<>(UB);
            ubAlgo2.setInput(g);
            ubAlgo2.run();
            upperBounds[3] = ubAlgo2.getTreewidth();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in GreedyDegree");
        }
        sw.stop();
        time[3] = sw.getTime();
        System.out.printf("ub-%d (%s): %d (%d ms)\n", upperBoundIndex, fileName, upperBounds[upperBoundIndex], time[upperBoundIndex]);
        upperBoundIndex++;*/

        int UB = Integer.MAX_VALUE;
        for (int i = 0; i < algoNumber; i++) {
            UB = Math.min(UB, upperBounds[i]);
        }

        System.out.printf("UB (%s): %d\n", fileName, UB);

        return UB;
    }

    public static void main(String[] args) throws IOException {
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        String fileName = DiskGraph.getMatchingGraph("**BN_19.dgf");
        //String fileName = DiskGraph.getMatchingGraph("**pigs.dgf");

        if (args.length > 0) {
            fileName = args[0];
        }

        getUpperBound(fileName);
    }
}
