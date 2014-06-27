package sadiasrc.heuristic;

import sadiasrc.graph.IndexVertex;

import java.util.Iterator;

public interface IChooser extends Iterator<IndexVertex>
{
	public IndexVertex choose();
	public long getUB();
	
}
