package boolwidth.cutbool;

import sadiasrc.graph.*;
import sadiasrc.util.IndexedSet;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class CCMISHybrid {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<VSubSet> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

	private static VSubSet leftVertices;
	private static VSubSet rightVertices;
	private static VSubSet minLeftRightVertices;

	private static long zeroCount;
	
	public static long BoolDimBranch(BiGraph G)
	{
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
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());

        zeroCount = 0;

		long count = boolDimBranch(G, all, out, rest);

        // System.out.println("zero count: " + zeroCount);

        return count;
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
        // check termination conditions
		if(rest.isEmpty())
		{
			if(out.isEmpty()) {
                return 1;
            } else {
                // zeroCount++;
                return 0;
            }
		}

		// check to see if the graph is disconnected
        boolean isConnected = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);

		// if not connected then call for components and multiply
		if(!isConnected)
		{
			
			long total=1;
			for(VSubSet vs : BasicGraphAlgorithms.connectedComponentsVSubSets(G, all))
			{
				VSubSet newAll = vs;
				VSubSet newOut = newAll.intersection(out);
				VSubSet newRest = newAll.intersection(rest);

				long next = boolDimBranch(G,newAll,newOut,newRest);
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

        long total = 0;

        // try v out and branch
        VSubSet vOutAll = all.clone();
        VSubSet vOutRest = rest.clone();
        VSubSet vOutOut = out.clone();

		// moving v to out
		vOutRest.remove(v);
		vOutOut.add(v);
		if (isValid(G, vOutAll, vOutOut, vOutRest)) {
			total = boolDimBranch(G, vOutAll, vOutOut, vOutRest);
		}

		// try v in and branch
        VSubSet vInAll = all.clone();
        VSubSet vInRest = rest.clone();
        VSubSet vInOut = out.clone();
		putIn(G, vInAll, vInOut, vInRest, v);
        if (isValid(G, vInAll, vInOut, vInRest)) {
			total += boolDimBranch(G, vInAll, vInOut, vInRest);
		}

		return total;
	}

    /* look for vertices that can not be dominated and vertices that have to be in */
    private static boolean isValid(BiGraph G, VSubSet all, VSubSet out, VSubSet rest) {
        boolean valid = true;
        boolean changed = true;
        Stack<IndexVertex> toAdd = new Stack<>();

        // check if there will be a vertex that cannot be dominated if v is OUT then outValid = false otherwise branch
        while(changed)
        {
            changed = false;

            for(IndexVertex w : all) {
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
                if(intersection.isEmpty()) {
                    if(out.contains(w)){
                        valid = false;
                        break;
                    } else {
                        if(!toAdd.contains(w)) {
                            toAdd.push(w);
                        }
                    }
                } else {
                    if(out.contains(w) && (intersection.size() == 1)) {
                        IndexVertex u = intersection.first();
                        if(u != null) {
                            if(!toAdd.contains(u)) {
                                toAdd.push(u);
                            }
                        }
                    }
                }
            }
            while(valid && !toAdd.isEmpty())
            {
                IndexVertex x = toAdd.pop();
                if(rest.contains(x)) {
                    putIn(G, all, out, rest, x);
                } else {
                    valid = false;
                }
                changed=true;
            }
        }
        return valid;
    }

    private static void putIn(IndexGraph g, VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
	{
		all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
	}

}
