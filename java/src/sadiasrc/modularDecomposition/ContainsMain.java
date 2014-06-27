package sadiasrc.modularDecomposition;

/* 
 * A wrapper for main demonstrating the construction of the MD 
 * tree for a graph encoded in a file supplied as the first command
 * line argument.  See 'Graph.java' for the input file format for graphs.
 */
public class ContainsMain {

	public static void main(String[] args) {

		Graph g = new Graph("g.txt");
		System.out.println(g.getMDTree());
	}
}
