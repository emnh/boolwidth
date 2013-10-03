package interfaces;

import exceptions.InvalidPositionException;
import graph.BiGraph;
import graph.PosSubSet;
import graph.Vertex;
import boolwidth.DNode;

public interface IDecomposition<TVertex extends DNode<TVertex, V>, V, E>
		extends IBinaryTree<TVertex, PosSubSet<Vertex<V>>, E> {

	public void addAbove(TVertex vertex, PosSubSet<Vertex<V>> set)
			throws InvalidPositionException;

	public TVertex addLeft(TVertex parent, PosSubSet<Vertex<V>> subSetLeft)
			throws InvalidPositionException;

	public TVertex addRight(TVertex parent, PosSubSet<Vertex<V>> subSetRight)
			throws InvalidPositionException;

	public BiGraph<V, E> getCut(DNode<?, V> dn);

    public IGraph<Vertex<V>, V, E> getGraph();

	public boolean isDisjoint(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2);

	public boolean isSubset(PosSubSet<Vertex<V>> set, PosSubSet<Vertex<V>> sub);

	public int numGraphVertices();

	public PosSubSet<Vertex<V>> union(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2);
}
