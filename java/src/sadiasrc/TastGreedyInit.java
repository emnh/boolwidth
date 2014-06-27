
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.LocalSearch;
import sadiasrc.heuristic.TwoPartition;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;


public class TastGreedyInit {

	public static void main(String[] args) {
	String fileName =  ControlInput.GRAPHLIB+ "prob2/BN_9.dgf";
	//String fileName =  ControlInput.GRAPHLIB+ "prob/munin3.dgf";
	
	ControlInput cio= new ControlInput();
	
	
	IndexGraph G = new IndexGraph();
	G=cio.getTestGraph(fileName, G);

	System.out.println(G.numVertices()+ " "+ G.numEdges());
	
	Preprocessing pp = new Preprocessing(G);
	//pp.Preprocess(G);
	System.out.println(G.numVertices()+ " "+G.numEdges());
	
	IndexVertex choosen =null;
	ArrayList<IndexVertex> LEFT;
	HashSet<IndexVertex> RIGHT;
	
	LEFT = new ArrayList<IndexVertex>();
	RIGHT= new HashSet<IndexVertex>();
	for(IndexVertex v: G.vertices())
	{
		RIGHT.add(v);
	}
	
	choosen = BasicGraphAlgorithms.BFS(G,RIGHT.iterator().next());
	choosen = BasicGraphAlgorithms.BFS(G,choosen);
	LEFT.add(choosen);
	RIGHT.remove(choosen);
	
	long minUB=(long)Double.MAX_VALUE;
	long maxUB=0;
	int minpos=0;
	long start = System.currentTimeMillis();
	for(int i=0;i<G.numVertices();i++)
	{
		long ubcur=0, min=(long)Double.MAX_VALUE;
		for(IndexVertex v : RIGHT)
		{
			LEFT.add(v);
			BiGraph BG = new BiGraph(LEFT,G);
			ubcur = CutBool.countNeighborhoodsbyListing(BG);
			if(ubcur<min)
			{
				min=ubcur;
				
				choosen =v;
				if(LEFT.size()>(G.numVertices()/3))
				{
//					System.out.println("Cur cutval :"+ubcur+ " minUB : "+minUB);
//					System.out.println("Left size : "+LEFT.size());
					if(maxUB<min)
					{
						maxUB=min;
					}
					if(minUB>min && LEFT.size()<(2*G.numVertices()/3))
					{
						minUB=min;
						minpos=i;
					}
				}
			}
			LEFT.remove(v);
						
		}
		
		LEFT.add(choosen);
		RIGHT.remove(choosen);
	}
	System.out.println("Seq"+LEFT);
	System.out.println("MINpos "+minpos);
	System.out.println(" minUB : "+minUB+ " maxUB : " +maxUB);
	long end = System.currentTimeMillis();
	System.out.println(" after "+ (end-start));
	///Random DBD
//	DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
//	//keep a stack of those nodes that need to be split further, initially containing the root
//	Stack<DecompNode> s = new Stack<DecompNode>();
//	s.push(decomp.root());
//	DecompNode p = s.pop();
//	int count=0;
//	ArrayList<IndexVertex> oldleft= new ArrayList<IndexVertex>();
//	ArrayList<IndexVertex> oldright= new ArrayList<IndexVertex>();
//	for(IndexVertex v : LEFT)
//	{
//		//calculate probability based on how many vertices already placed
//		if(count<minpos)	
//		{
//			decomp.addLeft(p, v);
//			oldleft.add(v);
//		}
//		else
//		{
//			decomp.addRight(p, v);
//			oldright.add(v);
//		}
//		count++;
//	}
//	for(IndexVertex v: RIGHT)
//	{
//		decomp.addRight(p, v);
//		oldright.add(v);
//	}
//	//push the children on the stack
//			s.push(p.getLeft());
//			s.push(p.getRight());
//	while(!s.isEmpty())
//	{
//		//pick next node to randomly split
//				p = s.pop();
//
//				//compute the sizes of the two subsets
//				int n = p.getGraphSubSet().numVertices();
//				//can't split vertexSets of size 1
//				if(n<=1) continue;
//				int numRight = n/2;
//				int numLeft = n-numRight;
//				
//				//for each vertex choose a random side to put it
//				
//				for(IndexVertex v : p.getGraphSubSet().vertices())
//				{
//					//calculate probability based on how many vertices already placed
//					//calculate probability based on how many vertices already placed
//					if(Math.random()*n<numLeft)	
//					{
//						decomp.addLeft(p, v);
//						numLeft--;
//					}
//					else
//					{
//						decomp.addRight(p, v);
//						numRight--;
//					}
//					n--;
//				}
//
//		//push the children on the stack
//		s.push(p.getLeft());
//		s.push(p.getRight());
//	}
//	//end = System.currentTimeMillis();
//	long Initialbound= CutBool.countMIS(decomp);
//	System.out.println("boolw : "+ Initialbound+ " after "+ (end-start));
	//System.out.println(decomp);
	//improve(G,oldleft,oldright);
	}
	
	public static void improve(IndexGraph G,ArrayList<IndexVertex> oldleft, ArrayList<IndexVertex> oldright )
	{
//		ArrayList<IndexVertex> oldleft = new ArrayList<IndexVertex>(G.numVertices());
//		ArrayList<IndexVertex> oldright = new ArrayList<IndexVertex>(G.numVertices());
//		oldleft = MinCut.iterator().next();
//		oldright = MinCut.iterator().next();
		
		long lower_b=CutBool.countMIS(G, oldleft);
		
		LocalSearch ls= new LocalSearch();
		boolean improved=false;
		
		long  new_lb=0;
		ArrayList<IndexVertex> newleft = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> newright = new ArrayList<IndexVertex>(G.numVertices());
			
		
		long total_LS= 0,total_UNA=0;
		int i;
		for(int no_try=0;no_try<200000;no_try++)
		{
			
		
			Collection<ArrayList<IndexVertex>> NewSplit =ls.tryToImproveCut(G, oldleft,oldright, 1, lower_b, 0,total_LS);

		    newleft = new ArrayList<IndexVertex>(G.numVertices());
		    newright = new ArrayList<IndexVertex>(G.numVertices());
		    i=0;
		    for(ArrayList<IndexVertex> vs : NewSplit)
		    {		
			  if(i==0)
			  {
				newleft = vs;
				i++;
			  }
			  else
				newright =vs;			
		    }  
		
//		    System.out.println("New left: " +newleft);
//			System.out.println("New right: "+newright);
//			System.out.println("Cutval : "+new_lb);
		    
		    long start_LS= System.currentTimeMillis();
		    new_lb=CutBool.countMIS(G, newleft);
		    long end_LS= System.currentTimeMillis();
		    
		    total_LS+=(end_LS-start_LS);
//		    BiGraph BG= new BiGraph(newleft, G);
//		    long start_UN= System.currentTimeMillis();
//		    new_lb=CutBool.countNeighborhoodsbyListing(BG);
//		    long end_UN= System.currentTimeMillis();
//		    
//		    total_UNA+=(end_UN-start_UN);
		    
		    oldleft=newleft;
		    oldright=newright;
		    lower_b=new_lb;
		    if(new_lb<lower_b)
		    	improved=true;

		
		}
		
//		System.out.println("New left: " +newleft);
//		System.out.println("New right: "+newright);
		System.out.println("Cutval : "+new_lb);
		

	}

	
	
}