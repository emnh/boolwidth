package control;

import boolwidth.CutBool;
import boolwidth.heuristics.cutbool.MISBackTrack;
import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.Vertex;
import io.DiskGraph;

import java.util.ArrayList;

/**
 * Created by emh on 5/22/14.
 */
public class MISBackTrackTest {

    public static void main(String[] args) {

        //String fileName = ControlUtil.GRAPHLIB + "prob/pigs-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "protein/1sem_graph.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "other/risk.dgf";
        String fileName = ControlUtil.GRAPHLIB + "protein/1r69_graph.dimacs";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs";

        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);
        ArrayList<Vertex<Integer>> lefts = new ArrayList<Vertex<Integer>>();
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);

        long startTime = System.nanoTime();
        int bw = CutBool.countNeighborhoods(bigraph);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        System.out.printf("UNN (%dms): %d\n", duration, bw);

        startTime = System.nanoTime();
        long est = MISBackTrack.countNeighborhoods(graph);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.printf("MIS backtrack (%dms): %d", duration, est);
    }
}
