package io;

import interfaces.IGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.Util;
import boolwidth.Decomposition;
import control.ControlUtil;

public class GraphViz {

	public static final boolean RENDER_GRAPHS = false;
	public static final String OUTPUT_FORMAT = "png";
	public static final String GRAPHVIZ_BIN_PATH = "/usr/bin/";
	public static final String GRAPHVIZ_OUTPUT_DIR = ControlUtil.OUTPUT_DIR;

	public static void dotRenderGraph(String dotfile, String imgfile) {
		GraphViz.renderGraph("/usr/bin/dot", dotfile, imgfile);
	}

	public static void neatoRenderGraph(String dotfile, String imgfile) {
		GraphViz.renderGraph("/usr/bin/neato", dotfile, imgfile);
	}

	public static void renderGraph(String renderbin, String dotfile,
			String imgfile) {

		String[] cmdarray = { renderbin, "-T", OUTPUT_FORMAT, "-o", imgfile,
				dotfile };

		Process p = null;

		try {
			p = Runtime.getRuntime().exec(cmdarray);

			BufferedReader stderr = new BufferedReader(new InputStreamReader(p
					.getErrorStream()));
			String line = "";
			while (line != null) {
				line = stderr.readLine();
				if (line != null) {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			System.out.println("interrupted");
		}
	}

	public static void saveDotFile(String filenameprefix, String towrite) {
		GraphViz.saveDotFile(filenameprefix, towrite, "neato");
	}

	public static void saveDotFile(String filenameprefix, String towrite,
			String rendertool) {
		String dotfile = filenameprefix + ".dot";
		String imgfile = filenameprefix + "." + OUTPUT_FORMAT;
		Util.stringToFile(dotfile, towrite);
		if (RENDER_GRAPHS) {
			System.out.printf("rendering \"%s\" with graphviz..\n", imgfile);
			renderGraph(GRAPHVIZ_BIN_PATH + rendertool, dotfile, imgfile);
		}
	}

	public static void saveGraphDecomposition(String fileNamePrefix,
			IGraph<?, ?, ?> graph, long bw, Decomposition<?, ?, ?> dc, long time) {

		fileNamePrefix = ControlUtil.getOutputDir(fileNamePrefix);

		String label = String.format("time: %d ms", time);

		Util.stringToFile(fileNamePrefix + "graph.dimacs", graph.toDimacs());

		saveDotFile(fileNamePrefix + "graph", graph.toGraphViz("bw: " + bw));
		saveDotFile(fileNamePrefix + "dc", dc.toGraphViz(label));
		saveDotFile(fileNamePrefix + "dc-cluster", dc.toGraphVizCluster(label),
		"dot");
	}
}
