package sadiasrc.decomposition;

import sadiasrc.exceptions.BoundaryViolationException;
import sadiasrc.exceptions.InvalidPositionException;
import sadiasrc.graph.IEdge;
import sadiasrc.graph.IForest;
import sadiasrc.graph.IVertex;

public abstract class PartialDecomposition<V extends IVertex, E extends IEdge<? extends V>> implements IForest<V,E>{

	@Override
	public String graphname() {
		return this.graphname();
	}

	
	@Override
	public boolean contains(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(V v) {
			return false;
	}

	@Override
	public Iterable edges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable incidentEdges(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insertEdge(IEdge e) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertVertex(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] intAdjacencyMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable neighbours(IVertex v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numEdges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numVertices() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IVertex opposite(IVertex v, IEdge e) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeEdge(IEdge e) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeVertex(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable vertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addChild(IVertex child, IVertex parent, IEdge edge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addRoot(IVertex v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable children(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExternal(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInternal(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRoot(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasParent(IVertex v) throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IVertex parent(IVertex v) throws InvalidPositionException,
			BoundaryViolationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable roots() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean areAdjacent(IVertex u, IVertex v)
			throws InvalidPositionException {
		// TODO Auto-generated method stub
		return false;
	}

}
