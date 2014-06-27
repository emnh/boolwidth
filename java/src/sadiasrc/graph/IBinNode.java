package sadiasrc.graph;

public interface IBinNode<TNode extends IBinNode<TNode>> extends IVertex {

	/** Representation of a node used by a binary tree */

	public TNode getLeft();

	public TNode getParent();

	public TNode getRight();

	public boolean hasLeft();

	public boolean hasRight();
	
	public boolean hasParent();

	public void setLeft(TNode left);

	public void setParent(TNode parent);

	public void setRight(TNode right);
}
