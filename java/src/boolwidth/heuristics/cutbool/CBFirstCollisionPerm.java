package boolwidth.heuristics.cutbool;

import graph.BiGraph;
import graph.PosSet;
import graph.PosSubSet;
import graph.Vertex;

import java.util.ArrayList;
import java.util.TreeSet;

import util.Util;
import boolwidth.CutBool;

public class CBFirstCollisionPerm {

	/** @return comparative estimate of 2^Boolean-width of given cut. */
	public static <V, E> int estimateNeighborhoods(BiGraph<V, E> g,
			long upper_bound, int collision_limit) {

		PosSet<Vertex<V>> rights = new PosSet<Vertex<V>>(g.rightVertices());
		PosSet<Vertex<V>> lefts = new PosSet<Vertex<V>>(g.leftVertices());

		TreeSet<PosSubSet<Vertex<V>>> initialhoods = new TreeSet<PosSubSet<Vertex<V>>>();
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
		PosSubSet<Vertex<V>> emptyset;
		PosSubSet<Vertex<V>> fullset;
		if (rights.size() - right_twin_count > lefts.size() - left_twin_count) {
			initialhoods = leftnodes;
			emptyset = new PosSubSet<Vertex<V>>(rights);
		} else {
			initialhoods = rightnodes;
			emptyset = new PosSubSet<Vertex<V>>(lefts);
		}

		// compute fullset
		fullset = emptyset;
		for (PosSubSet<Vertex<V>> hood : initialhoods) {
			fullset = fullset.union(hood);
		}

		int collisions = 0;
		//Random rnd = new Random();

		TreeSet<PosSubSet<Vertex<V>>> hoods = new TreeSet<PosSubSet<Vertex<V>>>();
		ArrayList<PosSubSet<Vertex<V>>> hoods_a = new ArrayList<PosSubSet<Vertex<V>>>();

		// special for size 1, or we'll get stuck in loop
		// TODO: just run exact algorithm with low bound
		if (initialhoods.size() == 1) {
			if (initialhoods.first().isEmpty()) {
				return 1;
			} else {
				return 2;
			}
		}

		hoods.addAll(initialhoods);
		hoods_a.addAll(initialhoods);

		//TreeSet<Pair<Integer>> used = new TreeSet<Pair<Integer>>();

		// compute how long it takes to reach certain number of collisions
		// when generating hoods by random unions of 2 previous hoods
		int i = 0;
		int jsum = 0;
		double javg = 0;
		for (i = 0; collisions < collision_limit; i++) {
			//for (i = 0; i < 100; i++) {
			if (
					(upper_bound != CutBool.BOUND_UNINITIALIZED && i > upper_bound)
					|| i > 1000*1000) {
				return CutBool.BOUND_EXCEEDED;
			}

			PosSubSet<Vertex<V>> newhood = emptyset;
			int j = 0;
			//System.out.printf("newhood: ");
			for (PosSubSet<Vertex<V>> hood : Util.shuffle(initialhoods)) {
				newhood = newhood.union(hood);
				//System.out.printf("%s, ", Long.toBinaryString(newhood.words[0]));
				j++;
				if (newhood.equals(fullset)) {
					collisions++;
					break;
				}
				if (j > 1) {
					if (hoods.contains(newhood)) {
						collisions++;
					} else {
						hoods.add(newhood);
						//hoods_a.add(newhood);
					}
				}
			}
			//jsum += j;
			javg += (1 << j); //((javg * i) + (1 << j)) / (i + 1);

			//System.out.println();
		}

		return hoods.size(); //(int) (javg / i);
	}
}
