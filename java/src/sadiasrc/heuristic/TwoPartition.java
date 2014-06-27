package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import sadiasrc.util.IndexedSet;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.decomposition.TreeDecomposition;
import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IGraphElement;
import sadiasrc.graph.IVSet;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public class TwoPartition {

//Create and return an ordering of the vertices picking the vertex from right with least uncommon neighbors with left
	public static ArrayList<IndexVertex> CreateSequenceleastuncommon(IndexGraph G)
	{
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:G.vertices())
			right.add(v);
		IndexVertex v = G.MinDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		while(!right.isEmpty())
		{
//			System.out.println("Left"+left);
//			System.out.println("Right"+right);
			ArrayList<IndexVertex> NA = new ArrayList<IndexVertex>(G.numVertices());
			ArrayList<IndexVertex> NAclosed = new ArrayList<IndexVertex>(G.numVertices());
			
			for(IndexVertex k:left)
			{
				NAclosed.add(k);
				for(IndexVertex l: G.neighbours(k)){
					if(!NA.contains(l)&&right.contains(l))
						NA.add(l);
					if(!NAclosed.contains(l)&&right.contains(l))
						NAclosed.add(l);
				}
						
			}
			
			ArrayList<ArrayList<IndexVertex>> Nu = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
			for(int i=0; i<G.numVertices();i++){
				Nu.add(new ArrayList<IndexVertex>());
			}
			for(IndexVertex x : NA)
			{
				ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(NA.size());
				for(IndexVertex y : G.neighbours(x)){
					//if(right.contains(y))
						temp.add(y);}
				//System.out.println("x"+x+"temp"+temp);
				Nu.set(x.id(), temp);
				
			}
			for(ArrayList<IndexVertex> z: Nu)
			{
				z.removeAll(NAclosed);
			}
			int min=right.size();
			IndexVertex u = null;
			for(IndexVertex c :right)
			{
				int sizeN_A=Nu.get(c.id()).size();
//				int sizeNA=0;
//				if(NA.contains(c))
//					sizeNA=NA.size()-1;
//				else 
//					sizeNA=NA.size();
				if((sizeN_A)<min)
				{
					min=Nu.get(c.id()).size();
					u=c;
				}
						
			}
			left.add(u);
			right.remove(u);
				
		}
		
		
		return left;
		
	}

	public static ArrayList<IndexVertex> buildSequence(IndexGraph G, GreedyChooser c)
	{
		long start= System.currentTimeMillis();
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:G.vertices())
			right.add(v);
		IndexVertex v = G.MinDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		
		
		while(!right.isEmpty())
		{
			if((System.currentTimeMillis()-start)>30*60*1000)
				return null;
			IndexVertex a = c.next(left);
			left.add(a);
			right.remove(a);
		}
		return left;
	}
	
