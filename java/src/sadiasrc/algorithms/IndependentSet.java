package sadiasrc.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.org.apache.xpath.internal.operations.Or;

import sadiasrc.util.IndexedSet;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.VertexSet;

public class IndependentSet {

	public static int linIS(IndexGraph g, List<IndexVertex> order)
	{
		// sizes is a list which for each neighbourhood stores the optimal size of an independent Set
		// can easily be changed to store sets instead of sizes
		Map<VertexSet<IndexVertex>,Integer> sizes = new HashMap<VertexSet<IndexVertex>, Integer>();
		
		//add the empty neighbourhood with size 0
		sizes.put(new VertexSet<IndexVertex>(), 0);
		
		//store the vertices you have seen so far (while you iterate through order) starting with an empty set
		VertexSet<IndexVertex> seen = new VertexSet<IndexVertex>();
		
		//will store the optimal solution
		int maxIS = 0;
		long UB=0;
		
		//go through the vertices in the given order
		for(IndexVertex v : order)
		{
			//if(seen.size()%(g.numVertices()/10)==0)
				//System.out.println("10% done");
			//add v to the already seen vertices (left side of the cut)
			seen.add(v);
			
			//make a new map for the next cut (after v is moved from right to left)
			Map<VertexSet<IndexVertex>,Integer> newsizes = new HashMap<VertexSet<IndexVertex>, Integer>();
			//System.out.println("v"+v);
			
			//print
//			for(Entry<VertexSet<IndexVertex>, Integer> e : sizes.entrySet())
//			{
//				VertexSet<IndexVertex> set = e.getKey();
//				int size = e.getValue();
//				System.out.println("set"+set+ "size"+size);
//			}
			
			// go through all neighbourhoods
			for(Entry<VertexSet<IndexVertex>, Integer> e : sizes.entrySet())
			{
				VertexSet<IndexVertex> set = e.getKey();
				int size = e.getValue();
				
				
				// If v is in the neighbourhood we can not add it to the Independent set, otherwise we may.
				if(set.contains(v))
				{
					//remove v from the neighbourhood
					set.remove(v);
					//if the neighbourhood is already in the new list update, else add 
					if(!newsizes.containsKey(set) || newsizes.get(set)<size)
					{
						newsizes.put(set, size);
						//System.out.println("putting set"+set+ "size"+size);
					}
				}
				else
				{
					// If v has no neighbours in the set we can still keep the set as is without adding v
					if(!newsizes.containsKey(set) || newsizes.get(set)<size)
					{
						newsizes.put(set, size);
						//System.out.println("putting set"+set+ "size"+size);
					}

					//make a copy of the Independent Set and increase the size of the solution (add v to the solution)
					VertexSet<IndexVertex> newset = (VertexSet) set.clone();
					size++;
					maxIS = Math.max(maxIS, size);

					
					//update the neighbourhood by adding the neighbours of v
					for(IndexVertex u : g.neighbours(v))
					{
						if(!seen.contains(u))
							newset.add(u);
					}
					// add the new set to the map
					if(!newsizes.containsKey(newset) || newsizes.get(newset)<size)
					{
						newsizes.put(newset, size);
						//System.out.println("putting newset"+newset+"size"+size);
					}
				}
			}
			//set the map to the one for the current cut
			sizes = newsizes;
			//if(UB<sizes.size())
				//UB =sizes.size();
		}
		
		//System.out.println("UB from MIS : "+UB);
		return maxIS;
	}
	public static int linIS2(IndexGraph g, List<IndexVertex> order)
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
		VSubSet unseen = new VSubSet(groundset);
		for(IndexVertex v : g.vertices())
			unseen.add(v);

		
		//will store the optimal solution
		int maxIS = 0;
		long UB=0;
		int maxListSize = 0;
		//go through the vertices in the given order
		for(IndexVertex v : order)
		{
			//if(seen.size()%(g.numVertices()/10)==0)
				//System.out.println("10% done");
			//add v to the already seen vertices (left side of the cut)
			seen.add(v);
			unseen.remove(v);
			maxListSize = Math.max(maxListSize, sizes.size());
			//update the neighbourhoods
			for(IndexVertex u : g.neighbours(v))
			{
				neighbours.get(u.id()).remove(v);
			}
			//make a new map for the next cut (after v is moved from right to left)
			Map<VSubSet,Integer> newsizes = new HashMap<VSubSet, Integer>();
				//System.out.println("v"+v);
			
			//print
//			for(Entry<VertexSet<IndexVertex>, Integer> e : sizes.entrySet())
//			{
//				VertexSet<IndexVertex> set = e.getKey();
//				int size = e.getValue();
//				System.out.println("set"+set+ "size"+size);
//			}
			
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
						newsizes.put(newset, size);
					}
				}
			}
			//set the map to the one for the current cut
			sizes = newsizes;
			//if(UB<sizes.size())
				//UB =sizes.size();
		}
		for(Entry<VSubSet, Integer> e : sizes.entrySet())
		{
			VSubSet set = e.getKey();
			int size = e.getValue();
			System.out.println("Containing: "+set + " size : "+ size);
			
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
			//if(unseen.size()%(g.numVertices()/10)==0)
				//System.out.println("10% done");
			assert unseen.contains(v);
			maxListSize = Math.max(maxListSize, sizes.size());
		//	System.out.println("Considering: "+v);
		//	System.out.println("The list has size: "+sizes.size());
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
						newsizes.put(newset, size);
					}
				}
			}
			v = selectNext(g,seen,unseen,newsizes,neighbours);
			//set the map to the one for the current cut
			sizes = newsizes;

			//for( Entry<VSubSet, Integer> set : newsizes.entrySet())
			//	System.out.println(set.getKey()+" of size "+set.getValue());
		}
		
		System.out.println("Max list size used is: "+maxListSize);
		return maxIS;
	}

	private static IndexVertex selectNext(IndexGraph g, VSubSet seen,
			VSubSet unseen, Map<VSubSet, Integer> sizes, ArrayList<VSubSet> neighbours) {
		int[] score = new int[g.numVertices()];
		
		//find a vertex with only one neighbour in unseen (since this will be fixed greedily)
		for(IndexVertex x : unseen)
		{
			int curdeg = neighbours.get(x.id()).size();
			if(curdeg ==0 || (curdeg==1 && curdeg < g.degree(x)))
				return x;
		}
		
		//find a vertex with only one neighbour in unseen and take its neighbour (since this makes a twin)
		for(IndexVertex x : seen)
		{
			int curdeg = neighbours.get(x.id()).size();
			if(curdeg==1)
			{
				for(IndexVertex u : g.neighbours(x))
				{	if(unseen.contains(u))
						return u;
				}
			}
		}

		//find a relevant high degree vertex (since this makes few new sets and make many other vertices of lower degree)
		for(VSubSet set : sizes.keySet())
		{
			for(IndexVertex x : set)
				score[x.id()]++;
		}
		int maxScore = -1;
		IndexVertex v = null;
		for(IndexVertex x : unseen)
		{
			if(score[x.id()]>maxScore)
			{	v = x;
				maxScore = score[x.id()];
			}
		}
		return v;
	}
	
	public static ArrayList<IndexVertex> runtimeOrder(IndexGraph g, IndexVertex start)
	{
		
		//ArrayList containg order
		
		ArrayList<IndexVertex> Order= new ArrayList<IndexVertex>(g.numVertices());
		//Order.add(start);
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
			Order.add(v);
			//if(unseen.size()%(g.numVertices()/10)==0)
				//System.out.println("10% done");
			assert unseen.contains(v);
			maxListSize = Math.max(maxListSize, sizes.size());
		//	System.out.println("Considering: "+v);
		//	System.out.println("The list has size: "+sizes.size());
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
						newsizes.put(newset, size);
					}
				}
			}
			v = selectNext(g,seen,unseen,newsizes,neighbours);
			//set the map to the one for the current cut
			sizes = newsizes;

			//for( Entry<VSubSet, Integer> set : newsizes.entrySet())
			//	System.out.println(set.getKey()+" of size "+set.getValue());
		}
		
		System.out.println("Max list size used is: "+maxListSize);
		return Order;
	}

}
