package sadiasrc.decomposition;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.VertexSet;
import sadiasrc.util.IndexedSet;

import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Contains methods for finding the boolean-width of a graph.
 */
public class CutBool {

	public final static int BOUND_EXCEEDED = 0;

	public final static int BOUND_UNINITIALIZED = 0;

	/**
	 * Returns the best general upper bound on 2^bw, that is, 2^(n / 3 rounded
	 * up).
	 * 
	 * @param n
	 *            number of vertices
	 * @return
	 */
	public static int bestGeneralUpperBound(int n) {
		if (n <= 4) {
			return 1;
		}
		int bw = (n - 1) / 3 + 1;
		return 1 << bw;
	}

	/**
	 * Returns the best general upper bound on 2^bw, if the cut is not at the
	 * top, that is, 2^(n / 2).
	 * 
	 * @param n
	 *            number of vertices
	 * @return
	 */
	public static int bestGeneralUpperBound(int n, boolean toplevelcut) {
		if (toplevelcut) {
			return bestGeneralUpperBound(n);
		} else {
			int bw = (n - 1) / 2 + 1;
			if (bw >= 31) {
				System.out
				.println("Warning: returning MAX_VALUE for bestGeneralUpperBound");
				return Integer.MAX_VALUE;
			}
			return 1 << bw;
		}
	}

	
	/** @return 2^Boolean-width of given decomposition. */
	public static <TVertex extends DecompNode, V, E> int booleanWidth(
			BoolDecomposition decomp) {
		return booleanWidth(decomp, BOUND_UNINITIALIZED);
	}

	/** @return 2^Boolean-width of given decomposition. */
	public static <TVertex extends DecompNode, V, E> int booleanWidth(
			BoolDecomposition decomposition, long upper_bound) {
		// int n = decomp.graph.numVertices();
		int hoods = 0;
		int bw = 1;
		boolean nice = decomposition.hasRight(decomposition.root())
		&& decomposition.hasLeft(decomposition.root());
		for (DecompNode dn : decomposition.vertices()) {
			if (dn == decomposition.root()) {
				continue;
			}
			if (nice && dn == decomposition.right(decomposition.root())) {
				continue;
			}
			
			int thisHoods = (int) countNeighborhoods(decomposition.getCut(dn));
			
			if (upper_bound != BOUND_UNINITIALIZED
					&& thisHoods == BOUND_EXCEEDED) {
				return BOUND_EXCEEDED;
			}

			if (thisHoods > hoods) {
				int lh = 1 + (int) (Math.log(thisHoods - 1) / Math.log(2.0));
				bw = Math.max(bw, lh);
				hoods = thisHoods;
			}
			
		}
		// decomp.setAttr("hoods", hoods);
		BoolDecomposition.setBoolWidth(decomposition, hoods);
		return hoods;
	}

	/** @return 2^Boolean-width of given decomposition.
	 *  Sets the "hoods" attribute for each cut.
	 *  TODO: make a getter/setter class for hoods attribute
	 * */
	public static <TVertex extends DecompNode, V, E> int computeAllHoodCounts(
			BoolDecomposition decomp) {
		int hoods = 0;
		for (DecompNode dn : decomp.vertices()) {
			int thisHoods = (int) countNeighborhoods(decomp.getCut(dn));
			dn.setAttr("hoods", thisHoods);
			if (thisHoods > hoods) {
				hoods = thisHoods;
			}
		}
		//decomp.setAttr("hoods", hoods);
		return hoods;
	}
	
