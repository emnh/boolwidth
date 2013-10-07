package boolwidth;

import graph.BinNode;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IVertexFactory;

/**
 * 
 * @author Martin Vatshelle Wrapper class for nodes in a DecompositionTree
 * @param <V>
 */
public abstract class DNode<TNode extends DNode<TNode, V>, V> extends
BinNode<TNode, PosSubSet<Vertex<V>>> {

    // Just for serialization
    @Deprecated
    public DNode() {

    }

	// TODO: replace PosSubSet<Vertex<V>> with VertexSubSet and remove this
	private transient int subSet = 0;

	/** Default generics parameterization **/
	public static final class D<V> extends DNode<D<V>, V> {

		public static final class Factory<V> implements
		IVertexFactory<DNode.D<V>, PosSubSet<Vertex<V>>> {
			@Override
			public DNode.D<V> createNew(PosSubSet<Vertex<V>> element, int id) {
				return new DNode.D<V>(element, id);
			}
		}

		public D(PosSubSet<Vertex<V>> element, int id) {
			super(element, id);
		}

	}

	public DNode(PosSubSet<Vertex<V>> element, int id) {
		super(element, id);
	}

	public int getSubSet() {
		return this.subSet;
	}

	public void setSubSet(int subSet) {
		this.subSet = subSet;
	}

	@Override
	public String toString() {
		return String.format("DNode(%d): bag=%s", this.id, element());
	}

}