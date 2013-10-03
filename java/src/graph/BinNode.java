package graph;

import interfaces.IVertexFactory;

/** Representation of a node used by a binary tree */

public abstract class BinNode<TNode extends BinNode<TNode, V>, V> extends
Vertex<V> {


    // Just for serialization
    @Deprecated
    public BinNode() {

    }

	protected TNode left, right, parent;

	/** Default generics parameterization **/
	public static final class D<V> extends BinNode<D<V>, V> {

		public class Factory<N> implements IVertexFactory<BinNode.D<N>, N> {
			@Override
			public BinNode.D<N> createNew(N element, int id) {
				return new BinNode.D<N>(element, id);
			}
		}

		public D(V element, int id) {
			super(element, id);
		}

	}

	public BinNode(V element, int id) {
		super(element, id);
		setLeft(null);
		setRight(null);
		setParent(null);
	}

	public TNode getLeft() {
		return this.left;
	}

	public TNode getParent() {
		return this.parent;
	}

	public TNode getRight() {
		return this.right;
	}

	public void setLeft(TNode left) {
		this.left = left;
	}

	public void setParent(TNode parent) {
		// System.out.printf("%s.setParent(%s)\n", this, parent);
		this.parent = parent;
	}

	public void setRight(TNode right) {
		this.right = right;
	}
}