package sadiasrc.heuristic;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.Collection;

public class CreateOrdering {

	public static ArrayList<IndexVertex> BuildSequence(IndexGraph G,IChooser chooser)
	{
		ArrayList<IndexVertex> order = new ArrayList<IndexVertex>();
		
		
		while(chooser.hasNext())//right is not empty
		{
			
			//choose one vertex from right according to heuristic and move it from right to left
			order.add(chooser.next());
			
		/*	 System.out.println("LEFT : "+chooser.LEFT);
			 System.out.println("RIGHT : "+chooser.RIGHT);
			 System.out.println("N_LEFT :"+chooser.N_LEFT);*/
			/*for(IndexVertex u: G.vertices())
			{
				System.out.println(" N_u "+ u+ ": "+chooser.N_v_RIGHT.get(u.id()));
			}*/
			
			//System.out.println("Choosen vertex : "+ v);
			
						
			
		}		
		//return left as ordering
		
		return order;
	}
	
	
	
}
