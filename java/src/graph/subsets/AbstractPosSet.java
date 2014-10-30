package graph.subsets;

import interfaces.IPosSet;
import interfaces.ISetPosition;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public abstract class AbstractPosSet<E extends ISetPosition> extends
		AbstractSet<E> implements NavigableSet<E>, IPosSet<E> {

	@Override
	public E ceiling(E e) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.ceiling is not yet implemented");
		// return null;
	}

	@Override
	public Comparator<? super E> comparator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method SortedSet<E>.comparator is not yet implemented");
		// return null;
	}

	@Override
	public Iterator<E> descendingIterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.descendingIterator is not yet implemented");
		// return null;
	}

	@Override
	public NavigableSet<E> descendingSet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.descendingSet is not yet implemented");
		// return null;
	}

	@Override
	public E first() {
		return getVertex(0);
	}

	@Override
	public E floor(E e) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.floor is not yet implemented");
		// return null;
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.headSet is not yet implemented");
		// return null;
	}

	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.headSet is not yet implemented");
		// return null;
	}

	@Override
	public E higher(E e) {
		int i = getId(e) + 1;
		if (i >= size()) {
			return null;
		} else {
			return getVertex(i);
		}
	}

	@Override
	public E last() {
		return getVertex(size() - 1);
	}

	@Override
	public E lower(E e) {
		int i = getId(e) - 1;
		if (i >= size()) {
			return null;
		} else {
			return getVertex(i);
		}
	}

	@Override
	public E pollFirst() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.pollFirst is not yet implemented");
		// return null;
	}

	@Override
	public E pollLast() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.pollLast is not yet implemented");
		// return null;
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
			E toElement, boolean toInclusive) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.subSet is not yet implemented");
		// return null;
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.subSet is not yet implemented");
		// return null;
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.tailSet is not yet implemented");
		// return null;
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException(
				"The method NavigableSet<E>.tailSet is not yet implemented");
		// return null;
	}

}
