package sadiasrc.decomposition;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.ArrayList;

import sadiasrc.util.IndexedSet;

public class Caterpiller {
	
	
	
	public long getLinearBooleanWidth(ArrayList<IndexVertex> seq,IndexGraph G)
	{
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet Left = new VSubSet(groundSet);
		long ub=0,max=0,min =Integer.MAX_VALUE;
					
		//compute cutbool for every cut in the sequence //caterpillar
					
		for(IndexVertex v: seq)
		{
			Left.add(v);
			VSubSet c= new VSubSet(Left);
		    ub = CutBool.countMIS(G,c);
		    max=Math.max(max, ub);
		    
		 /*   if(Left.size()>=(G.numVertices()/3) && Left.size()<=(2*(G.numVertices()/3))){
		    	min=Math.min(min, ub);
		    	left = new ArrayList<IndexVertex>(G.numVertices());
		    	for(IndexVertex x: Left)
		    		left.add(x);
		    
		    }*/
		}
		return max;

	}

}
