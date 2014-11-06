package graph;

import exceptions.BoundaryViolationException;
import exceptions.EmptyTreeException;
import exceptions.InvalidPositionException;
import exceptions.NonEmptyTreeException;
import interfaces.IBinaryTree;
import interfaces.IGraph;
import interfaces.IVertexFactory;

import java.util.*;

/**
 * A realization of a binary tree, _not_ as an extension of an adjacency list
 * graph
 */

public class CleanBinaryTree<TVertex extends BinNode<TVertex, V>, V, E> extends
		AbstractSet<TVertex> implements IBinaryTree<TVertex, V, E> {

    // For serialization only
    @Deprecated
    public CleanBinaryTree()
    {
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public IGraph<TVertex, V, E> copy() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected TVertex root = null;

	protected IVertexFactory<TVertex, V> vertexFactory;

	private static int nodecounter = 0;

	// TODO: make decorator
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();

	/** Creates an empty binary tree */
	public CleanBinaryTree(IVertexFactory<TVertex, V> factory) {
		this.vertexFactory = factory;
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

		newParent.setParent(parent);
		if (isLeft) {
			parent.setLeft(newParent);
		} else {
			parent.setRight(newParent);
		}

		vertex.setParent(newParent);
		newParent.setLeft(vertex);

		TVertex newNode = createVertex(element, size());
		newNode.setParent(newParent);
		newParent.setRight(newNode);
	}

	protected TVertex addLeft(TVertex p, TVertex l)
			throws InvalidPositionException {
		if (hasLeft(p)) {
			throw new InvalidPositionException("Node already has a left child");
		}
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
		if (hasRight(p)) {
			throw new InvalidPositionException("Node already has a right child");
		}
		r.setParent(p);
		p.setRight(r);
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

	@Override
	public boolean areAdjacent(TVertex u, TVertex v)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.areAdjacent is not yet implemented");
		// return false;
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

	public ArrayList<TVertex> breadthFirst(TVertex top) {
		ArrayList<TVertex> bfs = new ArrayList<TVertex>();
		bfs.add(top);
		for (int i = 0; i < bfs.size(); i++) {
			TVertex node = bfs.get(i);
			if (hasLeft(node)) {
				// assert !dfs.contains(left(root));
				bfs.add(left(node));
			}
			if (hasRight(node)) {
				// assert !dfs.contains(right(root));
				bfs.add(right(node));
			}
		}
		return bfs;
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

	/**
	 * Create new vertex
	 * 
	 * @param v
	 *            vertex element
	 * @param id
	 * @return
	 */
	public TVertex createVertex(V v, int id) {
		return this.vertexFactory.createNew(v, id);
	}

	public ArrayList<TVertex> depthFirst() {
		return depthFirst(this.root);
	}

	public ArrayList<TVertex> depthFirst(TVertex top) {
		ArrayList<TVertex> dfs = new ArrayList<TVertex>();
		dfs.add(top);
		depthFirst(top, dfs);
		return dfs;
	}

	protected void depthFirst(TVertex top, ArrayList<TVertex> dfs) {
		if (hasLeft(top)) {
			dfs.add(left(top));
			depthFirst(left(top), dfs);
		}
		if (hasRight(top)) {
			dfs.add(right(top));
			depthFirst(right(top), dfs);
		}
	}

	@Override
	public Iterable<E> edgeElements() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.edgeElements is not yet implemented");
		// return null;
	}

	@Override
	public Iterable<Edge<TVertex, V, E>> edges() {
		ArrayList<Edge<TVertex, V, E>> edges = new ArrayList<Edge<TVertex, V, E>>();
		int i = 0;
		for (TVertex v : vertices()) {
			if (hasLeft(v)) {
				edges.add(new Edge<TVertex, V, E>(null, v, left(v), i++));
			}
			if (hasRight(v)) {
				edges.add(new Edge<TVertex, V, E>(null, v, right(v), i++));
			}
		}
		return edges;
	}

	@Override
	public ArrayList<TVertex> endVertices(Edge<TVertex, V, E> e)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.endVertices is not yet implemented");
		// return null;
	}

	/**
	 * Just for pretty printing purposes
	 */
	public void fixIds() {
		int i = 0;
		for (TVertex v : breadthFirst(this.root)) {
			v.setId(i++);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttr(String key) {
		if (this.attributes.containsKey(key)) {
			return (T) this.attributes.get(key);
		} else {
			// TODO: get default from configuration
			return null;
		}
	}

	@Override
	public int getId(TVertex v) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IPosSet<TVertex>.getId is not yet implemented");
		// return 0;
	}

	public int getNextID() {
		// we're not using the IDs in binary tree, but give some ID just for
		// prettier printing
		return nodecounter++;
	}

	@Override
	public TVertex getVertex(int i) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IPosSet<TVertex>.getVertex is not yet implemented");
		// return null;
	}

	@Override
	public boolean hasAttr(String key) {
		return this.attributes.containsKey(key);
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

	@Override
	public Iterable<Edge<TVertex, V, E>> incidentEdges(TVertex v)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.incidentEdges is not yet implemented");
		// return null;
	}

	/** Method is not supported */
	public Edge<TVertex, V, E> insertEdge(TVertex a, TVertex b, E elem) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TVertex insertVertex(V o) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.insertVertex is not yet implemented");
		// return null;
	}

	@Override
	public int[] intBitsAdjacencyMatrix() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.intBitsAdjacencyMatrix is not yet implemented");
		// return null;
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

	// TODO: figure out what to do with this.
	// it's here just to maintain compatibility with Decomposition
	// Decomposition should be fixed to not use this method
	// protected TVertex insertVertex(TVertex o) {
	// return o;
	// // throw new UnsupportedOperationException(
	// // "The method IGraph<TVertex,V,E>.insertVertex is not yet implemented");
	// // return null;
	// }

	@Override
	public Iterator<TVertex> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method AbstractCollection<TVertex>.iterator is not yet implemented");
		// return null;
	}

	/** Returns the left child of the given node. Running time: O(1) */
	public TVertex left(TVertex p) throws InvalidPositionException,
			BoundaryViolationException {
		if (!hasLeft(p)) {
			throw new BoundaryViolationException("No left child");
		}
		return p.getLeft();
	}

	@Override
	public int numEdges() {
		return numVertices() - 1;
	}

	@Override
	public int numVertices() {
		return depthFirst().size();
	}

	@Override
	public TVertex opposite(TVertex v, Edge<TVertex, V, E> e)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.opposite is not yet implemented");
		// return null;
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
	 * Removes given node if it is external. Returns the element of the removed
	 * node. Running time: O(n)
	 */
	public boolean remove(TVertex n) throws InvalidPositionException {
		boolean removed = false;
		if (isRoot(n)) {
			this.root = null;
			removed = true;
		} else {
			TVertex p = parent(n);
			if (hasLeft(p) && left(p) == n) {
				p.setLeft(null);
				removed = true;
			}
			if (hasRight(p) && right(p) == n) {
				p.setRight(null);
				removed = true;
			}
		}
		return removed;
	}

	@Override
	public E removeEdge(Edge<TVertex, V, E> e) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.removeEdge is not yet implemented");
		// return null;
	}

	@Override
	public V removeVertex(TVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.removeVertex is not yet implemented");
		// return null;
	}

	@Override
	public E replace(Edge<TVertex, V, E> p, E o)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.replace is not yet implemented");
		// return null;
	}

	public void replace(TVertex n, TVertex replacement) {
		boolean replaced = false;
		if (isRoot(n)) {
			replacement.setParent(null);
			this.root = replacement;
			replaced = true;
		} else {
			TVertex p = parent(n);
			if (hasLeft(p) && left(p) == n) {
				p.setLeft(replacement);
				replacement.setParent(p);
				replaced = true;
			}
			if (hasRight(p) && right(p) == n) {
				p.setRight(replacement);
				replacement.setParent(p);
				replaced = true;
			}
		}
		assert replaced;
	}

	@Override
	public V replace(TVertex p, V o) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.replace is not yet implemented");
		// return null;
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

	@Override
	// TODO: accept configuration with types and defaults
	public void setAttr(String key, Object value) {
		this.attributes.put(key, value);
	}

	/** Sets the given node as the root. Running time: O(1) */
	public void setRoot(TVertex r) throws InvalidPositionException {
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

	@Override
	public String toDimacs() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.toDimacs is not yet implemented");
		// return null;
	}

	@Override
	public String toGraphViz(String label) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.toGraphViz is not yet implemented");
		// return null;
	}

	/** Running time: O(n) */
	@Override
	public String toString() {
		fixIds();
		StringBuffer buf = new StringBuffer();
		buf.append("Tree size: " + this.size() + "\n");
		for (TVertex n : breadthFirst(this.root)) {
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

	@Override
	public Iterable<V> vertexElements() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method IGraph<TVertex,V,E>.vertexElements is not yet implemented");
		// return null;
	}

	@Override
	public Iterable<TVertex> vertices() {
		return depthFirst();
	}

    @Override
    public Collection<TVertex> incidentVertices(TVertex v) throws InvalidPositionException {
        throw new UnsupportedOperationException(
                "The method IGraph<TVertex,V,E>.incidentVertices is not yet implemented");
    }

    @Override
    public int degree(TVertex v) {
        throw new UnsupportedOperationException();
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
