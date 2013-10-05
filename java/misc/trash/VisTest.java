package control;

import graph.ConstructGraph;
import graph.GraphIterator;
import graph.IntegerGraph;
import graph.Vertex;
import interfaces.IGraph;

import java.io.FileNotFoundException;

import javax.swing.JFrame;

import visualization.GraphBrowser;
import visualization.ShowGraph;
import boolwidth.ExactBoolWidth;

public class VisTest {

	public static <V, E> void decompositionsTest() throws FileNotFoundException {
		String inFileName = "graphLib_ours/hsugrid/hsu-4x4.dimacs";
		IGraph<Vertex<V>, V, E> graph = ControlUtil.getTestGraph(inFileName);
		// IntegerGraph graph = ConstructGraph.construct(inFileName);

		int upper_bound = 6;
		ExactBoolWidth<Vertex<V>, V, E> ebw = new ExactBoolWidth<Vertex<V>, V, E>(
				true);
		ExactBoolWidth<Vertex<V>, V, E>.Computer comp = ebw.new Computer(graph,
				upper_bound);
		long bw = comp.result;
		// ArrayList<Decomposition.D<V, E>> decomps = comp
		// .getDecompositionsOfWidth((int) bw);
		//
		// JFrame frame = new GraphBrowserS<DNode.D<V>, V, E>(
		// decomps);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setVisible(true);
	}

	public static void graphBrowserTest() {
		JFrame frame = new GraphBrowser(GraphIterator.getSmallCubes(22, 4));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] argv) throws FileNotFoundException {
		// decompositionsTest();
		// showGraphTest();
		graphBrowserTest();
	}

	public static void showGraphTest() throws FileNotFoundException {
		String inFileName = "graphLib_ours/hsugrid/hsu-4x4.dimacs";
		IntegerGraph graph = ConstructGraph.construct(inFileName);
		JFrame frame = ShowGraph.demo(graph);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
