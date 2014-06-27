package sadiasrc.decomposition;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import javax.naming.directory.InvalidAttributesException;
import sadiasrc.util.IndexedSet;
import sadiasrc.util.SubSet;
import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IVSet;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.VertexSet;


/**
 * A representation of a decomposition of a graph. A decomposition is a binary
 * tree and children of a node represents a cut in the graph.
 */

/**
 * @author mva021
 *
 */
public class BoolDecomposition extends DisjointBinaryDecomposition {

	public static long countNeighbourhoods(DisjointBinaryDecomposition decomp) {
		return countNeighbourhoods(decomp, decomp.root());
	}

	public static long countNeighbourhoods(DisjointBinaryDecomposition decomp,
			DecompNode root) {
		return 0;
	}

	public BoolDecomposition(IndexGraph graph) {
		super(graph);
	}

	// add egde between two decomposition nodes
	private static boolean addEdge(DecompNode node1,DecompNode node2)
	{
		return false;
	}


	//moving vertex from one leaf to another leaf of decomposition
	//return true if move successful
	private static boolean move(IndexVertex v, DecompNode leaf1, DecompNode leaf2)
	{
		if(!(leaf1.subSet(leaf1).contains(v)))
				return false;
		if(!(leaf2.subSet(leaf2).contains(v)))
			return true;
		leaf1.getGraphSubSet().remove(v);
		leaf2.getGraphSubSet().add(v);
		return true;

	}
	// method used to recursively split non-root nodes into two halves
	private static <V, E> void evenSplit(DisjointBinaryDecomposition decomp,
			Stack<DecompNode> s) {
		while (!s.isEmpty()) {
			DecompNode root = s.pop();

			if (root.isLeaf) {
				continue;
			}
			boolean left = true;
			for (IndexVertex v : root.getGraphSubSet().vertices()) {
				if (left) {
					decomp.addLeft(root, v);
				} else {
					decomp.addRight(root, v);
				}
				left = !left;
			}
			s.add(decomp.left(root));
			s.add(decomp.right(root));

		}
	}

	private static TreeMap<VSubSet,Boolean> isConnected;
	private static int numCon=0;
	private static int numElse=0;
	static int small;
	private static ArrayList<VSubSet> neighbourhoods;
	private static IndexedSet<IndexVertex> groundSet;

