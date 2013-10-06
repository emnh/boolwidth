package control;

import graph.AdjacencyListGraph;
import io.DiskGraph;
import graph.Vertex;
import interfaces.IGraph;

import java.io.File;

import util.Util;

/**
 * @author emh
 * 
 */

// TODO: fix portable paths
// TODO: factor out graphviz functionality to separate class

public class ControlUtil {

	public static final String OUTPUT_DIR = "output/";
	public static final String GRAPHLIB = "data/graphLib/";
	public static final String GRAPHLIB_OURS = "data/graphLib_ours/";

	public static String getOutputDir(String inputFile) {
		String outputDir = OUTPUT_DIR + inputFile + "/";
		// make parent dirs
		// new File(filenameprefix).getParentFile().mkdirs();
		new File(outputDir).mkdirs();

		return outputDir;
	}

	public static <V, E> IGraph<Vertex<V>, V, E> getTestGraph() {
		String fileName = GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs";
		// String fileName = GRAPHLIB + "protein/1brf_graph.dimacs";
		return getTestGraph(fileName);
	}

	public static <V, E> IGraph<Vertex<V>, V, E> getTestGraph(String fileName) {
		// String fileName = GRAPHLIB + "protein/1brf_graph.dimacs";
		AdjacencyListGraph<Vertex<V>, V, E> graph = new AdjacencyListGraph.D<V, E>();

		DiskGraph.readGraph(fileName, graph);
		return graph;
	}

	/**
	 * Output a file in OUTPUT_DIR that mirrors the directory structure of input
	 * file, with specified new extension.
	 * 
	 * @param inputfile
	 * @param newext
	 * @param towrite
	 * @deprecated
	 */
	@Deprecated
	public static void outputForInput(String inputfile, String newext,
			String towrite) {
		if (!newext.contains(".")) {
			newext = "." + newext;
		}
		String outputfile = OUTPUT_DIR + stripExt(inputfile) + newext;
		new File(outputfile).getParentFile().mkdirs();
		Util.stringToFile(outputfile, towrite);
	}

	public static String stripExt(String infile) {
		return infile.replaceFirst("\\.[^\\.]+$", "");
	}
}
