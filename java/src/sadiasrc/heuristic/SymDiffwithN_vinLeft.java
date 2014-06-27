package sadiasrc.heuristic;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;

public class SymDiffwithN_vinLeft {
	
	    //Create and return an ordering of the vertices picking the vertex from right with 
		//least uncommon neighbors with some vertex in left

		public static ArrayList<IndexVertex> CreateSequenceSYMDiff(IndexGraph G)
		{
			long start=System.currentTimeMillis();
			ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
			ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
			for(IndexVertex v:G.vertices())
				right.add(v);
			IndexVertex v = G.MinDegreeVertex(right);
			left.add(v);
			right.remove(v);
			
			while(!right.isEmpty())
			{
				if((System.currentTimeMillis()-start)>30*60*1000)
					return null;
//				System.out.println("Left"+left);
//				System.out.println("Right"+right);
				
				//v E Left N(v) in right
				ArrayList<ArrayList<IndexVertex>> N_Left_v = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
				for(int i=0; i<G.numVertices();i++)
					N_Left_v.add(new ArrayList<IndexVertex>());
				
				//N(v) for all v in left
				for(IndexVertex x : left)
				{
					ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
					for(IndexVertex y : G.neighbours(x)){
						if(right.contains(y))
							temp.add(y);}
					//System.out.println("x"+x+"temp"+temp);
					N_Left_v.set(x.id(), temp);
					
				}
				
				//N(_A) _A=right
				ArrayList<ArrayList<IndexVertex>> N_Right_v = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
				for(int i=0; i<G.numVertices();i++)
					N_Right_v.add(new ArrayList<IndexVertex>());
				
				//N(u) for all u in right
				for(IndexVertex x : right)
				{
					ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
					for(IndexVertex y : G.neighbours(x)){
						if(right.contains(y))
							temp.add(y);}
					//System.out.println("x"+x+"temp"+temp);
					N_Right_v.set(x.id(), temp);
					
				}
				
				int min=G.numVertices();
				IndexVertex u = null;
				
				//picking up the minimum			
				for(IndexVertex c :right)
				{
					
					
					for(IndexVertex d: left)
					{
						ArrayList<IndexVertex> temp1 = new ArrayList<IndexVertex>(N_Left_v.get(d.id()));
						ArrayList<IndexVertex> temp2 = new ArrayList<IndexVertex>(N_Right_v.get(c.id()));
						
//						System.out.println("c "+c +" :"+ temp2);
//						System.out.println("d "+d +" :"+temp1);
						
						temp1.removeAll(temp2);
						temp2.removeAll(temp1);
						if(temp1.contains(c))
							temp1.remove(c);
						if(temp2.contains(d))
							temp2.remove(c);
						
						
						int symdiff = temp1.size()+temp2.size();
//						System.out.println("symdiff"+symdiff);
						
						if(symdiff<min)
						{
							min=symdiff;
//							System.out.println("symdiff"+symdiff);
							u=c;
						}
					}
							
				}
//				System.out.println("choosen "+u);
				left.add(u);
				right.remove(u);
					
			}
			
			
			return left;
			
		}

}
