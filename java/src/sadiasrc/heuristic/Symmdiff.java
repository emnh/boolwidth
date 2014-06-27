package sadiasrc.heuristic;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import sadiasrc.util.IndexedSet;

public class Symmdiff extends AbstractChooser implements IChooser{

	public Symmdiff(IndexGraph g) 
	{
		super(g);
	}

	@Override
	public IndexVertex choose() 
	{
		IndexVertex choosen=null;

//		if(N_LEFT.isEmpty())
//		{
//		    System.out.println("Start vertex chosen ");
//			choosen = BasicGraphAlgorithms.BFS(G,RIGHT.iterator().next());
//			choosen = BasicGraphAlgorithms.BFS(G,choosen);
//			return choosen;
//		}

		
		//O(n)
		/*for(IndexVertex x : LEFT)
		{
			//If a vertex of left has only one neighbour in the subset to choose then move it to left
			if(N_v_RIGHT.get(x.id()).size()==1)
			{
				System.out.println("Choosing from case 2 : "+N_v_RIGHT.get(x.id()).first());
				return N_v_RIGHT.get(x.id()).first();
			}
		}*/
		
		//If a vertex in N_left has no neighbour in the right then move it to left
		//O(n)
		for(IndexVertex v :  N_LEFT)
		{
			if(N_v_RIGHT.get(v.id()).size()==0)
			{
				//System.out.println("Chosing from case 1: "+v);
				return v;
			}
		}
		

		/*int minSD = G.numVertices();
		for(IndexVertex u :  N_LEFT)
		{
			VSubSet N_u_right = N_v_RIGHT.get(u.id());
			
			for(IndexVertex v : LEFT)
			{
				VSubSet N_v_right = new VSubSet(N_v_RIGHT.get(v.id()));
				N_v_right.remove(u);
				int sd = VSubSet.symDiff(N_u_right, N_v_right).size();
				if(sd==0)
				{
					minSD = sd;
					choosen = u;
					return u;
				}
			}			
		}*/
		
		
		int min=RIGHT.size();
		//for all u in RIGHT N(u)-N(LEFT)
		for(IndexVertex u: RIGHT)
		{
			//N(u) \cap Right
			VSubSet N_u=new VSubSet(N_v_RIGHT.get(u.id()));
			
			//N(u) \ N[left]
			for(IndexVertex x : N_LEFT)
				N_u.remove(x);
			
			if(N_u.size()<min)
			{
				min=N_u.size();
				choosen=u;
			}
					
		}
		return choosen;
	}

	
	

}
