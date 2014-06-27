package sadiasrc.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sadiasrc.util.IndexedSet;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.VertexSet;

public class IndependentSetWithoutOrdering {

	public static int linIS(IndexGraph g, List<IndexVertex> order)
	{
		// sizes is a list which for each neighbourhood stores the optimal size of an independent Set
		// can easily be changed to store sets instead of sizes
		Map<VSubSet,Integer> sizes = new HashMap<VSubSet, Integer>();
		
		//groundset is the vertices of the graph, solutions will be subsets of the groundset
		IndexedSet<IndexVertex> groundset = new IndexedSet<IndexVertex>(g.vertices());
		
		//list of the neighbourhoods to the remaining part of the graph
		ArrayList<VSubSet> neighbours = new ArrayList<VSubSet>(g.numVertices());
		for(int i = 0; i<g.numVertices(); i++)
		{
			VSubSet nset = new VSubSet(groundset);
			nset.addAll(g.neighbours(g.getVertex(i)));
			neighbours.add(nset);
		}
		
		//add the empty neighbourhood with size 0
		sizes.put(new VSubSet(groundset), 0);
		
		//store the vertices you have seen so far (while you iterate through order) starting with an empty set
		VSubSet seen = new VSubSet(groundset);
		//and the vertices you have not seen
		VSubSet unseen = new VSubSet(groundset);
		for(IndexVertex v : g.vertices())
			unseen.add(v);

		
		//will store the optimal solution
		int maxIS = 0;
		int maxListSize = 0;
		
		//go through the vertices in the given order
		for(IndexVertex v : order)
		{
			maxListSize = Math.max(maxListSize, sizes.size());
			//System.out.println("Considering: "+v);
			//System.out.println("The list has size: "+sizes.size());
			//add v to the already seen vertices (left side of the cut)
			seen.add(v);
			unseen.remove(v);
			//update the neighbourhoods
			for(IndexVertex u : g.neighbours(v))
			{
				neighbours.get(u.id()).remove(v);
			}
			//System.out.println("Have added "+v);
			
			//make a new map for the next cut (after v is moved from right to left)
			Map<VSubSet,Integer> newsizes = new HashMap<VSubSet, Integer>();
			
			// go through all neighbourhoods
			for(Entry<VSubSet, Integer> e : sizes.entrySet())
			{
				VSubSet set = e.getKey();
				int size = e.getValue();
				
				// If v is in the neighbourhood we can not add it to the Independent set, otherwise we may.
				if(set.contains(v))
				{
					//update the size (This will never change maxIS and should be removed)
					maxIS = Math.max(maxIS, size);
					//remove v from the neighbourhood
					set.remove(v);
					//if the neighbourhood is already in the new list update, else add
					Integer temp = newsizes.get(set);
					if(temp==null || temp<size)
						newsizes.put(set, size);
				}
				else
				{
					Integer temp = newsizes.get(set);
					boolean usedSet = false;
					// If v has no neighbours in the set we can still keep the set as is without adding v
					if((temp==null || temp<size) && neighbours.get(v.id()).intersects(unseen))
					{
						usedSet = true;
						newsizes.put(set, size);
					}

					//make a copy of the Independent Set and increase the size of the solution (add v to the solution)
					VSubSet newset;
					if(usedSet)
						newset = (VSubSet) set.clone();
					else
						newset = set;
					size++;
					maxIS = Math.max(maxIS, size);

					//update the neighbourhood by adding the neighbours of v
					newset.AddAll(neighbours.get(v.id()));
					
					temp = newsizes.get(newset);
					// add the new set to the map
					if(temp==null || temp<size)
						newsizes.put(newset, size);
				}
			}
			//set the map to the one for the current cut
			sizes = newsizes;
			//for( Entry<VSubSet, Integer> set : newsizes.entrySet())
			//	System.out.println(set.getKey()+" of size "+set.getValue());
		}
		
		System.out.println("Max list size used is: "+maxListSize);
		return maxIS;
	}
	
