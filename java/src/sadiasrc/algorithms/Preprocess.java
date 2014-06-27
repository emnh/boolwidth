package sadiasrc.algorithms;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.ArrayList;
import java.util.Stack;

import sadiasrc.util.IndexedSet;

public class Preprocess {
	
	//~ Instance fields --------------------------------------------------------
	static int taken_in_MIS=0;
  
    public static int  Preprocess_for_MIS(IndexGraph G)
    {
    	int p=1,q=1;
    	while(p>0 ||q>0){
    		
    		 taken_in_MIS+=removeIsolateV(G);
    		 p = removeDegree1V(G);
    		 taken_in_MIS+=p;
    		 q = eliminateSuperSet(G);
    	}
    	return taken_in_MIS;
	}
    
    
  //removes all isolated vertices in the graph   
    public static int removeIsolateV(IndexGraph G)
    {
    	int isletcount=0;
    	Stack<IndexVertex> stack = new Stack<IndexVertex>();
    	for(IndexVertex x:G.vertices())
    		stack.push(x);
    
    	while(!stack.isEmpty())
    	{
    		IndexVertex x=stack.pop();
   		
    		if(G.neighbours(x).isEmpty()){
    			isletcount++;
    			G.removeVertex(x);
    			//System.out.println("Removing "+x);
    			
    		}
    	}
    	return isletcount;
    	
    }
    
   //recursively takes leaves into MIS
    public static int removeDegree1V(IndexGraph G)
    {
    	int pendantcount=0;
    	Stack<IndexVertex> stack = new Stack<IndexVertex>();
    	for(IndexVertex x:G.vertices())
    		stack.push(x);
    	while(!stack.isEmpty())
    	{
    		IndexVertex x=stack.pop();
    		if(G.contains(x))
    		{
    			if(G.degree(x)==1){
    				pendantcount++;
    				for(IndexVertex y:G.neighbours(x))
    				{
    					//System.out.println("Removing "+y);
    					G.removeVertex(y);
    				}
    				
    				G.removeVertex(x);
    				//System.out.println("Removing "+x);
    				
    			}
    		}
    		
    	}
    	
    	return pendantcount;
    	
    }
    
    //removes supersets from the graphs 
    public static int eliminateSuperSet(IndexGraph G)
    {
    	int super_set_count=0;
    	ArrayList<VSubSet> neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
    	IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		ArrayList<ArrayList<IndexVertex>> Supersets = new ArrayList<ArrayList<IndexVertex>>();
		boolean[] hasSubset = new boolean[G.numVertices()]; //is twin with some earlier vertex
    	
		for(IndexVertex x:G.vertices())
    	{
			
			//System.out.println("x "+x);
			if(!hasSubset[x.id()]) 
			{
				//System.out.println("Already found");
				//continue;
				
				VSubSet nx =neighbourhoods.get(x.id());
			
				ArrayList<IndexVertex> SupersetV = new ArrayList<IndexVertex>();
				
				for(IndexVertex y:G.vertices())
				{
					
					if(y.id()>x.id() && (!hasSubset[y.id()]) )
					{
						//System.out.println("y "+y);
						VSubSet ny =neighbourhoods.get(y.id());
    				
						boolean areTrueTwins = false;
						if(G.areAdjacent(x, y))
						{ 
							ny.setBit(x.id(),false);//.remove(x);//
							nx.setBit(y.id(),false);//remove(y);//
							areTrueTwins = true;
							
//							System.out.println("nx"+nx);
//		    				System.out.println("ny"+ny);
//							
							if(nx.containsAll(ny))
		    				{
		       					//System.out.println(x+ "found Superset of "+y);
		       					SupersetV.add(x);
		       					hasSubset[x.id()]=true;
		    				}
							else if(ny.containsAll(nx))
		    				{
		       					//System.out.println(y+ "found Superset of "+x);
		       					SupersetV.add(y);
		       					hasSubset[y.id()]=true;
		    				}
							
							if(areTrueTwins)
		    				{ 
		    					ny.setBit(x.id(),true);//.add(x);//
		    					nx.setBit(y.id(),true);//.add(y);//
		    				}
						}
    				
    				//if(nx.size() == ny.size())
    					//System.out.println("checking for twin");
 
    				//System.out.println("nx"+nx);
    				//System.out.println("ny"+ny);
       				

       				
    			}
    		}//for y
    		Supersets.add(SupersetV);
    		//System.out.println(SupersetV);
			}//if
    	}//for x
    	
		for(ArrayList<IndexVertex> ts : Supersets)
    	{
			//System.out.println(ts);
        	for(IndexVertex x:ts)
        	{
        		if(G.contains(x))
        		{
        			//System.out.println("Removing "+x);
        			G.removeVertex(x);
        			super_set_count++;
        			//System.out.println(G);
        		}
        	}
    	}
    	
    	return super_set_count;
		
    }
  }
