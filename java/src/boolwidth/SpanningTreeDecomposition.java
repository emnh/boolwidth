package boolwidth;

import exceptions.BoundaryViolationException;
import exceptions.InvalidPositionException;
import exceptions.NonEmptyTreeException;
import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.Edge;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IBinaryTree;
import interfaces.IDecomposition;
import interfaces.IGraph;
import interfaces.IVertexFactory;

import java.util.ArrayList;

/**
 * Implements decomposition represented by a spanning tree on the graph
 * 
 * @author emh
 * 
 */
public class SpanningTreeDecomposition<TVertex extends DNode<TVertex, V>, V, E>
extends AdjacencyListGraph<TVertex, PosSubSet<Vertex<V>>, E> implements
IDecomposition<TVertex, V, E> {

	public SpanningTreeDecomposition(
			IVertexFactory<TVertex, PosSubSet<Vertex<V>>> factory) {
		super(factory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addAbove(TVertex vertex, PosSubSet<Vertex<V>> set)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.addAbove is not yet implemented");
		//
	}

	@Override
	public TVertex addLeft(TVertex parent, PosSubSet<Vertex<V>> subSetLeft)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.addLeft is not yet implemented");
		// return null;
	}

	@Override
	public TVertex addRight(TVertex parent, PosSubSet<Vertex<V>> subSetRight)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.addRight is not yet implemented");
		// return null;
	}

	@Override
	public TVertex addRoot(PosSubSet<Vertex<V>> e) throws NonEmptyTreeException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.addRoot is not yet implemented");
		// return null;
	}

	@Override
	public void attach(TVertex root,
			IBinaryTree<TVertex, PosSubSet<Vertex<V>>, E> t1,
			IBinaryTree<TVertex, PosSubSet<Vertex<V>>, E> t2)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.attach is not yet implemented");
		//
	}

	@Override
	public Iterable<TVertex> children(TVertex v)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method ITree<TVertex,PosSubSet<Vertex<V>>,E>.children is not yet implemented");
		// return null;
	}

	@Override
	public ArrayList<TVertex> endVertices(
			Edge<TVertex, PosSubSet<Vertex<V>>, E> e)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.endVertices is not yet implemented");
		// return null;
	}

	@Override
	public BiGraph<V, E> getCut(DNode<?, V> dn) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.getCut is not yet implemented");
		// return null;
	}

	@Override
	public IGraph<Vertex<V>, V, E> getGraph() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean hasLeft(TVertex p) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.hasLeft is not yet implemented");
		// return false;
	}

	@Override
	public boolean hasRight(TVertex p) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.hasRight is not yet implemented");
		// return false;
	}

	@Override
	public TVertex insertVertex(PosSubSet<Vertex<V>> o) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.insertVertex is not yet implemented");
		// return null;
	}

	@Override
	public boolean isDisjoint(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.isDisjoint is not yet implemented");
		// return false;
	}

	@Override
	public boolean isExternal(TVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method ITree<TVertex,PosSubSet<Vertex<V>>,E>.isExternal is not yet implemented");
		// return false;
	}

	@Override
	public boolean isInternal(TVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method ITree<TVertex,PosSubSet<Vertex<V>>,E>.isInternal is not yet implemented");
		// return false;
	}

	@Override
	public boolean isRoot(TVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method ITree<TVertex,PosSubSet<Vertex<V>>,E>.isRoot is not yet implemented");
		// return false;
	}

	@Override
	public boolean isSubset(PosSubSet<Vertex<V>> set, PosSubSet<Vertex<V>> sub) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.isSubset is not yet implemented");
		// return false;
	}

	@Override
	public TVertex left(TVertex p) throws InvalidPositionException,
	BoundaryViolationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.left is not yet implemented");
		// return null;
	}

	@Override
	public int numGraphVertices() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.numGraphVertices is not yet implemented");
		// return 0;
	}

	@Override
	public TVertex opposite(TVertex v, Edge<TVertex, PosSubSet<Vertex<V>>, E> e)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.opposite is not yet implemented");
		// return null;
	}

	@Override
	public TVertex parent(TVertex v) throws InvalidPositionException,
	BoundaryViolationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method ITree<TVertex,PosSubSet<Vertex<V>>,E>.parent is not yet implemented");
		// return null;
	}

	@Override
	public E removeEdge(Edge<TVertex, PosSubSet<Vertex<V>>, E> e)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.removeEdge is not yet implemented");
		// return null;
	}

	@Override
	public E replace(Edge<TVertex, PosSubSet<Vertex<V>>, E> p, E o)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.replace is not yet implemented");
		// return null;
	}

	@Override
	public PosSubSet<Vertex<V>> replace(TVertex p, PosSubSet<Vertex<V>> o)
	throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IGraph<TVertex,PosSubSet<Vertex<V>>,E>.replace is not yet implemented");
		// return null;
	}

	@Override
	public TVertex right(TVertex p) throws InvalidPositionException,
	BoundaryViolationException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.right is not yet implemented");
		// return null;
	}

	@Override
	public TVertex root() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.root is not yet implemented");
		// return null;
	}

	@Override
	public TVertex sibling(TVertex n) throws InvalidPositionException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IBinaryTree<TVertex,PosSubSet<Vertex<V>>,E>.sibling is not yet implemented");
		// return null;
	}

	@Override
	public PosSubSet<Vertex<V>> union(PosSubSet<Vertex<V>> set1,
			PosSubSet<Vertex<V>> set2) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
		"The method IDecomposition<TVertex,V,E>.union is not yet implemented");
		// return null;
	}

}