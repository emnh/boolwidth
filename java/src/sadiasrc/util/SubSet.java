package sadiasrc.util;

import sadiasrc.graph.IVSet;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.*;


/**
 * This is a subset where one can quickly test containment
 * but iteration is slow.
 * You need to supply a groundset of which this is a subset.
 * If that groundset changes, the invariant of this set is not kept!
 * To be able to do this, one must make sure one supply an Iterable<E>
 * and not an IndexedSet<E> to the constructor.
 *
 * It uses bit manipulation, so in order to reflect this in the runningtimes
 * I introduce runningtimes like O(n/64)
 *
 * @author mva021
 *
 * @param <E>
 */
public class SubSet<E> extends AbstractSet<E> implements Set<E>, Comparable<SubSet<E>>{

	protected IndexedSet<E> groundSet;
	protected BitSet subset;

	/**
	 * This constructor is not available!
	 */
	public SubSet()
	{
		throw new UnsupportedOperationException("Subsets need a set to be subset of!");
	}

	public SubSet(IndexedSet<E> base,Collection<E> set) {
		this(base);
		if(set.size() == base.size() && set instanceof BitSet)
		{
			BitSet bs = (BitSet) set; 
			subset = (BitSet) bs.clone();
		}
		else
			addAll(set);
	}

	//O(n)
	//TODO: change such that a copy of the given set is made and kept.
	public SubSet(IndexedSet<E> set) {
		groundSet = set;
		subset = new BitSet(set.size());
	}

	public SubSet(SubSet<E> ss) {
		this(ss.groundSet,ss.subset);
	}

	public SubSet(Iterable<E> vertices) {
		this(new IndexedSet<E>(vertices));
		//System.out.println("Creating new groundset!");
	}

	public SubSet(IndexedSet<E> groundSet, BitSet bits) {
		this.groundSet = groundSet;
		this.subset = (BitSet) bits.clone();
	}

	public void setBit(int index)
	{
		subset.set(index);
	}

	public void setBit(int index, boolean value)
	{
		subset.set(index, value);
	}