	/** @return 2^Boolean-width of given cut. */
	public static long countNeighborhoodsbyListing(BiGraph g) {

		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(g.vertices());
		// set of right nodes
		final VSubSet rights = new VSubSet(groundSet);
		// set of left nodes
		final VSubSet lefts = new VSubSet(groundSet);
		

		/**
		 * If vertices that contribute many neighborhoods are added at the end,
		 * and the ones that don't contribute much are added at the start,
		 * we don't have to iterate on such a long list in the start.
		 * @author emh
		 */
		
		TreeSet<VSubSet> initialhoods;
		// set of neighborhoods of left nodes
		final TreeSet<VSubSet> leftnodes = new TreeSet<VSubSet>();
		// set of neighborhoods of right nodes
		final TreeSet<VSubSet> rightnodes = new TreeSet<VSubSet>();

		// initialize all neighborhood sets of 1 left node
		
		for (IndexVertex node : g.leftVertices()) {
			VSubSet neighbors = new VSubSet(groundSet,g.neighbours(node));
			if (neighbors.size() > 0) {
//				System.out.print("adding "+neighbors);
				leftnodes.add(neighbors);
				//if(the above)
//					System.out.println(" succeded");
//				else
//					System.out.println(" failed");
			}
		}
		
		for (IndexVertex node : g.rightVertices()) {
			VSubSet neighbors = new VSubSet(groundSet,g.neighbours(node));
			if (neighbors.size() > 0) {
				rightnodes.add(neighbors);
			}
		}
		//System.out.println("We have "+leftnodes.size()+","+rightnodes.size()+" nodes");
		TreeSet<VSubSet> hoods = new TreeSet<VSubSet>();
		// choose the smallest neighborhood set
		
		if (rightnodes.size() > leftnodes.size()) {
			// adds empty set, subset of rights
			hoods.add(new VSubSet(rights));
			initialhoods = leftnodes;
		} else {
			// adds empty set, subset of lefts
			hoods.add(new VSubSet(lefts));
			initialhoods = rightnodes;
		}

		for (VSubSet neighbors : initialhoods) {
			TreeSet<VSubSet> newhoods = new TreeSet<VSubSet>();
			for (VSubSet hood : hoods) {
				VSubSet newhood = hood.union(hood,neighbors);
				if (!(hoods.contains(newhood) || newhoods.contains(newhood))) {
					newhoods.add(newhood);
				}
			}
			hoods.addAll(newhoods);
		}
		return hoods.size();
	}
	/** @return 2^Boolean-width of given cut. */
	public static long countNeighborhoods(BiGraph g) {

		// set of right nodes
		final VertexSet<IndexVertex> rights = new VertexSet<IndexVertex>(g.numLeftVertices());
		// set of left nodes
		final VertexSet<IndexVertex> lefts = new VertexSet<IndexVertex>(g.numRightVertices());
		

		/**
		 * If vertices that contribute many neighborhoods are added at the end,
		 * and the ones that don't contribute much are added at the start,
		 * we don't have to iterate on such a long list in the start.
		 * @author emh
		 */
		
		TreeSet<VertexSet<IndexVertex>> initialhoods;
		// set of neighborhoods of left nodes
		final TreeSet<VertexSet<IndexVertex>> leftnodes = new TreeSet<VertexSet<IndexVertex>>();
		// set of neighborhoods of right nodes
		final TreeSet<VertexSet<IndexVertex>> rightnodes = new TreeSet<VertexSet<IndexVertex>>();

		// initialize all neighborhood sets of 1 left node
		
		for (IndexVertex node : g.leftVertices()) {
			VertexSet<IndexVertex> neighbors = new VertexSet<IndexVertex>(g.neighbours(node));
			if (neighbors.size() > 0) {
//				System.out.print("adding "+neighbors);
				leftnodes.add(neighbors);
				//if(the above)
//					System.out.println(" succeded");
//				else
//					System.out.println(" failed");
			}
		}
		
		for (IndexVertex node : g.rightVertices()) {
			VertexSet<IndexVertex> neighbors = new VertexSet<IndexVertex>(g.neighbours(node));
			if (neighbors.size() > 0) {
				rightnodes.add(neighbors);
			}
		}
		//System.out.println("We have "+leftnodes.size()+","+rightnodes.size()+" nodes");
		TreeSet<VertexSet<IndexVertex>> hoods = new TreeSet<VertexSet<IndexVertex>>();
		// choose the smallest neighborhood set
		
		if (rightnodes.size() > leftnodes.size()) {
			// adds empty set, subset of rights
			hoods.add(new VertexSet<IndexVertex>(rights));
			initialhoods = leftnodes;
		} else {
			// adds empty set, subset of lefts
			hoods.add(new VertexSet<IndexVertex>(lefts));
			initialhoods = rightnodes;
		}

		for (VertexSet<IndexVertex> neighbors : initialhoods) {
			TreeSet<VertexSet<IndexVertex>> newhoods = new TreeSet<VertexSet<IndexVertex>>();
			for (VertexSet<IndexVertex> hood : hoods) {
				VertexSet<IndexVertex> newhood = hood.union(neighbors);
				if (!(hoods.contains(newhood) || newhoods.contains(newhood))) {
					newhoods.add(newhood);
				}
			}
			hoods.addAll(newhoods);
		}
		return hoods.size();
	}
	
	public static long countNeighbourhoods(DisjointBinaryDecomposition decomp) {
		return countNeighbourhoods(decomp, decomp.root(),1);
	}

	private static long countNeighbourhoods(DisjointBinaryDecomposition decomp,
			DecompNode root, long lowerBound) {
		Collection<IndexVertex> vertices = root.getGraphSubSet().vertices();
		if(vertices.size()<63 && (1L<<vertices.size()) <= lowerBound)
			return lowerBound;
		BiGraph H = new BiGraph(vertices,decomp.graph());
		
		if(decomp.isExternal(root))
			lowerBound = Math.max(vertices.size(), lowerBound);
		else
		{
			lowerBound = Math.max(CutBool.countNeighborhoodsbyListing(H), lowerBound);
			
			for(DecompNode c : decomp.children(root))
				lowerBound = Math.max(countNeighbourhoods(decomp, c, lowerBound), lowerBound);
		}
		return lowerBound;
	}

	public static long countMIS(DisjointBinaryDecomposition decomp) {
		return countMIS(decomp, decomp.root(),1);
	}

