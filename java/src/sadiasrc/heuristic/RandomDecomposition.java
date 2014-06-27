package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.decomposition.TreeDecomposition;
import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IGraphElement;
import sadiasrc.graph.IVSet;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public class RandomDecomposition {

	public static DisjointBinaryDecomposition randomDBD(IndexGraph G)
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

			//compute the sizes of the two subsets
			int n = p.getGraphSubSet().numVertices();
			//can't split vertexSets of size 1
			if(n<=1) continue;
			int numRight = n/2;
			int numLeft = n-numRight;
			
			//for each vertex choose a random side to put it
			for(IndexVertex v : p.getGraphSubSet().vertices())
			{
				//calculate probability based on how many vertices already placed
				if(Math.random()*n<numLeft)	
				{
					decomp.addLeft(p, v);
					numLeft--;
				}
				else
				{
					decomp.addRight(p, v);
					numRight--;
				}
				n--;
			}
			//push the children on the stack
			s.push(p.getLeft());
			s.push(p.getRight());
		}
		return decomp;
	}
	public static DisjointBinaryDecomposition DBDbyBFS(IndexGraph G)
	{
		//Start out by making a decomposition with only one bag called the root
		//the root contains all vertices of $G$
		DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
		//keep a stack of those nodes that need to be split further, initially containing the root
		Stack<DecompNode> s = new Stack<DecompNode>();
		s.push(decomp.root());
		while(!s.isEmpty())
		{
			//pick next node from stack to split
			DecompNode p = s.pop();
			
			ArrayList<IndexVertex> temp=new ArrayList<IndexVertex>();
			int sizeofNode = p.getGraphSubSet().vertices().size();
			if(sizeofNode > 1 )
			{
				temp= BasicGraphAlgorithms.cloudbyBFS(G,p.getGraphSubSet().vertices());
			
				//System.out.println("cloud"+temp);

			
				for(IndexVertex v : p.getGraphSubSet().vertices())
				{
				
					if(temp.contains(v))	
					{
						decomp.addLeft(p, v);
					}
					else
					{
						decomp.addRight(p, v);
					
					}
			
				}
			}
			//push the children on the stack
			if(p.hasLeft())
				s.push(p.getLeft());
			if(p.hasRight())
				s.push(p.getRight());
		}
		return decomp;
	}

	//creates a random treedecomposition
	public static sadiasrc.decomposition.TreeDecomposition randomTD(IndexGraph G)
	{
		//create a new empty treedecomposition with a rootbag that contains no elements
		sadiasrc.decomposition.TreeDecomposition decomp = new sadiasrc.decomposition.TreeDecomposition(G);
		decomp.addRoot();
		//create a list of all vertices so that we can keep track of which has already been added 
		ArrayList<IndexVertex> vertices = new ArrayList<IndexVertex>();
		for(IndexVertex v : G)
			vertices.add(v);
		//keep a subset so we can call the connected components algorithm
		// TODO: connected components should handle more types of input.
		VSubSet set = new VSubSet(G.vertices());
		set.addAll(vertices);
		randomTD(G,decomp,decomp.root(),vertices,set);
		return decomp;
	}
	
	private static void randomTD(IndexGraph G,
			TreeDecomposition decomp, DecompNode node, ArrayList<IndexVertex> vertices, VSubSet set) 
	{
		//find a separator by random adding vertices to the current bag until what is left is disconnected
		while(vertices.size()>0 && BasicGraphAlgorithms.isConnected(G, set))
		{	
			//pick a random vertex, add to current bag, remove from both set and vertices
			IndexVertex v = random(vertices);
			node.getGraphSubSet().add(v);
			set.remove(v);
		}
		//if all the vertices left was placed in this bag no need to continue
		if(vertices.size()==0)
			return;
		//find the left and right side of the remaining vertices
		//this works since the above while loop ran until the graph was disconnected
		VSubSet left = new VSubSet(set.getGroundSet());
		VSubSet right = new VSubSet(set.getGroundSet());
		Collection<ArrayList<IndexVertex>> cc = BasicGraphAlgorithms.connectedComponents(G, set);
		int i=0;
		for(ArrayList<IndexVertex> c : cc)
		{
			if(i%2==0)
				left.addAll(c);
			else
				right.addAll(c);
			i++;
		}
		//create vertex lists to match the sets left and right
		ArrayList<IndexVertex> leftList = new ArrayList<IndexVertex>();
		ArrayList<IndexVertex> rightList = new ArrayList<IndexVertex>();
		for(IndexVertex v : vertices)
		{	
			if(left.contains(v))
				leftList.add(v);
			else
				rightList.add(v);
		}

		//add decomposition nodes to the treedecomposition
		decomp.addLeft(node);
		decomp.addRight(node);

		//add the vertices of the parent bag to the children they need to be in
		//remember, all edges has to be in some bag so if v has a neighbor u not placed yet
		//v has to be placed in some bag together with u
		for(IndexVertex v : node.getGraphSubSet().vertices())
		{
			boolean addL=false;
			boolean addR=false;
			for(IndexVertex u: G.neighbours(v))
			{	
				addL |= left.contains(u);
				addR |= right.contains(u);
			}
			if(addL) decomp.addVertex(decomp.left(node),v);
			if(addR) decomp.addVertex(decomp.right(node),v);			
		}
		//recurse on the leaves of the
		randomTD(G, decomp, decomp.left(node), leftList, left);
		randomTD(G, decomp, decomp.right(node), rightList, right);
	}

	//finds and removes a random element from a list in O(1) time
	private static <V> V random(ArrayList<V> list) {
		int index = (int) (Math.random()*list.size());
		V elem = list.get(index);
		if(index<list.size()-1)
			list.set(index, list.remove(list.size()-1));
		else
			list.remove(index);
		return elem;
	}
}
