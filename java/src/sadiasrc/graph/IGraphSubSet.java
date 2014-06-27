package sadiasrc.graph;

import java.util.Set;

public interface IGraphSubSet<V extends IVertex, E extends IEdge<V>> extends
		Set<IGraphElement> {

	public boolean addEdge(E e);

	public boolean addVertex(V v);

	public boolean containsEdge(E e);

	public boolean containsVertex(V v);

	public Iterable<E> edges();

	public IGraph<V, E> graph();

	public boolean intersects(IGraphSubSet<V, E> ss);

	public int numEdges();

	public int numVertices();

	public Iterable<V> vertices();
}