	private static long countMIS(DisjointBinaryDecomposition decomp,
			DecompNode root, long lowerBound) {
		Collection<IndexVertex> vertices = root.getGraphSubSet().vertices();
//		System.out.println("Vertices"+vertices);
		if(vertices.size()<63 && (1L<<vertices.size()) <= lowerBound)
			return lowerBound;
		BiGraph H = new BiGraph(vertices,decomp.graph());
//		System.out.println("Graph is"+decomp.graph);
//		System.out.println(H);
		if(decomp.isExternal(root))
			lowerBound = Math.max(vertices.size(), lowerBound);
		else
		{
			//lowerBound = Math.max(CutBool.countNeighborhoods(H), lowerBound);
			//System.out.println("Calling booldimbranch");
			lowerBound = Math.max(BoolDecomposition.boolDimBranch(H), lowerBound);
//			System.out.println("Cutbool of this cut is "+lowerBound);
			for(DecompNode c : decomp.children(root))
				lowerBound = Math.max(countMIS(decomp, c, lowerBound), lowerBound);
		}
		return lowerBound;
	}
	public static long countMIS(IndexGraph g, Collection<IndexVertex> vertices) {
		
		
		BiGraph H = new BiGraph(vertices,g);
//		System.out.println("Graph is"+decomp.graph);
//		System.out.println(H);
		
		return BoolDecomposition.boolDimBranch(H);
//		
	}
	public static long getCutbool(IndexGraph g, Collection<IndexVertex> vertices) {
		
		BiGraph H = new BiGraph(vertices,g);
//		System.out.println("Graph is"+decomp.graph);
//		System.out.println(H);
		
		return BoolDecomposition.boolDimBranch(H);
//		
	}
	public static long CutUB(IndexGraph G,VSubSet left)
	{
		IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
		long i=0,l=0,r=0,mm=0;
		BiGraph g = new BiGraph(left,G);
		MaximumMatching M=new MaximumMatching();
		mm=M.maximumMatching(g);
		VSubSet right = new VSubSet(groundSet);
		VSubSet toberemovedfromleft = new VSubSet(groundSet);
		right.addAll((Collection<? extends IndexVertex>) G.vertices());
		right.removeAll(left);
		for(IndexVertex v:left)
		{
			for(IndexVertex w:right)
			{
				if(G.areAdjacent(v, w))
				{
					i++;
					right.remove(w);
					toberemovedfromleft.add(v);
					break;
					
				}
			}
		}
		left.removeAll(toberemovedfromleft);
		l=left.size();
		r=right.size();
		//Do matching here
		BiGraph H = new BiGraph(left,G);
		
		
		return Math.max(mm,Math.min(i+r,Math.min(2*i, i+l)));
		
	}
	public static long CutLB(IndexGraph G,Collection<IndexVertex> vertices)
	{
		long lb=0;
		for(IndexVertex v: vertices)
		{
			if(G.degree(v)==1)
			{
				for(IndexEdge<IndexVertex> e:G.incidentEdges(v))
				{
					if(!vertices.contains(G.opposite(v, e)) &&G.degree(G.opposite(v, e))==1)
						lb++;
				}
			}
		}
		return lb;
		
	}
	public static long CutUB(BiGraph G)
	{
		IndexedSet<IndexVertex> groundSet1 = new IndexedSet<IndexVertex>(G.leftVertices());
		IndexedSet<IndexVertex> groundSet2 = new IndexedSet<IndexVertex>(G.rightVertices());
		long i=0,l=0,r=0,mm=0;
		MaximumMatching M=new MaximumMatching();
		mm=M.maximumMatching(G);
		VSubSet right = new VSubSet(groundSet2);
		VSubSet left = new VSubSet(groundSet1);
		VSubSet toberemovedfromleft = new VSubSet(groundSet1);
		right.addAll((Collection<? extends IndexVertex>) G.rightVertices());
		left.addAll(G.leftVertices());
		for(IndexVertex v:G.leftVertices())
		{
			if(left.contains(v))
			{
				for(IndexVertex w:G.rightVertices())
				{
					if(right.contains(w))
					{
						if(G.areAdjacent(v, w))
						{
							i++;
							right.remove(w);
							left.remove(v);
							break;
					
						}
					}
				}
			}
		}
		//left.removeAll(toberemovedfromleft);
		l=left.size();
		r=right.size();
				
		//return Math.min(mm,Math.min(i+r,Math.min(2*i, i+l)));
		return mm;
		
	}
	public static long CutLB(BiGraph G)
	{
		long lb=0;
		for(IndexVertex v: G.leftVertices())
		{
			if(G.degree(v)==1)
			{
				for(IndexEdge<IndexVertex> e:G.incidentEdges(v))
				{
					if(!G.leftVertices().contains(G.opposite(v, e)) &&G.degree(G.opposite(v, e))==1)
						lb++;
				}
			}
		}
		return lb;
		
	}
}
