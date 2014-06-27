import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.heuristic.GreedyChooser;
import sadiasrc.heuristic.LocalSearch;
import sadiasrc.heuristic.MinDegNeighbour;
import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.heuristic.TwoPartition;
import sadiasrc.io.ControlInput;

import java.util.ArrayList;
import java.util.Collection;

import sadiasrc.util.IndexedSet;
import sadiasrc.decomposition.CutBool;


public class TestLocalSearch {

	public static void main(String[] args) {

		String fileName =  ControlInput.GRAPHLIB+ "freq/celar02.dgf";
		//String fileName =  ControlInput.GRAPHLIB+ "other/Clebsch.dgf";
		
		ControlInput cio= new ControlInput();
		
		
		IndexGraph G = new IndexGraph();
		G=cio.getTestGraph(fileName, G);

		System.out.println(G.numVertices()+ " "+ G.numEdges());
		
		//create sequence
		long start=System.currentTimeMillis();
		
		GreedyChooser chooser = new MinDegNeighbour(G);
		
		//ArrayList<IndexVertex> seq =TwoPartition.buildSequence(G, chooser);
		ArrayList<IndexVertex> seq =MinDegreeFillin.sequence(G);//TwoPartition.CreateSequenceSYMDiff(G);
					
		//System.out.println("Seq :"+seq);
					
		Collection<ArrayList<IndexVertex>> MinCut =TwoPartition.FindMinCut(G,seq);
		
		ArrayList<IndexVertex> oldleft = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> oldright = new ArrayList<IndexVertex>(G.numVertices());
		oldleft = MinCut.iterator().next();
		oldright = MinCut.iterator().next();
		long lower_b=CutBool.countMIS(G, oldleft);
		
		LocalSearch ls= new LocalSearch();
		boolean improved=false;
		
		long  new_lb=0;
		ArrayList<IndexVertex> newleft = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> newright = new ArrayList<IndexVertex>(G.numVertices());
			
		
		long total_LS= 0;
		int i;
		for(int no_try=0;no_try<200000;no_try++)
		{
			long start_LS= System.currentTimeMillis();
		
			Collection<ArrayList<IndexVertex>> NewSplit =ls.tryToImproveCut(G, oldleft,oldright, 1, lower_b, 0,total_LS);

		    newleft = new ArrayList<IndexVertex>(G.numVertices());
		    newright = new ArrayList<IndexVertex>(G.numVertices());
		    i=0;
		    for(ArrayList<IndexVertex> vs : NewSplit)
		    {		
			  if(i==0)
			  {
				newleft = vs;
				i++;
			  }
			  else
				newright =vs;			
		    }  
		
//		    System.out.println("New left: " +newleft);
//			System.out.println("New right: "+newright);
//			System.out.println("Cutval : "+new_lb);
		    new_lb=CutBool.countMIS(G, newleft);
		    long end_LS= System.currentTimeMillis();
		    
		    total_LS+=(end_LS-start_LS);
		    
		    oldleft=newleft;
		    oldright=newright;
		    lower_b=new_lb;
		    if(new_lb<lower_b)
		    	improved=true;

		
		}
		
		System.out.println("New left: " +newleft);
		System.out.println("New right: "+newright);
		System.out.println("Cutval : "+new_lb);
		

	}

}
