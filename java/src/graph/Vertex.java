package graph;

import graph.subsets.Position;
import interfaces.IAttributeStorage;
import interfaces.IVertexFactory;

import java.util.HashMap;

/**
 * Implementation of a vertex for an undirected adjacency list graph. Each
 * vertex stores its incidence container and position in the vertex container of
 * the graph.
 */

public class Vertex<V> extends Position<V>
        implements Comparable<Vertex<V>>, IAttributeStorage {

    HashMap<String, Object> attributes = new HashMap<String, Object>();

    @Override
	public void setAttr(String key, Object value) {
		this.attributes.put(key, value);
	}

    @SuppressWarnings("unchecked")
	public <T> T getAttr(String key) {
		if (this.attributes.containsKey(key)) {
			return (T) this.attributes.get(key);
		} else {
			// TODO: get default from configuration
			return null;
		}
	}

    @Override
	public boolean hasAttr(String key) {
		return this.attributes.containsKey(key);
	}

	public static final class Factory<V> implements
			IVertexFactory<Vertex<V>, V> {

		@Override
		public Vertex<V> createNew(V element, int id) {
			return new Vertex<V>(element, id);
		}
	}

    // For serialization only
    @Deprecated
    public Vertex()
    {

    }

	public Vertex(V element, int id) {
		super(element, id);
	}

	public int compareTo(Vertex<V> v) {
		return id() - v.id();
	}

    /**
	 * Returns a string representation of the element stored at this vertex.
	 */
	@Override
	public String toString() {
		String ret;
		if (this.elem == null) {
			ret = Integer.toString(id());
		} else {
			ret = this.elem.toString();
		}
		return ret;
	}
}
