package boolwidth.heuristics;

import graph.PosSubSet;
import graph.Vertex;
import interfaces.IGraph;
import interfaces.IVertexFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import util.Util;
import boolwidth.Decomposition;

/**
 * @author emh
 * 
 *         Local search decomposition
 */
public class LSDecomposition<V, E> extends Decomposition<VertexSplit<V>, V, E> {

	// protected IGraph.D<V, E> graph;
	// protected IVertexFactory<VertexSplit<V>, PosSubSet<Vertex<V>> >
	// vertexFactory;

    // For serialization only
    @Deprecated
    public LSDecomposition()
    {

    }

	/** Default generics parameterization **/
	public static final class D<V, E> extends LSDecomposition<V, E> {

        // For serialization only
        @Deprecated
        public D()
        {

        }

		/** Running time: O (n log n) */
		public D(IGraph<Vertex<V>, V, E> graph) {
			super(graph, new VertexSplit.Factory<V>(graph));
		}

	}

	public LSDecomposition(IGraph<Vertex<V>, V, E> graph,
			IVertexFactory<VertexSplit<V>, PosSubSet<Vertex<V>>> factory) {
		super(graph, factory);
	}

	// public LSDecomposition<V, E> newRootedAt(VertexSplit<V> node) {
	// LSDecomposition<V, E> dc = new LSDecomposition<V, E>(graph,
	// vertexFactory);
	// dc.root = node;
	// return dc;
	// }

	// private int getNextID() {
	// return vList.size();
	// }

	public long boolWidth() {
		return boolWidth(this.root);
	}

	/**
	 * Returns 2^boolwidth of partial decomposition rooted at node. node doesn't
	 * necessarily have to be split all the way down, but at least once.
	 * 
	 * @return 2^boolwidth
	 */
	public long boolWidth(VertexSplit<V> node) {
		long maxcut = 0;
		for (VertexSplit<V> v : depthFirst(node)) {
			if (hasLeft(v) && hasRight(v)) {
				long cb = CutBoolComparator.maxLeftRightCutBool(this, v);
				maxcut = Math.max(maxcut, cb);
			}
		}
		if (maxcut == 0) {
			throw new RuntimeException("not initialized");
		}
		return maxcut;
	}

	public void fixParents() {
		this.root.fixParent(null);
		fixParents(this.root);
	}

	private void fixParents(VertexSplit<V> top) {
		if (hasLeft(top)) {
			left(top).fixParent(top);
			fixParents(left(top));
		}
		if (hasRight(top)) {
			right(top).fixParent(top);
			fixParents(right(top));
		}
	}

	public void splitRandom(VertexSplit<V> node, int left_size_initial) {
		ArrayList<Vertex<V>> nodelist = new ArrayList<Vertex<V>>();
		for (Vertex<V> v : node.vertices()) {
			nodelist.add(v);
		}
		Collections.shuffle(nodelist);

		node.setLeft(null);
		node.setRight(null);
		// assert !hasLeft(node);
		// assert !hasRight(node);
		// node.setLeft(new VertexSplit<V>(new PosSubSet<Vertex<V>>(),
		// getNextID()));
		// node.setRight(new VertexSplit<V>(new PosSubSet<Vertex<V>>(),
		// getNextID()));

		for (int i = 0; i < nodelist.size(); i++) {
			Vertex<V> v = nodelist.get(i);
			if (i < left_size_initial) {
				addLeft(node, v);
			} else {
				addRight(node, v);
			}
		}
		assert node.checkNode();
		// assert node.getLeft().getParent() == node;
		// assert node.getRight().getParent() == node;
	}

	public VertexSplit<V> splitRandomNew(VertexSplit<V> node,
			int left_size_initial) {
		VertexSplit<V> newnode = createVertex(node.vertices(), getNextID());
		newnode.setVertices(node.vertices());
		splitRandom(newnode, left_size_initial);
		return newnode;
	}

