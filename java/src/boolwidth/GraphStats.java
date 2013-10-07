package boolwidth;

import graph.IntegerGraph;
import graph.Vertex;

import io.ConstructGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GraphStats {
	public static void main(String[] args) throws Exception {
		// creating a graph
		String inFileName = "graphLib/protein/1brf_graph.dimacs";
		IntegerGraph graph = ConstructGraph.construct(inFileName);
		// starting timer
		long start = System.currentTimeMillis();
		String outFileName = inFileName.replaceFirst("graphLib",
				"decompositions");
		outFileName = outFileName.replaceFirst(".dimacs", ".stat");
		File outFile = new File(outFileName);
		String path = outFileName.substring(0, outFileName.lastIndexOf('/'));
		new File(path).mkdirs();
		outFile.createNewFile();
		String[] ss = new String[11];
		for (Vertex<Integer> u : graph.vertices()) {
			for (Vertex<Integer> v : graph.vertices()) {
				if (u.id() <= v.id()) {
					continue;
				}

				ArrayList<Vertex<Integer>> ul = new ArrayList<Vertex<Integer>>();
				ArrayList<Vertex<Integer>> vl = new ArrayList<Vertex<Integer>>();
				ArrayList<Vertex<Integer>> uvl = new ArrayList<Vertex<Integer>>();
				for (Vertex<Integer> w : graph.vertices()) {
					if (u == w || v == w) {
						continue;
					}
					if (graph.areAdjacent(u, w)) {
						if (graph.areAdjacent(v, w)) {
							uvl.add(w);
						} else {
							ul.add(w);
						}
					} else {
						if (graph.areAdjacent(v, w)) {
							vl.add(w);
						}
					}
				}

				int nonCom = ul.size() + vl.size();
				if (u.id() == 41 && v.id() == 38) {
					String s = "";
					s += "" + u + "," + v + ": " + nonCom + "\n";
					s += "in  u:";
					for (Object w : ul) {
						s += " " + w;
					}
					s += "\nin uv:";
					for (Object w : uvl) {
						s += " " + w;
					}
					s += "\nin  v:";
					for (Object w : vl) {
						s += " " + w;
					}
					s += "\n";
					System.out.println(s);
				}
				if (nonCom <= 10) {
					String s = ss[nonCom];
					if (s == null) {
						s = "";
					}
					s += "" + u + "," + v + ": " + nonCom + "\n";
					s += "in  u:";
					for (Object w : ul) {
						s += " " + w;
					}
					s += "\nin uv:";
					for (Object w : uvl) {
						s += " " + w;
					}
					s += "\nin  v:";
					for (Object w : vl) {
						s += " " + w;
					}
					s += "\n";
					ss[nonCom] = s;
				}
			}
		}
		try {
			FileWriter fw = new FileWriter(outFile);
			for (int a = 0; a < ss.length; a++) {
				if (ss[a] != null) {
					fw.write(ss[a]);
				}
				fw.write("\n----------------------------------------------\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.err.println("Output file not found");
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		System.out.println("time: " + (end - start));
	}
}
