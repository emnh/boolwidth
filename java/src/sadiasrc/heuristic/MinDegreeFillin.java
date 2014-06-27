package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import sadiasrc.util.DegreeList;
import sadiasrc.util.IndexedSet;

import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IEdge;
import sadiasrc.graph.IGraph;
import sadiasrc.graph.IVSet;
import sadiasrc.graph.IVertex;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.VertexSet;

public class MinDegreeFillin {	

	//O(n^2*k)
	public static ArrayList<IndexVertex> sequence(IndexGraph g)
	{
		
		DegreeList dl = new DegreeList(g);
//		for(ArrayList<IndexVertex> t: dl.degList)
//			System.out.println(t);
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(g.numVertices());
		ArrayList<ArrayList<IndexVertex>> neighborList = new ArrayList<ArrayList<IndexVertex>>(g.numVertices());
		for(int i=0; i<g.numVertices();i++)
			neighborList.add(new ArrayList<IndexVertex>());
		for(IndexVertex v:g.vertices())
			neighborList.set(v.id(),(ArrayList<IndexVertex>) g.neighbours(v));
//		System.out.println("Neighbor List");
//		for(ArrayList<IndexVertex> t: neighborList)
//			System.out.println(t);
		boolean[] added = new boolean[g.numVertices()];
		boolean[][] adj = new boolean[g.numVertices()][g.numVertices()];
		for (IndexVertex v : g.vertices()) {
			for (IndexVertex n : g.neighbours(v)) {
				adj[v.id()][n.id()] = true;
				
			}
		}
		//so far O(n^2)
		
		while(seq.size()<g.numVertices())	//O(n^2*k)
		{
			IndexVertex v = dl.getMin();
//			System.out.println("Min degree"+v);
//			System.out.println("Neighbors of v"+v);
//			System.out.println(neighborList.get(v.id()));
			for(IndexVertex n1 : neighborList.get(v.id())) //O(n*k)
			{				
				dl.decrease(n1);
				neighborList.get(n1.id()).remove(v);	//O(n)
//				System.out.println("Neighbor List");
//				for(ArrayList<IndexVertex> t: neighborList)
//					System.out.println(t);
			}
			for(IndexVertex n1 : neighborList.get(v.id()))
			{
				for(IndexVertex n2 : neighborList.get(v.id()))
				{
						if(n1.id()>= n2.id())
							continue;					
						if(!adj[n1.id()][n2.id()])
						{
							adj[n1.id()][n2.id()] = true;
							neighborList.get(n1.id()).add(n2);
							neighborList.get(n2.id()).add(n1);
							dl.increase(n1);
							dl.increase(n2);
						}
					}
				}
			dl.remove(v);
			
//			System.out.println("Neighbor List");
//			for(ArrayList<IndexVertex> t: neighborList)
//				System.out.println(t);
			seq.add(v);
//			System.out.println("Degree List");
//			for(ArrayList<IndexVertex> t: dl.degList)
//				System.out.println(t);
		}
		
		return seq;
	}