//Create and return an ordering of the vertices picking the vertex from right with 
	//least uncommon neighbours with left

	public static ArrayList<IndexVertex> CreateSequenceSYMDiff(IndexGraph G)
	{
		long start= System.currentTimeMillis();
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:G.vertices())
			right.add(v);
		IndexVertex v = G.MinDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		while(!right.isEmpty())
		{
			if((System.currentTimeMillis()-start)>30*60*1000)
				return null;
//			System.out.println("Left"+left);
//			System.out.println("Right"+right);
			
			//N(A) in right, A=left
			ArrayList<IndexVertex> NA = new ArrayList<IndexVertex>(G.numVertices());
			for(IndexVertex k:left)
			{
				for(IndexVertex l: G.neighbours(k))
					if(!NA.contains(l)&&right.contains(l))
						NA.add(l);
			}
			//N(_A) _A=right
			ArrayList<ArrayList<IndexVertex>> N_A = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
			for(int i=0; i<G.numVertices();i++)
				N_A.add(new ArrayList<IndexVertex>());
			
			//N(u) for all u in right
			for(IndexVertex x : right)
			{
				ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
				for(IndexVertex y : G.neighbours(x)){
					if(right.contains(y))
						temp.add(y);}
				//System.out.println("x"+x+"temp"+temp);
				N_A.set(x.id(), temp);
				
			}
			//for all u in right N(u)-N(left)
			for(ArrayList<IndexVertex> z: N_A)
			{
				z.removeAll(NA);
			}
			int min=right.size();
			IndexVertex u = null;
			
			//picking up the minimum			
			for(IndexVertex c :right)
			{
				int sizeN_A=N_A.get(c.id()).size();
				if((sizeN_A)<min)
				{
					min=N_A.get(c.id()).size();
					u=c;
				}
						
			}
			left.add(u);
			right.remove(u);
				
		}
		
		
		return left;
		
	}
	
	
	public static ArrayList<IndexVertex> CreateSequenceSYMDiffchosfromNLeft(IndexGraph G)
	{
		long start=System.currentTimeMillis();
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:G.vertices())
			right.add(v);
		IndexVertex v = G.MinDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		while(!right.isEmpty())
		{
			if((System.currentTimeMillis()-start)>30*60*1000)
				return null;
//			System.out.println("Left"+left);
//			System.out.println("Right"+right);
			
			//N(A) in right, A=left
			ArrayList<IndexVertex> NA = new ArrayList<IndexVertex>(G.numVertices());
			for(IndexVertex k:left)
			{
				for(IndexVertex l: G.neighbours(k))
					if(!NA.contains(l)&&right.contains(l))
						NA.add(l);
			}
			//System.out.println("NA"+NA);
			//N(_A) _A=right
			ArrayList<ArrayList<IndexVertex>> N_A = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
			for(int i=0; i<G.numVertices();i++)
				N_A.add(new ArrayList<IndexVertex>());
			
			//N(u) for all u in right
			for(IndexVertex x : right)
			{
				ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
				for(IndexVertex y : G.neighbours(x)){
					if(right.contains(y))
						temp.add(y);}
				//System.out.println("x"+x+"temp"+temp);
				N_A.set(x.id(), temp);
				
			}
			//for all u in right N(u)-N(left)
			for(ArrayList<IndexVertex> z: N_A)
			{
				z.removeAll(NA);
			}
			int min=right.size();
			IndexVertex u = null;
			//System.out.println("NA"+NA);
			if(NA.isEmpty())
				NA=right;
			//picking up the minimum			
			for(IndexVertex c :NA)
			{
				int sizeN_A=N_A.get(c.id()).size();
				if((sizeN_A)<=min)
				{
					min=N_A.get(c.id()).size();
					u=c;
				}
						
			}
			left.add(u);
			right.remove(u);
				
		}
		
		
		return left;
		
	}
	
	public static Collection<ArrayList<IndexVertex>> FindMinCut(IndexGraph G, Collection<IndexVertex> seq)
	{
		
		Collection<ArrayList<IndexVertex>> newcut = new ArrayList<ArrayList<IndexVertex>>() ;
		
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet Left = new VSubSet(groundSet);
		long ub=0,max=0,min =Integer.MAX_VALUE;
					
		//compute cutbool for every cut in the sequence //caterpillar
		int min_index=0,i=0;
					
		for(IndexVertex v: seq)
		{
			
			Left.add(v);
			i++;
			VSubSet c= new VSubSet(Left);
		    ub = CutBool.countMIS(G,c);
		    max=Math.max(max, ub);
		    if(Left.size()>=(G.numVertices()/3) && Left.size()<=(2*(G.numVertices()/3))){
		    	if(min<ub)
		    		min_index=i;
		    	min=Math.min(min, ub);
		    	left = new ArrayList<IndexVertex>(G.numVertices());
		    	for(IndexVertex x: Left)
		    		left.add(x);
		   //System.out.println("Left"+left+"UB"+ub);
		    }
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Cutbool="+max+" from caterpillar");
		
		
		ArrayList<IndexVertex> oldleft = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> oldright = new ArrayList<IndexVertex>(G.numVertices());
		i=0;
		for(IndexVertex v:seq)
		{
			if(i<min_index)
				oldleft.add(v);
			else
				oldright.add(v);
			i++;
			
		}
		//long lower_b=CutBool.countMIS(G, oldleft);
		//System.out.println("Old left: "+oldleft);
		//System.out.println("Old right: "+oldright);
		//System.out.println("Cutval : "+lower_b);
		newcut.add(oldleft);
		newcut.add(oldright);
		return newcut;
		
	}

	public static ArrayList<IndexVertex> CreateSequenceleastuncommon(Collection<IndexVertex> vertices,IndexGraph G)
	{
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:vertices)
			right.add(v);
		IndexVertex v = G.MinDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		while(!right.isEmpty())
		{
//			System.out.println("Left"+left);
//			System.out.println("Right"+right);
			ArrayList<IndexVertex> NA = new ArrayList<IndexVertex>(G.numVertices());
			for(IndexVertex k:left)
			{
				for(IndexVertex l: G.neighbours(k))
					if(!NA.contains(l)&&right.contains(l))
						NA.add(l);
			}
			ArrayList<ArrayList<IndexVertex>> N_A = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
			for(int i=0; i<G.numVertices();i++)
				N_A.add(new ArrayList<IndexVertex>());
			for(IndexVertex x : right)
			{
				ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
				for(IndexVertex y : G.neighbours(x)){
					if(right.contains(y))
						temp.add(y);}
				//System.out.println("x"+x+"temp"+temp);
				N_A.set(x.id(), temp);
				
			}
			for(ArrayList<IndexVertex> z: N_A)
			{
				z.removeAll(NA);
			}
			int min=right.size();
			IndexVertex u = null;
			for(IndexVertex c :right)
			{
				int sizeN_A=N_A.get(c.id()).size();
//				int sizeNA=0;
//				if(NA.contains(c))
//					sizeNA=NA.size()-1;
//				else 
//					sizeNA=NA.size();
				if((sizeN_A)<min)
				{
					min=N_A.get(c.id()).size();
					u=c;
				}
						
			}
			left.add(u);
			right.remove(u);
				
		}
		
		
		return left;
		
	}
	public static ArrayList<IndexVertex> CreateSequencemostcommon(IndexGraph G)
	{
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		ArrayList<IndexVertex> right = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v:G.vertices())
			right.add(v);
		IndexVertex v = G.maxDegreeVertex(right);
		left.add(v);
		right.remove(v);
		
		while(!right.isEmpty())
		{
//			System.out.println("Left"+left);
//			System.out.println("Right"+right);
			ArrayList<IndexVertex> NA = new ArrayList<IndexVertex>(G.numVertices());
			for(IndexVertex k:left)
			{
				for(IndexVertex l: G.neighbours(k))
					if(!NA.contains(l)&&right.contains(l))
						NA.add(l);
			}
			ArrayList<ArrayList<IndexVertex>> N_A = new ArrayList<ArrayList<IndexVertex>>(G.numVertices());
			for(int i=0; i<G.numVertices();i++)
				N_A.add(new ArrayList<IndexVertex>());
			for(IndexVertex x : right)
			{
				ArrayList<IndexVertex> temp = new ArrayList<IndexVertex>(right.size());
				for(IndexVertex y : G.neighbours(x)){
					if(right.contains(y))
						temp.add(y);}
				//System.out.println("x"+x+"temp"+temp);
				N_A.set(x.id(), temp);
				
			}
			for(ArrayList<IndexVertex> z: N_A)
			{
				z.retainAll(NA);
			}
			int max=Integer.MIN_VALUE;
			IndexVertex u = null;
			for(IndexVertex c :right)
			{
				int sizeN_A=N_A.get(c.id()).size();
				if((sizeN_A)>max)
				{
					max=N_A.get(c.id()).size();
					u=c;
				}
						
			}
			left.add(u);
			right.remove(u);
				
		}
		
		return left;
		
	}
	//from a given sequence returns the left part where min cutbool is found considering balanced partition
	public static ArrayList<IndexVertex> getLeft(IndexGraph G,Collection<IndexVertex> seq)
	{
		ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
		IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet Left = new VSubSet(groundSet);
//		for(IndexVertex v: G.vertices())
//		left.add(v);
//		System.out.println("L"+left);
		long ub=0,max=0,min =Integer.MAX_VALUE;
		for(IndexVertex v: seq)
		{
			Left.add(v);
			VSubSet c= new VSubSet(Left);
		    ub = CutBool.countMIS(G,c);
		    max=Math.max(max, ub);
		    if(Left.size()>=(G.numVertices()/3) && Left.size()<=(2*(G.numVertices()/3))){
		    	min=Math.min(min, ub);
		    	left = new ArrayList<IndexVertex>(G.numVertices());
		    	for(IndexVertex x: Left)
		    		left.add(x);
		   // System.out.println("Left"+left+"UB"+ub);
		    }
		}
//		System.out.println("Min="+min);
//		System.out.println("Left"+left);
	 
		return left;
		
	}
	
	//returns decomposition based on the given sequence
	public static DisjointBinaryDecomposition BisectionfromSymmetricDiff(IndexGraph G,Collection<IndexVertex> seq)
	{
		//Start out by making a decomposition with only one bag called the root
		//the root contains all vertices of $G$
		DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
		//keep a stack of those nodes that need to be split further, initially containing the root
		Stack<DecompNode> s = new Stack<DecompNode>();
		s.push(decomp.root());
		while(!s.isEmpty())
		{
			//pick next node to randomly split
			DecompNode p = s.pop();
			
			//compute the sizes of the subset to split
			int n = p.getGraphSubSet().numVertices();
			//can't split vertexSets of size 1
			if(n<=1) continue;
			//determine from the sequence where to split
			ArrayList<IndexVertex> left = getLeft(G, p.getGraphSubSet().vertices());
			//if no cut found then split at 1/2 
			if(left.isEmpty()||(left.size()==p.getGraphSubSet().numVertices()))
			{
				int i=0;
				for(IndexVertex v : p.getGraphSubSet().vertices())
				{
					if(i<n/2)
					{
						decomp.addLeft(p,v );
						i++;
					}
					else
						decomp.addRight(p, v);
						
				}
			}
			
			else
			{
				//for each vertex 
				for(IndexVertex v : p.getGraphSubSet().vertices())
				{
				//for each vertex from left of the split put it in left of p
					if(left.contains(v))	
						decomp.addLeft(p, v);
			    //for each vertex from right of the split put it in right of p		
					else
						decomp.addRight(p, v);
					
				}
			}
			//push the children on the stack
			s.push(p.getLeft());
			s.push(p.getRight());
		}
		return decomp;
	}
	
	ArrayList<IndexVertex> Order(ArrayList<IndexVertex> left, IndexGraph G)
	{
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(); 
		return seq;
		
	}
}
