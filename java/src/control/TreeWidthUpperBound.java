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

    public static void processFiles() throws IOException {
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(DiskGraph.getMatchingGraph("**BN_21.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_20.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr299.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr152.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_9.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1aac_graph-pp.dimacs"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_2.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_3.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1psr_graph.dimacs"));
        fileNames.add(DiskGraph.getMatchingGraph("**rat99.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**rd100.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**lin105.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**rat99.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1psr_graph-pp.dimacs"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr107.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_14-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_10.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr124.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_3-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_4-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_2-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_7-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_102.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_103.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr76.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**eil76.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**eil76.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_1-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_12-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr76.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_8-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**macaque71.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oesoca+-hugin.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_0-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_102-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_103-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_100.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_101.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_99.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_98.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**macaque71-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**kneser8-3.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_10-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_97.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_96.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_78.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_79.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_94.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_95.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_100-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_101-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_123.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_122.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_115.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_114.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_112.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_121.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_113.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_120.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_124.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_125.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_104.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**ship-ship.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_107.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_106.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_105.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_119.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_116.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_110.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_118.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_117.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_111.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_108.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_109.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_96-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_97-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_99-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_98-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_94-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_95-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**risk.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow_solo.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**vsd-hugin.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**ship-ship-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_90-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_86-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_91-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_89-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_93-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_88-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_87-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_92-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow-trad.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_78-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_79-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**ship-ship-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow_solo-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_80-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_85-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_81-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_84-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_83-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_82-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oesoca+-hugin-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow_bas.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow_solo-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow-trad-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow-trad-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**wilson-hugin.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**risk-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**Clebsch.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oow_bas-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**munin_kgo_complete-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**oesoca+-hugin-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**petersen.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1fjl_graph-pp.dimacs-002.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1b0n-003.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1b0n-005.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1b0n-004.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1b0n-002.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1b0n-001.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**myciel2.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**1fjl-pp-001.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**a280.tsp-pp-001.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**wpp.dgf"));

        for (String file : fileNames) {
            getUpperBound(file);
        }
    }

    public static void main(String[] args) throws IOException {
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        String fileName = DiskGraph.getMatchingGraph("**BN_20.dgf");
        //String fileName = DiskGraph.getMatchingGraph("**pigs.dgf");

        if (args.length > 0) {
            fileName = args[0];
        }

        processFiles();
        //getUpperBound(fileName);
    }
}
