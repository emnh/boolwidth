package interfaces;

import java.util.Iterator;
import java.util.Set;

public interface ISubSet<TSubSet extends ISubSet<TSubSet, T>, T> extends
		Comparable<TSubSet>, Iterable<T>// , Set<T>
{
	public Set<T> getSet();

	public Set<T> getSubSet();

	public Iterator<TSubSet> setIterator();

	public Iterator<TSubSet> setIterator(int k);

	// new subset which is this subset union some other subset
	public TSubSet union(TSubSet set);
}
