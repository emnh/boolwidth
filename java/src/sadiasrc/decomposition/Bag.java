package sadiasrc.decomposition;

import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.IEdge;
import sadiasrc.graph.IGraph;
import sadiasrc.graph.IGraphSubSet;
import sadiasrc.graph.IVertex;

public abstract class Bag<V extends IVertex, E extends IEdge<V>> extends
		IndexVertex {

	public Bag(IGraph<?, ?> owner, int index) {
		super(owner, index);
	}

	public abstract IGraphSubSet<V, E> getGraphSubSet();
}
