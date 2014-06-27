package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;



import java.util.HashMap;
import java.util.Random;





import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;


public class LocalSearch {
	public class Result {
		public DisjointBinaryDecomposition  decomposition;
		public boolean success = false;
	}
	
	
	private DisjointBinaryDecomposition  decomposition;
	private int graph_boolwidth_upper_bound;
	private int graph_boolwidth_lower_bound;
	public static final int SEARCHSTEPS = 5000;

	// search time in seconds
	public static final int SEARCHTIME =1200;

	public static final int INNER_SEARCHSTEPS = 1;
	// stats
	public int failsToImproveCut = 0;

	public int triesToImproveCut = 0;
	
	/**
	 * @param IndexGraph
	 * @param Decomposition
	 * @return A result made of decomposition and boolean if decomposition is better than what send
	 */
	public Result localSearch(IndexGraph g,
			DisjointBinaryDecomposition oldBestDecomposition) {
		
		// initialize
		this.decomposition = new DisjointBinaryDecomposition(g);
		Result result = new Result();
		
		long start = System.currentTimeMillis();
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(g.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(g.numVertices());
		for (int i = 0; System.currentTimeMillis() - start < SEARCHTIME * 1000; i++) {			
			tryToImproveCut(g,left,right, INNER_SEARCHSTEPS, 1, 0,0);
		}
		result.success = true;
		result.decomposition = this.decomposition;
		return result;
		
		
	}

	//if improved then returns new cut else returns old cut
	public Collection<ArrayList<IndexVertex>> tryToImproveCut(IndexGraph g, ArrayList<IndexVertex> left,ArrayList<IndexVertex> right,
			int innerSearchsteps, long lower_bound, int depth,long total_LS) {
		
		boolean flag;
		Random rnd = new Random();
		triesToImproveCut++;
		Collection<ArrayList<IndexVertex>> newsplit = new ArrayList<ArrayList<IndexVertex>>() ;
		int maxfromleft = (2*left.size()-right.size()-1)/3+1;
		
		int maxfromright = (2*right.size()-left.size()-1)/3+1;
		
		int fromleft=rnd.nextInt(maxfromleft+1);
		int fromright=rnd.nextInt(maxfromright+1);
		
//		System.out.println("fromleft "+fromleft);
//		System.out.println("from right"+fromright);
		int newleftsize = left.size() + fromright - fromleft;
		int newrightsize = right.size() + fromleft - fromright;
		if (newleftsize < Math.max((left.size()+right.size()-1)/3+1, 1)) {
			flag= false;
		}
		if (newrightsize < Math.max((left.size()+right.size()-1)/3+1, 1)) {
			flag =false;
		}
		ArrayList<IndexVertex> lefts = new ArrayList<IndexVertex>(newleftsize);
		ArrayList<IndexVertex> rights = new ArrayList<IndexVertex>(newrightsize);
		// it's a comment
		
		lefts = ChooseRandom(left,fromleft);
		rights = ChooseRandom(right,fromright);
//		lefts = ChooseDegreeBasedOut(g,left,fromleft);
//		rights = ChooseDegreeBasedOut(g, right,fromright);
//		
		ArrayList<IndexVertex> newleft = new ArrayList<IndexVertex>(newleftsize);
		ArrayList<IndexVertex> newright = new ArrayList<IndexVertex>(newrightsize);
		
		//swapping and creating new left and right
		int i = 0;
		for (IndexVertex v : lefts) {
			if (i < lefts.size() - fromleft) {
				newleft.add(v);
			} else {
				newright.add(v);
			}
			i++;
		}
		i = 0;
		for (IndexVertex v : rights) {
			if (i < rights.size() - fromright) {
				newright.add(v);
			} else {
				newleft.add(v);
			}
			i++;
		}
		
		long lb= CutBool.countMIS(g,newleft);
		if(lb<lower_bound)
		{
			newsplit.add(newleft);
			newsplit.add(newright);
//		    System.out.println("New left: " +newleft);
//			System.out.println("New right: "+newright);
			System.out.println(total_LS+ " , "+lb);
			
			int lefthavingright = 0;
			int righthavingleft = 0;
			ArrayList<Integer> degree_left_to_right = new ArrayList<Integer>();
			ArrayList<Integer> degree_right_to_left = new ArrayList<Integer>();
			int num_edges=0;
			
			for(IndexVertex v: newleft)
			{
				boolean taken=false;
				int degree =0;
				for(IndexVertex u: g.neighbours(v))
				{
					if(newright.contains(u))
					{
						if(!taken){
						lefthavingright++;
						taken=true;
						}
						num_edges++;
						degree++;
						
					}
					
				}
				degree_left_to_right.add(degree);
				
						
			}
			int num_edgesfromright=0;
			for(IndexVertex v: newright)
			{
				boolean taken=false;
				int degree =0;
				for(IndexVertex u: g.neighbours(v))
				{
					if(newleft.contains(u))
					{
						if(!taken){
						righthavingleft++;
						taken=true;
						}
						num_edgesfromright++;
						
					}
					
				}
				degree_right_to_left.add(degree);
				
						
			}
			System.out.println(" LeftofCut : "+lefthavingright+" RightofCut : "+righthavingleft+" Edges in the Cut : "+num_edges+ " from right "+ num_edgesfromright);
			
		}
		else 
		{
			newsplit.add(left);
			newsplit.add(right);
		}
			return newsplit;
		
		
		}

	/**
	 * choose k elements from collection they will be the last k elements in the
	 * ArrayList returned while the first size - k elements are the ones not
	 * chosen
	 * 
	 * @param <T>
	 * @return
	 */
	private ArrayList<IndexVertex> ChooseRandom(ArrayList<IndexVertex> left,
			int k) {
		
		Random rnd = new Random();
		
		ArrayList<IndexVertex> list = new ArrayList<IndexVertex>(left);
		assert list.size() == left.size();
		int size = list.size();
		if (k> size) {
			k = size;
		}
		for (int i = size; i > 1 && i > size - k; i--) {
			Collections.swap(list, i - 1, rnd.nextInt(i));
		}
		return list;
		
	}
	
	
	/**Based on indegree
	 * choose k elements from collection they will be the last k elements in the
	 * ArrayList returned while the first size - k elements are the ones not
	 * chosen
	 * 
	 * @param <T>
	 * @return
	 */
	private ArrayList<IndexVertex> ChooseDegreeBased(IndexGraph G,ArrayList<IndexVertex> left,
			int k) {
		
		Random rnd = new Random();
		
		HashMap<IndexVertex, Integer> indegree= new HashMap<IndexVertex, Integer>();
		
		for(IndexVertex v:left)
		{
			int in_neighbor_of_v=0;
			for(IndexVertex n : G.neighbours(v))
			{
				if(left.contains(n))
					in_neighbor_of_v++;
					
			}
			indegree.put(v, in_neighbor_of_v);
		}
		
		
		ArrayList<IndexVertex> list = new ArrayList<IndexVertex>(left);
		assert list.size() == left.size();
		int size = list.size();
		if (k> size) {
			k = size;
		}
		
		for(int i=0;i<list.size();i++)
		{
			for(int j=1;j<list.size();j++)
			{
				if(indegree.get(list.get(i))>indegree.get(list.get(j)))
					Collections.swap(list, i, j);
			}
		}
		
		return list;
		
	}
	/**Based on outdegree big will be on the last
	 * choose k elements from collection they will be the last k elements in the
	 * ArrayList returned while the first size - k elements are the ones not
	 * chosen
	 * 
	 * @param <T>
	 * @return
	 */
	private ArrayList<IndexVertex> ChooseDegreeBasedOut(IndexGraph G,ArrayList<IndexVertex> left,
			int k) {
		
		Random rnd = new Random();
		
		HashMap<IndexVertex, Integer> outdegree= new HashMap<IndexVertex, Integer>();
		
		for(IndexVertex v:left)
		{
			int out_neighbor_of_v=0;
			for(IndexVertex n : G.neighbours(v))
			{
				if(!left.contains(n))
					out_neighbor_of_v++;
					
			}
			outdegree.put(v, out_neighbor_of_v);
		}
		
		
		ArrayList<IndexVertex> list = new ArrayList<IndexVertex>(left);
		assert list.size() == left.size();
		int size = list.size();
		if (k> size) {
			k = size;
		}
		
		for(int i=0;i<list.size();i++)
		{
			for(int j=1;j<list.size();j++)
			{
				if(outdegree.get(list.get(i))>outdegree.get(list.get(j)))
					Collections.swap(list, i, j);
			}
		}
		
		return list;
		
	}
	/**
	 * 
	 * @param decompNode
	 * @param searchsteps
	 *            how much work to do
	 * @return new subtree upper bound
	 */

/*	protected void tryToImproveSubTree(IndexGraph g,DecompNode decompNode, int searchsteps,
			int decomposition_bw_lower_bound, int depth) {

		
		long next_lower_bound = Math.max(decomposition_bw_lower_bound, CutBool.getCutbool(g,decompNode.getGraphSubSet().vertices()));

		for (int i = 0; i < searchsteps; i++) {
			
			this.graph_boolwidth_upper_bound=decomposition_bw_lower_bound;

			ArrayList<ArrayList<IndexVertex>> newsplits = <<(decompNode, graph_boolwidth_lower_bound, depth);
			this.triesToImproveCut++;

			if (newsplits.isEmpty()) {
				this.failsToImproveCut++;
			}

			// update current cut if the new one is different
			for (int j = 0; j < newsplits.size(); j++) {
				ArrayList<IndexVertex> newsplit = newsplits.get(j);
				if (decompNode.getLeft().getGraphSubSet().vertices() != newsplit
						&& decompNode.getLeft() != newsplit.getRight()) {
					// for downward search above bound to work
					if (!this.useActives || newsplit.activeCuts.size() <= 1) {
						decompNode.setLeft(newsplit.getLeft());
						decompNode.setRight(newsplit.getRight());
					}

					
			}

			
			// this cut may be closer to our goal, but won't improve the
			// graph_boolwidth_upper_bound so there's no use splitting it
			// further
			if (CutBoolComparator
					.maxLeftRightCutBool(this.decomposition, decompNode) >= getGraphBoolwidthUpperBound()) {
				// split.updateSubTreeUpperBound(CutBool.bestGeneralUpperBound(split.size(),
				// false));
				continue;
			}

			// choose who goes first. probably doesn't matter? select by size?
			VertexSplit<V> first;
			VertexSplit<V> second;
			if (rnd.nextBoolean()) {
				first = decompNode.getRight();
				second = decompNode.getLeft();
			} else {
				first = decompNode.getLeft();
				second = decompNode.getRight();
			}

			tryToImproveSubTree(first, searchsteps, next_lower_bound, depth + 1);
			// it's the max of all computed cuts so far on this particular
			// decomposition
			int lower_bound_for_other_side = next_lower_bound;
			lower_bound_for_other_side = Math.max(lower_bound_for_other_side,
					first.getSubTreeUpperBound());
			tryToImproveSubTree(second, searchsteps, lower_bound_for_other_side, depth + 1);
			if (decompNode.updateSubTreeUpperBound()) {
				// System.out.println("jolly good!");
			}

			
	}*/

	/**
	 * Makes a new split from split and if the new cut was better, it returns
	 * the new cut, else it returns the old cut.
	 * 
	 * @param split
	 * @param valuecmp
	 * @param lower_bound
	 * @return
	 */
	/*public ArrayList<ArrayList<IndexVertex>> tryToImproveCut(ArrayList<IndexVertex> split, IndexGraph G,
			long value) {

		// System.out.printf("initial: cutbool=%d, left=%s, right=%s\n", cmp
		// .getCutBool(split), split.getLeft(), split.getRight());

		double total = Math.random()*split.size();
		
		double lowestleftsize fromright = rnd.nextInt(SwapConstraints.maxFromRight(split) + 1);
		//		int maxfrom = Math.min(split.getLeft().size() - fromleft,
		//				split.getRight().size() - fromright);
		//		int addboth = rnd.nextInt(maxfrom + 1);
		//		fromleft += addboth;
		//		fromright += addboth;

		// fromleft = rnd.nextInt(Math.min(SwapConstraints.maxFromLeft(split),
		// 1) + 1);
		// fromright = rnd.nextInt(Math.min(SwapConstraints.maxFromRight(split),
		// 1) + 1);
		if (fromleft == 0 && fromright == 0) {
			fromleft = 1;
			fromright = 1;
		}
		//		fromleft = 1;
		//		fromright = 1;
		// System.out.printf("from: %d/%d\n", fromleft, fromright);

		// VertexSplit<V> newsplit = swapNodes(split);
		ArrayList<VertexSplit<V>> newsplits = new ArrayList<VertexSplit<V>>();
		newsplits.add(this.decomposition.swapRandomNodes(split, fromleft,
				fromright));

		//this.cmp.compare(newsplit2, newsplit) <=

		// completely re-randomize
		if (!this.greedy) {
			int newleftsize = rnd.nextInt(split.size() / 2 - split.size() / 3
					+ 1)
					+ split.size() / 3;
			newleftsize = split.size() / 2;
			newleftsize = Math.max(newleftsize, SwapConstraints
					.minSplitSize(split));
			newsplits.add(this.decomposition.splitRandomNew(split, newleftsize));
		}

		// System.out.println(newbg.leftVertices());
		// CutBool.countNeighborhoods(newbg, cutbool);

		for (int i = 0; i < newsplits.size(); i++) {
			VertexSplit<V> newsplit = newsplits.get(i);

			newsplit.getCached(this.rootset, this.foundsplits);

			if (keepCut(split, newsplit, depth)) {
				// System.out.printf("lb: %d, old->new cb=%d->%d sz=%d/%d -> %d/%d\n",
				// lower_bound, cmp.maxLeftRightCutBool(split), cmp
				// .maxLeftRightCutBool(newsplit), split.getLeft()
				// .size(), split.getRight().size(), newsplit
				// .getLeft().size(), newsplit.getRight().size());
				// TODO: put back assertion
				// assert cmp.maxLeftRightCutBool(newsplit) <= cmp
				// .maxLeftRightCutBool(split);
				// System.out.printf("%s -> %s\n", split, newsplit);

				split.setCached(this.rootset, this.foundsplits);
				newsplit.setCached(this.rootset, this.foundsplits);
				//			if (this.cmp.maxLeftRightCutBool(newsplit) < getGraphBoolwidthUpperBound()) {
				//				newsplit.setCached(this.rootset, this.foundsplits);
				//			}
				//split.setCached(this.rootset, this.foundsplits);

				split = newsplit;
			} else {
				newsplits.remove(i);
				i--;
			}
		}

		return newsplits;
	}*/


}
