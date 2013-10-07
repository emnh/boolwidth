package control;

import graph.IntegerGraph;
import graph.Vertex;

import io.GraphBuilder;
import io.GraphFileReader;

import java.io.BufferedWriter;
import java.io.FileWriter;

import boolwidth.CutBool;
import boolwidth.Decomposition;
import boolwidth.Splitter;

/**
 * Testclass that take files containing graphs as program arguments, and writes
 * the decomposition of each graph and its boolean-width to file "out.txt".
 */
public class GraphLibTest {

	public static void main(String[] args) {

		try {
			System.out.println("START");

			FileWriter fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);

			for (String file : args) {

				GraphFileReader r = new GraphFileReader(file);

				if (!r.emptyGraph) {
					// doesn't matter as the generics aren't used
					GraphBuilder<?, ?, ?> gb = new GraphBuilder<Vertex<Integer>, Integer, String>(
							r);
					IntegerGraph graph = gb.constructIntGraph();
					Decomposition.D<Integer, String> decomp = Splitter
							.evenSplit(graph);

					out.write("File: " + file + "\n");
					out.write("		numNodes: " + r.getNodesNum() + "		numEdges: "
							+ r.getEdgeNum() + "\n		single nodes: "
							+ (graph.numVertices() - r.getNodesNum())
							+ "		parallel edges: "
							+ (r.getEdgeNum() - graph.numEdges()) + "\n\n");

					if (r.getEdgeNum() > 900) {
						out.write("Graph too big. OutOfMemoryError."
								+ "\n\n***************************\n\n\n");

					} else {
						out.write(graph.toString() + "\n\n");
						out.write(decomp
								.toGraphViz("Decomposition - EvenSplit"));
						out.flush();
						try {
							long start = System.currentTimeMillis();
							out
									.write("\nThe boolean-width of this decomposition is : "
											+ CutBool.booleanWidth(decomp)
											+ "\n");
							long end = System.currentTimeMillis();

							out.write("time: " + (end - start)
									+ "\n\n***************************\n\n\n");
						} catch (OutOfMemoryError e) {
							System.out.println(file + " OutOfMemoryError");
							out.write("\nBoolean-width: Out Of Memory Error"
									+ "\n\n***************************\n\n\n");
							continue;
						}
					}
				} else {
					System.out.println(file + " is an empty graph.");
					continue;
				}

				System.out.println("Graph " + file + " written to file.");
			}
			out.flush();
			out.close();
			fstream.close();
			System.out.println("END");

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		} finally {

		}

	}
}
