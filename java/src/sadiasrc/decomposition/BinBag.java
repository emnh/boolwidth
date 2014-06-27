package sadiasrc.decomposition;

import sadiasrc.graph.IGraphElement;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexGraphSubSet;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.IBinNode;

/**
 * @author MaVa
 *
 * @param <V> The type of bags so that we can extend
 */
public class BinBag<V extends BinBag<V>> 
		extends Bag<IndexVertex, IndexEdge<IndexVertex>> 
		implements IBinNode<V> {  
	protected V left;
	protected V right;
	protected V parent;

	
	IndexGraphSubSet gss;

	public BinBag(BinaryDecomposition owner, int index) {
		super(owner, index);
		this.gss = new IndexGraphSubSet(owner.graph());
	}

	@Override
	public IndexGraphSubSet getGraphSubSet() {
		return gss;
	}

	@Override
	public V getLeft() {
		return this.left;
	}

	@Override
	public V getParent() {
		return this.parent;
	}

	@Override
	public V getRight() {
		return this.right;
	}

	@Override
	public boolean hasLeft() {
		return this.left != null;
	}

	@Override
	public boolean hasRight() {
		return this.right != null;
	}

	@Override
	public void setLeft(V left) {
		this.left = left;
	}

	@Override
	public void setParent(V parent) {
		this.parent = parent;
	}

	@Override
	public void setRight(V right) {
		this.right = right;
	}

	@Override
	public boolean hasParent() {		
		return this.parent!=null;
	}
}
