package boolwidth.cutbool.ccmis_trial;

/*
import graph.IndexGraph;
import graph.IndexVertex;
import graph.subsets.PosSubSet<IndexVertex>;
import util.IndexedSet;
*/

import graph.AdjacencyListGraph;
import graph.subsets.PosSubSet;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class CCMISRe {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<PosSubSet<IndexVertex>> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

	public static <V, E> long BoolDimBranch(AdjacencyListGraph<IndexVertex, Integer, String> G)
	{
//		System.out.println("Bigraph is");
//		System.out.println(G);
		neighbourhoods = new ArrayList<PosSubSet<IndexVertex>>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new PosSubSet<IndexVertex>(groundSet,G.neighbours(G.getVertex(i))));
		}

		PosSubSet<IndexVertex> all = new PosSubSet<IndexVertex>(groundSet);
		PosSubSet<IndexVertex> out = new PosSubSet<IndexVertex>(groundSet);
		PosSubSet<IndexVertex> rest = new PosSubSet<IndexVertex>(groundSet);

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

	public static long boolDimBranch(AdjacencyListGraph<IndexVertex, Integer, String> G,
                                     PosSubSet<IndexVertex> all,  PosSubSet<IndexVertex> out, PosSubSet<IndexVertex> rest)
	{
		//checking termination conditions

        //if (!all.equals(out.union(rest))) {
        //    System.out.printf("all: %s\n", all);
        //    System.out.printf("out: %s\n", out);
        //    System.out.printf("rest: %s\n", rest);
            //System.out.printf("equals: %b\n", all.equals(out.union(rest)));
        //}
		
		//check if P and X are empty
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);

        // con = true; // hack

		//If not connected then call for components and multiply
		if(!con)
		{
			
			long total=1;
			//returns list of components
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				PosSubSet<IndexVertex> nall = new PosSubSet<IndexVertex>(groundSet);
				nall.addAll(vs);
                PosSubSet<IndexVertex> nout = nall.intersection(out);
                PosSubSet<IndexVertex> nrest = nall.intersection(rest);
				/*PosSubSet<IndexVertex> nout = nall.clone();
				PosSubSet<IndexVertex> nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);*/

				long next = boolDimBranch(G,nall,nout,nrest);
                if (next == 0) return 0;
                total *= next;
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		
		IndexVertex v = G.maxDegreeVertex(rest);
        //System.out.printf("maxvertex: %s, degree: %s, neighbours: %s\n\n", v, G.degree(v), G.neighbours(v));

		//if v is out
		
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest

        PosSubSet<IndexVertex> oldAll = all.clone();
        PosSubSet<IndexVertex> oldRest = rest.clone();
        PosSubSet<IndexVertex> oldOut = out.clone();

		//Moving v to out
		rest.remove(v);
		out.add(v);

		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		// check if there will be a vertex that cannot be dominated if v is OUT then outvalid=false otherwise call
		while(changed)
		{
			changed = false;

			for(IndexVertex w : all)
			{
                PosSubSet<IndexVertex> intersection = rest.intersection(neighbourhoods.get(w.id()));
				if(intersection.isEmpty())
				{
					if(out.contains(w)){
						outValid=false;
						break;
					}
					else
					{
                        // If w has no neighbor in rest, and is in rest (not out), it must be in this MIS,
                        // because if we put it out, it would make outValid false.
                        // It already has no neighbors in the MIS, because
                        // we always remove all neighbors from rest and all when we put in a node.
						if(!toAdd.contains(w))
							toAdd.push(w);
					}
				}
				else
                {
                    // If w is in out,
                    // one of its neighbors must be in this MIS,
                    // otherwise we could add w, so it would not be maximal.
                    if(out.contains(w) && intersection.size() == 1)
					{
						IndexVertex u = intersection.first();
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
					putIn(all, out, rest, x);
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
        out.cloneInPlace(oldOut);
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);
        //System.out.printf("old all: %s\n", all);
        //System.out.printf("old out: %s\n", out);
        //System.out.printf("old rest: %s\n", rest);

		//try v in
		putIn(all, out, rest, v);
		toAdd = new Stack<IndexVertex>();
		boolean inValid = true;
		changed = true;

		//check if there will be a vertex that cannot be dominated if v is IN then invalid=false otherwise call 
		while(changed)
		{
			changed = false;
			for(IndexVertex w : all)
			{
                PosSubSet<IndexVertex> intersection = rest.intersection(neighbourhoods.get(w.id()));
				if(intersection.isEmpty())
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
				{	if(out.contains(w) && intersection.size() == 1)
					{
						IndexVertex u = intersection.first();
                        //System.out.printf("first vertex: %d\n", u.id());
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
					putIn(all, out, rest, toAdd.pop());
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

        // move vertices back
        out.cloneInPlace(oldOut);
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);
        //System.out.printf("old ret all: %s\n", all);
        //System.out.printf("old ret out: %s\n", out);
        //System.out.printf("old ret rest: %s\n", rest);

		return total;

	}
	
	private static void putIn(PosSubSet<IndexVertex> all, PosSubSet<IndexVertex> out, PosSubSet<IndexVertex> rest, IndexVertex v)
	{
		//System.out.println("Before adding "+v);
		//System.out.println(all+", "+out+", "+rest);
		all.remove(v);
        out.remove(v);
		rest.remove(v);

        PosSubSet<IndexVertex> hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
		//System.out.println("after adding "+v);
		//System.out.println(all+", "+out+", "+rest);
	}

}