	public static long boolDimBranch(IndexGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return boolDimBranch(G,all,out,rest);
	}


	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long boolDimBranch(IndexGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
//		System.out.println("out"+out);
//		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			numCon++;
			long total=1;
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
		//TODO: improve this choice
		IndexVertex v = G.maxDegreeVertex(rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		System.out.println("choosen"+v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
	//		System.out.println("branching with "+v+" out");
			total = boolDimBranch(G, all, out, rest);
//			System.out.println("total = "+total);
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
	//		System.out.println("branching with "+v+" in");
			total += boolDimBranch(G, all, out, rest);
//			System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}
	public static long BoolDimBranch(BiGraph G)
	{
//		System.out.println("Bigraph is");
//		System.out.println(G);
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		long t= boolDimBranch(G,all,out,rest);
		//System.out.println("Cut bool is"+t);
		return t;
	}



	public static long boolDimBranch(BiGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
//		System.out.println("out"+out);
//		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
//			if(out.isEmpty())
//				return new BigInteger("1");
//			else
//				return new BigInteger("0");
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			numCon++;
			long total=1;
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
		//TODO: improve this choice
		IndexVertex v = G.maxDegreeVertex(rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
//			System.out.println("branching with "+v+" out");
			total = boolDimBranch(G, all, out, rest);
//			System.out.println("total = "+total);
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
	public static long boolDimBranchmin(BiGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return boolDimBranchmin(G,all,out,rest);
	}


	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long boolDimBranchmin(BiGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
//		System.out.println("out"+out);
//		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			numCon++;
			long total=1;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total *= boolDimBranchmin(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		//TODO: improve this choice
		IndexVertex v = G.MinDegreeVertex(rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
//			System.out.println("branching with "+v+" out");
			total = boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
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
			total += boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}
	
	///mindeg indexgraph
	public static long boolDimBranchmin(IndexGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return boolDimBranchmin(G,all,out,rest);
	}


	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long boolDimBranchmin(IndexGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
	
		if(rest.isEmpty())
		{
			if(out.isEmpty())
			{
				//System.out.println("Returning 1");
				return 1;
			}
			else
			{
				//System.out.println("Returning 0");
				return 0;
			}
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			//System.out.println("Connected components");
			numCon++;
			long total=1;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total *= boolDimBranchmin(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		//TODO: improve this choice
		IndexVertex v = G.MinDegreeVertex(rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		//System.out.println("here");
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
			//System.out.println("branching with "+v+" out");
			total = boolDimBranchmin(G, all, out, rest);
			//System.out.println("total = "+total);
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
			//System.out.println("branching with "+v+" in");
			total += boolDimBranchmin(G, all, out, rest);
			//System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}
	public static long boolDimBranchCutVertex(BiGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return boolDimBranchmin(G,all,out,rest);
	}


	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long boolDimBranchCutVertex(BiGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
//		System.out.println("out"+out);
//		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			numCon++;
			long total=1;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total *= boolDimBranchmin(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		//TODO: improve this choice
		IndexVertex v = BasicGraphAlgorithms.cutVertex(G,rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
//			System.out.println("branching with "+v+" out");
			total = boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
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
			total += boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}
	//cutvertex indexgraph
	public static long boolDimBranchCutVertex(IndexGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());
		//TODO: change Graph to return Collection instead of Iterable, at least IndexGraph
		return boolDimBranchmin(G,all,out,rest);
	}


	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all out union rest
	 * @param out the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */
	public static long boolDimBranchCutVertex(IndexGraph G,VSubSet all,  VSubSet out, VSubSet rest)
	{
		//checking termination conditions
		
//		System.out.println("out"+out);
//		System.out.println("rest"+rest);
		if(rest.isEmpty())
		{
			if(out.isEmpty())
				return 1;
			else
				return 0;
		}

		//check to see if the graph is disconneced
		Boolean con;
		if(isConnected.containsKey(all))
		{
			con = isConnected.get(all);
			if(con==null)
				con = true;
		}
		else
		{
			con = BasicGraphAlgorithms.isConnected(G,all);
			isConnected.put(all.clone(), con);
		}

		if(!con)
		{
			numCon++;
			long total=1;
			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
			{
				VSubSet nall = new VSubSet(groundSet);
				nall.addAll(vs);
				VSubSet nout = nall.clone();
				VSubSet nrest = nall.clone();
				nout.retainAll(out);
				nrest.retainAll(rest);

				total *= boolDimBranchmin(G,nall,nout,nrest);
//				System.out.println("total = "+total);
			}

			return total;
		}

		//Find a vertex to branch on
		//TODO: improve this choice
		IndexVertex v = BasicGraphAlgorithms.cutVertex(G,rest);
		

		//if v is out
		//look for vertices that can not be dominated
		//and vertices that have to be in
		boolean outValid = true;
		boolean changed = true;
		//0=from out 1=from rest
		//Moving v to out
		rest.remove(v);
		out.add(v);
//		if(v.id()==3)
//		{	System.out.println("Staring 3 out");
//			System.out.println("out: "+out);
//			System.out.println("rest: "+rest);
//		}
		
		ArrayList<ArrayList<IndexVertex>> removed = new ArrayList<ArrayList<IndexVertex>>(2);
		removed.add(new ArrayList<IndexVertex>());
		removed.add(new ArrayList<IndexVertex>());
		Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

		while(changed)
		{
			changed = false;
//			Iterable<IndexVertex> iter;
//			if(first)
//			{	iter = G.neighbours(v);
//				ArrayList<IndexVertex> closedNeighbourhood = new ArrayList<IndexVertex>();
//				first = false;
//			}
//			else
//				iter =  all;

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
//			System.out.println("branching with "+v+" out");
			total = boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
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
			total += boolDimBranchmin(G, all, out, rest);
//			System.out.println("total = "+total);
		}

		//move vertices back
		out.addAll(removed.get(0));
		all.addAll(removed.get(0));
		rest.addAll(removed.get(1));
		all.addAll(removed.get(1));

		return total;

	}

	private static void putIn(ArrayList<ArrayList<IndexVertex>> removed, BiGraph g,VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
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

//	public static long boolDimBranch(BiGraph G, VertexSet<IndexVertex> in, VertexSet<IndexVertex> out, VertexSet<IndexVertex> outDom, VertexSet<IndexVertex> rest,int d)
//	{
//		if(rest.isEmpty())
//		{
//			if(out.isEmpty())
//				return 1;
//			else
//				return 0;
//		}
//		if(rest.size()<=10)
//			small++;
//		VertexSet<IndexVertex> unfinished = new VertexSet<IndexVertex>(rest);
//		for(IndexVertex v : out)
//		{
//			VertexSet<IndexVertex> ns = new VertexSet<IndexVertex>(G.neighbours(v));
//			ns.retainAll(rest);
//			if(ns.size()==0)
//				return 0;
//			else
//				unfinished.add(v);
//		}
////		System.out.println("checking connectivity");
////		rest.db();
//		boolean con;
//		if(isConnected.containsKey(unfinished))
//			con = isConnected.get(unfinished);
//		else
//		{
//			con = BasicGraphAlgorithms.isConnected(G,unfinished);
//			isConnected.put(unfinished, con);
//		}
//
//		if(!con)
//		{
//			numCon++;
//			long total=1;
//			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,unfinished))
//			{
////				System.out.println(G+","+vs);
//				VertexSet<IndexVertex> nv = new VertexSet<IndexVertex>(vs);
//				VertexSet<IndexVertex> nin = new VertexSet<IndexVertex>(in);
//				VertexSet<IndexVertex> nout = new VertexSet<IndexVertex>(out);
//				VertexSet<IndexVertex> noutDom = new VertexSet<IndexVertex>(outDom);
//				//nin.retainAll(nv);
//				nout.retainAll(nv);
//				//noutDom.retainAll(nv);
//	//			System.out.println("TESTING RETAIN");
//				VertexSet<IndexVertex> nrest = new VertexSet<IndexVertex>(rest);
////				System.out.println(nrest);
//	//			System.out.println("RETAIN: "+nv);
//				nrest.retainAll(nv);
//
////				System.out.println(nrest);
//	//			System.out.println("END TESTING RETAIN");
//
////				System.out.println("Total: "+in.size()+","+out.size()+","+rest.size());
////				System.out.println("New Sets:\n"+vs+"\n"+nin+"\n"+nout+"\n"+nrest);
////				System.out.println("Current: "+vs.size()+" split into: "+nin.size()+","+nout.size()+","+nrest.size());
////				System.out.println("Calling with disconnected components");
//				total *= boolDimBranch(G,nin,nout,noutDom,nrest,d+1);
//			}
//			return total;
//		}
//		//TODO: fix max deg method to find max deg in rest
//		numElse++;
////		System.out.println("Looking through: "+rest);
//		rest.db();
//		IndexVertex v = G.maxDegreeVertex(rest);
////		System.out.println("Found: "+v);
//		rest.db();
//		//considering if the maxdegree vertex is out
////		System.out.println("removing "+v);
//		rest.remove(v);
//		rest.db();
//
//		//if v is not used
//		out.add(v);
////		System.out.println("Calling with maxdegree vertex"+v+"out");
////		String input = "in:"+in+" out:"+out+" outDom:"+outDom+" rest:"+rest;
//		long value= boolDimBranch(G, in, out, outDom, rest,d+1);
//
////		System.out.println(input+" Returned "+value);
//		long total = value;
//		//System.out.println("Total is "+total);
//		///
//		//if v is used
//		out.remove(v);
//		in.add(v);
//		ArrayList<IndexVertex> outNeighbours = new ArrayList<IndexVertex>();
//		ArrayList<IndexVertex> restNeighbours = new ArrayList<IndexVertex>();
//
//		for(IndexVertex w:G.neighbours(v))
//		{
//			if(rest.contains(w))
//				restNeighbours.add(w);
//			if(out.contains(w))
//				outNeighbours.add(w);
//		}
//		rest.db();
////		System.out.println("removing neighbours");
//		rest.removeAll(restNeighbours);
//		out.removeAll(outNeighbours);
//		outDom.addAll(restNeighbours);
//		outDom.addAll(outNeighbours);
//
//		/// insert here which part to exclude
//
////		System.out.println("Calling with maxdegree vertex"+v+"out with it neighbors deleted from rest");
////		value= boolDimBranch(G, in, out, rest,d+1);
////		System.out.println("Returned value is "+value);
////		total-= value;
////		System.out.println("Total is "+total);
//
//		///
//
//
//		//considering if maxdegree vertex is in
//		//System.out.println("Calling with maxdegree vertex"+v+"in and removing its neighbours from rest");
////		input = "in:"+in+" out:"+out+" outDom:"+outDom+" rest:"+rest;
//		value= boolDimBranch(G, in, out, outDom, rest,d+1);
////		System.out.println(input+" returned "+value);
//		total += value;
//		//System.out.println("Total is "+total);
////		rest.db();
////		System.out.println("restoring");
//		//restoring sets
//		out.addAll(outNeighbours);
//		rest.addAll(restNeighbours);
//		outDom.removeAll(restNeighbours);
//		outDom.removeAll(outNeighbours);
//		in.remove(v);
//		rest.add(v);
////		rest.db();
//
//		return total;
//
//	}
//	public static long boolDimBranch2(BiGraph G)
//	{
//		return boolDimBranch2(G, new VertexSet<IndexVertex>(),new VertexSet<IndexVertex>(),new VertexSet<IndexVertex>(),new VertexSet<IndexVertex>(G.vertices()),0);
//	}
//	public static long boolDimBranch2(BiGraph G, VertexSet<IndexVertex> in, VertexSet<IndexVertex> out, VertexSet<IndexVertex> outDom, VertexSet<IndexVertex> rest,int d)
//	{
//		//if(d<5)
//		//	System.out.println("Sets:\n in"+in+"\n out"+out+"\n outDom"+outDom+"\n rest"+rest);
//
//		if(rest.size()==0)
//		{
//			if(out.size()==0)
//				return 1;
//			else
//				return 0;
//		}
//		VertexSet<IndexVertex> unfinished = new VertexSet<IndexVertex>(rest);
//		for(IndexVertex v : out)
//		{
//			VertexSet<IndexVertex> ns = new VertexSet<IndexVertex>(G.neighbours(v));
//			ns.retainAll(rest);
//			if(ns.size()==0)
//				return 0;
//			else
//				unfinished.add(v);
//		}
//
//		if(!BasicGraphAlgorithms.isConnected(G,unfinished))
//		{
//			long total=1;
//			for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,unfinished))
//			{
////				System.out.println(G+","+vs);
//				VertexSet<IndexVertex> nv = new VertexSet<IndexVertex>(vs);
//				VertexSet<IndexVertex> nin = new VertexSet<IndexVertex>(in);
//				VertexSet<IndexVertex> nout = new VertexSet<IndexVertex>(out);
//				VertexSet<IndexVertex> noutDom = new VertexSet<IndexVertex>(outDom);
//				//nin.retainAll(nv);
//				nout.retainAll(nv);
//				//noutDom.retainAll(nv);
////				System.out.println("TESTING RETAIN");
//				VertexSet<IndexVertex> nrest = new VertexSet<IndexVertex>(rest);
////				System.out.println(nrest);
////				System.out.println("RETAIN: "+nv);
//				nrest.retainAll(nv);
//
////				System.out.println(nrest);
////				System.out.println("END TESTING RETAIN");
//
////				System.out.println("Total: "+in.size()+","+out.size()+","+rest.size());
////				System.out.println("New Sets:\n"+vs+"\n"+nin+"\n"+nout+"\n"+nrest);
////				System.out.println("Current: "+vs.size()+" split into: "+nin.size()+","+nout.size()+","+nrest.size());
//				//System.out.println("Calling with disconnected components");
//				total *= boolDimBranch2(G,nin,nout,noutDom,nrest,d+1);
//			}
//			return total;
//		}
//		//TODO: fix max deg method to find max deg in rest
//
//		IndexVertex v = G.MinDegreeVertex(rest);
//
//		//considering if the neighbor of mindegree vertex is out
//		rest.remove(v);
////		System.out.println("removing "+v);
//
//		//if v is not used
//		out.add(v);
//		//System.out.println("Calling with neighbor of mindegree vertex"+v+"out");
//		long value= boolDimBranch2(G, in, out, outDom, rest,d+1);
//		//System.out.println("Returned value is "+value);
//		long total = value;
//		//System.out.println("Total is "+total);
//		///
//		//if v is used
//		out.remove(v);
//		in.add(v);
//		ArrayList<IndexVertex> outNeighbours = new ArrayList<IndexVertex>();
//		ArrayList<IndexVertex> restNeighbours = new ArrayList<IndexVertex>();
//
//		for(IndexVertex w:G.neighbours(v))
//		{
//			if(rest.contains(w))
//				restNeighbours.add(w);
//			if(out.contains(w))
//				outNeighbours.add(w);
//		}
//
//		rest.removeAll(restNeighbours);
//		out.removeAll(outNeighbours);
//		outDom.addAll(restNeighbours);
//		outDom.addAll(outNeighbours);
//
//		/// insert here which part to exclude
//
////		System.out.println("Calling with maxdegree vertex"+v+"out with it neighbors deleted from rest");
////		value= boolDimBranch(G, in, out, rest,d+1);
////		System.out.println("Returned value is "+value);
////		total-= value;
////		System.out.println("Total is "+total);
//
//		///
//
//
//		//considering if maxdegree vertex is in
//		//System.out.println("Calling with maxdegree vertex"+v+"in and removing its neighbours from rest");
//		value= boolDimBranch2(G, in, out, outDom, rest,d+1);
//		//System.out.println("Returned value is "+value);
//		total += value;
//		//System.out.println("Total is "+total);
//
//		//restoring sets
//		out.addAll(outNeighbours);
//		rest.addAll(restNeighbours);
//		outDom.removeAll(restNeighbours);
//		outDom.removeAll(outNeighbours);
//		in.remove(v);
//		rest.add(v);
//		return total;
//
//	}


	//Bron-Kerbosch Recursive backtracking
	public static long BK1(BiGraph G)
	{
		return BK1(G, new VertexSet<IndexVertex>(),new VertexSet<IndexVertex>(G.vertices()),new VertexSet<IndexVertex>(),0);
	}
	public static long BK1(BiGraph G, VertexSet<IndexVertex> R,VertexSet<IndexVertex> P,VertexSet<IndexVertex> X,long total)
	{
		System.out.println("R"+R+"P"+P+"X"+X);
		long t=0;

		for(IndexVertex v:P)
		{
			R.add(v);
			VertexSet<IndexVertex> Pn= new VertexSet<IndexVertex>(P);

			VertexSet<IndexVertex> Xn= new VertexSet<IndexVertex>(X);

			VertexSet<IndexVertex> NvinXn=new VertexSet<IndexVertex>();
			for(IndexVertex w:G.neighbours(v))
				if(Xn.contains(w))
						NvinXn.add(w);
			Xn.removeAll(NvinXn);

			Pn.remove(v);
			VertexSet<IndexVertex> NvinPn=new VertexSet<IndexVertex>();
			for(IndexVertex w:G.neighbours(v))
				if(Pn.contains(w))
						NvinPn.add(w);
			Pn.removeAll(NvinPn);

			if(Pn.size()==0 && Xn.size()==0)
			{
				System.out.println("R"+R);
				total++;
				System.out.println("Increased total is "+total);

			}
			else if (Xn.size()!=0 && Pn.size()==0)
				continue;
			else
				total=BK1(G,R,Pn,Xn,total);

			R.remove(v);
			X.add(v);
		}
		return total;

	}



	private static boolean isOK(BiGraph g, VertexSet<IndexVertex> in,
			VertexSet<IndexVertex> out) {
		for(IndexVertex v : out)
		{
			boolean hasIn=false;
			for(IndexVertex w:g.neighbours(v))
			{
				if(in.contains(w))
				{	hasIn=true;
					break;
				}
			}
			if(!hasIn)
				return false;
		}
		for(IndexVertex v : in)
		{
			boolean hasIn=false;
			for(IndexVertex w:g.neighbours(v))
			{
				if(in.contains(w))
				{
					hasIn=true;
					break;
				}
			}
			if(hasIn)
				return false;
		}
		return true;
	}

	public static void setBoolWidth(BoolDecomposition decomposition, int hoods) {
		// TODO Auto-generated method stub

	}
}