	public static Stack<VSubSet> greedySeparate(IndexGraph g, ArrayList<IndexVertex> seq)
	{
		IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(g.vertices());
		VSubSet vs = new VSubSet(groundSet);
		vs.addAll((Collection<? extends IndexVertex>) g.vertices());
		VSubSet bag = new VSubSet(groundSet);
		Stack<VSubSet> s1 = new Stack<VSubSet>();		
		return greedySeparate(g, vs,bag,seq,s1);
		
	}
	public static DisjointBinaryDecomposition DBDbyMDH(IndexGraph G)
	{
		//Start out by making a decomposition with only one bag called the root
		//the root contains all vertices of $G$
		DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
		ArrayList<IndexVertex> seq=MinDegreeFillin.sequence(G);
		//keep a stack of those nodes that need to be split further, initially containing the root
		Stack<DecompNode> s = new Stack<DecompNode>();
		s.push(decomp.root());
		while(!s.isEmpty())
		{
			//pick next node to split
			DecompNode p = s.pop();
			
			
			IndexedSet<IndexVertex> groundSet;
			groundSet = new IndexedSet<IndexVertex>(G.vertices());
			VSubSet bag = new VSubSet(groundSet);
			VSubSet vs = new VSubSet(groundSet);
			for(IndexVertex v:p.getGraphSubSet().vertices())
				vs.add(v);
			System.out.println("p ["+p.id()+"]="+vs);
			
			//If the node is a clique then random decomposition
			if(BasicGraphAlgorithms.isClique(G, vs))
			{		
				//compute the sizes of the two subsets
				int n = p.getGraphSubSet().numVertices();
				//can't split vertexSets of size 1
				if(n<=1) continue;
				int numRight = n/2;
				int numLeft = n-numRight;
				
				//for each vertex choose a random side to put it
				for(IndexVertex v : p.getGraphSubSet().vertices())
				{
					//calculate probability based on how many vertices already placed
					if(Math.random()*n<numLeft)	
					{
						decomp.addLeft(p, v);
						numLeft--;
					}
					else
					{
						decomp.addRight(p, v);
						numRight--;
					}
					n--;
				}
				
				//push the children on the stack
				if(p.hasLeft())
				{
					System.out.println("Pushing node"+p.getLeft().getGraphSubSet().vertices());
				
					s.push(p.getLeft());
				}
				if(p.hasRight())
				{
					System.out.println("Pushing node"+p.getLeft().getGraphSubSet().vertices());
					s.push(p.getRight());
				}
				
			}//random decomposition for clique is done
			//If the node is a clique then random decomposition
			
			//if not a clique then 
			else
			{
				ArrayList<IndexVertex> seqinnode=new ArrayList<IndexVertex>();
				//Create sequence for smaller components from the original sequence
				if(!p.equals(decomp.root()))
				{
					for(IndexVertex v:seq)
						if(p.getGraphSubSet().vertices().contains(v))
							seqinnode.add(v);					
				}
				else
					seqinnode=seq;
				//end creating sequence
				int i = seqinnode.size()-1;
				//Look for separator			
				System.out.println("SeqInNode"+seqinnode);
				while(BasicGraphAlgorithms.isConnected(G,vs))
				{
					if(BasicGraphAlgorithms.isClique(G, vs))
						break;
					IndexVertex sep= seqinnode.get(i);
					vs.remove(sep);
					bag.add(sep);
					i--;
				}
				System.out.println("vs "+vs);
				System.out.println("bag"+bag);
				
				//bag empty and vs is not clique
				if(bag.isEmpty())
				{		
					//compute the sizes of the two subsets
					int n = p.getGraphSubSet().numVertices();
					//can't split vertexSets of size 1
					if(n<=1) continue;
					int numRight = n/2;
					int numLeft = n-numRight;
					
					//for each vertex choose a random side to put it
					for(IndexVertex v : p.getGraphSubSet().vertices())
					{
						//calculate probability based on how many vertices already placed
						if(Math.random()*n<numLeft)	
						{
							decomp.addLeft(p, v);
							numLeft--;
						}
						else
						{
							decomp.addRight(p, v);
							numRight--;
						}
						n--;
					}
					
					//push the children on the stack
					if(p.hasLeft())
					{
						System.out.println("Pushing node"+p.getLeft().getGraphSubSet().vertices());
					
						s.push(p.getLeft());
					}
					if(p.hasRight())
					{
						System.out.println("Pushing node"+p.getLeft().getGraphSubSet().vertices());
						s.push(p.getRight());
					}
					
				}//random decomposition for clique is done
				//end bag empty and vs not clique
				
				else if(!bag.isEmpty())
				{
					if(p.getGraphSubSet().vertices().size()>1)
					{
						for(IndexVertex v : p.getGraphSubSet().vertices())
						{
				
							if(bag.contains(v))	
							{
								decomp.addLeft(p, v);
							}
							else
							{
								decomp.addRight(p, v);
							}	
			
						}
						//push the bag on the stack
						if(p.hasLeft())
						{
							System.out.println("Pushing node"+p.getLeft().getGraphSubSet().vertices());
							s.push(p.getLeft());
						
						}
					}
					//root of the connected components
					if(p.hasRight())
					{
						DecompNode temp=p.getRight();
						VSubSet comps= new VSubSet(groundSet);
						for(IndexVertex k:vs)
							comps.add(k);
						for(ArrayList<IndexVertex> cc : BasicGraphAlgorithms.connectedComponents(G,vs))
						{
							VSubSet ccpush=new VSubSet(groundSet);
							for(IndexVertex k:cc)
							{
								decomp.addLeft(temp, k);
								ccpush.add(k);
							}
							s.push(temp).getLeft();
							System.out.println("Pushing node"+temp.getLeft().getGraphSubSet().vertices());
							comps.removeAll(ccpush);
							for(IndexVertex k:comps)
								decomp.addRight(temp, k);
							temp=temp.getRight();			
				
						}
					}
				}//end if
			}//end while
		}//end else
		return decomp;
	}

