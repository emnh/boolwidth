package control;

import graph.Edge;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IGraph;

import java.io.IOException;
import java.util.ArrayList;

import boolwidth.CutBool;

class CycleBuilder {

	ArrayList<HoleVertex> vertices;
	int[][] adjacency;

	// holes

	public CycleBuilder(int N) {
		this.vertices = new ArrayList<HoleVertex>(N);
		Hole root = new Hole();
		for (int i = 0; i < N; i++) {
			HoleVertex v = new HoleVertex();
			v.hole = root;
			this.vertices.add(v);
			root.cycle.add(v);
		}
		this.adjacency = new int[N][N];
		for (int i = 0; i <= N; i++) {
			setAdj(i, (i + 1) % N, 1);
		}
	}

	public void addEdge() {
		// ArrayList<IntegerGraph> candidates = new ArrayList<IntegerGraph>();
		/*
		 * for each pair of non-adjacent vertices: find out what the smallest
		 * hole in the graph is after the edge is added. pick the edge that
		 * maximizes the smallest hole.
		 */
	}

	public int getAdj(int i, int j) {
		if (i > j) {
			int tmp = i;
			i = j;
			j = tmp;
		}
		return this.adjacency[i][j];
	}

	public void setAdj(int i, int j, int val) {
		if (i > j) {
			int tmp = i;
			i = j;
			j = tmp;
		}
		this.adjacency[i][j] = val;
	}

}

class Girth {

	// from: http://www.cs.auckland.ac.nz/~ute/220ft/graphalg/node14.html
	// returns minimum girth >= 3
	public static <V, E> int girth(IGraph<Vertex<V>, V, E> G) {

		int n = G.numVertices();
		int best = n + 1; // girth n+1 if no cycles found

		// look for a cycle from all but last two
		for (int i = 0; i < n - 2; i++) {

			PosSubSet<Vertex<V>> span = new PosSubSet<Vertex<V>>(G);
			span.set(i, true);

			int depth = 1; // do a BFS search keeping track of depth

			ArrayList<Vertex<V>> distList = new ArrayList<Vertex<V>>();
			distList.add(G.getVertex(i));

			while (depth * 2 <= best && best > 3) {

				ArrayList<Vertex<V>> nextList = new ArrayList<Vertex<V>>();

				for (Vertex<V> v : distList) {
					for (Edge<Vertex<V>, V, E> e : G.incidentEdges(v)) {
						Vertex<V> u = e.opposite(v);

						if (!span.get(G.getId(u))) {
							span.set(G.getId(u), true);
							nextList.add(u);
						} else {
							// we have found some walk/cycle

							// is there a cross edge at this level
							if (distList.contains(u)) {
								best = depth * 2 - 1;
								break;
							}

							// else even length cycle (as an upper bound)
							if (nextList.contains(u)) {
								best = depth * 2;
							}
						}
					} // for vertices at current depth

					// next try set of vertices further away
					distList = nextList;
					depth++;
				}
			}
		}
		return best;
	}
}

class Hole {
	public ArrayList<HoleVertex> cycle;
}

class HoleVertex {
	Hole hole;
}

public class ThesisOutput {

	public static void main(String[] args) throws IOException {
		// int bw_upper_bound = 64;
		//
		// for (NamedGraphComparator ng : GraphIterator.getSmallCubes(22, 4)) {
		// String outputprefix = String.format("exact/%s/%s", ng.category,
		// ng.getName());
		// System.out.printf("GRAPH ======== %s\n", outputprefix);
		// ExactTest
		// .testExactBoolWidth(ng.getGraph(), outputprefix, bw_upper_bound);
		// }
	}

	public static void printBinom(int n, int k) {
		System.out.println(CutBool.binom(n)[k]);
	}
}
