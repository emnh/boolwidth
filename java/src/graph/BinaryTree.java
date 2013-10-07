package graph;

import exceptions.BoundaryViolationException;
import exceptions.EmptyTreeException;
import exceptions.InvalidPositionException;
import exceptions.NonEmptyTreeException;
import interfaces.IBinaryTree;
import interfaces.IVertexFactory;

import java.util.ArrayList;
import java.util.Collections;

import util.Util;
import util.Util.Pair;

/**
 * A realization of a binary tree as an extension of an adjacency list graph
 * TODO: use CleanBinaryTree instead. make a nicer class using 2 back-ends,
 * CleanBinaryTree and AdjacencyListGraph
 * @deprecated
 * */
@Deprecated
public class BinaryTree<TVertex extends BinNode<TVertex, V>, V, E> extends
AdjacencyListGraph<TVertex, V, E> implements IBinaryTree<TVertex, V, E> {

	protected TVertex root = null;

	/** Creates an empty binary tree */
	public BinaryTree(IVertexFactory<TVertex, V> factory) {
		super(factory);
	}

	/**
	 * Adds new node containing element above given vertex. New node as a right
	 * child. Running time: O (n log n)
	 */
	public void addAbove(TVertex vertex, V element)
	throws InvalidPositionException {
		if (!canAddAbove(vertex, element)) {
			throw new InvalidPositionException("Cannot add above this vertex");
		}

		TVertex parent = parent(vertex);
		boolean isLeft = false;
		if (left(parent(vertex)) == vertex) {
			isLeft = true;
		}

		// add node between vertex and parent
		TVertex newParent = createVertex(element, getNextID());
		insertVertex(newParent);

		newParent.setParent(parent);
		if (isLeft) {
			parent.setLeft(newParent);
		} else {
			parent.setRight(newParent);
		}

		vertex.setParent(newParent);
		newParent.setLeft(vertex);

		TVertex newNode = createVertex(element, size());
		insertVertex(newNode);
		newNode.setParent(newParent);
		newParent.setRight(newNode);
	}

	// TODO: not properly tested
	// assumes the subtree is not part of another tree
	public void addChildren(TVertex node) {
		if (hasLeft(node)) {
			TVertex left = left(node);
			node.setLeft(null);
			left.setId(getNextID());
			addLeft(node, left);
			addChildren(left);
			assert node.getLeft() == left;
		}
		if (hasRight(node)) {
			TVertex right = right(node);
			node.setRight(null);
			right.setId(getNextID());
			addRight(node, right);
			addChildren(right);
			assert node.getRight() == right;
		}
	}

	protected TVertex addLeft(TVertex p, TVertex l)
	throws InvalidPositionException {
		if (hasLeft(p)) {
			throw new InvalidPositionException("Node already has a left child");
		}
		this.insertVertex(l);
		super.insertEdge(l, p, null);
		l.setParent(p);
		p.setLeft(l);
		return l;
	}

	/**
	 * Adds a new node with a given element as the left child of a given node.
	 * Returns the new node. Running time: O(1)
	 */
	public TVertex addLeft(TVertex p, V elem) throws InvalidPositionException {
		TVertex newNode = createVertex(elem, numVertices());
		return addLeft(p, newNode);
	}

	protected TVertex addRight(TVertex p, TVertex r)
	throws InvalidPositionException {
		checkConsistency();
		if (hasRight(p)) {
			throw new InvalidPositionException("Node already has a right child");
		}
		this.insertVertex(r);
		super.insertEdge(r, p, null);
		r.setParent(p);
		p.setRight(r);
		checkConsistency();
		return r;
	}

	/**
	 * Adds a new node with a given element as the right child of a given node.
	 * Returns the new node. Running time: O(1)
	 */
	public TVertex addRight(TVertex p, V elem) throws InvalidPositionException {
		TVertex newNode = createVertex(elem, numVertices());
		return addRight(p, newNode);
	}

	protected TVertex addRoot(TVertex r) throws NonEmptyTreeException {
		if (!isEmpty()) {
			throw new NonEmptyTreeException("Tree already has a root");
		}
		this.root = r;
		this.root.setId(0);
		insertVertex(this.root);
		return this.root;
	}

	/**
	 * Adds a new root node with a given element to an empty tree. Returns the
	 * new node. Running time: O(1)
	 */
	public TVertex addRoot(V e) throws NonEmptyTreeException {
		TVertex bn = createVertex(e, 0);
		return addRoot(bn);
	}

	public void attach(TVertex extNode, IBinaryTree<TVertex, V, E> t1,
			IBinaryTree<TVertex, V, E> t2) throws InvalidPositionException {
		if (isInternal(extNode)) {
			throw new InvalidPositionException(
			"Cannot attach from internal node");
		}
		if (!t1.isEmpty()) {
			attachLeft(extNode, t1, t1.root());
		}
		if (!t2.isEmpty()) {
			attachRight(extNode, t2, t2.root());
		}
	}

	private void attachLeft(TVertex n, IBinaryTree<TVertex, V, E> bt,
			TVertex root) {
		TVertex newNode = addLeft(n, root.element());
		if (bt.hasLeft(root)) {
			attachLeft(newNode, bt, bt.left(root));
		}
		if (bt.hasRight(root)) {
			attachRight(newNode, bt, bt.right(root));
		}
	}

	private void attachRight(TVertex n, IBinaryTree<TVertex, V, E> bt,
			TVertex root) {
		TVertex newNode = addRight(n, root.element());
		if (bt.hasLeft(root)) {
			attachLeft(newNode, bt, bt.left(root));
		}
		if (bt.hasRight(root)) {
			attachRight(newNode, bt, bt.right(root));
		}
	}

	/**
	 * Checks if a node containing element can be added above given vertex.
	 * Running time: O(n log n)
	 */
	protected boolean canAddAbove(TVertex vertex, V element) {
		if (isRoot(vertex)) {
			return false;
		}

		return true;
	}

	/**
	 * Check that the underlying graph represents the same tree as the BinNodes.
	 * 
	 * @return
	 */
	public boolean checkConsistency() {
		boolean consistent = true;

		consistent &= this.root.parent == null;
		assert consistent;

		ArrayList<TVertex> binnodes = depthFirst();
		ArrayList<TVertex> vertices = Util.asList(vertices());
		Collections.sort(binnodes);
		Collections.sort(vertices);
		consistent &= binnodes.equals(vertices);
		assert consistent : String.format("\nbinnodes=%s, vertices=%s",
				binnodes, vertices);

		// check edges
		for (TVertex v : binnodes) {
			// first, build the 2 edgelists (using Pairs of vertices,
			// since BinNodes don't have Edge objects)
			// TODO: using Vertex<V> for comparable, not TVertex. can be fixed
			// by
			// making Vertex generic on itself
			ArrayList<Pair<Vertex<V>>> edges = new ArrayList<Pair<Vertex<V>>>();
			ArrayList<Pair<Vertex<V>>> binedges = new ArrayList<Pair<Vertex<V>>>();

			for (Edge<TVertex, V, E> e : incidentEdges(v)) {
				edges.add(new Pair<Vertex<V>>(e.left(), e.right()));
			}

			if (hasLeft(v)) {
				binedges.add(new Pair<Vertex<V>>(v, left(v)));
			}
			if (hasRight(v)) {
				binedges.add(new Pair<Vertex<V>>(v, right(v)));
			}
			// System.out.printf("%s.parent=%s, isroot=%s\n", v, v.parent,
			// isRoot(v));
			assert v.parent == null == isRoot(v) : String.format(
					"v=%s, e=%s, be=%s", v, edges, binedges);
			if (hasParent(v)) {
				binedges.add(new Pair<Vertex<V>>(v, parent(v)));
			}

			// check number of edges
			consistent &= edges.size() == binedges.size();
			assert consistent : String.format("%s %s", edges, binedges);

			// check edges
			Collections.sort(edges);
			Collections.sort(binedges);
			consistent &= new Util.LexCollection<Pair<Vertex<V>>>().compare(
					edges, binedges) == 0;
			assert consistent;
		}
		return consistent;
	}

	/**
	 * Running time: O(1) TODO: This is a relic from before generics were
	 * implemented correctly. Can be safely removed.
	 * */
	protected TVertex checkNode(TVertex v) throws InvalidPositionException {
		return v;
	}

	/**
	 * Returns an iterator over the children of a given node. Running time: O(1)
	 */
	public Iterable<TVertex> children(TVertex v)
	throws InvalidPositionException {
		TVertex n = checkNode(v);
		ArrayList<TVertex> children = new ArrayList<TVertex>();
		if (hasLeft(n)) {
			children.add(n);
		}
		if (hasRight(n)) {
			children.add(n);
		}
		return children;
	}

	public ArrayList<TVertex> depthFirst() {
		ArrayList<TVertex> dfs = new ArrayList<TVertex>();
		dfs.add(this.root);
		for (int i = 0; i < dfs.size(); i++) {
			TVertex node = dfs.get(i);
			if (hasLeft(node)) {
				// assert !dfs.contains(left(root));
				dfs.add(left(node));
			}
			if (hasRight(node)) {
				// assert !dfs.contains(right(root));
				dfs.add(right(node));
			}
		}
		return dfs;
	}

	/* TODO */
	public void fixId() {

	}

	/** Returns true if given node has a left child. Running time: O(1) */
	public boolean hasLeft(TVertex n) throws InvalidPositionException {
		return n.getLeft() != null;
	}

	/** Returns true if given node has a left child. Running time: O(1) */
	public boolean hasParent(TVertex n) throws InvalidPositionException {
		assert n.parent == null == isRoot(n);
		return n.parent != null;
	}

	/** Returns true if given node has a right child. Running time: O(1) */
	public boolean hasRight(TVertex n) throws InvalidPositionException {
		return n.getRight() != null;
	}

	/** Method is not supported */
	@Override
	public Edge<TVertex, V, E> insertEdge(TVertex a, TVertex b, E elem) {
		throw new UnsupportedOperationException();
	}

	/** Returns true if the tree has no root. Running time: O(1) */
	@Override
	public boolean isEmpty() {
		return this.root == null;
	}

	/** Returns true if the given vertex is an external node. Running time: O(1) */
	public boolean isExternal(TVertex v) throws InvalidPositionException {
		TVertex n = checkNode(v);
		return !hasLeft(n) && !hasRight(n);
	}

	/** Returns true if the given vertex is an internal node. Running time: O(1) */
	public boolean isInternal(TVertex v) throws InvalidPositionException {
		return !isExternal(v);
	}

	/** Returns true if the given vertex is the root node. Running time: O(1) */
	public boolean isRoot(TVertex v) throws InvalidPositionException {
		TVertex n = checkNode(v);
		return n == root();
	}

	/** Returns the left child of the given node. Running time: O(1) */
	public TVertex left(TVertex p) throws InvalidPositionException,
	BoundaryViolationException {
		if (!hasLeft(p)) {
			throw new BoundaryViolationException("No left child");
		}
		return p.getLeft();
	}

	/** Returns the parent of a given node. Running time: O(1) */
	public TVertex parent(TVertex v) throws InvalidPositionException,
	BoundaryViolationException {
		TVertex n = checkNode(v);
		if (isRoot(n)) {
			throw new BoundaryViolationException("No parent");
		}
		return n.getParent();
	}

	/**
	 * Removes given node if it is external. Running time: O(n)
	 */
	@Override
	public boolean remove(TVertex n) throws InvalidPositionException {
		boolean removed = false;
		if (isInternal(n)) {
			throw new InvalidPositionException(
			"Cannot remove node with children");
		}
		if (isRoot(n)) {
			removed = remove(n);
			this.root = null;
		} else {
			TVertex p = parent(n);
			if (hasLeft(p) && left(p) == n) {
				removed = remove(n);
				p.setLeft(null);
			}
			if (hasRight(p) && right(p) == n) {
				removed = remove(n);
				p.setRight(null);
			}
		}
		return removed;
	}

	public boolean remove(TVertex n, boolean removeInternal)
	throws InvalidPositionException {
		System.out.printf("removing: %s\n", n);
		if (isInternal(n) && removeInternal) {
			if (hasLeft(n)) {
				remove(left(n), true);
				System.out.println("removed left");
			}
			if (hasRight(n)) {
				remove(right(n), true);
				System.out.println("removed right");
			}
		}
		System.out.printf("vList: %s\n", this.vList);
		System.out.printf("eList: %s\n", this.eList);
		System.out.printf("aList: %s\n", this.adjacencyList);
		return remove(n);
	}

	// TODO: gave up on debugging this method.
	@SuppressWarnings("all")
	public void replace(TVertex split, TVertex replacement) {

		if (true) {
			throw new RuntimeException("not debugged");
		}

		System.out.printf("\nreplacing %s with %s\n", split, replacement);
		assert checkConsistency();
		fixIds(this.vList);
		fixIds(this.eList);
		assert this.vList.contains(split);
		if (hasLeft(split)) {
			assert this.vList.contains(left(split));
		}
		if (hasRight(split)) {
			assert this.vList.contains(right(split));
		}

		// remember where to put back in
		TVertex parent = split.getParent();
		Boolean wasLeftChild = null;
		if (parent != null) {
			wasLeftChild = parent.getLeft() == split;
			if (!wasLeftChild) {
				assert parent.getRight() == split : String
				.format(
						"\nsplit(%s) was not a child of its parent(%s,l:%s,r:%s)",
						split, parent, parent.getLeft(), parent
						.getRight());
			}
		}

		// remove old
		remove(split, true);

		// insert new
		TVertex invariantl = replacement.getLeft();
		TVertex invariantr = replacement.getRight();
		if (parent == null) {
			assert isRoot(this.root);
			replacement.setParent(null);
			System.out.println("addRoot");
			addRoot(replacement);
			assert this.root == replacement;
			assert invariantl == replacement.getLeft();
			assert invariantr == replacement.getRight();
		} else {
			// if split was a left child of its parent, replace left child of
			// the parent..
			if (wasLeftChild) {
				addLeft(parent, replacement);
				assert replacement.getParent() == parent;
				assert invariantl == replacement.getLeft();
				assert invariantr == replacement.getRight();
				// ..and vice versa
			} else {
				addRight(parent, replacement);
				assert replacement.getParent() == parent;
				assert invariantl == replacement.getLeft();
				assert invariantr == replacement.getRight();
			}
		}
		assert invariantl == replacement.getLeft();
		assert invariantr == replacement.getRight();
		System.out.printf("v=%s,l=%s,r=%s\n", replacement, replacement
				.getLeft(), replacement.getRight());
		addChildren(replacement);
		System.out.printf("vList: %s\n", this.vList);
		System.out.printf("eList: %s\n", this.eList);
		System.out.printf("aList: %s\n", this.adjacencyList);
		System.out.printf("split: %s, isRoot=%s\n", split, isRoot(split));
		assert checkConsistency();
	}

	/** Returns the right child of a given node. Running time: O(1) */
	public TVertex right(TVertex p) throws InvalidPositionException,
	BoundaryViolationException {
		if (!hasRight(p)) {
			throw new BoundaryViolationException("No right child");
		}
		return p.getRight();
	}

	/** Returns the root node. Running time: O(1) */
	public TVertex root() throws EmptyTreeException {
		if (isEmpty()) {
			throw new EmptyTreeException("The tree is empty");
		}
		return this.root;
	}

	/** Sets the given node as the root. Running time: O(1) */
	public void setRoot(TVertex r) throws InvalidPositionException {
		if (isEmpty()) {
			insertVertex(r);
		}
		r.setId(0);
		r.setLeft(this.root.getLeft());
		r.setParent(null);
		r.setRight(this.root.getRight());
		this.root = r;
	}

	/** Returns the sibling of a given node. Running time: O(1) */
	public TVertex sibling(TVertex n) throws InvalidPositionException {
		if (isRoot(n)) {
			throw new InvalidPositionException("has no sibling");
		}
		TVertex p = parent(n);
		if (hasLeft(p) && hasRight(p)) {
			TVertex sibNode = left(p);
			if (sibNode == n) {
				sibNode = right(p);
			}
			return sibNode;
		}
		throw new InvalidPositionException("has no sibling");
	}

	/** Returns the number of nodes in the tree */
	@Override
	public int size() {
		return numVertices();
	}

	/** Running time: O(n) */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Tree size: " + this.size() + "\n");
		for (TVertex n : this.vertices()) {
			// if(n==null) continue;
			if (isRoot(n)) {
				/*
				 * if (n.element() == null) buf.append("Root:" + n.id() +
				 * ", - \n"); else buf.append("Root:" + n.id() + ", " +
				 * n.element() + "\n");
				 */
				buf.append(n + "\n");
			} else {
				buf.append(n + ", parent=" + parent(n).id() + "\n");
			}
			/*
			 * if (n.element() == null) { buf.append("TreeNode:" + n.id() +
			 * ", - , Parent:" + parent(n).id() + "\n");
			 * 
			 * continue; } if (n.element() != null && parent(n) != null)
			 * buf.append(n + "\n"); buf.append("TreeNode:" + n.id() + "," +
			 * print(n.element()) + ", Parent:" + parent(n).id() + "\n");
			 */
		}
		return buf.toString();
	}

	// //TODO: isn't this just the default for iterables? remove?
	// private String print(N element) {
	// String s = "";
	// if (element instanceof Iterable<?>) {
	// for (Object o : ((Iterable<?>) element)) {
	// s += ", " + o.toString();
	// }
	// s = "[ " + s + " ]";
	// } else {
	// s = " " + element.toString();
	// }
	// return s;
	// }
}
