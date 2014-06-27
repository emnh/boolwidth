package sadiasrc.algorithms;

import java.util.ArrayList;

import sadiasrc.decomposition.Caterpiller;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.GreedyChooser;
import sadiasrc.heuristic.GrowNeighborhood;
import sadiasrc.heuristic.MinDegNeighbour;
import sadiasrc.heuristic.TwoPartition;
import sadiasrc.io.ControlInput;

public class TestIndependentSet {
	
	public static void main(String[] args)
	{
		ControlInput cio= new ControlInput();
		Caterpiller caterpiller= new Caterpiller();
		
		long start;//start time for an ordering
		long  end;//end time for an ordering including evaluating caterpillar
		long UBfromCP=0;
		GreedyChooser chooser;
		ArrayList<IndexVertex> OrderedSequence;
	
		
		String fileName =  ControlInput.GRAPHLIB+ "" +
				"New/227.dgf";//diabetes-pp-002.dgf";
		
		IndexGraph G = new IndexGraph();
		
		G =	cio.getTestGraph(fileName, G);
		
		start=System.currentTimeMillis();
		//chooser = new GrowNeighborhood(G);
		chooser = new MinDegNeighbour(G);
		OrderedSequence =TwoPartition.buildSequence(G, chooser);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		end=System.currentTimeMillis();
		System.out.println("Time taken: "+ (end-start)+ " ms");
		
		start=System.currentTimeMillis();
		UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end=System.currentTimeMillis();
		System.out.println("UB : "+UBfromCP+" Time taken: "+ (end-start)+ " ms");
		
		
		start=System.currentTimeMillis();
		int maxis =IndependentSet.linIS(G, OrderedSequence);
		end=System.currentTimeMillis();
		System.out.println("Independent Set : "+ maxis+ " time taken : "+ (end-start)+ " ms");
	}

}
