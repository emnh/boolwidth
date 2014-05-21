package control;

import boolwidth.heuristics.cutbool.CBBacktrackEstimate;
import boolwidth.heuristics.cutbool.MISBackTrack;
import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.Vertex;
import interfaces.IGraph;
import io.ConstructGraph;

import java.util.ArrayList;
import java.util.Formatter;

import io.DiskGraph;
import util.Util;
import boolwidth.CutBool;
import boolwidth.heuristics.cutbool.CBFirstCollision;
import boolwidth.heuristics.cutbool.CBFirstCollisionPerm;

public class CutBoolHeuristicTest {

    public static void doComparison() {
        int N = 9;
        double probability = 0.5;

        StringBuilder csv = new StringBuilder();
        Formatter f = new Formatter(csv);
        //		f.format("Vertices,Edges,Edge probability,"
        //				+ "2^boolwidth,est min,est avg,est max\n");
        f.format("Vertices,Edges,Edge probability,"
                + "2^boolwidth,est\n");

        final int MAX_ITER = 1;
        final int sampleCount = 100;
        for (int i = 0; i < MAX_ITER; i++) {
            probability = Math.random() * 0.6 + 0.2;
            if (MAX_ITER > 100 && i % (MAX_ITER / 100) == 0) {
                System.out.println(i);
            }
            for (N = 8; N < 9; N++) {
                BiGraph<Integer, Integer> g = ConstructGraph.randomBiGraph(N,
                        N, probability);
                int bw = CutBool.countNeighborhoods(g);
                //CutBoolHeuristics.EstResult est = CutBoolHeuristics
                //.neighborhoodEstimator(g, N);
                System.out.printf("exact %d: ", bw);
                for (int k = 0; k < 1; k++) {
                    //int est = CBFirstCollisionPerm.estimateNeighborhoods(g, CutBool.BOUND_UNINITIALIZED, 100);
                    long est = CBBacktrackEstimate.estimateNeighborhoods(g, sampleCount);
                    System.out.printf("%d, ", est);
                }
                int est = CBFirstCollision.estimateNeighborhoods(g, CutBool.BOUND_UNINITIALIZED, 100);
                System.out.println();
                //System.out.printf("bw: %d, est: %d\n");

                f.format("%d,%d,%f,%d,", g.numVertices(), g.numEdges(), probability, bw);
                f.format("%d\n", est);
                //f.format("%f,%f,%f\n", est.min, est.avg, est.max);
                // System.out.printf("bw: %d, est: %f\n", bw, est);
            }
        }

        Util.stringToFile("cbcorr.csv", csv.toString());
    }


	public static void main(String[] args) {

        String fileName = ControlUtil.GRAPHLIB + "prob/pigs-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "protein/1sem_graph-pp.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "other/risk.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "protein/1r69_graph.dimacs";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs";
        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);

        int bw = CutBool.countNeighborhoods(bigraph);
        System.out.printf("exact %d\n", bw);
        long est = MISBackTrack.countNeighborhoods(bigraph);
        System.out.printf("estimate %d", est);


	}
}

// System.out.printf("ln^2(n)/p: %f\n", Math.pow(Math.log(N)
// / Math.log(2), 2)
// / probability);