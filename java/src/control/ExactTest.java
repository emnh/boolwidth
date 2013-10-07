package control;

import graph.Edge;
import graph.IntegerGraph;
import graph.Vertex;
import interfaces.IGraph;
import io.ConstructGraph;
import io.GraphViz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import boolwidth.CutBool;
import boolwidth.Decomposition;
import boolwidth.ExactBoolWidth;

class ChangeLing extends IntegerGraph {

	ArrayList<Edge<Vertex<Integer>, Integer, String>> deadedges = new ArrayList<Edge<Vertex<Integer>, Integer, String>>();

	boolean lastremoved = false;
	Edge<Vertex<Integer>, Integer, String> lastchanged;

	public ChangeLing(int n) {
		super(n);
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				insertEdge(i, j);
			}
		}
	}

	public void addRandomEdge() {
		int re = (int) (Math.random() * numEdges());
		int j = 0;
		// System.out.println("add: " + numEdges());
		for (Edge<Vertex<Integer>, Integer, String> edge : this.deadedges) {
			if (j == re) {
				this.lastremoved = false;
				this.lastchanged = insertEdge(edge.left().id(), edge.right()
						.id());
				this.deadedges.remove(edge);
				break;
			}
			j++;
		}
	}

	public void removeRandomEdge() {
		int re = (int) (Math.random() * numEdges());
		int j = 0;
		// System.out.println("remove: " + numEdges());
		for (Edge<Vertex<Integer>, Integer, String> edge : edges()) {
			if (j == re) {
				this.lastchanged = edge;
				this.lastremoved = true;
				this.deadedges.add(edge);
				removeEdge(edge);
				break;
			}
			j++;
		}
	}

	public void undo() {
		if (this.lastremoved) {
			this.lastremoved = false;
			insertEdge(this.lastchanged.left().id(), this.lastchanged.right()
					.id());
			this.deadedges.remove(this.lastchanged);
		} else {
			this.lastremoved = true;
			this.deadedges.add(this.lastchanged);
			removeEdge(this.lastchanged);
		}
	}
}

public class ExactTest {

