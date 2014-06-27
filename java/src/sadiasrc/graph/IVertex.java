package sadiasrc.graph;

public interface IVertex extends IGraphElement {

	public IVertex createInstance(IGraph<?, ?> g);

	public boolean equals(IVertex v);
}
