package sadiasrc.modularDecomposition;

/*
 * A modular decomposition tree of a simple, undirected graph.
 */
class MDTree extends RootedTree {
		
	/*
	 * Creates the modular decomposition tree for the supplied graph.
	 */
	protected MDTree(Graph g) {
		super();
		setRoot(buildMDTree(g));			
	}
	
	
	/*
	 * Builds the modular decomposition tree for the supplied graph.
	 * @return The root of the constructed modular decomposition tree.
	 */
	private MDTreeNode buildMDTree(Graph g) {
		
		if (g.getNumVertices() == 0) { return null; }
		
		RecSubProblem entireProblem = new RecSubProblem(g);	
		
		MDTreeNode root = entireProblem.solve();		
		root.clearVisited();
		return root;			
	}
}
