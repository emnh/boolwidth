package boolwidth;

import exceptions.EmptyTreeException;
import exceptions.InvalidPositionException;
import graph.BiGraph;
import graph.CleanBinaryTree;
import graph.Edge;
import graph.PosSet;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IDecomposition;
import interfaces.IGraph;
import interfaces.IVertexFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;

import util.Util.Pair;

/**
 * A representation of a decomposition of a graph. A decomposition is a binary
 * tree and children of a node represents a cut in the graph. Each node of the
 * decomposition is a DNode containing a VertexSubSet.
 * 
 * TODO: all places in this class where new DNodes are created may be duplicate
 * binary tree functionality
 */

public class Decomposition<TVertex extends DNode<TVertex, V>, V, E> extends
		CleanBinaryTree<TVertex, PosSubSet<Vertex<V>>, E> implements
		IDecomposition<TVertex, V, E> {

    // For serialization only
    @Deprecated
    public Decomposition()
    {
    }

	/**
	 * Default generics parameterization. Don't put new methods in here.
	 * **/
	public static final class D<V, E> extends Decomposition<DNode.D<V>, V, E> {

		public static <V, E> D<V, E> create(IGraph<Vertex<V>, V, E> graph) {
			return new D<V, E>(graph);
		}

		/** Running time: O (n log n) */
		public D(IGraph<Vertex<V>, V, E> graph) {
			super(graph, new DNode.D.Factory<V>());
		}
	}

	public static <TVertex extends DNode<TVertex, V>, V, E> Decomposition<TVertex, V, E> create(
			IGraph<Vertex<V>, V, E> graph,
			IVertexFactory<TVertex, PosSubSet<Vertex<V>>> factory) {
		return new Decomposition<TVertex, V, E>(graph, factory);
	}

	protected IGraph<Vertex<V>, V, E> graph;

	/**
	 * Merge 2 decompositions TODO: might want to mark as partial until top is
	 * reached
	 * 
	 * @param left
	 * @param right
	 */
	public Decomposition(Decomposition<TVertex, V, E> left,
			Decomposition<TVertex, V, E> right,
			IVertexFactory<TVertex, PosSubSet<Vertex<V>>> factory) {
		super(factory);
		assert left.graph == right.graph;
		this.graph = left.graph;

		PosSubSet<Vertex<V>> ts = new PosSubSet<Vertex<V>>(this.graph);

		ts.addAll(left.root().element());
		ts.addAll(right.root().element());

		addRoot(ts);

		attach(root(), left, right);
	}

	/**
	 * Create single node decomposition, as building block for merging bottom-up
	 * TODO: might want to mark as partial
	 * 
	 * @param graph
	 * @param v
	 */
	public Decomposition(IGraph.D<V, E> graph, Vertex<V> v,
			IVertexFactory<TVertex, PosSubSet<Vertex<V>>> factory) {
		super(factory);
		this.graph = graph;

		PosSubSet<Vertex<V>> ts = new PosSubSet<Vertex<V>>(graph);
		ts.add(v);
		addRoot(ts);
	}

	/** Running time: O (n log n) */
	public Decomposition(IGraph<Vertex<V>, V, E> graph,
			IVertexFactory<TVertex, PosSubSet<Vertex<V>>> factory) {
		super(factory);
		this.graph = graph;

		PosSubSet<Vertex<V>> ts = new PosSubSet<Vertex<V>>(graph);
		for (Vertex<V> v : graph.vertices()) {
			ts.add(v);
		}
		addRoot(ts);
	}

	/**
	 * Adds a left child containing given treeset to the given parent node. Adds
	 * the treeset to existing set if the parent already has a left child.
	 * Running time: O (n log n)
	 */
	@Override
	public TVertex addLeft(TVertex p, PosSubSet<Vertex<V>> subSetLeft)
			throws InvalidPositionException {
		if (!p.element().containsAll(subSetLeft)) {
			throw new InvalidPositionException("Set is not a subset");
		}

		if (hasRight(p)) {
			PosSubSet<Vertex<V>> subSetRight = p.getRight().element();

			if (!isDisjoint(subSetLeft, subSetRight)) {
				throw new InvalidPositionException("Subsets are not disjoint");
			}
		}
		if (hasLeft(p)) {
			p.element().addAll(subSetLeft);
		}
		// TVertex newNode = new TVertex(subSetLeft, this.vList.size());
		TVertex newNode = createVertex(subSetLeft, getNextID());

		addLeft(p, newNode);
		return newNode;
	}

	/**
	 * Adds a left child containing the given vertex to the given parent. Adds
	 * the vertex to the existing set if the parent already has a left child.
	 * Returns true if added, false otherwise. Running time: O(log n)
	 */
	public boolean addLeft(TVertex p, Vertex<V> v)
			throws InvalidPositionException {
		if (!p.element().contains(v)) {
			return false;
		}
		if (hasRight(p) && right(p).element().contains(v)) {
			return false;
		}
		if (hasLeft(p)) {
			return left(p).element().add(v);
		}
		TVertex newNode = createVertex(new PosSubSet<Vertex<V>>(this.graph),
				getNextID());
		return addLeft(p, newNode).element().add(v);
	}

	/**
	 * Adds a right child containing given treeset to the given parent node.
	 * Adds the treeset to existing set if the parent already has a right child.
	 * Running time: O (n log n)
	 */
	@Override
	public TVertex addRight(TVertex p, PosSubSet<Vertex<V>> subSetRight)
			throws InvalidPositionException {
		if (!p.element().containsAll(subSetRight)) {
			throw new InvalidPositionException("Set is not a subset");
		}

		if (hasLeft(p)) {
			PosSubSet<Vertex<V>> subSetLeft = p.getLeft().element();

			if (!isDisjoint(subSetRight, subSetLeft)) {
				throw new InvalidPositionException("Subsets are not disjoint");
			}
		}
		if (hasRight(p)) {
			p.element().addAll(subSetRight);
		}
		TVertex newNode = createVertex(subSetRight, getNextID());
		addRight(p, newNode);
		return newNode;
	}

	/**
	 * Adds a right child containing the given vertex to the given parent. Adds
	 * the vertex to the existing set if the parent already has a right child.
	 * Returns true if added, false otherwise. Running time: O(log n)
	 */
	public boolean addRight(TVertex p, Vertex<V> v)
			throws InvalidPositionException {
		if (!p.element().contains(v)) {
			return false;
		}
		if (hasLeft(p) && left(p).element().contains(v)) {
			return false;
		}
		if (hasRight(p)) {
			return right(p).element().add(v);
		}
		TVertex newNode = createVertex(new PosSubSet<Vertex<V>>(this.graph),
				getNextID());
		return addRight(p, newNode).element().add(v);
	}

	/**
	 * Checks if a node containing element can be added above given vertex.
	 * Running time: O(n log n)
	 */
	@Override
	protected boolean canAddAbove(TVertex vertex, PosSubSet<Vertex<V>> set) {
		if (!super.canAddAbove(vertex, set)) {
			return false;
		}
		// The vertices we try to add is in parent bag
		boolean canAdd = parent(vertex).element().containsAll(set);
		// and not in the child bag
		canAdd &= isDisjoint(set, vertex.element());
		// and not in the sibling bag
		canAdd &= isDisjoint(set, sibling(vertex).element());

		return canAdd;
	}

	private String computeLabel(String specific, String userlabel) {
		long bw = 0;
		for (TVertex n : vertices()) {
			bw = Math.max(bw, CutBool.countNeighborhoods(getCut(n)));
		}
		String label = specific + "\n";
		label += String.format("vertices=%d, edges=%d, bw=%d\n", numVertices(),
				numEdges(), bw);
		label += String.format("graph: vertices=%d, edges=%d\n", this.graph
				.numVertices(), this.graph.numEdges());
		label += userlabel;
		label = label.replaceAll("\n", "\\\\n");
		return label;
	}

	/**
	 * @return A bigraph where the left side contains all vertices of the given
	 *         DNode, and the right side contains the rest of the vertices in
	 *         the graph.
	 */
	public BiGraph<V, E> getCut(DNode<?, V> dn) {
		return new BiGraph<V, E>(new PosSet<Vertex<V>>(dn.element()),
				this.graph);
	}

	/*
	 * public TVertex parent(TVertex v) throws InvalidPositionException,
	 * BoundaryViolationException { return (TVertex)
	 * parent((BinNode<PosSubSet<Vertex<V>>>) v); //return super.parent(v); }
	 */

	public IGraph<Vertex<V>, V, E> getGraph() {
		return this.graph;
	}

	private void graphVizGraphOptions(Formatter f) {
		f.format("overlap = scale;\n");
		f.format("splines = true;\n");
	}

	/** Running time: O (n log n) */
	public boolean isDisjoint(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2) {
		boolean contains = false;
		Iterator<Vertex<V>> it = set2.iterator();
		while (it.hasNext()) {
			contains |= set1.contains(it.next());
			if (contains) {
				return !contains;
			}
		}
		return !contains;
	}

	public boolean isSubset(PosSubSet<Vertex<V>> set, PosSubSet<Vertex<V>> sub) {
		return set.containsAll(sub);
	}

	/**
	 * @return number of vertices in the decomposed graph
	 */
	public int numGraphVertices() {
		return this.graph.numVertices();
	}

	public void printCutSizes() {
		class SizeBW extends Pair<Integer> {
			public SizeBW(int a, int b) {
				super(a, b);
			}

			@Override
			public String toString() {
				return String.format("%d-%d", getLeft(), getRight());
			}
		}
		ArrayList<SizeBW> sizes = new ArrayList<SizeBW>();
		int n = numGraphVertices();
		for (TVertex v : vertices()) {
			int vs = v.element().size();
			int min = Math.min(vs, n - vs);
			int bw = CutBool.countNeighborhoods(getCut(v));
			if (min > 1) {
				sizes.add(new SizeBW(min, bw));
			}
		}
		Collections.sort(sizes);
		Collections.reverse(sizes);
		System.out.printf("Decomposition: [cutsize-bw]: %s\n", sizes);
	}

	/*
	 * protected void toGraphVizEdges(Formatter f) { for (EEdge<TVertex,
	 * PosSubSet<Vertex<V>>, E> e : edges()) { f.format("\"%s\" -- \"%s\";\n",
	 * e.left().id(), e.right().id()); } }
	 */

	public boolean removeFromLeaf(TVertex leaf, Vertex<V> v) {
		return isExternal(leaf) && leaf.element().remove(v);
	}

	/** Running time: O (1) */
	@Override
	public TVertex root() throws EmptyTreeException {
		return this.root;
	}

	public void toFile(File outFile, long hoods) {
		try {
			FileWriter fw = new FileWriter(outFile);
			fw.write("" + hoods + "\n");
			fw.write(toString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.err.println("Output file not found");
			e.printStackTrace();
		}

	}

	@Override
	public String toGraphViz(String label) {
		StringBuffer s = new StringBuffer();
		Formatter f = new Formatter(s);
		label = computeLabel("", label);
		f.format("graph {\n");
		f.format("label = \"%s\"; \n", label);

		graphVizGraphOptions(f);

		// we don't want tree upside down
		f.format("rankdir = BT;\n");
		// for displaying node sets as records
		f.format("node [shape = record]\n");
		toGraphVizNodes(f);
		toGraphVizEdges(f);
		s.append("subgraph real");
		s.append(this.graph.toGraphViz("real graph"));
		f.format("}\n");
		return s.toString();
	}

	public String toGraphVizCluster(String userlabel) {
		StringBuffer s = new StringBuffer();
		Formatter f = new Formatter(s);
		String label = computeLabel(
				"decomposition: left child is blue, right child is red\n",
				userlabel);
		f.format("graph {\n");

		graphVizGraphOptions(f);

		f.format("label = \"%s\"; \n", label);
		// we don't want tree upside down
		// f.format("rankdir = BT;\n");
		// for displaying node sets as records
		// f.format("node [shape = record]\n");
		toGraphVizNodesCluster(f, null);
		toGraphVizEdgesCluster(f);
		s.append("subgraph real");
		s.append(this.graph.toGraphViz("real graph"));
		s.append("}\n");
		return s.toString();
	}

	protected void toGraphVizEdges(Formatter f) {
		for (Edge<TVertex, PosSubSet<Vertex<V>>, E> e : edges()) {
			f.format("\"%s\" -- \"%s\";\n", e.left().hashCode(), e.right()
					.hashCode());
		}
	}

	protected void toGraphVizEdgesCluster(Formatter f) {
		// for (EEdge<TVertex, PosSubSet<Vertex<V>>, E> e : edges()) {
		// f.format("cluster_%d -- cluster_%d;\n",
		// e.left().id(), e.right().id());
		// }
	}

	protected void toGraphVizNodes(Formatter f) {
		for (TVertex n : vertices()) {
			// f.format("%s [ label = \"%d\" ],", n.hashCode(), n.id());
			// f.format(" [ element = %s ]; \n", n.element().toString());
			StringBuilder label = new StringBuilder();
			Formatter fl = new Formatter(label);
			boolean first = true;
			if (n.element().size() == 1) {
				for (Vertex<V> v : n.element()) {
					if (first) {
						first = false;
					} else {
						label.append(" | ");
					}
					fl.format("<n%d> %d", v.id(), v.id());
				}
			}
			f.format("%s [ label = \" cut=%d | %s\" ];\n", n.hashCode(),
					CutBool.countNeighborhoods(getCut(n)), label);
		}
	}

	/**
	 * produce nested cluster subgraphs representing decomposition
	 */
	protected void toGraphVizNodesCluster(Formatter f, TVertex parent) {
		ArrayList<TVertex> children = new ArrayList<TVertex>();
		if (parent == null) {
			// parent == root();
			children.add(root());
		} else {
			if (parent.getLeft() != null) {
				children.add(parent.getLeft());
			}
			if (parent.getRight() != null) {
				children.add(parent.getRight());
			}
		}
		for (TVertex n : children) {
			if (n.element().size() <= 1) {
				for (Vertex<V> v : n.element()) {
					f.format("%s;\n", v.hashCode());
				}
			} else {
				f.format("\nsubgraph cluster_%d{\n", n.id());
				if (parent != null) {
					f.format("style=filled;\n");
					if (n == parent.getLeft()) {
						f.format("fillcolor = lightblue;\n");
					} else {
						f.format("fillcolor = pink;\n");
					}
				}
				f.format("label = \" hoods=%d\";\n", CutBool
						.countNeighborhoods(getCut(n)));
				toGraphVizNodesCluster(f, n);
				f.format("}\n");
			}
		}
	}

	/** Running time: O (n log n) */
	public PosSubSet<Vertex<V>> union(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2) throws InvalidPositionException {
		if (set1.addAll(set2)) {
			return set1;
		} else {
			throw new InvalidPositionException();
		}
	}

}