	@Override
    @SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof SubSet)
		{
			//System.out.println("comparing");
			SubSet<E> ss = (SubSet<E>) o;
			if (ss.getGroundSet() != groundSet)
				return false;
			return ss.subset.equals(subset);
		}
		return false;
	}

	//O(n/64)
	@Override
	public int size() {
		return subset.cardinality();
	}

	//O(1)
	@Override
	public boolean isEmpty() {
		return subset.isEmpty();
	}

	//O(1)
	@Override
	public boolean contains(Object o) {
		if(!groundSet.contains(o))
			return false;
		return subset.get(groundSet.indexOf(o));
	}

	//O(n/64)
	public E first()
	{
		if(isEmpty())
			throw new IndexOutOfBoundsException("There is no elements in this set");
		return groundSet.get(subset.nextSetBit(0));
	}

	public IndexedSet<E> getGroundSet()
	{
		return groundSet;
	}
	public int indexOf(E e)
	{
		return groundSet.indexOf(e);
	}

	//O(n)
	public ArrayList<E> list()
	{
		ArrayList<E> al = new ArrayList<E>(size());
		for(int i=0; al.size()<size();i++)
		{
			if(subset.get(i))
				al.add(groundSet.get(i));
		}
		return al;
	}

	//O(n)
	@Override
	public Iterator<E> iterator() {
		return list().iterator();
	}

	//O(n)
	@Override
	public Object[] toArray() {
		return list().toArray();
	}

	//O(n)
	@Override
	public <T> T[] toArray(T[] a) {
		return list().toArray(a);
	}

	//O(1)
	@Override
	public boolean add(E e) {
		int i = groundSet.indexOf(e);
		if(i<0)
			return false;
		subset.set(i);
		return true;
	}


	//O(1)
	@Override
	public boolean remove(Object o) {
		int i = groundSet.indexOf(o);
		if(i<0)
			return false;
		subset.clear(i);
		return true;
	}

	//O(n)
	public boolean AddAll(SubSet<E> ss)
	{
		int num = size();
		subset.or(ss.subset);
		return size()>num;
	}

	//O(n/64)
	public boolean retainAll(SubSet<E> ss) {
		subset.and(ss.subset);
		return false;
	}

	//O(n/64)
	public boolean intersects(SubSet<E> ss)
	{
		return subset.intersects(ss.subset);
	}

    //O(n/64)
    public SubSet<E> intersection(SubSet<E> ss) {
        SubSet<E> intersect = new SubSet<>(groundSet, subset);
        intersect.subset.and(ss.subset);
        return intersect;
    }

	//O(n/64)
	public boolean oneIntersects(SubSet<E> ss) {
		int l=subset.nextSetBit(0);
		int r=ss.subset.nextSetBit(0);
		int count=0;
		while(l>=0 && r>=0 && count<2)
		{
			if(l==r)
			{	count++;
				l=subset.nextSetBit(l+1);
				if(l>0)
					r=ss.subset.nextSetBit(l);
			}
			else
			{
				if(l<r)
					l=subset.nextSetBit(r);
				else
					r=ss.subset.nextSetBit(l);
			}
		}
		return count==1;
	}

	/**
	 * @param ss
	 * @return the unique element in the intersection if such exist, null otherwise
	 */
	public E oneIntersectElement(SubSet<E> ss) {
		int l=subset.nextSetBit(0);
		int r=ss.subset.nextSetBit(0);
		E u = null;
		int count=0;
		while(l>=0 && r>=0 && count<2)
		{
			if(l==r)
			{	count++;
				u = groundSet.get(l);
				l=subset.nextSetBit(l+1);
				if(l>0)
					r=ss.subset.nextSetBit(l);
			}
			else
			{
				if(l<r)
					l=subset.nextSetBit(r);
				else
					r=ss.subset.nextSetBit(l);
			}
		}
		if(count==1)
			return u;
		else return null;
	}

	//O(n/64)
	public boolean removeAll(SubSet<E> ss) {
		if(subset.intersects(ss.subset))
		{
			subset.andNot(ss.subset);
			return true;
		}
		return false;
	}

	//O(n/64)
	@Override
	public void clear() {
		subset.clear();
	}

	@Override
	public int hashCode() {
		return subset.hashCode();
	}
	@Override
	public SubSet<E> clone() throws CloneNotSupportedException {
		return new SubSet<E>(this.groundSet,subset);
	}

	public int compareTo(SubSet<E> ss) {
		if(ss.getGroundSet() != groundSet)
			throw new InputMismatchException("These subsets are not comparable, since they have different groundsets!");
		if(ss.size()!=size())
		{
			return size()-ss.size();
		}
		//flip
		subset.xor(ss.subset);
		int c = -1;
		//compare first difference
		if(subset.isEmpty())
			c = 0;
		else if(ss.subset.get(subset.nextSetBit(0)))
			c=1;
		//flip back
		subset.xor(ss.subset);

		return c;
	}

	public static void main(String[] args) {
		IndexGraph G = new IndexGraph(10);
		SubSet<IndexVertex> bs = new SubSet<IndexVertex>(G.vertices());//empty subset of the vertices of G
		bs.add(G.getVertex(2));
		bs.add(G.getVertex(8));
		bs.add(G.getVertex(5));
		SubSet<IndexVertex> x = new SubSet<IndexVertex>(G.vertices());
		x.add(G.getVertex(5));
		x.add(G.getVertex(9));
		//x.add(G.getVertex(7));
		char c = '=';
		if(bs.compareTo(x)<0) c='<';
		if(bs.compareTo(x)>0) c='>';
		System.out.println(""+bs+c+x);
		System.out.println(bs.oneIntersects(x));
	}

}