	@SuppressWarnings("static-access")
	public static Stack<VSubSet> greedySeparate(IndexGraph g, IVSet vs, IVSet bag,ArrayList<IndexVertex> seq,Stack<VSubSet> s)
	{
		int i = seq.size()-1;
		
		while(BasicGraphAlgorithms.isConnected(g,vs))
		{
			if(BasicGraphAlgorithms.isClique(g, vs))
				break;
			IndexVertex sep= seq.get(i);
			vs.remove(sep);
			bag.add(sep);
			i--;
		}
		
		s.push((VSubSet) bag);
		if(!BasicGraphAlgorithms.isClique(g, bag))
		{
			ArrayList<IndexVertex> seqinbag=new ArrayList<IndexVertex>();
			for(IndexVertex v:seq)
				if(bag.contains(v))
					seqinbag.add(v);
			//System.out.println("seqinbag"+seqinbag);
			if(bag.size()>=2)
			{
				IndexedSet<IndexVertex> groundSet;
				groundSet = new IndexedSet<IndexVertex>(g.vertices());
				VSubSet newbag=new VSubSet(groundSet);
				greedySeparate(g,bag,newbag,seqinbag,s);
			}
		}
		
		if(!vs.isEmpty())
		{
			
			IndexedSet<IndexVertex> groundSet2;
			groundSet2 = new IndexedSet<IndexVertex>(g.vertices());
			VSubSet comps= new VSubSet(groundSet2);
			for(IndexVertex k:vs)
				comps.add(k);
			//System.out.println("pushing c1..cn"+comps);
			s.push(comps);
			for(ArrayList<IndexVertex> cc : BasicGraphAlgorithms.connectedComponents(g,vs))
			{
				VSubSet ccpush=new VSubSet(groundSet2);
				for(IndexVertex k:cc)
					ccpush.add(k);
				//System.out.println("pushing  ci"+ccpush);
				s.push(ccpush);
			
			
				comps.removeAll(ccpush);
				
				if(!comps.isEmpty())
				{
					//System.out.println("pushing rest cc"+comps);
					s.push(comps);
				}
			
				if(!BasicGraphAlgorithms.isClique(g, ccpush))
				{
					ArrayList<IndexVertex> seqinthiscomponet=new ArrayList<IndexVertex>();
			
					for(IndexVertex v:seq)
						if(ccpush.contains(v))
							seqinthiscomponet.add(v);
					//System.out.println("seqinthiscomponent"+seqinthiscomponet);
					if(ccpush.size()>=2)
					{
						IndexedSet<IndexVertex> groundSet;
						groundSet = new IndexedSet<IndexVertex>(g.vertices());
						VSubSet newbag=new VSubSet(groundSet);
						greedySeparate(g, ccpush,newbag,seqinthiscomponet,s);
					}
				}
			}
	
		}
				
		return s;
	}

}
