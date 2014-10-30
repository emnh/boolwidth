package boolwidth.cutbool.ccmis_trial;

import graph.AdjacencyListGraph;
import graph.subsets.PosSubSet;

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
    public static boolean isConnected(AdjacencyListGraph<IndexVertex, Integer, String> G, PosSubSet<IndexVertex> vs, ArrayList<PosSubSet<IndexVertex>> neighbours)
    {
        if(vs.isEmpty())
            return false;

        PosSubSet<IndexVertex> vused = vs.inverse(); // new VSubSet(vs.getGroundSet());

        Stack<IndexVertex> s = new Stack<IndexVertex>();
        s.push(vs.first());
        vused.add(s.peek());
        //used[s.peek().id()] = true;
        while(!s.isEmpty())
        {
            IndexVertex v = s.pop();
            PosSubSet<IndexVertex> hood = neighbours.get(v.id());
            hood = hood.subtract(vused);
            for(IndexVertex n : hood)
            {
                s.push(n);
                vused.add(n);
            }
        }
        for(IndexVertex v : vs) {
            if(!vused.contains(v)) {
                return false;
            }
        }
        return true;
    }

	public static boolean isConnected2(AdjacencyListGraph<IndexVertex, Integer, String> G, PosSubSet<IndexVertex> vs)
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
	
	public static Collection<ArrayList<IndexVertex>> connectedComponents(AdjacencyListGraph<IndexVertex, Integer, String> G, PosSubSet<IndexVertex> vs)
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
