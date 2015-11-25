package sadiasrc.decomposition;

import sadiasrc.graph.*;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;


import sadiasrc.util.IndexedSet;

public class CCMIS {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<VSubSet> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

	private static VSubSet leftVertices;
	private static VSubSet rightVertices;
	private static VSubSet minLeftRightVertices;
	
	public static long BoolDimBranch(BiGraph G)
	{
//		System.out.println("Bigraph is");
//		System.out.println(G);
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet, G.neighbours(G.getVertex(i))));
		}

		leftVertices = new VSubSet(groundSet);
		leftVertices.addAll(G.leftVertices());
		rightVertices = new VSubSet(groundSet);
		rightVertices.addAll(G.rightVertices());

		minLeftRightVertices = leftVertices.size() >= rightVertices.size() ? rightVertices : leftVertices;
		
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		//all=rest U out
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		//rest =P
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//out =X
		return boolDimBranch(G,all,out,rest);
	}

	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all = (out union rest)
	 * @param out =X the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest =P the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */

	public static long boolDimBranch(BiGraph G, VSubSet all, VSubSet out, VSubSet rest)
	{
		//checking termination conditions

        //System.out.printf("all: %s\n", all);
        //System.out.printf("out: %s\n", out);
        //System.out.printf("rest: %s\n", rest);

		//check if P and X are empty
		if(rest.isEmpty())
		{
			if(out.isEmpty()) {
                return 1;
            } else {
                //System.out.println("no chance");
                return 0;
            }
		}

		// check to see if the graph is disconnected
        boolean isConnected = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);

		// if not connected then call for components and multiply
		if(!isConnected)
		{
			
			long total=1;
			// returns list of components
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G, all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				long next = boolDimBranch(G,nall,nout,nrest);
                if (next == 0) return 0;
                total = Math.multiplyExact(total, next);
			}

			return total;
		}

		// find a vertex to branch on
		int maxDeg = -1;
		IndexVertex v = null;
		VSubSet selection = rest.intersection(minLeftRightVertices);
		for (IndexVertex w : selection)	{
			int t = neighbourhoods.get(w.id()).intersection(rest).size();
			if(t > maxDeg) {
				v = w;
				maxDeg = t;
			}
		}
		if (v == null) {
			selection = rest;
			for (IndexVertex w : selection)	{
				int t = neighbourhoods.get(w.id()).intersection(rest).size();
				if(t > maxDeg) {
					v = w;
					maxDeg = t;
				}
			}
		}

		// IndexVertex v = G.maxDegreeVertex(rest);
        // System.out.printf("maxvertex: %s, degree: %s, neighbours: %s\n\n", v, G.degree(v), G.neighbours(v));

		//if v is out
		
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest

        VSubSet oldAll = all.clone();
        VSubSet oldRest = rest.clone();
        VSubSet oldOut = out.clone();

		//Moving v to out
		rest.remove(v);
		out.add(v);

		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		//check if there will be a vertex that cannot be dominated if v is OUT then outvalid=false otherwise call 
		while(changed)
		{
			changed = false;

			for(IndexVertex w : all)
			{
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
				if(intersection.isEmpty())
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
				{
                    if(out.contains(w) && (intersection.size() == 1))
					{
						//IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
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
			while(outValid && !toAdd.isEmpty())
			{
				IndexVertex x = toAdd.pop();
				if(rest.contains(x)) {
                    putIn(G, all, out, rest, x);
                } else {
                    outValid = false;
                }
				changed=true;
			}
		}

		long total = 0;
		if(outValid)
		{
			total = boolDimBranch(G, all, out, rest);
		}

		// move vertices back
        out.cloneInPlace(oldOut);
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);

		// try v in
		putIn(G, all, out, rest, v);
		toAdd = new Stack<IndexVertex>();
		boolean inValid = true;
		changed = true;

		// check if there will be a vertex that cannot be dominated if v is IN then invalid=false otherwise call
		while(changed)
		{
			changed = false;
			for(IndexVertex w : all)
			{
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
				//if(!rest.intersects(neighbourhoods.get(w.id())))
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
				{
                    if(out.contains(w) && (intersection.size() == 1))
					{
						//IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
                        IndexVertex u = intersection.first();
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
				if(rest.contains(toAdd.peek())) {
                    putIn(G, all, out, rest, toAdd.pop());
                } else {
                    inValid = false;
                }
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

		return total;

	}
	
	private static void putIn(IndexGraph g, VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
	{
		//System.out.println("Before adding "+v);
		//System.out.println(all+", "+out+", "+rest);
		all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
	}

}
