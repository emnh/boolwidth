package sadiasrc.util;

import sadiasrc.graph.IGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;

public class DegreeList {

	IGraph<IndexVertex,?> g;
	ArrayList<InPlaceArrayList<IndexVertex>> degList;
	int[] degree;   //the degree of each vertex
	int[] index;	//index[i] it the index of vertex i in degList[degree[i]]
	int minDeg,maxDeg;
	// should there be an int size?

	public DegreeList(IGraph<IndexVertex,?> g) //initializes to the degree of the graph g
	{
		this.g = g;
		minDeg = g.numVertices();
		maxDeg = 0;
		degList = new ArrayList<InPlaceArrayList<IndexVertex>>(g.numVertices());
		degree = new int[g.numVertices()];
		index = new int[g.numVertices()];
	

		for(IndexVertex v : g.vertices())
		{
			int d = g.degree(v);
			add(v,d);
		}
		
	}
	
	public void printDegList()
	{
		for(int i=minDeg;i<maxDeg;i++)
		{
			InPlaceArrayList<IndexVertex> nodes= degList.get(i);
			System.out.println(nodes);
		}
	}
	/**
	 * @return true if there is no degrees stored in this list
	 */
	public boolean isEmpty()
	{
		if(minDeg > maxDeg)
			return true;
		if(minDeg==maxDeg)
			return degList.get(minDeg).size()==0;
		return false;
	}

	//O(1)
	public IndexVertex getMin()
	{
		//Warning Random choice makes debugging harder
		int index = (int)(Math.random()*degList.get(minDeg).size());
		return degList.get(minDeg).get(index);
	}
	//O(1)
	public IndexVertex getMax()
	{
		//Warning Random choice makes debugging harder
		int index = (int)(Math.random()*degList.get(maxDeg).size());
		return degList.get(maxDeg).get(index);
	}

	//O(1) (unless deglist has not been initialized high enough)
	private void add(IndexVertex v, int d)
	{
		degree[v.id()]=d;
		while(degList.size()<=d)
		{
			degList.add(new InPlaceArrayList<IndexVertex>());
		}
		index[v.id()]=degList.get(d).size();
		degList.get(d).add(v);
		minDeg = Math.min(minDeg, d);
		maxDeg = Math.max(maxDeg, d);
	}

	//O(1) Only removes the vertex, does not update degree of neighbours
	public void remove(IndexVertex v)
	{
		
		int d = degree[v.id()];
		int i = index[v.id()];
		InPlaceArrayList<IndexVertex> nodes = degList.get(d);
		if(!nodes.contains(v))
			return;
		//System.out.println("Removing "+v);
		IndexVertex removed = nodes.remove(i);
		if(nodes.size()>i) //if i was not the last vertex in the list this will be true
		{
			index[nodes.get(i).id()] = i; //Set the index of the node that took the place i			
		}
		
		while(degList.get(minDeg).size()==0 && minDeg<maxDeg)
			minDeg++;
		while(degList.get(maxDeg).size()==0 && minDeg<maxDeg)
			maxDeg--;
	}
	
	public void increase(IndexVertex n1) {
		//System.out.println("increasing "+n1);
		remove(n1);
		int d = degree[n1.id()]+1;
		add(n1,d);
	}


	public void decrease(IndexVertex n1) {
		 //System.out.println("decreasing "+n1);
		remove(n1);
		int d = degree[n1.id()]-1;
		add(n1,d);
	}

}
