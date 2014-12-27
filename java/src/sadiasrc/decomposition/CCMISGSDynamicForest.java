package sadiasrc.decomposition;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import graph.algorithms.dynamic_forest.DynamicForest;
import sadiasrc.graph.*;
import sadiasrc.util.IndexedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;

public class CCMISGSDynamicForest {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<VSubSet> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

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

		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		//all=rest U out
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		//rest =P
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//out =X

		// Create dynamic forest
		Graph dfGraph = new DefaultGraph("CCMIS");
		ConnectedComponents cc = new ConnectedComponents();

		for(int i=0; i < G.numVertices(); i++) {
			IndexVertex v = G.getVertex(i);
			dfGraph.addNode("" + v.id());
		}
		for(int i=0; i < G.numVertices(); i++) {
			IndexVertex v = G.getVertex(i);
			for (IndexVertex u : G.neighbours(v)) {
				//System.out.printf("add edge: %d - %d\n", u.id(), v.id());
				if (v.id() < u.id()) {
					dfGraph.addEdge("" + v.id() + "," + u.id(), "" + v.id(), "" + u.id());
				}
			}
		}
		cc.init(dfGraph);
		cc.setCutAttribute("Hide");

		return boolDimBranch(dfGraph, cc, G, all, out, rest);
	}

	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all = (out union rest)
	 * @param out =X the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest =P the set of vertices not yet considered
	 * @return
	 * @throws java.security.InvalidAlgorithmParameterException
	 */

	public static long boolDimBranch(Graph dfGraph, ConnectedComponents cc, BiGraph G, VSubSet all, VSubSet out, VSubSet rest)
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

		//check to see if the graph is disconneced
        boolean isConnected = true;

		//boolean isConnectedBasic = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);
		boolean isConnectedBasic = true;
		boolean isConnectedDF = cc.getConnectedComponentsCount() <= 1;
		isConnected = isConnectedBasic;
		if (isConnectedBasic != isConnectedDF) {
			//System.out.printf("isConnected: %s, %s, cc: %d\n", isConnectedBasic, isConnectedDF, cc.getConnectedComponentsCount());
		}

		//If not connected then call for components and multiply
		if(!isConnected)
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

				// Create dynamic forest
				Graph newDFGraph = new DefaultGraph("CCMIS");
				for (IndexVertex v : all) {
					newDFGraph.addNode("" + v.id());
				}
				for (IndexVertex v : all) {
					for (IndexVertex u : G.neighbours(v)) {
						if (v.id() < u.id() && all.contains(u)) {
							newDFGraph.addEdge("" + v.id() + "," + u.id(), "" + v.id(), "" + u.id());
						}
					}
				}
				ConnectedComponents newCC = new ConnectedComponents();
				newCC.init(newDFGraph);
				newCC.setCutAttribute("Hide");

				long next = boolDimBranch(newDFGraph, newCC, G, nall, nout, nrest);
                if (next == 0) return 0;
                total = Math.multiplyExact(total, next);
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
                    putIn(dfGraph, G, all, out, rest, x);
                } else {
                    outValid = false;
                }
				changed=true;
			}
		}

		long total = 0;
		if(outValid)
		{
			total = boolDimBranch(dfGraph, cc, G, all, out, rest);
		}

		// move vertices back
        out.cloneInPlace(oldOut);
		for (IndexVertex x : oldAll.subtract(all)) {
			if (dfGraph.getNode("" + x.id()) == null) dfGraph.addNode("" + x.id());
		}
		for (IndexVertex x : oldAll.subtract(all)) {
			//System.out.printf("adding back1: %s\n", x);
			for (IndexVertex y : G.neighbours(x)) {
				if (oldAll.contains(y) && x.id() < y.id()) {
					String edgeID = "" + x.id() + "," + y.id();
					if (dfGraph.getEdge(edgeID) == null) {
						dfGraph.addEdge(edgeID, "" + x.id(), "" + y.id());
					}
				}
			}
		}
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);

		// try v in
		putIn(dfGraph, G, all, out, rest, v);
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
                    putIn(dfGraph, G, all, out, rest, toAdd.pop());
                } else {
                    inValid = false;
                }
				changed=true;
			}
		}
		if(inValid)
		{
//			System.out.println("branching with "+v+" in");
			total +=boolDimBranch(dfGraph, cc, G, all, out, rest);
//			System.out.println("total = "+total);
		}

		// move vertices back
        out.cloneInPlace(oldOut);
		for (IndexVertex x : oldAll.subtract(all)) {
			if (dfGraph.getNode("" + x.id()) == null) dfGraph.addNode("" + x.id());
		}
		for (IndexVertex x : oldAll.subtract(all)) {
			//System.out.printf("adding back1: %s\n", x);
			for (IndexVertex y : G.neighbours(x)) {
				if (oldAll.contains(y) && x.id() < y.id()) {
					String edgeID = "" + x.id() + "," + y.id();
					if (dfGraph.getEdge(edgeID) == null) {
						dfGraph.addEdge(edgeID, "" + x.id(), "" + y.id());
					}
				}
			}
		}
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);

		return total;

	}
	
	private static void putIn(Graph dfGraph, IndexGraph g, VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
	{
		//System.out.println("Before adding "+v);
		//System.out.println(all+", "+out+", "+rest);
		if (all.contains(v)) {
			//System.out.printf("cutting v: %s, all: %s\n", v, all);
			dfGraph.removeNode("" + v.id());
		}
		all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
		for (IndexVertex u : hood) {
			if (all.contains(u)) {
				//System.out.printf("cutting u: %s, all: %s\n", u, all);
				dfGraph.removeNode("" + u.id());
			}
		}
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
	}

}
