package control;

import graph.BiGraph;
import io.ConstructGraph;

import java.util.Formatter;

import util.Util;
import boolwidth.CutBool;
import boolwidth.heuristics.cutbool.CBFirstCollision;
import boolwidth.heuristics.cutbool.CBFirstCollisionPerm;

public class CutBoolHeuristicTest {

	public static void main(String[] args) {
		// final int N1 = 10;
		// final int N2 = 10;
		int N = 9;
		double probability = 0.5;

		// BiGraph<Integer, Integer> g = ConstructGraph
		// .matchingBiGraph(N);
		// BiGraph<Integer, Integer> g = ConstructGraph
		// .matchingBiGraphWithDiagonals(N);
		// ControlUtil.saveDotFile("test", g.toGraphViz("test"));

		// double est = CutBoolHeuristics.sumAllPerms(g);

		StringBuilder csv = new StringBuilder();
		Formatter f = new Formatter(csv);
		//		f.format("Vertices,Edges,Edge probability,"
		//				+ "2^boolwidth,est min,est avg,est max\n");
		f.format("Vertices,Edges,Edge probability,"
				+ "2^boolwidth,est\n");

		final int MAX = 101;
		for (int i = 0; i < MAX; i++) {
			probability = Math.random() * 0.6 + 0.2;
			if (MAX > 100 && i % (MAX / 100) == 0) {
				System.out.println(i);
			}
			for (N = 30; N < 31; N++) {
				BiGraph<Integer, Integer> g = ConstructGraph.randomBiGraph(N,
						N, probability);
				int bw = CutBool.countNeighborhoods(g);
				//CutBoolHeuristics.EstResult est = CutBoolHeuristics
				//.neighborhoodEstimator(g, N);
				System.out.printf("%d: ", bw);
				for (int k = 0; k < 10; k++) {
					int est = CBFirstCollisionPerm.estimateNeighborhoods(g, CutBool.BOUND_UNINITIALIZED, 100);
					System.out.printf("%d, ", est);
				}
				int est = CBFirstCollision.estimateNeighborhoods(g, CutBool.BOUND_UNINITIALIZED, 100);
				System.out.println();
				//System.out.printf("bw: %d, est: %d\n");

				f.format("%d,%d,%f,%d,", g.numVertices(), g.numEdges(),
						probability, bw);
				f.format("%d\n", est);
				//f.format("%f,%f,%f\n", est.min, est.avg, est.max);
				// System.out.printf("bw: %d, est: %f\n", bw, est);
			}
		}

		Util.stringToFile("cbcorr.csv", csv.toString());
	}
}

// System.out.printf("ln^2(n)/p: %f\n", Math.pow(Math.log(N)
// / Math.log(2), 2)
// / probability);