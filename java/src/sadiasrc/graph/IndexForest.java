package sadiasrc.graph;

import java.util.ArrayList;

import sadiasrc.exceptions.BoundaryViolationException;
import sadiasrc.exceptions.InvalidPositionException;

public class IndexForest 
extends IndexGraph 
implements IForest<IndexVertex, IndexEdge<IndexVertex>> {

	VertexSet<IndexVertex> roots;
	ArrayList<IndexVertex> parent;
	
	public IndexForest(int n) {
		roots = new VertexSet<IndexVertex>(n);
	}

	public IndexVertex findRoot(IndexVertex v)
	{
		while(!isRoot(v))
			v=parent(v);
		return v;
	}

	@Override
	public boolean insertEdge(IndexEdge<IndexVertex> e)
			throws InvalidPositionException {
		if(findRoot(e.endVertices().get(0)).equals(e.endVertices().get(1)))
			return false;
		return super.insertEdge(e);
	}
	public boolean addChild(IndexVertex child, IndexVertex parent,
			IndexEdge<IndexVertex> edge) {
		boolean newChild = !contains(child);
		if(newChild)
		{	if(!insertVertex(child))
				return false;
		}
		
		boolean newParent = !contains(parent);
		if(newParent)
		{
			if(!insertVertex(parent))
			{
				if(newChild)
					removeVertex(child);
				return false;
			}
		}
			
		if(!insertEdge(edge))
		{
			if(newChild)
				removeVertex(child);
			if(newParent)
				removeVertex(parent);
			return false;		
		}
		return false;
	}

	public boolean addRoot(IndexVertex v) {
		if(super.insertVertex(v))
			return roots.add(v);
		return false;
	}

	public Iterable<IndexVertex> children(IndexVertex v)
			throws InvalidPositionException {
		ArrayList<IndexVertex> children = new ArrayList<IndexVertex>();
		for(IndexVertex w : super.neighbours(v))
			if(parent(w).equals(v))
				children.add(w);
		return children;
	}

	public boolean isExternal(IndexVertex v) throws InvalidPositionException {
		return degree(v)<=1 && !isRoot(v);
	}

	public boolean isInternal(IndexVertex v) throws InvalidPositionException {
		return !isExternal(v);
	}

	public boolean isRoot(IndexVertex v) throws InvalidPositionException {
		return roots.contains(v);
	}

	public boolean hasParent(IndexVertex v) throws InvalidPositionException {
		return !isRoot(v);
	}

	public IndexVertex parent(IndexVertex v) throws InvalidPositionException,
			BoundaryViolationException {
		return parent.get(v.id());
	}

	public Iterable<IndexVertex> roots() {
		return roots;
	}
}
