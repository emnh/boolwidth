package sadiasrc.graph;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import sadiasrc.util.IndexedSet;
import sadiasrc.util.MultiIterator;

public class IndexGraphSubSet extends AbstractSet<IGraphElement> implements
		IGraphSubSet<IndexVertex, IndexEdge<IndexVertex>> {

	protected IndexGraph groundSet;
	protected VSubSet vertices;
	protected IndexedSet<IndexEdge<IndexVertex>> edges;

	public IndexGraphSubSet(IndexGraph g) {
		this.groundSet = g;
		this.vertices = new VSubSet(g.vertices());
		this.edges = new IndexedSet<IndexEdge<IndexVertex>>(g.edges());
	}

	public IndexGraphSubSet(IndexGraph g, Collection<? extends IGraphElement> c) {
		this(g);
		addAll(c);
	}

	public IndexGraphSubSet(IndexGraph g, Iterable<? extends IGraphElement> c) {
		this(g);
		for (IGraphElement e : c) {
			add(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(IGraphElement obj) {
		if (obj instanceof IndexVertex) {
			return addVertex((IndexVertex) obj);
		}
		if (obj instanceof IndexEdge) {
			return addEdge((IndexEdge<IndexVertex>) obj);
		}
		return false;
	}

	@Override
	public boolean addEdge(IndexEdge<IndexVertex> e) {
		if (e.owner() == this.groundSet) {
			return this.edges.add(e);
		} else {
			return false;
		}
	}

	@Override
	public boolean addVertex(IndexVertex v) {
		if (v.owner() == this.groundSet) {
			return this.vertices.add(v);
		} else {
			return false;
		}
	}

	@Override
	public void clear() {
		this.vertices.clear();
		this.edges.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object obj) {
		if (obj instanceof IndexVertex) {
			return containsVertex((IndexVertex) obj);
		}
		if (obj instanceof IndexEdge) {
			return containsEdge((IndexEdge<IndexVertex>) obj);
		}
		return false;
	}

	@Override
	public boolean containsEdge(IndexEdge<IndexVertex> e) {
		return this.edges.contains(e);
	}

	@Override
	public boolean containsVertex(IndexVertex v) {
		return this.vertices.contains(v);
	}

	@Override
	public Collection<IndexEdge<IndexVertex>> edges() {
		return this.edges;
	}

	@Override
	public IGraph<IndexVertex, IndexEdge<IndexVertex>> graph() {
		return this.groundSet;
	}

	@Override
	public boolean intersects(
			IGraphSubSet<IndexVertex, IndexEdge<IndexVertex>> ss) {
		if (this.vertices.intersects(ss.vertices())) {
			return true;
		}
		if (this.edges.intersects(ss.edges())) {
			return true;
		}
		return false;
	}

	@Override
	public Iterator<IGraphElement> iterator() {
		ArrayList<Iterator<? extends IGraphElement>> al = new ArrayList<Iterator<? extends IGraphElement>>(
				2);
		al.add(vertices().iterator());
		al.add(edges().iterator());
		return new MultiIterator<IGraphElement>(al.iterator());
	}

	@Override
	public int numEdges() {
		return this.edges.size();
	}

	@Override
	public int numVertices() {
		return this.vertices.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (!contains(o)) {
			return false;
		}
		if (o instanceof IndexVertex) {
			return removeVertex((IndexVertex) o);
		}
		if (o instanceof IndexEdge) {
			return removeEdge((IndexEdge<IndexVertex>) o);
		}

		return false;
	}

	protected boolean removeEdge(IndexEdge<IndexVertex> o) {
		return this.edges.remove(o);
	}

	protected boolean removeVertex(IndexVertex o) {
		return this.vertices.remove(o);
	}

	@Override
	public int size() {
		return numVertices() + numEdges();
	}

	@Override
	public Collection<IndexVertex> vertices() {
		return this.vertices;
	}
}
