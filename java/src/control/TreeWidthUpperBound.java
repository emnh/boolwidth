package control;

import io.DiskGraph;
import nl.uu.cs.treewidth.algorithm.*;
import nl.uu.cs.treewidth.input.DgfReader;
import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.input.InputException;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.timing.Stopwatch;

import java.io.IOException;
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
        int upperBound = 0;
        try {
            Permutation<GraphInput.InputData> p = new LexBFS<>(); //The upperbound algorithm
            PermutationToTreeDecomposition<GraphInput.InputData> pttd = new PermutationToTreeDecomposition<GraphInput.InputData>(p);
            pttd.setInput(g);
            pttd.run();
            upperBound = pttd.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in PTTD");
        }
        sw.stop();
        long time = sw.getTime();

        sw.start();
        int upperBound2 = 0;
        try {
            GreedyFillIn<GraphInput.InputData> ubAlgo = new GreedyFillIn<>();
            ubAlgo.setInput(g);
            ubAlgo.run();
            upperBound2 = ubAlgo.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in GreedyFillIn");
        }
        sw.stop();
        long time2 = sw.getTime();

        sw.start();
        int upperBound3 = 0;
        try {
            GreedyDegree<GraphInput.InputData> ubAlgo = new GreedyDegree<>();
            ubAlgo.setInput(g);
            ubAlgo.run();
            upperBound3 = ubAlgo.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("treewidthlib bug in GreedyDegree");
        }
        sw.stop();
        long time3 = sw.getTime();

        int UB = Math.min(Math.min(upperBound, upperBound2), upperBound3);

        System.out.printf("UB (%s): %d [%d(%d ms), %d(%d ms), %d(%d ms)]\n",
                fileName, UB,
                upperBound, time,
                upperBound2, time2,
                upperBound3, time3);

        return UB;
    }

    public static void main(String[] args) throws IOException {
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        String fileName = DiskGraph.getMatchingGraph("**homer.dgf");

        if (args.length > 0) {
            fileName = args[0];
        }

        getUpperBound(fileName);
    }
}
