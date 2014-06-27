package sadiasrc.graph;

import java.util.ArrayList;
import java.util.Collection;

import sadiasrc.util.HashIndexSet;

public class InducedSubgraph extends IndexGraphSubSet{

	/**
	 * Generate an empty subgraph of g
	 * @param g
	 */
	public InducedSubgraph(IndexGraph g) {
		super(g);
		this.groundSet = g;
		this.vertices = new VSubSet(g.vertices());

	}

	@Override
	public boolean addEdge(IndexEdge<IndexVertex> e) {
		return false;
	}
	public Collection<IndexEdge<IndexVertex>> edges() {
		ArrayList<IndexEdge<IndexVertex>> edgelist = new ArrayList<IndexEdge<IndexVertex>>();
		for(IndexEdge<IndexVertex> e : groundSet.edges())
		{
			if(containsAll( e.endVertices()))
				edgelist.add(e);
		}
			
		return edgelist;
	}
	
	@Override
	public boolean containsEdge(IndexEdge<IndexVertex> e) {
		return containsAll(e.endVertices());
	}
	
	@Override
	public int numEdges() {
		int total_edges=0;
		for(IndexEdge<IndexVertex> e : edges())
			total_edges++;
		return total_edges;
	}
}