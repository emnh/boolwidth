package control;

import boolwidth.CutBool;
import boolwidth.heuristics.cutbool.*;
import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.Edge;
import graph.Vertex;
import io.DiskGraph;
import sadiasrc.decomposition.CCMIS;
import sadiasrc.graph.IndexGraph;

import java.util.ArrayList;

/**
 * Created by emh on 5/22/14.
 */
public class MISBackTrackTest {

    public static void main(String[] args) {

        ArrayList<String> fileNames  = new ArrayList<String>();

        fileNames.add(ControlUtil.GRAPHLIB + "other/risk.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "prob/pigs-pp.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1sem_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "prob2/BN_100.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1r69_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1ail_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "other/macaque71.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/jean.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1aba_graph.dimacs");
        //fileNames.clear();
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/david.dgf");
        fileNames.add(ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs");

        //fileNames.clear();
        //fileNames.add(ControlUtil.GRAPHLIB + "protein/1sem_graph.dimacs");

        for (String file : fileNames) {
            System.out.println(file);
            AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
            DiskGraph.readGraph(file, graph);
            processFile(file);
            System.out.println("");
        }
    }

    public static sadiasrc.graph.BiGraph convertSadiaBiGraph(BiGraph<Integer, String> bigraph) {

        sadiasrc.graph.BiGraph sadiaBiGraph = new sadiasrc.graph.BiGraph(bigraph.numLeftVertices(), bigraph.numRightVertices());

        for (Edge<Vertex<Integer>, Integer, String> e : bigraph.edges()) {
            int id1 = e.endVertices().get(0).id();
            int id2 =  e.endVertices().get(1).id();
            sadiaBiGraph.insertEdge(id1, id2);
        }

        return sadiaBiGraph;
    }

    public static void processFile(String fileName) {
        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);
        ArrayList<Vertex<Integer>> lefts = new ArrayList<Vertex<Integer>>();
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);

        BiGraph<Integer, String> bigraphdup = new BiGraph<>(graph);

        final int sampleCount = 100;
        long startTime = System.nanoTime();
        int bw = CutBool.countNeighborhoods(bigraph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        long est;

        System.out.printf("UNN (bigraph) (%dms): %d\n", duration, bw);

        /*
        startTime = System.nanoTime();
        est = MISBackTrack.countNeighborhoods(bigraph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("MIS backtrack (%dms): %d\n", duration, est);
        */

        startTime = System.nanoTime();
        //IndexGraph g = new IndexGraph(bigraph);

        est = CCMIS.BoolDimBranch(convertSadiaBiGraph(bigraph));
        //est = MISBackTrackPersistent.countNeighborhoods(g);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("Sadia MIS backtrack (%dms): %d\n", duration, est);

        /*
        startTime = System.nanoTime();
        est = MISBackTrackPersistent.countNeighborhoods(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("MIS backtrack persistent (%dms): %d\n", duration, est);
        */

        startTime = System.nanoTime();
        est = MISBackTrackPersistentApproximation.countNeighborhoods(bigraph, sampleCount);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("MIS approximation (%dms): %d\n", duration, est);

        startTime = System.nanoTime();
        est = CBBacktrackEstimate.estimateNeighborhoods(bigraph, sampleCount);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("CB bactrack approximation (%dms): %d\n", duration, est);

        startTime = System.nanoTime();
        est = CBBacktrackEstimateBinary.estimateNeighborhoods(bigraph, sampleCount);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("CB bactrack bin approximation (%dms): %d\n", duration, est);
    }
}
