package sadiasrc.graph;


/** Representation of a node used by a binary tree */

public abstract class BinNode<TNode extends BinNode<TNode>> extends IndexVertex
		implements IBinNode<TNode> {

	protected TNode left, right, parent;

	public BinNode(IGraph<?, ?> owner, int index) {
		super(owner, index);
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

	public boolean hasLeft() {
		return this.left != null;
	}

	public boolean hasRight() {
		return this.left != null;
	}

	@Override
	public boolean hasParent() {
		return this.parent!=null;
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