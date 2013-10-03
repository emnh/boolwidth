package interfaces;

public interface IVertexFactory<TVertex, V> {

	public TVertex createNew(V element, int id);

}
