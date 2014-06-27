package sadiasrc.modularDecomposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/*
 * A node in a rooted tree.
 */
class RootedTreeNode {

	// The parent of this node.
	private RootedTreeNode parent;
	
	// The first child of this node.
	private RootedTreeNode firstChild;
	
	// This node's sibling to its left.
	private RootedTreeNode leftSibling;
	
	// This node's sibling to its right.
	private RootedTreeNode rightSibling;
	
	// The number of this node's children.
	private int numChildren;

	//List of vertices below
	private ArrayList<Vertex> part;
	
	/* The default constructor. */
	protected RootedTreeNode() {
		parent = null;
		firstChild = null;
		leftSibling = null;
		rightSibling = null;
		numChildren = 0;
		part= new ArrayList<Vertex>();
	}
		
	/* 
	 * Creates a node with a single child.
	 * @param child The child of this node.
	 */ 
	protected void addChild(RootedTreeNode child) {
		child.remove();
		if (firstChild != null) {
			firstChild.leftSibling = child;
			child.rightSibling = firstChild;
		}
		
		firstChild = child;
		child.parent = this;
		numChildren++;
	}
	
	/* Returns the number of this node's children. */
	protected int getNumChildren() {
		return numChildren;
	}
	
	
	/* Returns true iff this node has no children. */
	protected boolean hasNoChildren() {
		return (numChildren == 0);
	}
	
	
	/* Returns true iff this node has a single child. */ 
	protected boolean hasOnlyOneChild() {
		return (numChildren == 1);
	}
	
	
	/* 
	 * Replaces this node in its tree with the supplied node.  This node's
	 * takes its children with it when it is removed.  That is, the supplied
	 * node does not assume its children.  Thus, this method might be better
	 * referred to as 'replaceSubtreeWith'.
	 * @param replacement The node to replace this one in the tree.
	 */
	protected void replaceWith(RootedTreeNode replacement) {
		replacement.remove();
		replacement.leftSibling = leftSibling;
		replacement.rightSibling = rightSibling;
		if (leftSibling != null) {leftSibling.rightSibling = replacement; }
		if (rightSibling != null) {rightSibling.leftSibling = replacement; }
		replacement.parent = parent;
		if (parent != null && parent.firstChild == this) { 
			parent.firstChild = replacement; 
		}
		parent = null;
		leftSibling = null;
		rightSibling = null;		
	}
	
	/* 
	 * Removes this node from its tree.  The node takes its children with it
	 * as it is removed, and so this method might be better called 'removeSubtree'.
	 */
	protected void remove() {
		if (parent != null) { parent.numChildren--; }
		if (leftSibling != null) { leftSibling.rightSibling = rightSibling; }
		if (rightSibling != null) { rightSibling.leftSibling = leftSibling; }
		if (parent != null && parent.firstChild == this) { 
			parent.firstChild = rightSibling; 
		}
		parent = null;
		leftSibling = null;
		rightSibling = null;
	}
	
	
	/*
	 * Insert supplied node as the left sibling of this node.
	 * @param justBefore The node to be made this node's left sibling.
	 */
	protected void insertBefore(RootedTreeNode justBefore) {
		remove();
		leftSibling = justBefore.leftSibling;
		if (justBefore.leftSibling != null) { 
			justBefore.leftSibling.rightSibling = this; 
		}
		
		rightSibling = justBefore;
		justBefore.leftSibling = this;
		
		parent = justBefore.parent;
		
		if (justBefore.parent != null) {
			justBefore.parent.numChildren++;
		}
		
		if (justBefore.parent != null && 
				justBefore.parent.firstChild == justBefore) { 
			parent.firstChild = this; 
		}
		
				
	}
	
	
	/*
	 * Insert supplied node as the right sibling of this node.
	 * @param justAfter The node to be made this node's right sibling.
	 */
	protected void insertAfter(RootedTreeNode justAfter) {
		remove();
		
		rightSibling = justAfter.rightSibling;
		if (justAfter.rightSibling != null) {
			justAfter.rightSibling.leftSibling = this;
		}
		
		leftSibling = justAfter;
		justAfter.rightSibling = this;
		
		parent = justAfter.parent;
		
		if (justAfter.parent != null) {
			justAfter.parent.numChildren++;
		}
	}
	
	
	/*
	 * Moves this node to be the first amongst all its siblings, and thus
	 * its parent's first child.
	 */
	protected void makeFirstChild() {
		
		if (parent.firstChild == this) { return; }
		
		RootedTreeNode newRightSibling = parent.firstChild;
		remove();
		insertBefore(newRightSibling);		
	}

	
	/* Returns the parent of this node. */
	protected RootedTreeNode getParent() {
		return parent;
	}


