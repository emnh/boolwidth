import sadiasrc.graph.IndexGraph;


public class TestGrid {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int gridsize=4;
		IndexGraph G = IndexGraph.Grid(gridsize);
		System.out.println("Grid"+gridsize);
		System.out.println(G);

	}

}
