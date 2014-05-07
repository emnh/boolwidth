package control;

import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.IntegerGraph;
import graph.PosSet;
import graph.Vertex;
import io.ConstructGraph;

import java.io.IOException;

import boolwidth.CutBool;

public class CutBoolRunner {

	public static void main(String[] args) throws IOException {

		testEmpty();

		// AdjacencyListGraph<String,String> graph;
		// String fileName = "graphLib/protein/1brf_graph.dimacs";
		// graph = ConstructGraph.buildGraph(fileName);
		IntegerGraph graph = ConstructGraph.randomGraph(20, 0.1);
		// IntegerGraph graph = ExactTest.createCycle(30);

		PosSet<Vertex<Integer>> lefts = new PosSet<Vertex<Integer>>();
		int i = 0;
		int lefts_bits = 0;
		int rights_bits = 0;
		// half of the nodes on each side
		for (Vertex<Integer> v : graph.vertices()) {
			if (i % 2 == 0) {
				lefts.add(v);
				lefts_bits |= 1 << i;
			} else {
				rights_bits |= 1 << i;
			}
			i++;
		}
		BiGraph<Integer, String> bg = new BiGraph<Integer, String>(lefts,
				graph);

		//		BiGraph<?, ?> bg = null;
		//		final int MAX = 25;
		//		long[] hoods = new long[MAX];
		//		for (int j = 2; j < MAX; j++) {
		//			bg = ConstructGraph.gridDiagonal(j);
		//			// System.out.println(bg);
		//			hoods[j] = CutBool.countNeighborhoods(bg);
		//			long diff = hoods[j - 1] * 2 - hoods[j];
		//			System.out.printf("%d, N=%dx%d=%d, hoods=%d, bw=%.0f\n", diff, j,
		//					j, j * j, hoods[j], Math.ceil(Math.log(hoods[j])
		//							/ Math.log(2)));
		//		}
		//		System.exit(1);

		System.out.println("vertices:" + bg.numVertices());
		System.out.println("edges:" + graph.numEdges());
		System.out.println("edges bg:" + bg.numEdges());
		System.out.println(bg);

		// FileWriter fd = new FileWriter(new File("test.dot"));
		// fd.write(bg.toGraphViz(fileName));
		// fd.close();

		long start, end;

		CutBool.countNeighborhoods(bg);
		start = System.currentTimeMillis();
		System.out.println("neighborhoods: " + CutBool.countNeighborhoods(bg));
		end = System.currentTimeMillis();
		System.out.println("time:" + (end - start));

		CutBool.countNeighborhoodsLazy(bg);
		start = System.currentTimeMillis();
		System.out.println("lazy neighborhoods: " + CutBool.countNeighborhoodsLazy(bg));
		end = System.currentTimeMillis();
		System.out.println("time:" + (end - start));

		start = System.currentTimeMillis();
		CutBool cb = new CutBool(true);
		System.out.println("neighborhoods: "
				+ cb.countNeighborhoods(graph.intAdjacencyMatrix(),
						rights_bits, lefts_bits, 0));
		System.out.println(cb.stats);
		end = System.currentTimeMillis();
		System.out.println("time:" + (end - start));
	}

	public static void testEmpty() {
		AdjacencyListGraph.D<String, String> graph = new AdjacencyListGraph.D<String, String>();
		graph.insertVertex("1");
		PosSet<Vertex<String>> lefts = new PosSet<Vertex<String>>();
		BiGraph<String, String> bg = new BiGraph<String, String>(lefts, graph);
		assert 1 == CutBool.countNeighborhoods(bg);
	}
}
