package sadiasrc.decomposition;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexGraphSubSet;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class SimpleTreedecomposition {
	IndexGraph G;
	ArrayList<IndexVertex> set;
	ArrayList<SimpleTreedecomposition> children;
	int tw;

	public SimpleTreedecomposition(ArrayList<IndexVertex> bag, IndexGraph G)
	{
		this.G = G;
		children= new ArrayList<SimpleTreedecomposition>();
		tw=0;
		set = new ArrayList<IndexVertex>();
		for(IndexVertex v : bag)
			set.add(v);
	}
	public SimpleTreedecomposition(IndexVertex v,IndexGraph G)
	{
		this.G=G;
		tw=0;
		children= new ArrayList<SimpleTreedecomposition>();
		set = new ArrayList<IndexVertex>();
		set.add(v);
	}
	
	public SimpleTreedecomposition(IndexGraph G, ArrayList<IndexVertex> seq)
	{
		this.G = G;
		IndexGraph ng = new IndexGraph(G);
		Stack<IndexVertex> st = new Stack<IndexVertex>();
		for(int i=seq.size()-1; i>=0; i--)
			st.push(ng.getVertex(seq.get(i).id()));

//		System.out.println(st.toString());
		
//		System.out.println("Calling with:\n"+ng);
		Construct(this, ng, st);
	}
	
	private void Construct(SimpleTreedecomposition t,IndexGraph ng, Stack<IndexVertex> st)
	{
		int num = st.size();
		if(num*(num-1)==ng.numEdges()*2)
		{
			children = new ArrayList<SimpleTreedecomposition>();
			t.set = new ArrayList<IndexVertex>();
			while(!st.isEmpty())
				t.set.add(st.pop());
		}
		else
		{
			ArrayList<IndexVertex> neighbours = new ArrayList<IndexVertex>();
			IndexVertex v = st.pop();
//			System.out.println("Considering:"+v);
			for(IndexVertex n : ng.neighbours(v))
			{
				neighbours.add(n);
			}

			for(IndexVertex n1 : ng.neighbours(v))
			for(IndexVertex n2 : ng.neighbours(v))
			{
				if(n1.id() <= n2.id())
					continue;
				if(!ng.areAdjacent(n1, n2))
				{
					ng.insertEdge(n1, n2);
				}
			}
//			System.out.println(ng.numEdges());
			ArrayList<IndexEdge<IndexVertex>> toberemoved = new ArrayList<IndexEdge<IndexVertex>>();
			toberemoved.addAll((Collection<? extends IndexEdge<IndexVertex>>) ng.incidentEdges(v));
			for(IndexEdge<IndexVertex> e : toberemoved)
				ng.removeEdge(e);
//			System.out.println(ng.numEdges());
			
//			System.out.println("new graph:"+G);
			Construct(t, ng, st);
			ArrayList<IndexVertex> bag = new ArrayList<IndexVertex>();
			bag.addAll(neighbours);
			bag.add(v);
			SimpleTreedecomposition td = new SimpleTreedecomposition(bag,G);
			t.attach(td,neighbours);
		}
	}
	private boolean attach(SimpleTreedecomposition t, ArrayList<IndexVertex> neighbours) {
		
		if(set.containsAll(neighbours))
		{	
			children.add(t);
			return true;
		}

		boolean ok = false;
		for(SimpleTreedecomposition c : children)
		{
			ok = ok || c.attach(t,neighbours);
			if(ok)
				return true;
		}
		return ok;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		toString(s, 0);
		return s.toString();
	}
	
	public void toString(StringBuilder s, int l)
	{
		for(int i=0; i<l;i++)
			s.append(' ');
		for(IndexVertex v : set)
			s.append(v.toString()+",");
		s.append('\n');

		//for(SimpleTreedecomposition c : children )
		for(int i=0; i<children.size();i++)
		{
			SimpleTreedecomposition c = children.get(i);
			c.toString(s,l+1);
		}
	}
	
	//O(n^2) Can be done faster!!!
	public void removeRed() {
		for(IndexVertex v : set)
		{
			for(SimpleTreedecomposition c : children )
				c.removeRed(v);
		}
	}
	public int treewidth()
	{
		tw = set.size();
		int max=0;
		for(SimpleTreedecomposition c : children )
		{
			if(c.treewidth()>max)
				max=c.treewidth();
		}
		if(tw>max)
			return tw;
		else
			return max;
		
	}
	public void cleanUp(){
		if(isleaf())
			return;
		else
		{
			ArrayList<SimpleTreedecomposition> empty = new ArrayList<SimpleTreedecomposition>();
			for(SimpleTreedecomposition c : children)
			{
				c.cleanUp();
				if(c.set.isEmpty())
					empty.add(c);
				else
				{
					for(IndexVertex v : c.set)	//warning! this is slow
					{
						if(!set.contains(v))
							set.add(v);
					}
				}
			}
			for(SimpleTreedecomposition c : empty)
				children.remove(c);
		}
	}
	
	public DisjointBinaryDecomposition createBinDecomp()
	{
		DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
		createBinDecomp(decomp, decomp.root(),0);
		return decomp;
	}
	
	private void createBinDecomp(DisjointBinaryDecomposition decomp, DecompNode root,int cnr) {
//		System.out.println("Calling with "+cnr+" of "+children.size()+" on "+root);
		if(!isleaf())
		{
			if(children.size()==1)
				children.get(0).createBinDecomp(decomp, root, 0);
			else
			{
				if(cnr < children.size()-2)
				{
//					System.out.println("adding "+ children.get(cnr)+" left of "+root);
					decomp.addLeft(root, convert(children.get(cnr).set,G));
					//System.out.println(root.hasLeft());
					decomp.addRight(root, convert(set,G));
					if(root.hasRight())
						createBinDecomp(decomp, root.right, cnr+1);
					if(root.hasLeft())
						children.get(cnr).createBinDecomp(decomp, root.left, 0);
				}
				else //children.size()=2
				{
					decomp.addLeft(root, convert(children.get(cnr).set,G));
					decomp.addRight(root, convert(children.get(cnr+1).set,G));
					children.get(cnr+1).createBinDecomp(decomp, root.right, 0);
					children.get(cnr).createBinDecomp(decomp, root.left, 0);
					
				}
			}
		}
		else
		{
			decomp.splitBag(root);
		}
	}
	
	private ArrayList<IndexVertex> convert(ArrayList<IndexVertex> al, IndexGraph G)
	{
		ArrayList<IndexVertex> cor = new ArrayList<IndexVertex>();
		for(IndexVertex v : al)
		{
			if(v!=null && G!=null && cor!= null)
				cor.add(G.getVertex(v.id()));
		}
		return cor;
	}
	
	public void removeRed(IndexVertex v)
	{
		for(IndexVertex x : set)
		 for(SimpleTreedecomposition c : children )
			c.removeRed(x);
		if(set.contains(v))
			set.remove(v);
	}
	public boolean isleaf()
	{
		
		if(this.children.isEmpty())
			return true;
		else
			return false;
	}
	public void makealleaf() {
		
		for(SimpleTreedecomposition c : children )
			c.makealleaf();
		if(!this.isleaf())
		{
			SimpleTreedecomposition td = new SimpleTreedecomposition(set,G);
			children.add(td);
		}
			
	}
}