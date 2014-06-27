package sadiasrc.graph;

import java.util.ArrayList;

import sadiasrc.exceptions.InvalidPositionException;

public interface IEdge<V extends IVertex> extends IGraphElement {

	public ArrayList<V> endVertices();

	public boolean equals(IEdge<V> e);

	public V opposite(V v) throws InvalidPositionException;
}
