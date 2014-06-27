package sadiasrc.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import sadiasrc.util.IndexedSet;
import sadiasrc.util.SubSet;

public class BasicGraphAlgorithms {

	public static boolean isConnected(IndexGraph G)
	{
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet vs = new VSubSet(groundSet);
		for(IndexVertex v:G.vertices())
			vs.add(v);
		return isConnected(G, vs);
	}
	public static boolean isClique(IndexGraph G,IVSet vs)
	{
		for(IndexVertex u : vs)
			for(IndexVertex v : vs)
				if(!u.equals(v))
				{
					if(!G.areAdjacent(u, v))
						return false;
				}
		return true;
			
	}
	public static boolean isClique(IndexGraph G,Collection<IndexVertex> cvs)
	{
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet vs = new VSubSet(groundSet);
		for(IndexVertex x:cvs)
			vs.add(x);
		for(IndexVertex u : vs)
			for(IndexVertex v : vs)
				if(!u.equals(v))
				{
					if(!G.areAdjacent(u, v))
						return false;
				}
		return true;
			
	}

	public static boolean isConnected(IndexGraph G, VSubSet vs, ArrayList<VSubSet> neighbours)
	{
		if(vs.isEmpty())
			return false;

        VSubSet vused = vs.inverse(); // new VSubSet(vs.getGroundSet());

		Stack<IndexVertex> s = new Stack<IndexVertex>();
		s.push(vs.first());
        vused.add(s.peek());
		//used[s.peek().id()] = true;
		while(!s.isEmpty())
		{
			IndexVertex v = s.pop();
            VSubSet hood = neighbours.get(v.id());
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
	public static boolean isConnected(IndexGraph G, Collection<IndexVertex> cvs)
	{
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet vs = new VSubSet(groundSet);
		for(IndexVertex x:cvs)
			vs.add(x);
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

	public static Collection<ArrayList<IndexVertex>> connectedComponents(IndexGraph G)
	{
		return connectedComponents(G, new VSubSet(G.vertices()));
	}
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
//		System.out.println("Graph:\n"+G);
//		System.out.println("Found:");
//		for(ArrayList<IndexVertex> al: components)
//			System.out.println(al);
		return components;
	}
	public static Collection<ArrayList<IndexVertex>> connectedComponents(IndexGraph G, Collection<IndexVertex> cvs)
	{
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet vs = new VSubSet(groundSet);
		for(IndexVertex x:cvs)
			vs.add(x);
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
//		System.out.println("Graph:\n"+G);
//		System.out.println("Found:");
//		for(ArrayList<IndexVertex> al: components)
//			System.out.println(al);
		return components;
	}
	public static IndexVertex cutVertex(IndexGraph G)
	{
		return cutVertex(G, new VSubSet(G.vertices()));
	}
	public static IndexVertex cutVertex(IndexGraph G, IVSet vs)
	{
		if(vs.isEmpty())
			return null;
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet rest = new VSubSet(groundSet);
		rest.addAll(vs);
		for(IndexVertex v : vs)
		{
			rest.remove(v);
			if(!isConnected(G, rest))
				return v;
			rest.add(v);
			
		}
		return null;
		
	}
//	public static ArrayList<IndexVertex> cloudbyBFS(IndexGraph G)
//	{
////		IndexedSet<IndexVertex> groundSet2;
////		groundSet2 = new IndexedSet<IndexVertex>(G.vertices());
////		VSubSet v = new VSubSet(groundSet2);
////		for(IndexVertex x: G.vertices())
////			v.add(x);
//		return cloudbyBFS(G, G.vertices());
//	}
	public static ArrayList<IndexVertex> cloudbyBFS(IndexGraph G, Collection<IndexVertex> cvs)
	{
		
		IndexedSet<IndexVertex> groundSet;
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		VSubSet vs = new VSubSet(groundSet);
		for(IndexVertex x:cvs)
			vs.add(x);
		if(vs.isEmpty())
			return null;
		
		boolean[] used = new boolean[G.numVertices()];
		for(IndexVertex v : G.vertices())
			if(!vs.contains(v))
				used[v.id()]=true;
		IndexVertex sv = G.maxDegreeVertex(vs);
		//System.out.println("sv"+sv);
		
		int set_size=vs.size();

		while(used[sv.id()])
		{
			vs.remove(sv);
			sv = G.maxDegreeVertex(vs);
			continue;
		}

			Queue<IndexVertex> q =  new LinkedList<IndexVertex>();
			q.add(sv);
			used[sv.id()] = true;

			ArrayList<IndexVertex> cc=new ArrayList<IndexVertex>();
			while(!q.isEmpty()&&(cc.size()<=set_size/3))
			{
				IndexVertex v = q.remove();
				cc.add(v);
				for(IndexVertex n : G.neighbours(v))
				{
					if(!used[n.id()])
					{
						q.add(n);
						used[n.id()] = true;
					}
				}
			}
			
		return cc;
	}
	public static IndexVertex BFS(IndexGraph G, IndexVertex root)
	{
		boolean[] visited = new boolean[G.numVertices()];
		Queue<IndexVertex> vertexQueue =  new LinkedList<IndexVertex>();
		vertexQueue.add(root);
		visited[root.id()] = true;
		
		IndexVertex current = root;
		
		while(!vertexQueue.isEmpty())
		{
			current = vertexQueue.remove();
			for(IndexVertex child : G.neighbours(current))
			{					
				if(!visited[child.id()])
				{
					vertexQueue.add(child);
					visited[child.id()] = true;					
						
				}
			}
		}
			
		return current;
	}
}
