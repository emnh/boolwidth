package io;

import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.IntegerGraph;
import graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * Constructs a graph from a given file. The first two integers of the file give
 * the number of vertices and the number of edges. Each edge is then listed by a
 * string label and the two integers representing its endvertices.
 */

public class ConstructGraph {

	public static final class BiGraphConstructor<E> {

		private BiGraph<Integer, E> g;
		private ArrayList<Vertex<Integer>> lefts;
		private ArrayList<Vertex<Integer>> rights;

		public void addEdge(int i, int j) {
			this.g.insertEdge(this.lefts.get(i), this.rights.get(j), null);
		}

		/**
		 * Essentially the same as taking the diagonal of an NxN grid on one
		 * side
		 * 
		 * @param N
		 * @return
		 */
		public BiGraph<Integer, E> gridDiagonal(int N) {
			N--;
			this.g = matchingBiGraphWithDiagonals(N);
			this.lefts.add(this.g.insertLeft(N));
			addEdge(N, N - 1);
			return this.g;
		}

		private void initialize(int leftcount, int rightcount) {
			this.g = new BiGraph<Integer, E>(Math.max(leftcount, rightcount));
			this.lefts = new ArrayList<Vertex<Integer>>();
			this.rights = new ArrayList<Vertex<Integer>>();

			for (int i = 0; i < leftcount; i++) {
				this.lefts.add(this.g.insertLeft(i));
			}
			for (int i = 0; i < rightcount; i++) {
				this.rights.add(this.g.insertRight(i));
			}
		}

		public BiGraph<Integer, E> matchingBiGraph(int N) {
			initialize(N, N);
			for (int i = 0; i < N; i++) {
				addEdge(i, i);
			}
			return this.g;
		}

		public BiGraph<Integer, E> matchingBiGraphWithDiagonals(int N) {
			this.g = matchingBiGraph(N);
			for (int i = 0; i < N; i++) {
				if (i + 1 < N) {
					addEdge(i + 1, i);
				}
			}
			return this.g;
		}

		public BiGraph<Integer, E> randomBiGraph(int N1, int N2,
				double probability) {

			initialize(N1, N2);

			int i = 0;
			int j = 0;

			Random r = new Random();
			for (i = 0; i < N1; i++) {
				for (j = 0; j < N2; j++) {
					boolean isEdge = r.nextDouble() <= probability;
					if (isEdge) {
						addEdge(i, j);
					} else {
						;
					}
				}
			}
			return this.g;
		}

	}

	/**
	 * TODO: remove or make it use GraphBuilder, which supports more file
	 * formats. Builds an adjacency list graph where both vertices and edges
	 * holds a string element.
	 * @deprecated
	 */
	@Deprecated
	public static AdjacencyListGraph.D<String, String> buildGraph(String file)
	throws FileNotFoundException {
		System.out.println("warning: buildGraph is deprecated");
		Scanner sc = setFile(file);
		// get rid of the two strings labels
		sc.next();
		sc.next();
		int nodesNum = sc.nextInt();
		int edgeNum = sc.nextInt();
		AdjacencyListGraph.D<String, String> graph = new AdjacencyListGraph.D<String, String>();
		ArrayList<Vertex<String>> nodes = new ArrayList<Vertex<String>>();
		for (int i = 0; i < nodesNum; i++) {
			nodes.add(graph.insertVertex("Node:" + (i + 1)));
		}
		for (int i = 0; i < edgeNum; i++) {
			// to remove the "e" string
			sc.next();
			int node1 = sc.nextInt();
			int node2 = sc.nextInt();
			graph.insertEdge(nodes.get(node1 - 1), nodes.get(node2 - 1), "-");
		}
		sc.close();
		return graph;
	}

	public static IntegerGraph construct(String file)

	throws FileNotFoundException {
		Scanner sc = setFile(file);
		// get rid of the two strings labels
		sc.next();
		sc.next();
		int nodesNum = sc.nextInt();
		IntegerGraph graph = new IntegerGraph(nodesNum);
		int edgeNum = sc.nextInt();
		for (int i = 0; i < edgeNum; i++) {
			if (!sc.next().equals("e")) {
				throw new InputMismatchException();
			}
			int node1 = sc.nextInt();
			int node2 = sc.nextInt();
			graph.insertEdge(node1 - 1, node2 - 1);
		}
		return graph;
	}

