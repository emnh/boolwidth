import java.util.ArrayList;

import sadiasrc.decomposition.Caterpiller;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.Degeneracy;
import sadiasrc.heuristic.GreedyChooser;
import sadiasrc.io.ControlInput;


public class TestDegeneracy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName =  ControlInput.GRAPHLIB+ "other/1.txt";
		ControlInput cio= new ControlInput();
		IndexGraph G = new IndexGraph();
		G =	cio.getTestGraph(fileName, G);
		
		Caterpiller caterpiller= new Caterpiller();
		
		long start;//start time for an ordering
		long  end;//end time for an ordering including evaluating caterpillar
		long UBfromCP=0;
		ArrayList<IndexVertex> OrderedSequence;
		
		start=System.currentTimeMillis();
		OrderedSequence =Degeneracy.degeneracyOrdering(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from degeneracy caterpillar");
		
		start=System.currentTimeMillis();
		OrderedSequence =Degeneracy.ReverseDegeneracyOrdering(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from reverse degeneracy caterpillar");
	
	}

}