	/* Returns the first child of this node. */
	protected RootedTreeNode getFirstChild() {
		return firstChild;
	}
	

	/* Returns the left sibling of this node. */
	protected RootedTreeNode getLeftSibling() {
		return leftSibling;
	}


	/* Returns the right sibling of this node. */
	protected RootedTreeNode getRightSibling() {
		return rightSibling;
	}

	
	/* Returns a collection of the leaves of the subtree rooted at this node. */
	protected Collection<RootedTreeNode> getLeaves() {
		
		LinkedList<RootedTreeNode> leaves = new LinkedList<RootedTreeNode>();
		
		if (isALeaf()) { leaves.add(this); }
		else {
			RootedTreeNode currentChild = firstChild;
			while(currentChild != null) {
				leaves.addAll(currentChild.getLeaves());
				currentChild = currentChild.rightSibling;
			}
		}
		return leaves;
	}

	
	/* Returns true iff this node is a leaf, i.e. it has no children. */
	protected boolean isALeaf() {
		return hasNoChildren();
	}
	
	
	/*
	 * Adds as children the children from the supplied node.
	 * @param from The node whose children are to be added to this node's children.
	 */
	protected void addChildrenFrom(RootedTreeNode from) {
		RootedTreeNode currentChild = from.firstChild;
		while (currentChild != null) {
			RootedTreeNode nextChild = currentChild.rightSibling;
			addChild(currentChild);
			currentChild = nextChild;
		}
	}
	
	
	/* 
	 * Replaces this node in its subtree by its children.  That is, the children
	 * of this node are made to become children of this node's parent, and this
	 * node is removed from its tree.
	 */
	protected void replaceThisByItsChildren() {
		
		RootedTreeNode currentChild = getFirstChild();
		while (currentChild != null) {
			RootedTreeNode nextChild = currentChild.getRightSibling();
			currentChild.insertBefore(this);
			currentChild = nextChild;
		}
		this.remove();
	}
	
	
	/*
	 * Replaces the children of this node with the node supplied.  The
	 * children are removed from this node's tree.
	 * @param replacement This node's new child.
	 */
	protected void replaceChildrenWith(RootedTreeNode replacement) {
		RootedTreeNode currentChild = getFirstChild();
		while (currentChild != null) {
			RootedTreeNode nextChild = currentChild.getRightSibling();
			currentChild.remove();
			currentChild = nextChild;
		}
		addChild(replacement);
	}

	
	/* Returns true iff this node is the root of a tree. */ 
	protected boolean isRoot() {
		return (parent == null);
	}
	
	
	/*
	 * Returns a string representation of the subtree rooted at this node.
	 * The subtree is enclosed in brackets, the number of this node's children
	 * is supplied, followed by the representations of its children's subtrees
	 * in order.
	 * @return The string representation of the subtree rooted at this node.
	 */
	public String toString() {
		String result = "(numChildren=" + numChildren + " ";
		RootedTreeNode currentChild = firstChild;
		if (currentChild != null) { 
			result += currentChild;
			currentChild = currentChild.rightSibling;
		}
		while (currentChild != null) {
			result += ", " + currentChild;
			currentChild = currentChild.rightSibling;
		}
		return result + ")";
	}
}
