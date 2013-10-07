package interfaces;

import java.util.Set;

public interface IPosSet<TVertex extends ISetPosition> extends Set<TVertex> {

	/**
	 * O(1)
	 */
	public int getId(TVertex v);

	public TVertex getVertex(int i);

}