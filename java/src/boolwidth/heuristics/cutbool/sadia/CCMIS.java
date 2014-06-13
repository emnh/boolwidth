package boolwidth.heuristics.cutbool.sadia;

import boolwidth.heuristics.cutbool.sadia.BasicGraphAlgorithms;
import graph.BiGraph;

import graph.Vertex;
/*
import graph.IndexGraph;
import graph.IndexVertex;
import graph.VSubSet;
import util.IndexedSet;
*/

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class CCMIS {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<VSubSet> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

	public static long BoolDimBranch(IndexGraph G)
	{
//		System.out.println("Bigraph is");
//		System.out.println(G);
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);

		//all=rest U out
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		//rest =P
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//out =X
		long t= boolDimBranch(G,all,out,rest);

		
		return t;
	}

	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all = (out union rest)
	 * @param out =X the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest =P the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */

	public static long boolDimBranch(IndexGraph G, VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
		System.out.printf("out: %s\n", out);
		System.out.printf("rest: %s\n", rest);
		
		//check if P and X are empty
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con = BasicGraphAlgorithms.isConnected(G,all);

        //con = true; // hack

		//If not connected then call for components and multiply
		if(!con)
		{
			
			long total=1;
			//returns list of components
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total *= boolDimBranch(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		
		IndexVertex v = G.maxDegreeVertex(rest);
        System.out.printf("maxvertex: %s, degree: %s, neighbhors: %s\n", v, G.degree(v), G.neighbours(v));
		

		//if v is out
		
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		
		//Moving v to out
		rest.remove(v);
		out.add(v);
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		//check if there will be a vertex that cannot be dominated if v is OUT then outvalid=false otherwise call 
		while(changed)
		{
			changed = false;

			for(IndexVertex w : all)
			{
				if(!rest.intersects(neighbourhoods.get(w.id())))
				{
					if(out.contains(w)){
						outValid=false;
						break;
					}
					else
					{
						if(!toAdd.contains(w))
							toAdd.push(w);
					}
				}
				else
				{	if(out.contains(w) )
					{
						IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
						if(u!=null)
						{
							if(!toAdd.contains(u))
								toAdd.push(u);
						}
					}
				}
			}
			while(outValid && !toAdd.isEmpty())
			{
				IndexVertex x = toAdd.pop();
				if(rest.contains(x))
					putIn(removed, G, all, out, rest, x);
				else
					outValid=false;
				changed=true;
			}
		}


		long total = 0;
		if(outValid)
		{
			total = boolDimBranch(G, all, out, rest);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));
		out.remove(v);
		rest.add(v);

		//try v in
		removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());

		putIn(removed, G, all, out, rest, v);
		toAdd = new Stack<IndexVertex>();
		boolean inValid = true;
		changed = true;

		
		//check if there will be a vertex that cannot be dominated if v is IN then invalid=false otherwise call 
		while(changed)
		{
			changed = false;
			for(IndexVertex w : all)
			{
				if(!rest.intersects(neighbourhoods.get(w.id())))
				{
					if(out.contains(w)){
						inValid=false;
						break;
					}
					else
					{
						if(!toAdd.contains(w))
							toAdd.push(w);
					}
				}
				else
				{	if(out.contains(w))
					{
						IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
						if(u!=null)
						{
							if(!toAdd.contains(u))
								toAdd.push(u);
						}
					}
				}
			}
			while(inValid && !toAdd.isEmpty())
			{
				if(rest.contains(toAdd.peek()))
					putIn(removed, G, all, out, rest, toAdd.pop());
				else
					inValid = false;
				changed=true;
			}
		}
		if(inValid)
		{
//			System.out.println("branching with "+v+" in");
			total +=boolDimBranch(G, all, out, rest);
//			System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}
	
	private static void putIn(ArrayList<ArrayList<IndexVertex>> removed, IndexGraph g,VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
	{
		//System.out.println("Before adding "+v);
		//System.out.println(all+", "+out+", "+rest);
		all.remove(v);
		if(out.contains(v))
		{
			removed.get(0).add(v);
			out.remove(v);
			all.remove(v);
		}
		else if(rest.contains(v))
		{
			removed.get(1).add(v);
			rest.remove(v);
			all.remove(v);
		}
		for(IndexVertex w : g.neighbours(v))
		{
			if(out.contains(w))
			{
				removed.get(0).add(w);
				out.remove(w);
				all.remove(w);
			}
			else if(rest.contains(w))
			{
				removed.get(1).add(w);
				rest.remove(w);
				all.remove(w);
			}
		}
		//System.out.println("after adding "+v);
		//System.out.println(all+", "+out+", "+rest);

	}

}
