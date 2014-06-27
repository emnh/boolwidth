package sadiasrc.graph;

/**
 * Implementation of a vertex for a graph.
 */

public class IndexVertex extends IndexGraphElement implements IVertex, Comparable<IndexVertex>{
	public static String LABEL_FIELD = "label";

	public IndexVertex(IGraph<?, ?> owner, int index) {
		super(owner, index);
	}

	public IVertex createInstance(IGraph<?, ?> g) {
		return new IndexVertex(g, g.numVertices());
	}

	@Override
	public boolean equals(IVertex v) {
		if(v==null) return false;
		if (v.getClass() == this.getClass()) {
			return super.equals(v);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[v:" + id() + "]";
	}

	@SuppressWarnings("static-access")
	public String getAttr(IndexVertex v) {
		return v.LABEL_FIELD;
	}

	@SuppressWarnings("static-access")
	public void setAttr(IndexVertex v,String label) {
		v.LABEL_FIELD=label;

	}

	@Override
	public int compareTo(IndexVertex o) {
		if(o==null)
			return -1;
		return id()-o.id();
	}

	@Override
	public int hashCode() {
		return id();
	}

}