	public static IntegerGraph cubeGraph(boolean wrap, int... k) {
		int dimct = k.length;
		int nodect = 1;
		for (int i = 0; i < dimct; i++) {
			nodect *= k[i];
		}
		// System.out.println(nodect);
		IntegerGraph g = new IntegerGraph(nodect);
		int[] pos = new int[dimct];

		finish: while (true) {
			// create edges along all dimensions
			for (int i = 0; i < dimct; i++) {
				int idx = cubeIndexMap(pos, k);
				assert idx < nodect;
				int oldposi = pos[i];
				if (pos[i] + 1 < k[i]) {
					pos[i]++;
					int nextidx = cubeIndexMap(pos, k);
					g.insertEdge(idx, nextidx);
				} else if (wrap) {
					pos[i] = (pos[i] + 1) % k[i];
					int nextidx = cubeIndexMap(pos, k);
					g.insertEdge(idx, nextidx);
				}
				pos[i] = oldposi;
			}

			// increment position by odometer principle
			pos[0]++;
			for (int i = 0; i < dimct; i++) {
				if (pos[i] >= k[i]) {
					if (!(i + 1 < dimct)) {
						break finish;
					}
					pos[i] = 0;
					pos[i + 1]++;
				} else {
					break;
				}
			}
		}

		return g;
	}

	public static int cubeIndexMap(int[] pos, int[] k) {
		int idx = 0;
		int mul = 1;

		for (int i = 0; i < k.length; i++) {
			// idx *= k[i];
			idx += pos[i] * mul;
			mul *= k[i];
		}
		// System.out.printf("%d = %s\n", idx, Arrays.toString(pos));
		return idx;
	}

	public static IntegerGraph cycleGraph(int N) {
		return gridGraph(N, 1, true);
	}

	public static BiGraph<Integer, Integer> gridDiagonal(int N) {
		BiGraphConstructor<Integer> bgconstructor = new BiGraphConstructor<Integer>();
		return bgconstructor.gridDiagonal(N);
	}

	public static IntegerGraph gridGraph(int k1, int k2) {
		return gridGraph(k1, k2, false);
	}

	public static IntegerGraph gridGraph(int k1, int k2, boolean wrap) {
		IntegerGraph g = new IntegerGraph(k1 * k2);
		for (int i = 0; i < k1; i++) {
			for (int j = 0; j < k2; j++) {
				int idx = i * k2 + j;
				int idxnext;
				if (j + 1 < k2) {
					idxnext = idx + 1;
					g.insertEdge(idx, idxnext);
				} else if (wrap) {
					idxnext = i * k2 + (j + 1) % k2;
					g.insertEdge(idx, idxnext);
				}
				if (i + 1 < k1) {
					idxnext = (i + 1) * k2 + j;
					g.insertEdge(idx, idxnext);
				} else if (wrap) {
					idxnext = (i + 1) % k1 * k2 + j;
					g.insertEdge(idx, idxnext);
				}
			}
		}
		return g;
	}

	public static BiGraph<Integer, Integer> matchingBiGraph(int N) {
		BiGraphConstructor<Integer> bgconstructor = new BiGraphConstructor<Integer>();
		return bgconstructor.matchingBiGraph(N);
	}

	public static BiGraph<Integer, Integer> matchingBiGraphWithDiagonals(int N) {
		BiGraphConstructor<Integer> bgconstructor = new BiGraphConstructor<Integer>();
		return bgconstructor.matchingBiGraphWithDiagonals(N);
	}

	public static BiGraph<Integer, Integer> randomBiGraph(int N1, int N2,
			double probability) {
		BiGraphConstructor<Integer> bgconstructor = new BiGraphConstructor<Integer>();
		return bgconstructor.randomBiGraph(N1, N2, probability);
	}

	/**
	 * Construct a random graph
	 * 
	 * @param N
	 *            number of nodes
	 * @param probability
	 *            probability that edge is created
	 * @return the graph
	 */
	public static IntegerGraph randomGraph(int N, double probability) {
		IntegerGraph g = new IntegerGraph(N);

		int i = 0;
		int j = 0;

		Random r = new Random();
		for (i = 0; i < N; i++) {
			for (j = i + 1; j < N; j++) {
				boolean isEdge = r.nextDouble() <= probability;
				if (isEdge) {
					g.insertEdge(i, j);
				} else {
					;
				}
			}
		}
		return g;
	}

	/**
	 * Construct a random graph
	 * 
	 * @param n
	 *            number of nodes
	 * @param m
	 *            number of edges
	 * @return the graph
	 */
	public static IntegerGraph rndGraph(int n, int m) {
		m = Math.min(m, n * (n - 1) / 2);
		int[][] mat = new int[n][n];
		for (int a = 0; a < m;) {
			int i = (int) (Math.random() * n);
			int j = (int) (Math.random() * n);
			if (mat[i][j] == 0) {
				a++;
			}
			mat[i][j] = mat[j][i] = 1;
		}
		IntegerGraph g = new IntegerGraph(n);
		for (int a = 0; a < n; a++) {
			for (int b = 0; b < a; b++) {
				if (mat[a][b] == 1) {
					g.insertEdge(a, b);
				}
			}
		}
		return g;
	}

	public static Scanner setFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (file == null) {
			throw new FileNotFoundException("File not chosen");
		} else {
			return new Scanner(file);
		}
	}
}
