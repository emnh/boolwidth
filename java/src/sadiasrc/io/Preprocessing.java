package sadiasrc.io;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import sadiasrc.util.IndexedSet;
import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public class Preprocessing {
	
	//~ Instance fields --------------------------------------------------------

    public IndexedSet<IndexVertex> groundSet;
    
	
	public VSubSet toberemoved;//List of vertices to be removed
	public ArrayList<IndexEdge<IndexVertex>> edgestoremove;//List of edges to be removed
	public int twincount;
    
    public  Preprocessing(IndexGraph G)
    {
     	
    	groundSet = new IndexedSet<IndexVertex>(G.vertices());
    	toberemoved = new VSubSet(groundSet);
    	edgestoremove = new ArrayList<IndexEdge<IndexVertex>>();
    	twincount=0;    	
    	
	}
    
    public  IndexGraph Preprocess(IndexGraph G)
    {
     countIsolateV(G);
	 pendantandtwin(G);
	return G;

    }
    
    public  IndexGraph Preprocess(BiGraph G)
    {
     removeIsolateVertex(G);
	 removeTwin(G);
	return G;

    }
    
    
    // removes isolated vertex from bipartite graph
    public long removeIsolateVertex(BiGraph G)
    {
    	long isletcount=0;
    	Stack<IndexVertex> stack = new Stack<IndexVertex>();
    	for(IndexVertex x:G.vertices())
    		stack.push(x);
    
    	while(!stack.isEmpty())
    	{
    		IndexVertex x=stack.pop();
   		
    		if(G.neighbours(x).isEmpty()){
    			isletcount++;
    			G.removeVertex(x);
    			//toberemoved.add(x);
    		}
    	}
    	return isletcount;
    	
    }
    //remove twins from bipartite graph
    
  //removes twin from the graphs recursively
    public long removeTwin(BiGraph G)
    {
    	twincount=0;
    	ArrayList<VSubSet> neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

    	ArrayList<ArrayList<IndexVertex>> twins = new ArrayList<ArrayList<IndexVertex>>();
		boolean[] hasTwin = new boolean[G.numVertices()]; //is twin with some earlier vertex
    	
		for(IndexVertex x:G.leftVertices())
    	{
			
			if(G.contains(x) && !hasTwin[x.id()]) 
			{
				//System.out.println("Already found");
				//continue;
				
				VSubSet nx =neighbourhoods.get(x.id());
			
			ArrayList<IndexVertex> xtwins = new ArrayList<IndexVertex>();
    		for(IndexVertex y:G.leftVertices())
    		{
    			if(y.id()>x.id() && !G.inMatchedEdge(x, y))
    			{
    				VSubSet ny =neighbourhoods.get(y.id());
    				
    				    				
    				//if(nx.size() == ny.size())
    					//System.out.println("checking for twin");
 
    				//System.out.println("nx"+nx);
    				//System.out.println("ny"+ny);
       				if(nx.equals(ny))
    				{
       					//System.out.println(y+ "found twin of "+x);
       					xtwins.add(y);
       					hasTwin[y.id()]=true;
    				}

       				
    			}
    		}//for y
    		twins.add(xtwins);
			}//if
    	}//for x
    	
		
		//right vertices check
		for(IndexVertex x:G.rightVertices())
    	{
			
			if(G.contains(x) && !hasTwin[x.id()]) 
			{
				//System.out.println("Already found");
				//continue;
				
				VSubSet nx =neighbourhoods.get(x.id());
			
			ArrayList<IndexVertex> xtwins = new ArrayList<IndexVertex>();
    		for(IndexVertex y:G.rightVertices())
    		{
    			if(y.id()>x.id() && !G.inMatchedEdge(x, y))
    			{
    				VSubSet ny =neighbourhoods.get(y.id());
    				
    				    				
    				//if(nx.size() == ny.size())
    					//System.out.println("checking for twin");
 
    				//System.out.println("nx"+nx);
    				//System.out.println("ny"+ny);
       				if(nx.equals(ny))
    				{
       					//System.out.println(y+ "found twin of "+x);
       					xtwins.add(y);
       					hasTwin[y.id()]=true;
    				}

       				
    			}
    		}//for y
    		twins.add(xtwins);
			}//if
    	}//for x
		/////////////////
		/*for(ArrayList<IndexVertex> ts : twins)
    	{
        	for(IndexVertex x:ts)
        	{
        		System.out.print(x+" ");
        	}
        	System.out.println();
    	}*/
    	
    	for(ArrayList<IndexVertex> ts : twins)
    	{
        	for(IndexVertex x:ts)
        	{
        		if(G.contains(x))
        		{
        		G.removeVertex(x);
        		twincount++;
        		}
        	}
    	}
    	
    	 	return twincount;
		
    }
    
    // reduces the graph by recursively applying reduction rules and 
    // returns the list of connected components left that cannot be reduced
    // Boolw (G) is the maximum of the Boolw(components)
    public Collection<IndexGraph> reduce(IndexGraph G)
    {
		Collection<IndexGraph> cc = new ArrayList<IndexGraph>();
		return cc;
    	
    }
    
 //removes all isolated vertices in the graph   
    public long countIsolateV(IndexGraph G)
    {
    	long isletcount=0;
    	Stack<IndexVertex> stack = new Stack<IndexVertex>();
    	for(IndexVertex x:G.vertices())
    		stack.push(x);
    
    	while(!stack.isEmpty())
    	{
    		IndexVertex x=stack.pop();
   		
    		if(G.neighbours(x).isEmpty()){
    			isletcount++;
    			G.removeVertex(x);
    			
    		}
    	}
    	return isletcount;
    	
    }
    
//recursively removes leaves from the graph
    public long removePendantV(IndexGraph G)
    {
    	long pendantcount=0;
    	Stack<IndexVertex> stack = new Stack<IndexVertex>();
    	for(IndexVertex x:G.vertices())
    		stack.push(x);
    	while(!stack.isEmpty())
    	{
    		IndexVertex x=stack.pop();
    		if(G.contains(x))
    		{
    			if(G.degree(x)==1 && G.numEdges()>1){
    				pendantcount++;
    				for(IndexVertex y:G.neighbours(x))
    					stack.push(y);
    				G.removeVertex(x);
    				
    			}
    		}
    		
    	}
    	
    	return pendantcount;
    	
    }
    
    //removes twin from the graphs recursively
    public long counttwinV(IndexGraph G)
    {
    	twincount=0;
    	ArrayList<VSubSet> neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

//    	IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
    	ArrayList<ArrayList<IndexVertex>> twins = new ArrayList<ArrayList<IndexVertex>>();
		boolean[] hasTwin = new boolean[G.numVertices()]; //is twin with some earlier vertex
    	
		for(IndexVertex x:G.vertices())
    	{
			
			if(!hasTwin[x.id()]) 
			{
				//System.out.println("Already found");
				//continue;
				
				VSubSet nx =neighbourhoods.get(x.id());
			
			ArrayList<IndexVertex> xtwins = new ArrayList<IndexVertex>();
    		for(IndexVertex y:G.vertices())
    		{
    			if(y.id()>x.id() && !G.inMatchedEdge(x, y))
    			{
    				VSubSet ny =neighbourhoods.get(y.id());
    				
    				boolean areTrueTwins = false;
    				if(G.areAdjacent(x, y))
    				{ 
    					ny.setBit(x.id(),false);//.remove(x);//
    					nx.setBit(y.id(),false);//remove(y);//
    					areTrueTwins = true;
    				}
    				
    				//if(nx.size() == ny.size())
    					//System.out.println("checking for twin");
 
    				//System.out.println("nx"+nx);
    				//System.out.println("ny"+ny);
       				if(nx.equals(ny))
    				{
       					//System.out.println(y+ "found twin of "+x);
       					xtwins.add(y);
       					hasTwin[y.id()]=true;
    				}

       				if(areTrueTwins)
    				{ 
    					ny.setBit(x.id(),true);//.add(x);//
    					nx.setBit(y.id(),true);//.add(y);//
    				}
    			}
    		}//for y
    		twins.add(xtwins);
			}//if
    	}//for x
    	
		/*for(ArrayList<IndexVertex> ts : twins)
    	{
        	for(IndexVertex x:ts)
        	{
        		System.out.print(x+" ");
        	}
        	System.out.println();
    	}*/
    	
    	for(ArrayList<IndexVertex> ts : twins)
    	{
        	for(IndexVertex x:ts)
        	{
        		if(G.contains(x))
        		{
        		G.removeVertex(x);
        		twincount++;
        		}
        	}
    	}
    	
    	 	return twincount;
		
    }
    public void pendantandtwin(IndexGraph G)
    {
    	long p=1,q=1;
    	while(p>0 ||q>0){
    	
    	p = removePendantV(G);
    	q = counttwinV(G);
    	}
    	
    	
    }
    
    public long countCutVertexinComponents(IndexGraph G)
    {
    	long cutvertexcount=0;
    	Stack<IndexGraph> stack = new Stack<IndexGraph>();   
    	stack.push(G);
    	
    	while(!stack.isEmpty())
    	{
    		IndexGraph t= stack.pop();
            countIsolateV(t);
            Stack<IndexVertex> vertexstack = new Stack<IndexVertex>();
    		for(IndexVertex v: t.vertices())
    			vertexstack.push(v);
    		boolean compFound=false;
    		while(!vertexstack.empty()&& !compFound)
        	{    		
    			//check if in matched edge
    			compFound=false;
    			IndexVertex v=vertexstack.pop();
    				if(!t.isPendant(v))
    				{
    					IndexedSet<IndexVertex> groundSet;
    					groundSet = new IndexedSet<IndexVertex>(t.vertices());
    					VSubSet vt = new VSubSet(groundSet);
    					for(IndexVertex q:t.vertices())
    						vt.add(q);
    					vt.remove(v);
    					if(!BasicGraphAlgorithms.isConnected(t,vt))//removing e disconnects t
    					{
    						compFound=true;
    						cutvertexcount++;
    						IndexedSet<IndexVertex> groundSet1 = new IndexedSet<IndexVertex>(t.vertices());
    						VSubSet v_t= new VSubSet(groundSet1);
    						
    						for(IndexVertex x:t.vertices())
    							v_t.add(x);
    						v_t.remove(v);
    						
    						for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(t,v_t))
    						{
    							IndexGraph com= new IndexGraph();
    							HashMap<IndexVertex,Integer> mapv = new HashMap<IndexVertex,Integer>();
    							for(IndexVertex a: vs)
    							{
    								mapv.put(a, com.nextIndex());
    								com.insertVertex();
    								
    								//System.out.println("v"+v+"v.id"+mapv.get(v));
    								
    							}
    							//System.out.println(com.vertices());
    							//System.out.println(t.edges());
    							for(IndexEdge<IndexVertex> edge:t.edges())
    							{
    								boolean insertedge=true;
    								for(IndexVertex y:edge.endVertices())
    								{
    									if(!vs.contains(y))
    										insertedge=false;
    								}
    								if(insertedge)
    								{
    									com.insertEdge(mapv.get(edge.endVertices().get(0)),mapv.get(edge.endVertices().get(1)));
    									//System.out.println("Inserting edge"+edge);
    								}
    								
    							}
    							//System.out.println(com);
    							stack.push(com);
    							
    						}//for pushing connected components
    						break;
    					}//if not connected
    					//break;
    					
    				}//if not pendant
        	}//while vertex stack empty
    		
    		
    	}//while graph stack empty
    	    	
		return cutvertexcount;
    	
    }
    
    ///
    public long CutVertex(IndexGraph G)
    {
    	long cutvertexcount=0;
    	Stack<IndexGraph> stack = new Stack<IndexGraph>();   
    	stack.push(G);
    	
    	//while(!stack.isEmpty())
    	{
    		IndexGraph t= stack.pop();
    		countIsolateV(t);
    		Stack<IndexVertex> vertexstack = new Stack<IndexVertex>();
    		for(IndexVertex v: t.vertices())
    			vertexstack.push(v);
    		
    		while(!vertexstack.empty())
        	{    		
    			//check if in matched edge
    			IndexVertex v=vertexstack.pop();
    				if(!t.isPendant(v))
    				{
    					IndexedSet<IndexVertex> groundSet;
    					groundSet = new IndexedSet<IndexVertex>(t.vertices());
    					VSubSet vt = new VSubSet(groundSet);
    					for(IndexVertex q:t.vertices())
    						vt.add(q);
    					vt.remove(v);
    					 					
    					if(!BasicGraphAlgorithms.isConnected(t,vt))//removing e disconnects t
    					{
    						System.out.println("Cutvertex"+v);
    						cutvertexcount++;
    						
    					}
    					    					
    				}
        		}
    		
    		
    	}
    	    	
		return cutvertexcount;
    	
    }
    ///
    public long countBridge(IndexGraph G)
    {
    	long cutedgecount=0;
    	   	
    	Stack<IndexGraph> stack = new Stack<IndexGraph>();   
    	stack.push(G);
    	
    //	while(!stack.isEmpty())
    	{
    		IndexGraph t= stack.pop();
    		countIsolateV(t);
    		Stack<IndexEdge<IndexVertex>> edgestack = new Stack<IndexEdge<IndexVertex>>();
    		for(IndexEdge<IndexVertex> e: t.edges())
    			edgestack.push(e);
    		
    		while(!edgestack.empty())
        	{    		
    			//check if in matched edge
    			IndexEdge<IndexVertex> e=edgestack.pop();
    				if(!t.isMatched(e))
    				{
    					//System.out.println("Removing edge"+e);
    					t.removeEdge(e);
    					//System.out.println(t);
    					    					
    					if(!BasicGraphAlgorithms.isConnected(t))//removing e disconnects t
    					{
    						System.out.println("Cutedge"+e);
    						cutedgecount++;
    						
    					}
    					t.insertEdge(e);
    				}
    					
    						
    				
        	}//end while
    		
    		
    	}
    	    	
		return cutedgecount;
    	
    }
    public long Bridge(IndexGraph G)
    {
    	long cutedgecount=0;
    	   	
    	Stack<IndexGraph> stack = new Stack<IndexGraph>();   
    	stack.push(G);
    	
    	while(!stack.isEmpty())
    	{
    		IndexGraph t= stack.pop();
    		countIsolateV(t);
    		Stack<IndexEdge<IndexVertex>> edgestack = new Stack<IndexEdge<IndexVertex>>();
    		for(IndexEdge<IndexVertex> e: t.edges())
    			edgestack.push(e);
    		
    		while(!edgestack.empty())
        	{    		
    			//check if in matched edge
    			IndexEdge<IndexVertex> e=edgestack.pop();
    				if(!t.isMatched(e))
    				{
    					//System.out.println("Removing edge"+e);
    					t.removeEdge(e);
    					//System.out.println(t);
    					    					
    					if(!BasicGraphAlgorithms.isConnected(t))//removing e disconnects t
    					{
    					//	System.out.println("Cutedge"+e);
    						cutedgecount++;
    						IndexedSet<IndexVertex> groundSet1 = new IndexedSet<IndexVertex>(t.vertices());
    						VSubSet v_t= new VSubSet(groundSet1);
    						//System.out.println("t"+t.vertices());
    						for(IndexVertex x:t.vertices())
    							v_t.add(x);
    						
//    						System.out.println("vt"+v_t);
    						for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(t,v_t))
    						{
//    							System.out.println("connectec comp"+vs);
    							IndexGraph com= new IndexGraph();
    							HashMap<IndexVertex,Integer> mapv = new HashMap<IndexVertex,Integer>();
    							for(IndexVertex v: vs)
    							{
    								mapv.put(v, com.nextIndex());
    								com.insertVertex();
    								
    								//System.out.println("v"+v+"v.id"+mapv.get(v));
    								
    							}
    							//System.out.println(com.vertices());
    							//System.out.println(t.edges());
    							for(IndexEdge<IndexVertex> edge:t.edges())
    							{
    								boolean insertedge=true;
    								for(IndexVertex y:edge.endVertices())
    								{
    									if(!vs.contains(y))
    										insertedge=false;
    								}
    								if(insertedge)
    								{
    									com.insertEdge(mapv.get(edge.endVertices().get(0)),mapv.get(edge.endVertices().get(1)));
    									//System.out.println("Inserting edge"+edge);
    								}
    								
    							}
    							//System.out.println(com);
    							stack.push(com);
    							
    						}
    						break;
    					}
    					else
    						t.insertEdge(e);
    				}
        	}
    		
    		
    	}
    	    	
		return cutedgecount;
    	
    }
    

}
