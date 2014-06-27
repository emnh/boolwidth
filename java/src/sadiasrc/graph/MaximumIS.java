package sadiasrc.graph;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.TreeMap;

import sadiasrc.util.IndexedSet;

public class MaximumIS {

	private static IndexedSet<IndexVertex> groundSet;

	public static long maximumIndependentSet(IndexGraph G)
	{
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//System.out.println("All"+all);
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return maximumIndependentSet(G,all,out,rest);
	}


	/**
	 * @param G The graph of which we want to compute the MaxIS
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the MaxIS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long maximumIndependentSet(IndexGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		
		//checking termination conditions
		System.out.println("all"+all);
		System.out.println("out"+out);
		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 0;
			else
				return Integer.MIN_VALUE;
		}

		Boolean isconnected= BasicGraphAlgorithms.isConnected(G,all);
		
		if(!isconnected)
		{
			System.out.println("Connected component found");
			long total=0;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total += maximumIndependentSet(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		//TODO: improve this choice
		IndexVertex v = G.maxDegreeVertex(rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		
		//Moving v to out
		rest.remove(v);
		out.add(v);
		
		long Outcount = 0;
		
		{
			System.out.println("branching with "+v+" out");
			Outcount = maximumIndependentSet(G, all, out, rest);
			//		System.out.println("total = "+total);
		}

		//move vertices back
		out.remove(v);
		rest.add(v);

		//try v in

		long Incount=0;
		
		//make rest\N[v] out\N(v)
		VSubSet newall = new VSubSet(groundSet);
		VSubSet newout = new VSubSet(groundSet);
		VSubSet newrest = new VSubSet(groundSet);
		
		newall.addAll(all);
		newrest.addAll(rest);
		newout.addAll(out);
		
		newall.removeAll(G.neighbours(v));
		newall.remove(v);
		newrest.removeAll(G.neighbours(v));
		newrest.remove(v);
		newout.removeAll(G.neighbours(v));
		newall.addAll(newrest);
		newall.addAll(newout);
		
		{
			System.out.println("branching with "+v+" in");
			Incount = 1+maximumIndependentSet(G, newall, newout, newrest);
		//		System.out.println("total = "+total);
		}

		return Math.max(Incount, Outcount);

	}

}
