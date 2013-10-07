package control;

import graph.IntegerGraph;

import io.ConstructGraph;
import io.GraphViz;

import java.io.File;

import util.Util;

public class DimacsToDot {

	public static void main(String[] args) throws Exception {

		String inFileName = args[0];
		final String output_prefix = "graphviz_output/";
		String dotfile = output_prefix
				+ inFileName.replaceFirst("[^\\.]+$", "dot");
		new File(dotfile).getParentFile().mkdirs();
		String pngfile = dotfile.replaceFirst("[^\\.]+$", "png");

		IntegerGraph graph = ConstructGraph.construct(inFileName);
		String gvz = graph.toGraphViz(inFileName);
		Util.stringToFile(dotfile, gvz);

		GraphViz.neatoRenderGraph(dotfile, pngfile);

	}

}