	public static int linIS(IndexGraph g, IndexVertex start)
	{
		// sizes is a list which for each neighbourhood stores the optimal size of an independent Set
		// can easily be changed to store sets instead of sizes
		Map<VSubSet,Integer> sizes = new HashMap<VSubSet, Integer>();
		
		//groundset is the vertices of the graph, solutions will be subsets of the groundset
		IndexedSet<IndexVertex> groundset = new IndexedSet<IndexVertex>(g.vertices());
		
		//list of the neighbourhoods to the remaining part of the graph
		ArrayList<VSubSet> neighbours = new ArrayList<VSubSet>(g.numVertices());
		for(int i = 0; i<g.numVertices(); i++)
		{
			VSubSet nset = new VSubSet(groundset);
			nset.addAll(g.neighbours(g.getVertex(i)));
			neighbours.add(nset);
		}
		
		//add the empty neighbourhood with size 0
		sizes.put(new VSubSet(groundset), 0);
		
		//store the vertices you have seen so far (while you iterate through order) starting with an empty set
		VSubSet seen = new VSubSet(groundset);
		//and the vertices you have not seen
		VSubSet unseen = new VSubSet(groundset);
		for(IndexVertex v : g.vertices())
			unseen.add(v);

		
		//will store the optimal solution
		int maxIS = 0;
		int maxListSize = 0;
		
		IndexVertex v = start;
		//go through the vertices in the given order
		while(!unseen.isEmpty())
		{
			assert unseen.contains(v);
			int[] score = new int[g.numVertices()];
			maxListSize = Math.max(maxListSize, sizes.size());
			System.out.println("Considering: "+v);
			System.out.println("The list has size: "+sizes.size());
			//add v to the already seen vertices (left side of the cut)
			seen.add(v);
			unseen.remove(v);
			//update the neighbourhoods
			for(IndexVertex u : g.neighbours(v))
			{
				neighbours.get(u.id()).remove(v);
			}
			//System.out.println("Have added "+v);
			
			//make a new map for the next cut (after v is moved from right to left)
			Map<VSubSet,Integer> newsizes = new HashMap<VSubSet, Integer>();
			
			// go through all neighbourhoods
			for(Entry<VSubSet, Integer> e : sizes.entrySet())
			{
				VSubSet set = e.getKey();
				int size = e.getValue();
//				System.out.println("Considering: "+set);
				
				// If v is in the neighbourhood we can not add it to the Independent set, otherwise we may.
				if(set.contains(v))
				{
					assert (size <= maxIS);
					//update the size (This will never change maxIS and should be removed)
					maxIS = Math.max(maxIS, size);
					//remove v from the neighbourhood
					set.remove(v);
					//if the neighbourhood is already in the new list update, else add
					Integer temp = newsizes.get(set);
					if(temp==null || temp<size)
					{
						if(temp == null)
						{
							for(IndexVertex x : set)
								score[x.id()]++;
						}
						newsizes.put(set, size);
					}
				}
				else
				{
					Integer temp = newsizes.get(set);
					boolean usedSet = false;
					// If v has no neighbours in the set we can still keep the set as is without adding v
					if((temp==null || temp<size) && neighbours.get(v.id()).intersects(unseen))
					{
						if(temp == null)
						{
							for(IndexVertex x : set)
								score[x.id()]++;
						}
						usedSet = true;
						newsizes.put(set, size);
					}

					//make a copy of the Independent Set and increase the size of the solution (add v to the solution)
					VSubSet newset;
					if(usedSet)
						newset = (VSubSet) set.clone();
					else
						newset = set;
					//increase size since v is added
					size++;
					maxIS = Math.max(maxIS, size);

					//update the neighbourhood by adding the neighbours of v
					newset.AddAll(neighbours.get(v.id()));
					
					temp = newsizes.get(newset);
					// add the new set to the map
					if(temp==null || temp<size)
					{	
						if(temp == null)
							for(IndexVertex x : newset)
								score[x.id()]++;
						newsizes.put(newset, size);
					}
				}
			}
			//set the map to the one for the current cut
			sizes = newsizes;
			int maxScore = -1;
			for(IndexVertex x : unseen)
			{
				if(score[x.id()]>maxScore)
				{	v = x;
					maxScore = score[x.id()];
				}
			}
			//for( Entry<VSubSet, Integer> set : newsizes.entrySet())
			//	System.out.println(set.getKey()+" of size "+set.getValue());
		}
		
		System.out.println("Max list size used is: "+maxListSize);
		return maxIS;
	}
}
