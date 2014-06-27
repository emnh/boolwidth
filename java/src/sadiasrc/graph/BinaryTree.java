package sadiasrc.graph;

import sadiasrc.exceptions.BoundaryViolationException;
import sadiasrc.exceptions.EmptyTreeException;
import sadiasrc.exceptions.InvalidPositionException;

import java.util.ArrayList;
import java.util.Stack;

/** A realization of a binary tree as an extension of an adjacency list graph */

public abstract class BinaryTree<V extends IBinNode<V>, E extends IEdge<V>>
		extends AdjacencyListGraph<V, E> implements IBinaryTree<V, E> {

	protected V root = null;

	/** Creates an empty binary tree */
	public BinaryTree() {
		super();
	}

	@Override
	public boolean addChild(V child, V parent,E edge) {
		//TODO : make sure that nothing is changed if false is returned
		if(!contains(child))
			{
			 if(!super.insertVertex(child))
				 return false;
			}
		if(!contains(parent))
		{
			if(!super.insertVertex(parent))
				return false;
		}
		if(!isRoot(child) || (parent.hasLeft()&& parent.hasRight()))
			return false;
		
		if(!super.insertEdge(edge))
		{
			return false;
		}
		child.setParent(parent);
		if(parent.hasLeft())
			parent.setRight(child);
		else
			parent.setLeft(child);
		return true;
	}

	@Override
	public boolean addLeft(V p, V child, E e) throws InvalidPositionException {
		if (hasLeft(p)) {
			return false;
		}
		if (!super.insertVertex(child)) {
			return false;
		}
		p.setLeft(child);
		child.setParent(p);
		return super.insertEdge(e);
	}

	@Override
	public boolean addRight(V p, V child, E e) throws InvalidPositionException {
		if (hasRight(p)) {
			return false;
		}
		if (!super.insertVertex(child)) {
			return false;
		}
		p.setRight(child);
		child.setParent(p);
		return super.insertEdge(e);
	}

	/**
	 * Adds a new root node to an empty tree. Returns true if tree was empty.
	 * Running time: O(1)
	 */
	public boolean addRoot(V r) {
		if (!isEmpty()) {
			return false;
		}
		if (super.insertVertex(r)) {
			this.root = r;
			return true;
		}
		return false;
	}


	@Override
	public Iterable<V> children(V p) throws InvalidPositionException {
		ArrayList<V> children = new ArrayList<V>();
		if (hasLeft(p)) {
			children.add(left(p));
		}
		if (hasRight(p)) {
			children.add(right(p));
		}
		return children;
	}

	public ArrayList<V> depthFirst() {
		ArrayList<V> dfs = new ArrayList<V>();
		Stack<V> reached = new Stack<V>();
		reached.push(this.root);
		while (!reached.isEmpty()) {
			V node = reached.pop();
			dfs.add(node);
			if (hasRight(node)) {
				// assert !dfs.contains(right(root));
				reached.push(right(node));
			}
			if (hasLeft(node)) {
				// assert !dfs.contains(left(root));
				reached.push(left(node));
			}
		}
		return dfs;
	}

	/** Returns true if given node has a left child. Running time: O(1) */
	@Override
	public boolean hasLeft(V p) throws InvalidPositionException {
		return p.hasLeft();
	}

	/** Returns true if given node has a right child. Running time: O(1) */
	@Override
	public boolean hasRight(V p) throws InvalidPositionException {
		return p.hasRight();
	}
	
	public boolean hasParent(V v) throws InvalidPositionException {
		return !isRoot(v);
	};

	@Override
	public boolean insertEdge(E e) {
		throw new UnsupportedOperationException("Don't know how to add this edge such that this is still a tree");
	}

	public boolean insertVertex(V v) {
		if(isEmpty())
			return addRoot(v);
		else
			throw new UnsupportedOperationException("Don't know where to put the vertex such that this is still a tree");
	}
	
	/** Returns true if the tree has no root. Running time: O(1) */
	@Override
	public boolean isEmpty() {
		return this.root == null;
	}

	/** Returns true if the given vertex is an external node. Running time: O(1) */
	@Override
	public boolean isExternal(V v) throws InvalidPositionException {
		return !isInternal(v);
	}

	/** Returns true if the given vertex is an internal node. Running time: O(1) */
	@Override
	public boolean isInternal(V v) throws InvalidPositionException {
		return hasLeft(v) || hasRight(v);
	}

	/** Returns true if the given vertex is the root node. Running time: O(1) */
	@Override
	public boolean isRoot(V v) throws InvalidPositionException {
		return v == this.root;
	}

	/** Returns the left child of the given node. Running time: O(1) */

	@Override
	public V left(V p) throws InvalidPositionException,
			BoundaryViolationException {
		if (!hasLeft(p)) {
			throw new BoundaryViolationException("No left child");
		}
		return p.getLeft();
	}

	/** Returns the parent of a given node. Running time: O(1) */
	@Override
	public V parent(V v) throws InvalidPositionException,
			BoundaryViolationException {
		// V n = checkNode(v);
		if (isRoot(v)) {
			throw new BoundaryViolationException("No parent");
		}
		return v.getParent();
	}

	/**
	 * Removes given node if it is external. Running time: O(n)
	 */
	@Override
	public boolean remove(V n) throws InvalidPositionException {
		boolean removed = false;
		if (isInternal(n)) {
			throw new InvalidPositionException(
					"Cannot remove node with children");
		}
		if (isRoot(n)) {
			removed = remove(n);
			this.root = null;
		} else {
			V p = parent(n);
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

	public boolean remove(V n, boolean removeInternal)
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

	/** Returns the right child of a given node. Running time: O(1) */
	public V right(V p) throws InvalidPositionException,
			BoundaryViolationException {
		if (!hasRight(p)) {
			throw new BoundaryViolationException("No right child");
		}
		return p.getRight();
	}

	/** Returns the root node. Running time: O(1) */
	public V root() throws EmptyTreeException {
		if (isEmpty()) {
			throw new EmptyTreeException("The tree is empty");
		}
		return this.root;
	}

	@Override
	public Iterable<V> roots() {
		ArrayList<V> roots = new ArrayList<V>();
		if(!isEmpty())
			roots.add(root());
		return roots;
	}

	/** Sets the given node as the root. Running time: O(1) */
	public void setRoot(V r) throws InvalidPositionException {
		if (isEmpty()) {
			insertVertex(r);
		}
		r.setLeft(this.root.getLeft());
		r.setParent(null);
		r.setRight(this.root.getRight());
		this.root = r;
	}

	/** Returns the sibling of a given node. Running time: O(1) */
	public V sibling(V n) throws InvalidPositionException {
		if (isRoot(n)) {
			throw new InvalidPositionException("has no sibling");
		}
		V p = parent(n);
		if (hasLeft(p) && hasRight(p)) {
			V sibNode = left(p);
			if (sibNode == n) {
				sibNode = right(p);
			}
			return sibNode;
		}
		throw new InvalidPositionException("has no sibling");
	}

	/** Returns the number of nodes in the tree */
	public int size() {
		return numVertices();
	}

	/** Running time: O(n) */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Tree size: " + this.size() + "\n");
		for (V n : this.vertices()) {
			// if(n==null) continue;
			if (isRoot(n)) {
				/*
				 * if (n.element() == null) buf.append("Root:" + n.id() +
				 * ", - \n"); else buf.append("Root:" + n.id() + ", " +
				 * n.element() + "\n");
				 */
				buf.append(n + "\n");
			} else {
				buf.append(n + ", parent=" + parent(n).toString() + "\n");
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