	/**
	 * Returns a new cut with fromleft nodes swapped from left to right and
	 * fromright nodes swapped from right to left
	 * 
	 * @param bag
	 * @param fromleft
	 * @param fromright
	 * @return
	 */
	public VertexSplit<V> swapNodes(VertexSplit<V> bag, Collection<Vertex<V>> fromlefts,
			Collection<Vertex<V>> fromrights) {

		VertexSplit<V> newsplit = createVertex(bag.vertices(), getNextID());
		newsplit.setVertices(bag.vertices());

		//		int leftsize = fromlefts == null ? 0 : fromlefts.size();
		//		int rightsize = fromrights == null ? 0 : fromrights.size();
		//		assert SwapConstraints.isValidSwap(bag, leftsize, rightsize);

		assert bag.checkNode();

		PosSubSet<Vertex<V>> newlefts = bag.getLeft().vertices().clone();
		PosSubSet<Vertex<V>> newrights = bag.getRight().vertices().clone();

		if (fromlefts != null) {
			for (Vertex<V> v : fromlefts) {
				newlefts.remove(v);
				newrights.add(v);
			}
		}
		if (fromrights != null) {
			for (Vertex<V> v : fromrights) {
				newrights.remove(v);
				newlefts.add(v);
			}
		}
		newsplit.setLeft(createVertex(newlefts, getNextID()));
		newsplit.setRight(createVertex(newrights, getNextID()));

		assert newsplit.checkNode();
		return newsplit;
	}

	/**
	 * Returns a new cut
	 * 
	 * @param <V>
	 * @param <E>
	 * @param bg
	 * @param left_size_initial
	 * @return
	 */
	public VertexSplit<V> swapRandomNodes(VertexSplit<V> bag) {
		// PosSet<Vertex<V>> lefts = new PosSet<Vertex<V>>(bg.leftVertices());
		// PosSet<Vertex<V>> rights = new PosSet<Vertex<V>>(bg.rightVertices());
		LSDecomposition<V, E> tmp = new LSDecomposition<V, E>(this.graph,
				this.vertexFactory);

		VertexSplit<V> newsplit = tmp.root(); // createVertex(node.vertices(),
		// getNextID());
		newsplit.setVertices(bag.vertices());
		Random rnd = new Random();
		int left_swapnode = rnd.nextInt(bag.getLeft().size());
		int right_swapnode = rnd.nextInt(bag.getRight().size());

		int i = 0;
		for (Vertex<V> v : bag.getLeft().vertices()) {
			if (i != left_swapnode) {
				// newsplit.getLeft().element().add(v);
				tmp.addLeft(newsplit, v);
			} else {
				tmp.addRight(newsplit, v);
			}
			i++;
		}
		i = 0;
		for (Vertex<V> v : bag.getRight().vertices()) {
			if (i != right_swapnode) {
				tmp.addRight(newsplit, v);
			} else {
				tmp.addLeft(newsplit, v);
			}
			i++;
		}

		assert newsplit.getLeft().size() == bag.getLeft().size();
		assert newsplit.getRight().size() == bag.getRight().size();

		return newsplit;
	}

