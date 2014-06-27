package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sadiasrc.util.IndexedSet;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public abstract class AbstractChooser implements IChooser{
	
	IndexGraph G;
	List<IndexVertex> LEFT;
	Set<IndexVertex> RIGHT;
	Set<IndexVertex> N_LEFT;
	IndexGraph H;
	DegreeList degInRight;
	DegreeList degInLeft;
	ArrayList<VSubSet> N_v_RIGHT;
	long UB;
	
	
	public AbstractChooser(IndexGraph g)
	{
		this.G=g;
		this.LEFT= new ArrayList<IndexVertex>();  
		this.RIGHT= new HashSet<IndexVertex>();
		this.N_LEFT= new HashSet<IndexVertex>();
		this.N_v_RIGHT = new ArrayList<VSubSet>(G.numVertices());
		this.UB=2;
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		//degInLeft = new DegreeList(g);
		
		//System.out.println("Inside abstract class constructor");
		for(IndexVertex v: G.vertices())
		{
			RIGHT.add(v);
		}
		
		for(int i=0; i<G.numVertices();i++)
			N_v_RIGHT.add(new VSubSet(groundSet));
	
		//create for all v in LEFT N(v) in RIGHT
		for(IndexVertex x : G.vertices())
		{
			VSubSet temp = new VSubSet(groundSet);
			
			
			for(IndexVertex y : G.neighbours(x))
			{
				if(RIGHT.contains(y))
					temp.add(y);
			}
			//System.out.println("temp:"+temp);
			N_v_RIGHT.set(x.id(), temp);
		}
				
		
	}	
	
	public long getUB()
	{
		return UB;
	}
	public boolean hasNext() {
		return (!RIGHT.isEmpty());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not allowed to remove any vertices");
	}
	
	//Input  : None
	//Output : next vertex chosen from N_LEFT or RIGHT
	//has to be override
	public IndexVertex next()
	{
		IndexVertex nextVertex = choose();
		//System.out.println("choosen "+nextVertex);
		//Update N_LEFT 
		//Remove v from N_LEFT
		if(N_LEFT.contains(nextVertex))
		{
			N_LEFT.remove(nextVertex);
		}
		//Add all neighbours of v in RIGHT to N_LEFT
		for(IndexVertex x: G.neighbours(nextVertex))
		{
			
			if(RIGHT.contains(x))// && !(N_Left.contains(x)))
			{	
				N_LEFT.add(x);				
			}
			//update N_v_RIGHT -remove nextvertex from all its neighbours neighbour list in RIGHT 
			N_v_RIGHT.get(x.id()).remove(nextVertex);
		}
		
		//move v from right to left
		LEFT.add(nextVertex);
		RIGHT.remove(nextVertex);
		return nextVertex;
	}
	
	public IndexVertex StartVertex()
	{
		IndexVertex startvertex = null;
		startvertex = BasicGraphAlgorithms.BFS(G,G.getVertex((int)(Math.random()*G.numVertices())));
		startvertex = BasicGraphAlgorithms.BFS(G,startvertex);
		return startvertex;
	}
	
//	public void moveVertexFronRightToLeft(IndexVertex v)
//	{
//		//Update N_LEFT 
//		//Remove v from N_LEFT
//		if(N_LEFT.contains(v))
//			N_LEFT.remove(v);
//		//Add all neighbours of v in RIGHT to N_LEFT
//		for(IndexVertex x: G.neighbours(v)){
//			if(RIGHT.contains(x))// && !(N_Left.contains(x)))
//				N_LEFT.add(x);
//			
//		}
//		
//		//move v from right to left
//		LEFT.add(v);
//		RIGHT.remove(v);
//	}
	

}
