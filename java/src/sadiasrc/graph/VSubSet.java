package sadiasrc.graph;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

import sadiasrc.util.IndexedSet;
import sadiasrc.util.SubSet;

public class VSubSet extends SubSet<IndexVertex> implements IVSet {

    class BitSetIterator implements Iterator<IndexVertex> {

        //for (int i = subset.nextSetBit(0); i >= 0; i = subset.nextSetBit(i+1)) {
        private int i = subset.nextSetBit(0);

        public BitSetIterator() {

        }

        public boolean hasNext() {
            return i >= 0;
        }

        public IndexVertex next() {
            IndexVertex ret = groundSet.get(i);
            i = subset.nextSetBit(i+1);
            return ret;
        }

        public void remove() {
            //System.out.printf("removing: %d\n", i);
            if (i >= 0) subset.clear(i);
            //throw new UnsupportedOperationException("remove");
        }
    }

	public VSubSet(Iterable<IndexVertex> vertices) {
		super(vertices);
	}

	public VSubSet(IndexedSet<IndexVertex> groundSet,
			Collection<IndexVertex> set) {
		super(groundSet,set);
	}

	public VSubSet(IndexedSet<IndexVertex> groundSet, BitSet set) {
		super(groundSet,set);
	}

	public VSubSet(VSubSet ss)
	{
		super(ss);
	}
	public VSubSet(IndexedSet<IndexVertex> groundSet) {
		super(groundSet);
	}


	
	public int compareTo(IVSet ss) {
		System.out.println("comparing");
		if(ss.size()!=size())
		{
			return ss.size()-size();
		}
		if(!(ss instanceof VSubSet))
		{
			IndexVertex firstDiff = null;
			for(IndexVertex v : ss)
			{
				if(firstDiff==null || firstDiff.compareTo(v)>0)
				{
					if(!contains(v))
						firstDiff=v;
				}
			}
			if(firstDiff==null)
				return 0;
			for(IndexVertex v : this)
			{
				if(v.compareTo(firstDiff)<0)
					return -1;
			}
			return 1;
		}
		VSubSet vss = (VSubSet) ss;
		//flip
		subset.xor(vss.subset);
		int c = -1;
		//compare first difference
		if(vss.subset.get(subset.nextSetBit(0)))
			c=1;
		//flip back
		subset.xor(vss.subset);

		return c;
	}

	public VSubSet clone(){
		return new VSubSet(this.groundSet,(BitSet) subset.clone());
	}

    public void cloneInPlace(VSubSet o) {
        this.groundSet = o.groundSet;
        this.subset = (BitSet) o.subset.clone();
        //return new VSubSet(this.groundSet,(BitSet) subset.clone());
    }

	public static void main(String[] args)
	{
		IndexGraph G = new IndexGraph(12);
		VSubSet ss = new VSubSet(G.vertices());
		ss.add(G.getVertex(4));
		ss.add(G.getVertex(7));
		ss.add(G.getVertex(8));
		ss.add(G.getVertex(11));
		System.out.println(ss);
		VSubSet cs = new VSubSet(ss);
		System.out.println(cs.equals(ss));
        System.out.println(ss.inverse());
	}

	public static VSubSet union(VSubSet s1, VSubSet s2) {
		VSubSet ns =new VSubSet(s1);
		ns.addAll(s2);
		return ns;
	}

    public static VSubSet intersection(VSubSet s1, VSubSet s2) {
        VSubSet ns = new VSubSet(s1);
        ns.retainAll(s2);
        return ns;
    }

	public VSubSet union(VSubSet s2) {
		VSubSet s = new VSubSet(groundSet, subset);
		s.subset.or(s2.subset);
		return s;
	}

	public VSubSet intersection(VSubSet s2) {
        VSubSet s = new VSubSet(groundSet, subset);
        s.subset.and(s2.subset);
        return s;
	}

    public VSubSet inverse() {
        VSubSet s = new VSubSet(groundSet, subset);
        s.subset.flip(0, groundSet.size());
        return s;
    }

    public void intersectionInPlace(VSubSet s2) {
        subset.and(s2.subset);
    }

    public void subtractInPlace(VSubSet s2) {
        subset.andNot(s2.subset);
    }

    public VSubSet subtract(VSubSet s2) {
        VSubSet s = new VSubSet(groundSet, subset);
        s.subset.andNot(s2.subset);
        return s;
    }

    public boolean isSubSet(VSubSet s2) {
        return this.subtract(s2).size() == 0;
    }

    //O(1)
    // Overridden for performance
    public boolean contains(IndexVertex o) {
        return subset.get(o.id());
    }

    //O(1)
    // Overridden for performance
    @Override
    public boolean addAll(Collection<? extends IndexVertex> c) {
        boolean modified = false;
        for (IndexVertex e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    //O(1)
    // Overridden for performance
    @Override
    public boolean add(IndexVertex e) {
        subset.set(e.id());
        return true;
    }

    //O(1)
    // Overridden for performance
    public boolean remove(IndexVertex e) {
        subset.clear(e.id());
        return true;
    }

    public Iterator<IndexVertex> iterator() {
        return new BitSetIterator();
    }

	public static VSubSet symDiff(VSubSet s1, VSubSet s2) {
			BitSet set = (BitSet) s1.subset.clone();
			set.xor(s2.subset);
			return new VSubSet(s1.groundSet,set);
		}

	public boolean intersects(Iterable<IndexVertex> vertices) {
		for(IndexVertex v : vertices)
			if(contains(v))
				return true;
		return false;
	}
}

