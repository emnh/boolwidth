import java.io.FileNotFoundException;
import java.util.ArrayList;

import sadiasrc.decomposition.Caterpiller;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.NDOrdering;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;


public class TestNDOrdering {

	public static void main(String[] args) {

		String fileName =  ControlInput.GRAPHLIB+ "prob/munin2.dgf";	
		ControlInput cio= new ControlInput();
		
		
		IndexGraph G = new IndexGraph();
		
		G =	cio.getTestGraph(fileName, G);
		Preprocessing p = new Preprocessing(G);
		p.Preprocess(G);
		
		
		NDOrdering fromMETIS= new NDOrdering(G,fileName);
		NDOrdering.makeAdjacencyFile(G,fileName);
		
		try {
			ArrayList<IndexVertex> seq =NDOrdering.getNDOrdering(G,fileName);
			System.out.println(seq);
			Caterpiller cp= new Caterpiller();
			long UBfromCP=cp.getLinearBooleanWidth(seq, G);
			System.out.println("Cutbool="+UBfromCP+" ms from NDOrderingcaterpillar");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
