package sadiasrc.decomposition;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.BinaryTree;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexGraphSubSet;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.IBinaryTree;

import java.util.ArrayList;
import java.util.Collection;

import sadiasrc.exceptions.InvalidPositionException;

/**
 * @author MaVa A binary decomposition of a IndexGraph. Subsets of siblings are
 *         not induced, and may overlap.
 */
public abstract class BinaryDecomposition 
		extends BinaryTree<DecompNode,IndexEdge<DecompNode>>
		implements IDecomposition<DecompNode, IndexEdge<DecompNode>, IndexVertex, IndexEdge<IndexVertex>, IndexGraph> {

	//the graph we are decomposing
	protected IndexGraph graph;

	//adds all vertices that can be added, 
	//returns true if some vertices were added, false otherwise
	public boolean addAll(DecompNode b, Iterable<IndexVertex> iter) {
		boolean change = false;
		for (IndexVertex v : iter) {
			change |= addVertex(b, v);
		}
		return change;
	}

	public DecompNode addChild(DecompNode parent) {
		DecompNode bb = newBag();
		addChild(bb, parent);
		return bb;
	}

	/*
	 * Returns true if the Bag can be added to the decomposition.
	 */
	public boolean addChild(DecompNode child, DecompNode parent) {
		if (!hasLeft(parent)) {
			return addLeft(parent, child);
		}
		return addRight(parent, child);
	}

	public boolean addLeft(DecompNode p, DecompNode child)
			throws InvalidPositionException {
		for(IndexVertex v : child.getGraphSubSet().vertices())
		{
			if (!canAddLeft(p, v)) 
				return false;
		}

		return super.addLeft(p, child, new IndexEdge<DecompNode>(this, this.eList
				.size(), p, child));
	}

	public boolean addLeft(DecompNode p, IndexVertex v)
			throws InvalidPositionException {
		if(p==null)
			return false;

		if (hasLeft(p)) {
			return addVertex(left(p), v);
		}
		if(canAddLeft(p, v))
		{
			DecompNode bb = newBag();
			bb.getGraphSubSet().add(v);
			return addLeft(p, bb);
		}
		else return false;
	}


	public boolean addRight(DecompNode p, DecompNode child)
			throws InvalidPositionException {
		for(IndexVertex v : child.getGraphSubSet().vertices())
		{
			if (!canAddRight(p, v)) 
				return false;
		}

		return super.addRight(p, child, new IndexEdge<DecompNode>(this,
			this.eList.size(), p, child));		
	}

	public boolean addRight(DecompNode p, IndexVertex v)
			throws InvalidPositionException {
		if(p==null)
			return false;
		if (hasRight(p)) {
			return addVertex(right(p), v);
		}
		if(canAddRight(p, v))
		{	DecompNode bb = newBag();
			bb.getGraphSubSet().add(v);
			return addRight(p, bb);
		}
		return false;
	}

	public boolean addLeft(DecompNode p, Iterable<IndexVertex> set)
	{
		boolean change = false;
		for(IndexVertex v : set)
			change |= addLeft(p, v);
		return change;
	}

	public boolean addRight(DecompNode p, Iterable<IndexVertex> set)
	{
		boolean change = false;
		for(IndexVertex v : set)
			change |= addRight(p, v);
		return change;
	}
	

	public boolean addVertex(DecompNode bag, IndexVertex v) {
		if (canAddVertex(bag, v)) {
			while(!bag.getGraphSubSet().contains(v))
			{
				bag.getGraphSubSet().add(v);
				bag = parent(bag);
			}
			return true;
		}
		return false;
	}

	private IndexGraphSubSet allOther(DecompNode root, DecompNode child) {
		IndexGraphSubSet ss = new IndexGraphSubSet(this.graph);
		if (hasLeft(root) && hasRight(root)) {
			ss.addAll(getSubSet(this.sibling(child)));
		}
		if (!isRoot(root)) {
			ss.addAll(allOther(parent(root), root));
		}
		return ss;
	}

	@Override
	public void attach(DecompNode root, IBinaryTree<DecompNode, IndexEdge<DecompNode>> t1,
			IBinaryTree<DecompNode, IndexEdge<DecompNode>> t2)
			throws InvalidPositionException {
		System.out.println("Unimplemented method used");
		//TODO: don't know how this will work yet
	}

	protected abstract boolean canAddLeft(DecompNode p, IndexVertex v);

	protected abstract boolean canAddRight(DecompNode p, IndexVertex v);

	protected abstract boolean canAddVertex(DecompNode bag, IndexVertex v);

	public IndexGraphSubSet getConflitingPart(DecompNode root) {
		IndexGraphSubSet cur = getSubSet(root);
		IndexGraphSubSet con = allOther(parent(root), root);
		con.retainAll(cur);// now other contains all vertices both in this
		// subtree and some other.
		for (IndexEdge<IndexVertex> e : cur.edges()) {
			ArrayList<IndexVertex> vs = e.endVertices();
			if (cur.contains(e)) {
				if (!cur.contains(vs.get(0)) || !cur.contains(vs.get(1))) {
					con.addEdge(e);
				}
			}
		}
		return con;
	}

//	// get graph subset already in the tree
//	public IndexGraphSubSet getSS(DecompNode root) {
//		IndexGraphSubSet ss = new IndexGraphSubSet(this.graph());
//		ss.addAll(root.getGraphSubSet());
//		if (root.hasLeft()) {
//			ss.addAll(getSS(root.getLeft()));
//		}
//		if (root.hasRight()) {
//			ss.addAll(getSS(root.getRight()));
//		}
//		return ss;
//	}

	/*
	 * (non-Javadoc)
	 *
	 * @see interfaces.IDecomposition#getSubSet(interfaces.IVertex)
	 */
	public IndexGraphSubSet getSubSet(DecompNode node)
			throws InvalidPositionException {
		return node.getGraphSubSet();
	}

	@Override
	public IndexGraph graph() {
		return this.graph;
	}

	protected DecompNode newBag() {
		return new DecompNode(this,size());
	}
	protected IndexEdge<DecompNode> newEdge(DecompNode a,DecompNode b) {
		return new IndexEdge<DecompNode>(this, numEdges(), a, b);
	}

	public boolean addRoot() {
		return super.addRoot(newBag());
	}
	
	public boolean addLeft(DecompNode p) throws InvalidPositionException {
		DecompNode child = newBag();
		return super.addLeft(p, child, newEdge(p,child));
	}
	public boolean addRight(DecompNode p) throws InvalidPositionException {
		DecompNode child = newBag();
		return super.addRight(p, child, newEdge(p,child));
	}
	
	public String toGraphViz(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return A bigraph where the left side contains all vertices of the given
	 *         DNode, and the right side contains the rest of the vertices in
	 *         the graph.
	 */
	public BiGraph getCut(DecompNode dn) {
		return new BiGraph((Collection<IndexVertex>) getSubSet(dn).vertices(), this.graph);
	}
}
