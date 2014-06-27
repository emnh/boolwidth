package sadiasrc.modularDecomposition;

/*
 * The different types of nodes in a modular decomposition tree. 
 */
enum MDNodeType {
	PRIME,SERIES,PARALLEL;

	/* Returns true iff this type is degenerate. */
	public boolean isDegenerate() {
		return (this == PARALLEL || this == SERIES);
	}
}