	/**
	 * Returns a new cut with fromleft nodes swapped from left to right and
	 * fromright nodes swapped from right to left
	 * 
	 * @param bag
	 * @param fromleft
	 * @param fromright
	 * @return
	 */
	public VertexSplit<V> swapRandomNodes(VertexSplit<V> bag, int fromleft,
			int fromright) {

		// TODO: get rid of tmp
		LSDecomposition<V, E> tmp = new LSDecomposition<V, E>(this.graph,
				this.vertexFactory);

		VertexSplit<V> newsplit = tmp.root(); // createVertex(node.vertices(),
		// getNextID());
		newsplit.setVertices(bag.vertices());

		assert SwapConstraints.isValidSwap(bag, fromleft, fromright);

		assert bag.checkNode();

		ArrayList<Vertex<V>> lefts = Util.choose(bag.getLeft().vertices(),
				fromleft);
		assert lefts.size() == bag.getLeft().size();
		ArrayList<Vertex<V>> rights = Util.choose(bag.getRight().vertices(),
				fromright);
		assert rights.size() == bag.getRight().size();

		int i = 0;
		for (Vertex<V> v : lefts) {
			if (i < lefts.size() - fromleft) {
				assert (hasLeft(newsplit) ? newsplit.getLeft().size() : 0) == i;
				tmp.addLeft(newsplit, v);
				assert newsplit.getLeft().size() == i + 1;
			} else {
				assert (hasRight(newsplit) ? newsplit.getRight().size() : 0) == i
				- (lefts.size() - fromleft);
				tmp.addRight(newsplit, v);
				assert newsplit.getRight().size() == i
				- (lefts.size() - fromleft) + 1;
			}
			i++;
		}
		assert (hasLeft(newsplit) ? newsplit.getLeft().size() : 0) == lefts
		.size()
		- fromleft;
		assert (hasRight(newsplit) ? newsplit.getRight().size() : 0) == fromleft;

		i = 0;
		for (Vertex<V> v : rights) {
			if (i < rights.size() - fromright) {
				tmp.addRight(newsplit, v);
			} else {
				assert (hasLeft(newsplit) ? newsplit.getLeft().size() : 0) == lefts
				.size()
				- fromleft + i - (rights.size() - fromright);
				tmp.addLeft(newsplit, v);
				assert (hasLeft(newsplit) ? newsplit.getLeft().size() : 0) == lefts
				.size()
				- fromleft + i - (rights.size() - fromright) + 1;
			}
			i++;
		}

		assert hasLeft(newsplit);
		assert hasRight(newsplit);

		assert newsplit.getLeft().size() == bag.getLeft().size() + fromright
		- fromleft : String.format(
				"change: %d/%d, new: %d/%d, old: %d/%d", fromleft, fromright,
				newsplit.getLeft().size(), newsplit.getRight().size(), bag
				.getLeft().size(), bag.getRight().size());
		assert newsplit.getRight().size() == bag.getRight().size() + fromleft
		- fromright : String.format(
				"change: %d/%d, new: %d/%d, old: %d/%d", fromleft, fromright,
				newsplit.getLeft().size(), newsplit.getRight().size(), bag
				.getLeft().size(), bag.getRight().size());

		// System.out.printf("n-n: %s-%s\n",
		// node.getLeft().vertices().toString(),
		// newsplit.getLeft().vertices().toString());
		assert !newsplit.getLeft().vertices().equals(bag.getLeft().vertices());
		assert !newsplit.getLeft().vertices().toString().equals(
				bag.getLeft().vertices().toString());

		return newsplit;
	}

	/*
	 * public Decomposition.D<V, E> toStandard() { Decomposition.D<V, E> d = new
	 * Decomposition.D<V, E>(graph);
	 * 
	 * for (DNode<V> v : vertices()) {
	 * 
	 * } return d; }
	 */
}

class SwapConstraints {

	public static <V> boolean isValid(VertexSplit<V> node) {
		int minsize = minSplitSize(node);
		return node.getLeft().size() >= minsize && node.getRight().size() >= minsize;
	}

	public static <V> boolean isValidSwap(VertexSplit<V> node, int fromleft,
			int fromright) {
		// make sure each side is not less than 1/3
		int newleftsize = node.getLeft().size() + fromright - fromleft;
		int newrightsize = node.getRight().size() + fromleft - fromright;
		if (newleftsize < minSplitSize(node)) {
			return false;
		}
		if (newrightsize < minSplitSize(node)) {
			return false;
		}
		return fromleft != 0 || fromright != 0;
	}

	/**
	 * Compute max that can be moved from left without violating size
	 * constraints
	 * 
	 * @param <V>
	 * @param node
	 * @return
	 */
	public static <V> int maxFromLeft(VertexSplit<V> node) {
		return Math.max(node.getLeft().size() - minSplitSize(node), 0);
	}

	public static <V> int maxFromRight(VertexSplit<V> node) {
		return Math.max(node.getRight().size() - minSplitSize(node), 0);
	}

	public static <V> int minSplitSize(VertexSplit<V> node) {
		return Math.max(Util.divRoundUp(node.size(), 3), 1);
	}
}