	public static IntegerGraph completeGraph(int n) {
		IntegerGraph g = new IntegerGraph(n);
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				g.insertEdge(i, j);
			}
		}
		return g;
	}

	public static IntegerGraph createCycle(int n) {
		IntegerGraph g = new IntegerGraph(n);
		for (int i = 0; i < n; i++) {
			g.insertEdge(i, (i + 1) % n);
		}
		return g;
	}

	public static IntegerGraph enumGraph(int n, int gnum) {
		IntegerGraph g = new IntegerGraph(n);
		int edgenum = 0;
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				edgenum++;
				if ((gnum & 1 << edgenum) != 0) {
					g.insertEdge(i, j);
				}
			}
		}
		return g;
	}

	public static void main(String[] args) throws IOException {

		// testExactBoolWidth("graphLib_ours/hsugrid/hsu-3x3.dimacs");

		// TODO: use heuristic for upper bound
		// compute random upper bound

		String filename = "graphLib_ours/hsugrid/hsu-4x4.dimacs";
		// String filename = "graphLib_ours/test/8.dimacs";
		int bw_upper_bound = 64; // (int) simpleHeuristic(filename);
		//
		IGraph<?, ?, ?> graph;
		//graph = ControlUtil.getTestGraph(filename);
		graph = ConstructGraph.gridGraph(5, 4);

		String outputprefix = filename;
		testExactBoolWidth(graph, outputprefix, bw_upper_bound);

		System.exit(1);

		// graph = ControlUtil.getTestGraph().getGraph();
		// testExactBoolWidth("graphLib_ours/new/");
		// System.exit(1);

		// for (int N = 5; N < 16; N++) {
		// final int iterations = 1000;
		// String graphfilename = String.format("highbw-graph/max-%d", N);
		// searchForHighBWGraphs(graphfilename, N, iterations);
		// }
	}

	public static void removeRandomEdge(IntegerGraph graph) {
		int re = (int) (Math.random() * graph.numEdges());
		// Edge<Vertex<Integer>, Integer, String> edge;
		int j = 0;
		for (Edge<Vertex<Integer>, Integer, String> edge : graph.edges()) {
			if (j == re) {
				graph.removeEdge(edge);
				break;
			}
			j++;
		}
	}

	private static void saveGraphDecomposition(String filenameprefix,
			IntegerGraph graph, long bw, Decomposition.D<Integer, String> dc,
			long time) throws IOException {
		filenameprefix = "exact/" + filenameprefix;
		GraphViz.saveGraphDecomposition(filenameprefix, graph, bw, dc, time);
	}

	/**
	 * 
	 * @param N
	 *            number of nodes in graph
	 * @param maxi
	 * @throws IOException
	 *             number of graphs to generate
	 */
	@SuppressWarnings("unused")
	private static IntegerGraph searchForHighBWGraphs(String filenameprefix,
			int N, int maxi) throws IOException {
		ExactBoolWidth<Vertex<Integer>, Integer, String> ebwi = new ExactBoolWidth<Vertex<Integer>, Integer, String>(
				false);
		IntegerGraph graph;
		long start;
		long end;
		long bw = 0;
		int[] bwstats = new int[N * 2]; // arbitrary. hope it's enough

		ChangeLing cling = new ChangeLing(N);
		graph = cling;

		long oldbw = 0;
		for (int i = 1; i <= maxi; i++) {
			// graph = ConstructGraph.randomGraph(N, Math.random());
			// graph = ConstructGraph.rndGraph(N, (int) (Math.random() * 2 * N)
			// + N);
			// graph = ConstructGraph.rndGraph(N, N);
			// graph = createCycle(i);
			// graph = enumGraph(N, i);

			if (cling.numEdges() < 2 * N - 2) {
				cling.addRandomEdge();
			} else if (cling.numEdges() > 2 * N + 2) {
				cling.removeRandomEdge();
			} else {
				if (cling.lastremoved) {
					cling.removeRandomEdge();
				} else {
					cling.addRandomEdge();
				}
				// graph = completeGraph(N);
				// graph = ConstructGraph.rndGraph(N, 3 * N);
			}
			// graph = ConstructGraph.rndGraph(N, N * 2 - 1);
			// System.out.println(N / (Math.log(N) * Math.log(N)));
			// graph = ConstructGraph.randomGraph(N, (Math.log(N) * Math.log(N))
			// / N);

			if (i * 100 % maxi == 0) {
				System.out.printf("%d%%\n", i * 100 / maxi);
			}
			// System.out.println("expected bw: " + Math.pow(Math.log(N), 2));

			oldbw = bw;

			// TODO: for new upper_bound, how much can boolwidth
			// change by add/remove one edge?
			start = System.currentTimeMillis();
			bw = ebwi.exactBooleanWidthNew(graph, oldbw); // * 2
			end = System.currentTimeMillis();

			if (bw != ExactBoolWidth.Computer.FAILED_TO_MEET_BOUND) {
				if (bw < oldbw) {
					// System.out.println("undid");
					bw = oldbw;
					cling.undo();
				}
				bwstats[(int) bw]++;
			} else {
				bw = ebwi.exactBooleanWidthNew(graph, CutBool
						.bestGeneralUpperBound(N));
				assert bw != ExactBoolWidth.Computer.FAILED_TO_MEET_BOUND : String
				.format("%d\n", CutBool.bestGeneralUpperBound(N));
				assert bw > oldbw;
				System.out.printf("v=%d, e=%d, bw=%d, time=%dms\n", graph
						.numVertices(), graph.numEdges(), bw, end - start);
				Decomposition.D<Integer, String> dc = ebwi.getDecomposition();
				saveGraphDecomposition(filenameprefix, graph, bw, dc, end
						- start);
			}
		}

		int sum = 0;
		for (int i = 0; i < bwstats.length; i++) {
			if (bwstats[i] > 0) {
				sum += bwstats[i];
				System.out.printf("count(bw(%d))=%d, cumulative: %.2f%%\n", i,
						bwstats[i], (float) sum * 100 / maxi);
			}
		}

		return graph;
	}

	public static int simpleHeuristic(String filename)
	throws FileNotFoundException {
		throw new UnsupportedOperationException("not implemented");
		// AdjacencyListGraph.D<String, String> graph = ConstructGraph
		// .buildGraph(filename);
		// Decomposition.D<String, String> decomp = RandomSplitter.randomSplit(
		// graph, Integer.MAX_VALUE);
		//
		// long minbw = CutBool.bestGeneralUpperBound(graph.numVertices());
		//
		// long start = System.currentTimeMillis();
		//
		// for (int i = 0; i < 10000; i++) {
		// decomp = RandomSplitter.randomSplit(graph, Integer.MAX_VALUE);
		// long bw = CutBool.booleanWidth(decomp, minbw);
		// if (bw != CutBool.BOUND_EXCEEDED && bw < minbw) {
		// // System.out.printf("i: %d, bw: %d\n", i, bw);
		// minbw = bw;
		// // mindecomp = decomp;
		// }
		// }
		//
		// long end = System.currentTimeMillis();
		//
		// System.out.printf("heuristic: time=%d ms, upper bound=%d\n", end
		// - start, minbw);
		// System.out.flush();
		//
		// return (int) minbw;
	}

	public static <TVertex extends Vertex<V>, V, E> long testExactBoolWidth(
			IGraph<TVertex, V, E> graph, String output_prefix, int upper_bound)
	throws FileNotFoundException, IOException {
		long start;
		long bw;
		long end;
		// AdjacencyListGraph.D<String, String> graph2;

		start = System.currentTimeMillis();
		ExactBoolWidth<TVertex, V, E> ebw = new ExactBoolWidth<TVertex, V, E>(
				true);
		bw = ebw.exactBooleanWidthNew(graph, upper_bound);
		end = System.currentTimeMillis();
		System.out.printf("v=%d, e=%d, bw=%d, time=%dms\n",
				graph.numVertices(), graph.numEdges(), bw, end - start);
		ebw.getStatistics().printStats();
		// System.out.println(ebw.getDecomposition());
		GraphViz.saveGraphDecomposition(output_prefix, graph, bw, ebw
				.getDecomposition(), end - start);
		return bw;
	}
}
