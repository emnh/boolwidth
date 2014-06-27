package sadiasrc.heuristic;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;

import sadiasrc.decomposition.CutBool;

public class GreedyInitWithStart extends AbstractChooser implements IChooser {
	
	public long Upperbound;
	
	public GreedyInitWithStart(IndexGraph G) {
		// TODO Auto-generated constructor stub
		super(G);
		this.Upperbound=0;
	}
		
	public void SetUB(long ub ) {
		// TODO Auto-generated constructor stub
		
		this.Upperbound=ub;
	}	
	public long getUB()
	{
		return UB;
	}
	/*public static long seqFromGreedyInit(IndexGraph g, long UBfromCP)
	{
		long start = System.currentTimeMillis();
		
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(g.numVertices());
		
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(g.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(g.numVertices());
		for(IndexVertex v:g.vertices())
			right.add(v);
		IndexVertex u = G.MinDegreeVertex(G.vertices());//G.vertices().iterator().next();
		left.add(u);
		right.remove(u);
		
		while(!right.isEmpty())
		{
			//System.out.println("left"+left);
			if((System.currentTimeMillis()-start)>30*60*1000)
				return 0;
			long ub=0,max=0,min =Integer.MAX_VALUE;
			
			for(IndexVertex v : right)
			{
				left.add(v);
				ub = CutBool.countMIS(G,left);
				if(ub<min)
				{
					min=ub;
					u=v;
				}
				left.remove(v);
							
			}
			if(UBfromCP<min)
				UBfromCP=min;		
					
			left.add(u);
			right.remove(u);
		}
		System.out.println("Ordered Sequence: "+left);	
		return UBfromCP;
	}

	public static long GreedyInitInSubset(IndexGraph g, long UBfromCP)
	{
		long start = System.currentTimeMillis();
		
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(g.numVertices());
		
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(g.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(g.numVertices());
		for(IndexVertex v:g.vertices())
			right.add(v);
		ArrayList<IndexVertex> N_Left= new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> SubsetToChooseFrom= new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v :  left)
		{
			for(IndexVertex u: G.neighbours(v))
				if(right.contains(u) && !(N_Left.contains(u)))
					N_Left.add(u);
		}
		IndexVertex u = G.vertices().iterator().next();
		left.add(u);
		right.remove(u);
		
		while(!right.isEmpty())
		{
			//System.out.println("left"+left);
			if((System.currentTimeMillis()-start)>30*60*1000)
				return 0;
			long ub=0,max=0,min =Integer.MAX_VALUE;
			
			if(N_Left.isEmpty())
				SubsetToChooseFrom=right;
			else
				SubsetToChooseFrom= N_Left;
			
			for(IndexVertex v : SubsetToChooseFrom)
			{
				left.add(v);
				ub = CutBool.countMIS(G,left);
				if(ub<min)
				{
					min=ub;
					u=v;
				}
				left.remove(v);
							
			}
			if(UBfromCP<min)
				UBfromCP=min;		
					

			if(N_Left.contains(u))
				N_Left.remove(u);
			for(IndexVertex x: G.neighbours(u)){
				if(right.contains(x) && !(N_Left.contains(x)))
					N_Left.add(x);
			}
			left.add(u);
			right.remove(u);			
		}
		System.out.println("Ordered Sequence: "+left);	
		return UBfromCP;
	}*/

	@Override
	public IndexVertex choose() {
		
		IndexVertex choosen =null;
		
		if(N_LEFT.isEmpty())
		{
//			System.out.println("start"+RIGHT.iterator().next());
//			choosen = BasicGraphAlgorithms.BFS(G,RIGHT.iterator().next());
//			choosen = BasicGraphAlgorithms.BFS(G,choosen);
//			//System.out.println("choosen"+choosen);
			
			choosen= G.MinDegreeVertex(RIGHT);
			return choosen;//RIGHT.iterator().next();//choosen;
		}
		if(RIGHT.size()==1)
			return RIGHT.iterator().next();
		
		long ub=0, min=( long)Double.MAX_VALUE;
		
		for(IndexVertex v : RIGHT)
		{
			LEFT.add(v);
			ub = CutBool.countMIS(G,LEFT);
			if(ub<min)
			{
				min=ub;
				choosen =v;
			}
			LEFT.remove(v);
						
		}
		if(UB<min)
			UB=min;	
		//System.out.println("choosen"+choosen);
		return choosen;
	}

}

