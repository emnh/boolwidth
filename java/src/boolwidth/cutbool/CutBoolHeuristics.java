package boolwidth.cutbool;

import graph.BiGraph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import util.PermutationGenerator;
import util.Util;

public class CutBoolHeuristics {

	public static final class EstResult {
		public double max = 0;
		public double min = 0;
		public double avg = 0;
	}

	/**
	 * Output a number hopefully strongly correlated with the number of
	 * neighborhoods
	 * 
	 * @param <V>
	 * @param <E>
	 * @param g
	 * @return
	 */
	public static <V, E> double neighborhoodEstimator(BiGraph<V, E> g,
			ArrayList<Vertex<V>> vertexorder) {
		double sum = 0;
		TreeSet<Vertex<V>> leftmarked = new TreeSet<Vertex<V>>();
		TreeSet<Vertex<V>> rightmarked = new TreeSet<Vertex<V>>();
		for (Vertex<V> v : vertexorder) {
			if (!leftmarked.contains(v)) {
				int unmarkedneighbors = 0;
				for (Vertex<V> n : g.incidentVertices(v)) {
					if (!rightmarked.contains(n)) {
						unmarkedneighbors++;
						rightmarked.add(n);
					}
				}
				if (unmarkedneighbors > 0) {
					sum += 1 / (double) unmarkedneighbors;
				}
				leftmarked.add(v);
			}
		}
		return sum;
	}

	/**
	 * Average the other function over n samples.
	 * 
	 * @param <V>
	 * @param <E>
	 * @param g
	 * @param samples
	 * @return
	 */
	public static <V, E> EstResult neighborhoodEstimator(BiGraph<V, E> g,
			int samplect) {
		ArrayList<Vertex<V>> lefts = new ArrayList<Vertex<V>>(Util.asList(g
				.leftVertices()));
		int n = 0;
		EstResult result = new EstResult();
		for (int i = 0; i < samplect; i++) {
			// System.out.println(Arrays.toString(perm));
			Collections.shuffle(lefts);
			double est = neighborhoodEstimator(g, lefts);
			result.avg += est;
			if (i == 0) {
				result.min = est;
				result.max = est;
			} else {
				result.min = Math.min(result.min, est);
				result.max = Math.max(result.max, est);
			}
			n++;
		}
		result.avg /= n;
		return result;
	}

	public static <V, E> double sumAllPerms(BiGraph<V, E> g) {
		PermutationGenerator pgen = new PermutationGenerator(g
				.numLeftVertices());
		double sum = 0;
		ArrayList<Vertex<V>> lefts_init = new ArrayList<Vertex<V>>(Util
				.asList(g.leftVertices()));
		int n = 0;
		for (int[] perm : pgen) {
			// System.out.println(Arrays.toString(perm));
			ArrayList<Vertex<V>> lefts_perm = new ArrayList<Vertex<V>>();
			for (int i : perm) {
				lefts_perm.add(lefts_init.get(i));
			}
			sum += neighborhoodEstimator(g, lefts_perm);
			n++;
		}
		return sum / n;
	}
}
