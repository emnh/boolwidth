package graph;

import io.ConstructGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import util.Util;

public class GraphIterator {

	/**
	 * Get cubes/grids etc with number of vertices <= N
	 * 
	 * @param N
	 */
	public static Collection<IntegerGraph> getSmallCubes(int N, int dimct) {
		ArrayList<IntegerGraph> graphs = new ArrayList<IntegerGraph>();
		int[] K = new int[dimct];
		Arrays.fill(K, 1);

		finish: while (true) {
			// make graph
			for (int iwrap = 0; iwrap < 2; iwrap++) {
				boolean wrap = iwrap != 0;
				String swrap = wrap ? "-wrap" : "";
				IntegerGraph graph = ConstructGraph.cubeGraph(wrap, K);
				graph.setAttr("category", "cubes");
				graph.setAttr("name", (String.format("g-N=%d-%s%s", graph
						.numVertices(), Util.formatIntArray(2, K), swrap)));
				graphs.add(graph);
			}

			// increment position by odometer principle
			K[0]++;
			for (int i = 0; i < dimct; i++) {
				// System.out.println(Arrays.toString(K));
				if (Util.product(K) > N) {
					if (!(i + 1 < dimct)) {
						break finish;
					}
					K[i + 1]++;
					// sorted: e.g. prev: 1 1 4 4 next: 1 2 2 2
					Arrays.fill(K, 0, i + 1, K[i + 1]);
				} else {
					break;
				}
			}
		}
		Collections.sort(graphs, new NamedGraphComparator());
		return graphs;
	}
}
