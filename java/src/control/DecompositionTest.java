package control;

import graph.IntegerGraph;

import io.ConstructGraph;

import java.io.File;
import java.util.Scanner;

import boolwidth.CutBool;
import boolwidth.Decomposition;
import boolwidth.Splitter;

public class DecompositionTest {
	public static void main(String[] args) throws Exception {
		// creating a graph
		// String inFileName = "graphLib/protein/1brf_graph.dimacs";
		String inFileName = "graphLib/protein/1a62_graph.dimacs";
		// String inFileName = "graphLib_ours/hsugrid/hsu-6x5.dimacs";
		// String inFileName = "testGraph";
		IntegerGraph graph = ConstructGraph.construct(inFileName);
		System.out.println("Graph: " + graph);

		// reading best decomposition found so far
		String outFileName = inFileName.replaceFirst("graphLib",
				"decompositions");
		outFileName = outFileName.replaceFirst(".dimacs", ".decomp");
		File outFile = new File(outFileName);
		String path = outFileName.substring(0, outFileName.lastIndexOf('/'));
		new File(path).mkdirs();
		outFile.createNewFile();

		int minbw = CutBool.bestGeneralUpperBound(graph.numVertices());
		boolean hasDecomp = false;
		int readbw = 0;
		Scanner ofsc;
		try {
			ofsc = new Scanner(outFile);
			if (ofsc.hasNextInt()) {
				hasDecomp = true;
				readbw = minbw = ofsc.nextInt();
			}
		} catch (Exception e) {
		}

		// starting timer
		long start = System.currentTimeMillis();
		long searchTime = 0;
		// building the decomposition

		// TODO: check if the graph is connected
		// TODO: check if the graph has cut vertices

		Decomposition.D<Integer, String> mindecomp = null;
		Decomposition.D<Integer, String> decomp;
		int iter = 10;
		for (int i = 0; i < iter; i++) {
			if (iter >= 100 && i % (iter / 100) == 0) {
				System.out.println(i * 100 / iter + "%");
			}
			long temp1 = System.currentTimeMillis();
			decomp = Splitter.searchSplit(graph, minbw);
			// decomp = RandomSplitter.randomSplit(graph,minbw);
			searchTime += System.currentTimeMillis() - temp1;
			System.out.println("split done");
			if (decomp.numVertices() < 8) {
				continue;
			}
			int bw = CutBool.booleanWidth(decomp, minbw);
			// System.out.println("count: "+bw);
			if (bw != CutBool.BOUND_EXCEEDED && bw < minbw) {
				System.out.printf("i: %d, bw: %d\n", i, bw);
				minbw = bw;
				mindecomp = decomp;
			}
		}
		decomp = mindecomp;
		if (!hasDecomp || minbw < readbw) {
			decomp.toFile(outFile, minbw);
		}

		// Decomposition<DNode.D<Integer>, Integer, String> test =
		// ObjectCloner.deepCopy(decomp);
		// System.out.println(test);
		// System.out.println();

		if (decomp != null) {
			System.out.println("Decomposition: " + decomp.toString());

		}
		System.out.println("The boolean-width of this decomposition is: "
				+ Math.log(minbw) / Math.log(2));
		long end = System.currentTimeMillis();
		System.out.println("time: " + (end - start));
		System.out.println("SearchTime: " + searchTime);
	}
}
