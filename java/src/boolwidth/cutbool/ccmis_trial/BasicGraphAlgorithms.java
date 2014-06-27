package boolwidth.cutbool.ccmis_trial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

//import util.IndexedSet;
//import util.SubSet;

public class BasicGraphAlgorithms {

	//DFS checks if all vertices can be reached otherwise returns false
	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param vs = (out union rest) P U X
	 */
	public static boolean isConnected(IndexGraph G, VSubSet vs)
	{
		if(vs.isEmpty())
			return false;
		boolean[] used = new boolean[G.numVertices()];
		for(IndexVertex v : G.vertices())
			if(!vs.contains(v))
				used[v.id()]=true;

		Stack<IndexVertex> s = new Stack<IndexVertex>();
		s.push(vs.first());
		used[s.peek().id()] = true;
		while(!s.isEmpty())
		{
			IndexVertex v = s.pop();
			for(IndexVertex n : G.neighbours(v))
			{
				if(!used[n.id()])
				{
					s.push(n);
					used[n.id()] = true;
				}
			}
		}
		for(boolean b : used)
			if(!b)
				return false;
		return true;
	}
	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param vs = (out union rest) P U X
	 * return list of components
	 */
	
	public static Collection<ArrayList<IndexVertex>> connectedComponents(IndexGraph G, VSubSet vs)
	{
		if(vs.isEmpty())
			return null;
		Collection<ArrayList<IndexVertex>> components = new ArrayList<ArrayList<IndexVertex>>() ;
		boolean[] used = new boolean[G.numVertices()];
		for(IndexVertex v : G.vertices())
			if(!vs.contains(v))
				used[v.id()]=true;

		for(IndexVertex sv : vs)
		{
			if(used[sv.id()]) continue;

			Stack<IndexVertex> s = new Stack<IndexVertex>();
			s.push(sv);
			used[sv.id()] = true;

			ArrayList<IndexVertex> cc=new ArrayList<IndexVertex>();
			while(!s.isEmpty())
			{
				IndexVertex v = s.pop();
				cc.add(v);
				for(IndexVertex n : G.neighbours(v))
				{
					if(!used[n.id()])
					{
						s.push(n);
						used[n.id()] = true;
					}
				}
			}
			components.add(cc);
		}
//		System.out.println("Graph: "+G);
//		System.out.println("Found: ");
//		for(ArrayList<IndexVertex> al: components)
//			System.out.println(al);
		return components;
	}

}
