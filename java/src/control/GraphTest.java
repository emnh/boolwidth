package control;

import graph.AdjacencyListGraph;
import graph.IntegerGraph;
import graph.Vertex;
import interfaces.IGraph;
import io.GraphBuilder;
import io.GraphFileReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GraphTest {

	public static void main(String[] arg) throws FileNotFoundException {
		// IntegerGraph g = ConstructGraph.rndGraph(5, 8);

		String fileName = "graphLib/other/knights8_8.dgf";
		GraphFileReader r = new GraphFileReader(fileName);
		GraphBuilder<Vertex<String>, String, String> gb = new GraphBuilder<Vertex<String>, String, String>(
				r);

		IntegerGraph g = gb.constructIntGraph();
		System.out.println("the integer graph is:\n" + g + "\n");

		ArrayList<String> nElements = new ArrayList<String>(
				r.getMaxNodeNum() + 1);
		ArrayList<String> eElements = new ArrayList<String>(r.getEdgeNum());
		for (int i = 0; i < r.getMaxNodeNum() + 1; i++) {
			nElements.add("Node: " + i);
		}
		for (int j = 0; j < r.getEdgeNum(); j++) {
			eElements.add("-");
		}

		AdjacencyListGraph.D<String, String> g2 = gb.buildAdjListGraph(
				nElements, eElements);
		System.out.println("the adjacencylist graph is:\n"
				+ g2.toGraphViz(fileName) + "\n");
		System.out.println("toString:\n" + g2.toString());

		// Edge<Integer,String> e = g.edges().iterator().next();
		// System.out.println("removing the edge: "+e+"\n");
		// g.removeEdge(e);
		// System.out.println("The remaining graph is:\n"+g);
	}

	public void test(IGraph<?, ?, ?> g) {
		for (Vertex<?> v : g.vertices()) {
			System.out.println(v.id());
		}
	}
}
