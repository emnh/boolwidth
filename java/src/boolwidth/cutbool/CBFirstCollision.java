package boolwidth.cutbool;

import graph.BiGraph;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import graph.Vertex;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import util.Util.Pair;
import boolwidth.CutBool;

public class CBFirstCollision {

	/** @return comparative estimate of 2^Boolean-width of given cut. */
	public static <V, E> int estimateNeighborhoods(BiGraph<V, E> g,
			long upper_bound, int collision_limit) {

		PosSet<Vertex<V>> rights = new PosSet<Vertex<V>>(g.rightVertices());
		PosSet<Vertex<V>> lefts = new PosSet<Vertex<V>>(g.leftVertices());

		TreeSet<PosSubSet<Vertex<V>>> initialhoods = new TreeSet<PosSubSet<Vertex<V>>>();
		ArrayList<PosSubSet<Vertex<V>>> initialhoods_a = new ArrayList<PosSubSet<Vertex<V>>>();
		TreeSet<PosSubSet<Vertex<V>>> leftnodes = new TreeSet<PosSubSet<Vertex<V>>>();
		TreeSet<PosSubSet<Vertex<V>>> rightnodes = new TreeSet<PosSubSet<Vertex<V>>>();

		// initialize all neighborhood sets of 1 left node
		int left_twin_count = 0;
		for (Vertex<V> node : g.leftVertices()) {
			PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(rights, g
					.incidentVertices(node));
			if (leftnodes.contains(neighbors)) {
				left_twin_count++;
			}
			leftnodes.add(neighbors);
		}
		int right_twin_count = 0;
		for (Vertex<V> node : g.rightVertices()) {
			PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(lefts, g
					.incidentVertices(node));
			if (rightnodes.contains(neighbors)) {
				right_twin_count++;
			}
			rightnodes.add(neighbors);
		}

		//TreeSet<PosSubSet<Vertex<V>>> hoods = new TreeSet<PosSubSet<Vertex<V>>>();
		if (rights.size() - right_twin_count > lefts.size() - left_twin_count) {
			initialhoods = leftnodes;
		} else {
			initialhoods = rightnodes;
		}

		int collisions = 0;
		Random rnd = new Random();

		// special for size 1, or we'll get stuck in loop
		// TODO: just run exact algorithm with low bound
		if (initialhoods.size() == 1) {
			if (initialhoods.first().isEmpty()) {
				return 1;
			} else {
				return 2;
			}
		}

		initialhoods_a.addAll(initialhoods);

		TreeSet<Pair<Integer>> used = new TreeSet<Pair<Integer>>();

		// compute how long it takes to reach certain number of collisions
		// when generating hoods by random unions of 2 previous hoods
		int i = 0;
		for (i = 0; collisions < collision_limit; i++) {
			if (
					(upper_bound != CutBool.BOUND_UNINITIALIZED && i > upper_bound)
					|| i > 1000*1000) {
				return CutBool.BOUND_EXCEEDED;
			}

			// pick 2 random hoods
			int[] rndidx = {
					rnd.nextInt(initialhoods.size()),
					rnd.nextInt(initialhoods.size())
			};
			if (rndidx[0] == rndidx[1]) {
				continue;
			}

			// don't do the same 2 again
			Pair<Integer> p = new Pair<Integer>(rndidx[0], rndidx[1]);
			if (used.contains(p)) {
				continue;
			}
			used.add(p);

			PosSubSet<Vertex<V>> n0 = initialhoods_a.get(rndidx[0]);
			PosSubSet<Vertex<V>> n1 = initialhoods_a.get(rndidx[1]);

			// generate union and check collision
			PosSubSet<Vertex<V>> newhood = n0.union(n1);
			if (initialhoods.contains(newhood)) {
				collisions++;
			} else {
				initialhoods.add(newhood);
				initialhoods_a.add(newhood);
			}
		}

		return i - collisions;
	}
}
