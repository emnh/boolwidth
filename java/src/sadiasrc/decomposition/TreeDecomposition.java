package sadiasrc.decomposition;

import java.util.ArrayList;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

public class TreeDecomposition extends BinaryDecomposition {

	ArrayList<Boolean> added;
	
	public TreeDecomposition(IndexGraph G) {
		super();
		graph=G;
		added = new ArrayList<Boolean>(G.numVertices());
		for(int i=0; i<G.numVertices(); i++)
			added.add(false);
	}

	public boolean contains(IndexVertex v)
	{
		return graph().contains(v) && added.get(v.id());
	}

	@Override
	protected boolean canAddLeft(DecompNode p, IndexVertex v) {
		if(hasLeft(p))
			return canAddVertex(left(p), v);
		if(contains(v))
			return p.getGraphSubSet().contains(v);
		return true;
	}

	@Override
	protected boolean canAddRight(DecompNode p, IndexVertex v) {
		if(hasRight(p))
			return canAddRight(right(p), v);
		if(contains(v))
			return p.getGraphSubSet().contains(v);
		return true;
	}

	@Override
	protected boolean canAddVertex(DecompNode bag, IndexVertex v) 
	{
		//can only add nodes belonging to the graph
		if(!graph().contains(v))
			return false;
		//if belongs to the graph, but not to the decomposition, then OK
		if(!contains(v))
			return true;
		//if already in the bag we can not add again
		if(bag.getGraphSubSet().contains(v))
			return false;
		//if in a neighbouring bag then OK
		if(hasLeft(bag) && left(bag).getGraphSubSet().contains(v))
			return true;
		if(hasRight(bag) && right(bag).getGraphSubSet().contains(v))
			return true;
		if(!isRoot(bag) && parent(bag).getGraphSubSet().contains(v))
			return true;
		//if in the decomposition, but not in any of the neighbouring bags we can not add
		return false;
	}

	@Override
	public boolean isComplete() {
		for(Boolean b : added)
			if(!b) return false;
		for(IndexEdge<IndexVertex> e : graph.edges())
			if(!covered(e))
				return false;
		return true;
	}

	private boolean covered(IndexEdge<IndexVertex> e) {
		IndexVertex a=e.endVertices().get(0);
		IndexVertex b=e.endVertices().get(1);
		for(DecompNode bag : vertices())
			if(bag.getGraphSubSet().contains(a) && bag.getGraphSubSet().contains(b))
			return true;
		return false;
	}

	public int treewidth()
	{
		int tw = 0;
		for(DecompNode bag : vertices())
			tw = Math.max(tw, bag.getGraphSubSet().numVertices());
		return tw;
	}
}
