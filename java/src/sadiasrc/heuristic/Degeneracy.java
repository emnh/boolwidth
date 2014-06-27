package sadiasrc.heuristic;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.HashSet;

import sadiasrc.util.DegreeList;

public class Degeneracy {
	public static ArrayList<IndexVertex> degeneracyOrdering(IndexGraph g)
	{
		//keep track of the degrees
		DegreeList dl = new DegreeList(g);
		//stores the solution
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(g.numVertices());
		
		//keeps track of the remainder of the graph
		//Should be a graph, but not yet since graph uses ArrayList
		ArrayList<HashSet<IndexVertex>> adjacency = new ArrayList<HashSet<IndexVertex>>();
		for(int i=0; i<g.numVertices(); i++)
		{
			adjacency.add(new HashSet<IndexVertex>());
		}
		for(IndexEdge<IndexVertex> e : g.edges())
		{
			IndexVertex a = e.endVertices().get(0);
			IndexVertex b = e.endVertices().get(1);
			adjacency.get(a.id()).add(b);
			adjacency.get(b.id()).add(a);
		}

		int count=0;
		//removes until no vertex remains
		while(count<g.numVertices())//(!dl.isEmpty())//check is empty
		{
			//find the minimum degree vertex and add to sequence
			IndexVertex v = dl.getMin();
			//System.out.println("Selected min"+v);
			seq.add(v);

			count++;
			//if(count<g.numVertices())
			{
			//make a list of the neighbours of v takes O(degree(v))
			ArrayList<IndexVertex> neighbours = new ArrayList<IndexVertex>(); 
			neighbours.addAll(adjacency.get(v.id()));
			
			//loop through all pairs of neighbours of v
			for(int i=0; i<neighbours.size(); i++)
			{
				IndexVertex n1 = neighbours.get(i);
				//reduce the degree of all neighbours of v
				if(!seq.contains(n1))
					dl.decrease(n1);

			}
			}
			//remove v from dl
			dl.remove(v);
			//dl.printDegList();
			
		}
		return seq;
	}
	public static ArrayList<IndexVertex> ReverseDegeneracyOrdering(IndexGraph g)
	{
		//keep track of the degrees
		DegreeList dl = new DegreeList(g);
		//stores the solution
		ArrayList<IndexVertex> seq = new ArrayList<IndexVertex>(g.numVertices());
		
		//keeps track of the remainder of the graph
		//Should be a graph, but not yet since graph uses ArrayList
		ArrayList<HashSet<IndexVertex>> adjacency = new ArrayList<HashSet<IndexVertex>>();
		for(int i=0; i<g.numVertices(); i++)
		{
			adjacency.add(new HashSet<IndexVertex>());
		}
		for(IndexEdge<IndexVertex> e : g.edges())
		{
			IndexVertex a = e.endVertices().get(0);
			IndexVertex b = e.endVertices().get(1);
			adjacency.get(a.id()).add(b);
			adjacency.get(b.id()).add(a);
		}

		int count=0;
		//removes until no vertex remains
		while(count<g.numVertices())//(!dl.isEmpty())
		{
			//find the minimum degree vertex and add to sequence
			IndexVertex v = dl.getMax();
			//System.out.println("Selecting "+v);
			seq.add(v);
			count++;
			//make a list of the neighbours of v takes O(degree(v))
			ArrayList<IndexVertex> neighbours = new ArrayList<IndexVertex>(); 
			neighbours.addAll(adjacency.get(v.id()));
			
			//loop through all pairs of neighbours of v
			for(int i=0; i<neighbours.size(); i++)
			{
				IndexVertex n1 = neighbours.get(i);
				//reduce the degree of all neighbours of v
				if(!seq.contains(n1))
				 dl.decrease(n1);

			}
			//remove v from dl
			dl.remove(v);
		}
		return seq;
	}


}
