package sadiasrc.heuristic;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;

import sadiasrc.util.IndexedSet;

public class DegreeList<E> {

	IndexedSet<E> set;
	ArrayList<ArrayList<IndexVertex>> degList;
	int[] degree;
	ArrayList<Integer> index;
	int minDeg,maxDeg;

	public DegreeList(IndexedSet<E> set)
	{
		this.set = set;
		minDeg = 0;
		maxDeg = 0;
		degList = new ArrayList<ArrayList<IndexVertex>>(set.size());
		degree = new int[set.size()]; //new ArrayList<Integer>(set.size());
		index = new ArrayList<Integer>(set.size());

	}

	public IndexVertex getMin()
	{
		int index = (int)(Math.random()*degList.get(minDeg).size());
		return degList.get(minDeg).get(index);
	}

	private void add(IndexVertex v, int d)
	{
        throw new UnsupportedOperationException("DegreeList");
        /*
		degree[v.id()]=d;
		while(degList.size()<=d)
		{
			degList.add(new ArrayList<IndexVertex>());
		}
		index[v.id()]=degList.get(d).size();
		degList.get(d).add(v);
		minDeg = Math.min(minDeg, d);
		maxDeg = Math.max(maxDeg, d);
		*/
	}

	public void remove(IndexVertex v)
	{
        throw new UnsupportedOperationException("DegreeList");

        /*
		int d = degree[v.id()];
		int i = index[v.id()];
		ArrayList<IndexVertex> nodes = degList.get(d);
		if(nodes.size()==i+1)
			nodes.remove(i);
		else
		{
			IndexVertex last = nodes.get(nodes.size()-1);
			nodes.set(i, nodes.get(nodes.size()-1));
			index[last.id()] = i;
			nodes.remove(nodes.size()-1);
		}
		while(degList.get(minDeg).size()==0 && minDeg<maxDeg)
			minDeg++;
		while(degList.get(maxDeg).size()==0 && minDeg<maxDeg)
			maxDeg--;
	    */
	}
	
	
	public void increase(IndexVertex n1) {
		int d = degree[n1.id()]+1;
		remove(n1);
		add(n1,d);
	}

	public void decrease(IndexVertex n1) {
		int d = degree[n1.id()]-1;
		remove(n1);
		add(n1,d);
	}

}
