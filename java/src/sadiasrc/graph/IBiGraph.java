package sadiasrc.graph;

import sadiasrc.exceptions.InvalidPositionException;

public interface IBiGraph<V extends IVertex, E extends IEdge<V>> extends
		IGraph<V, E> {
	public V insertLeft();

	public V insertRight();

	boolean isLeft(V v) throws InvalidPositionException;

	boolean isRight(V v) throws InvalidPositionException;

	public Iterable<V> leftVertices();

	public Iterable<V> rightVertices();

	// We could maybe add something like this, but don't know if we need it
	// public IGraph<V,E> convert();
	// then the normal insert should start working again
}
