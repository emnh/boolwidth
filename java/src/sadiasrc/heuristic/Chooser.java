package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

public abstract class Chooser implements Iterator<IndexVertex>{
	
	IndexGraph G;
	List<IndexVertex> LEFT;
	Set<IndexVertex> RIGHT;
	Set<IndexVertex> N_LEFT;
	
	public Chooser(IndexGraph g)
	{
		this.G=g;
		this.LEFT= new ArrayList<IndexVertex>();  
		this.RIGHT= new HashSet<IndexVertex>();
		this.N_LEFT= new HashSet<IndexVertex>();
		
		for(IndexVertex v: G.vertices())
		{
			RIGHT.add(v);
		}
		
		for(IndexVertex u :  LEFT)
		{
			for(IndexVertex w: G.neighbours(u))
				if(RIGHT.contains(w))
					N_LEFT.add(w);
		}
			
	}	
	
	public boolean hasNext() {
		return (!RIGHT.isEmpty());
	}

	@Override
	public void remove() {
		//TO DO
	}
	
	//Input  : None
	//Output : next vertex chosen from N_LEFT or RIGHT
	//has to be override
	public IndexVertex next()
	{
		return null;
		
	}
	
	public void moveVertexFronRightToLeft(IndexVertex v)
	{
		//Update N_LEFT 
		//Remove v from N_LEFT
		if(N_LEFT.contains(v))
			N_LEFT.remove(v);
		//Add all neighbours of v in RIGHT to N_LEFT
		for(IndexVertex x: G.neighbours(v)){
			if(RIGHT.contains(x))// && !(N_Left.contains(x)))
				N_LEFT.add(x);
			
		}
		
		//move v from right to left
		LEFT.add(v);
		RIGHT.remove(v);
	}
	

}
