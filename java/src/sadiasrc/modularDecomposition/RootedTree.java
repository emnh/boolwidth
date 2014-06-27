package sadiasrc.modularDecomposition;

/* 
 * A rooted tree.
 */
class RootedTree {

	// The root of the tree.
	private RootedTreeNode root;
	
	
	/* The default constructor. */
	protected RootedTree() {
		root = null;
	}
	
	
	/* 
	 * Creates a rooted tree with the given root.
	 * @param r The root of the newly created tree.
	 */ 
	protected RootedTree(RootedTreeNode r) {
		root = r; 
	}	
	
	
	/*
	 * Resets the root of this tree to be the node supplied.
	 * Effectively changes this tree to the one rooted at the supplied node. 
	 * @param r The new root of this tree.
	 */
	protected void setRoot(RootedTreeNode r) {
		root = r;
	}
	
	
	/* 
	 * Returns a string representation of this tree.
	 * @return The string representation of this tree.
	 */
	public String toString() {
		return root.toString();
	}
}

