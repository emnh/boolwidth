package sadiasrc.heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import sadiasrc.util.DegreeList;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

// In order to speed this up we should not use an IndexGraph.
// IndexGraph has an ArrayList<ArrayList<...>> to store the adjacencies.
// If we instead or in addition had a graph with an ArrayList<HashSet<IndexVertex>>
// we could check adjacency quickly.
// I think the AdjacencyListGraph class should be changed to make this smooth.
// For now I will just make the set instead of implementing a new graph class
public class MinDegreeFillin2 {	

	public static ArrayList<IndexVertex> sequence(IndexGraph g)
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

		//removes until no vertex remains
		while(!dl.isEmpty())
		{
			//find the minimum degree vertex and add to sequence
			IndexVertex v = dl.getMin();
			seq.add(v);

			//make a list of the neighbours of v takes O(degree(v))
			ArrayList<IndexVertex> neighbours = new ArrayList<IndexVertex>(); 
			neighbours.addAll(adjacency.get(v.id()));
			
			//loop through all pairs of neighbours of v
			for(int i=0; i<neighbours.size(); i++)
			{
				IndexVertex n1 = neighbours.get(i);
				//reduce the degree of all neighbours of v
				dl.decrease(n1);

				//hs contains all the neighbours of n1
				HashSet<IndexVertex> hs = adjacency.get(n1.id());

				for(int j=i+1; j<neighbours.size(); j++)
				{
					IndexVertex n2 = neighbours.get(j);
					//if not adjacent we will make them adjacent 
					if(!hs.contains(n2))
					{
						//make adjacent in the HashSet adjacency
						hs.add(n2);
						adjacency.get(n2.id()).add(n1);	//Expected time O(1)
						//update their degree in dl
						dl.increase(n1);
						dl.increase(n2);
					}
				}
			}
			//remove v from dl
			dl.remove(v);
		}
		return seq;
	}
}
