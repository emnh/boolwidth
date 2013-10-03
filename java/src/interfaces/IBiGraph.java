package interfaces;

import exceptions.InvalidPositionException;
import graph.Vertex;

public interface IBiGraph<V, E> extends IGraph.D<V, E> {

	public Vertex<V> insertLeft(V o);

	public Vertex<V> insertRight(V o);

	boolean isLeft(Vertex<V> v) throws InvalidPositionException;

	boolean isRight(Vertex<V> v) throws InvalidPositionException;

	public Iterable<Vertex<V>> leftVertices();

	public Iterable<Vertex<V>> rightVertices();

	// We could maybe add something like this, but don't know if we need it
	// public IGraph<V,E> convert();
	// then the normal insert should start working again
}