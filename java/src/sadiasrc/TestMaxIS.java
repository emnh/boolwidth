import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.MaximumIS;
import sadiasrc.io.ControlInput;


public class TestMaxIS {

	public static void main(String[] args) {
				
				String fileName =  ControlInput.GRAPHLIB + "other/Grid6.txt";
				ControlInput cio= new ControlInput();
				IndexGraph G=new IndexGraph();
				G =	cio.getTestGraph(fileName, G);
				
				long MaxIS = MaximumIS.maximumIndependentSet(G);
				System.out.println("Size of Maximum Independent Set : "+MaxIS);

	}

}
