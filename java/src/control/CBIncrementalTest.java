package control;

import boolwidth.CutBool;
import boolwidth.cutbool.CBIncremental;
import graph.AdjacencyListGraph;
import graph.Vertex;
import io.DiskGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.io.ControlInput;

import java.util.ArrayList;

/**
 * Created by hvide on 11/29/2015.
 */
public class CBIncrementalTest {
    public static void main(String[] args) {

        ArrayList<String> fileNames = new ArrayList<String>();

        /*
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1ail_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1aba_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/david.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "prob2/BN_93.dgf");
        */
        //fileNames.add(ControlUtil.GRAPHLIB + "prob2/BN_100.dgf");
        //fileNames.add(ControlUtil.GRAPHLIB + "protein/1aac_graph.dimacs");
        //fileNames.add(ControlUtil.GRAPHLIB + "prob2/BN_26.dgf");
        //fileNames.add(ControlUtil.GRAPHLIB + "coloring/queen7_7.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "prob/link-pp.dgf");

        for (String file : fileNames) {
            System.out.println("processing " + file + ":");
            processFile(file);
            System.out.println("");
        }
    }

    public static void processFile(String fileName) {
        ControlInput cio = new ControlInput();
        IndexGraph G = new IndexGraph();
        G =	cio.getTestGraph(fileName, G);

        long startTime = System.nanoTime();
        long cb = CBIncremental.GreedyCaterpillarWidth(G, fileName);
        long endTime = System.nanoTime();
        long elapsed = (endTime - startTime) / 1000000;
        System.out.printf("caterpillar boolean-width: (%dms): log2(%d)=%.2f\n", elapsed, cb, CutBool.getLogBW(cb));
    }
}
