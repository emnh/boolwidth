package boolwidth.heuristics;

import graph.PosSet;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IGraph;
import interfaces.IPosSet;
import interfaces.IVertexFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.TreeMap;

import boolwidth.CutBool;
import boolwidth.DNode;

public final class VertexSplit<V> extends DNode<VertexSplit<V>, V> implements
Cloneable  {


    // Just for serialization
    @Deprecated
    public VertexSplit() {
        groundSet = null;
    }

	public transient int activeCut = 0;
	public transient ArrayList<VertexSplit<V>> activeCuts = new ArrayList<VertexSplit<V>>();

	private transient final IPosSet<Vertex<V>> groundSet;

	public final static int CUTBOOL_INITVAL = -1;

	public final static int SUBCUTS_INITVAL = -1;

	private transient boolean parent_supported = false;

	// local search state variables
	// the VertexSplit that was modified to get this one
	private VertexSplit<V> horizontalParent;
	// a split is considered initialized if it has left and right child (or has
	// size 1)
	public transient boolean initialized = false;

	// max(cutbool(left), cutbool(right))
	private long cutbool = CUTBOOL_INITVAL;
	private transient long cutbool_lower_bound = CUTBOOL_INITVAL;
	private transient long subtree_upper_bound = SUBCUTS_INITVAL;
	private transient VertexSplit<V> bestcut = null;
	public transient int steps = 0;

	public static final class Factory<V> implements
	IVertexFactory<VertexSplit<V>, PosSubSet<Vertex<V>>> {

		IGraph<Vertex<V>, V, ?> graph;

        // Just for serialization
        @Deprecated
        public Factory() {

        }

		public Factory(IGraph<Vertex<V>, V, ?> graph) {
			this.graph = graph;
		}

		public VertexSplit<V> createNew(PosSubSet<Vertex<V>> element, int id) {
            // unresolved constructor is just an IntelliJ IDEA problem, not a compile problem
			return new VertexSplit<V>(this.graph, element, id);
		}

	}

	public VertexSplit(IPosSet<Vertex<V>> groundSet,
			Iterable<Vertex<V>> vertices, int id) {
		// checked
		super(new PosSubSet<Vertex<V>>(groundSet), id);
		assert !(vertices instanceof PosSubSet<?>) : "wrong constructor, overloading failed";
		for (Vertex<V> v : vertices) {
			this.element().add(v);
		}
		this.groundSet = groundSet;
	}

	public VertexSplit(IPosSet<Vertex<V>> groundSet,
			PosSubSet<Vertex<V>> vertices, int id) {
		super(vertices, id);
		this.groundSet = groundSet;
	}

	public boolean addActiveIfGood(VertexSplit<V> active) {
		boolean ret = false;
		if (!this.activeCuts.contains(active)) {
			this.activeCuts.add(active);
			ret = true;
		}
		return ret;
	}

	/**
	 * Build a valid decomposition with boolean width equal to
	 * subtree_upper_bound
	 */
	// TODO: pass interface not LocalSearchR
	public <E> void buildDecomposition(LocalSearchR<V, E> lsr) {
		if (size() > 1) {
			if (this.bestcut != null) {
				this.bestcut.left.buildDecomposition(lsr);
				this.bestcut.right.buildDecomposition(lsr);
				this.setLeft(this.bestcut.left);
				this.setRight(this.bestcut.right);
			} else {
				// assert !isOptimalSubTree();
				// any cuts from here down should work. do a random split.
				Stack<VertexSplit<V>> toinit = new Stack<VertexSplit<V>>();
				toinit.push(this);
				while (!toinit.isEmpty()) {
					VertexSplit<V> current = toinit.pop();
					if (current.size() < 2) {
						continue;
					}
					if (!current.initialized) {
						lsr.initCutRandom(current);
					}
					toinit.push(current.getLeft());
					toinit.push(current.getRight());
				}
			}
		}
	}

	public boolean checkNode() {
		boolean ok = true;
		if (size() > 1) {
			if (getLeft() != null && getRight() != null) {
				ok = getLeft().vertices().size() + getRight().vertices().size() == vertices()
				.size();
				assert ok;
				if (ok) {
					// checked
					ok = new PosSubSet<Vertex<V>>(this.groundSet) {
						public boolean check(Collection<Vertex<V>> lefts,
								Collection<Vertex<V>> rights,
								PosSubSet<Vertex<V>> total) {
							addAll(lefts);
							addAll(rights);

							assert this.equals(total) : String.format("\nchildren: %s\ntotal:    %s\n",
									Long.toBinaryString(this.words[0]),
									Long.toBinaryString(total.words[0]));
							return this.equals(total);
						}
					}.check(getLeft().vertices(), getRight().vertices(),
							vertices());
				}
				assert ok;
			}
		} else {
			ok = getLeft() == null;
			assert ok;
			ok = ok && getRight() == null;
			assert ok;
		}
		return ok;
	}

	@Override
	@SuppressWarnings("unchecked")
	public VertexSplit<V> clone() {
		VertexSplit<V> newnode;
		try {
			newnode = (VertexSplit<V>) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		return newnode;
	}

	public void copyChildren(VertexSplit<V> bag) {
		setLeft(bag.getLeft());
		setRight(bag.getRight());
	}
	// public String toString() {
	// return String.format("v%d", id(), hashCode());
	// }

	public void fixParent(VertexSplit<V> v) {
		setParent(v);
		this.parent_supported = true;
	}

	public void getCached(PosSet<Vertex<V>> rootset,
			TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>> foundsplits) {
		// converting to VertexSubset because it's comparable
		//		PosSubSet<Vertex<V>> newsplitleft = new PosSubSet<Vertex<V>>(rootset,
		//				this.getLeft().vertices());
		PosSubSet<Vertex<V>> newsplitleft = this.getLeft().vertices();
		if (foundsplits.containsKey(newsplitleft)) {
			//System.out.println("cache hit left");
			this.setLeft(foundsplits.get(newsplitleft));
		}
		//		PosSubSet<Vertex<V>> newsplitright = new PosSubSet<Vertex<V>>(rootset,
		//				this.getRight().vertices());
		PosSubSet<Vertex<V>> newsplitright = this.getRight().vertices();
		if (foundsplits.containsKey(newsplitright)) {
			//System.out.println("cache hit right");
			this.setRight(foundsplits.get(newsplitright));
		}
	}

	public long getCutBool() {
		assert hasCutBool();
		return this.cutbool;
	}

	public long getCutBoolLowerBound() {
		return this.cutbool_lower_bound;
	}

	public VertexSplit<V> getHorizontalParent() {
		return this.horizontalParent;
	}

	@Override
	public VertexSplit<V> getParent() {
		if (!this.parent_supported) {
			throw new UnsupportedOperationException();
		} else {
			return super.getParent();
		}
	}

	public long getSubTreeUpperBound() {
		if (this.subtree_upper_bound == SUBCUTS_INITVAL) {
			assert hasCutBool();
			this.subtree_upper_bound = Math.max(getCutBool(), CutBool
					.bestGeneralUpperBound(size(), false));
		}
		return this.subtree_upper_bound;
	}

	public boolean hasCutBool() {
		return this.cutbool != CUTBOOL_INITVAL;
	}

	public boolean isOptimalSubTree() {
		if (size() > 1) {
			assert hasCutBool();
			if (hasCutBool()) {
				assert getSubTreeUpperBound() >= this.cutbool;
				return getSubTreeUpperBound() == this.cutbool;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public int numVertices() {
		return vertices().size();
	}

	private Object readResolve() {
		this.parent_supported = true;
		return this;
	}

	public void setCached(PosSet<Vertex<V>> rootset,
			TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>> foundsplits) {
		//		PosSubSet<Vertex<V>> newsplitleft = new PosSubSet<Vertex<V>>(rootset,
		//				this.getLeft().vertices());
		//		PosSubSet<Vertex<V>> newsplitright = new PosSubSet<Vertex<V>>(rootset,
		//				this.getRight().vertices());
		PosSubSet<Vertex<V>> newsplitleft = this.getLeft().vertices();
		PosSubSet<Vertex<V>> newsplitright = this.getRight().vertices();
		foundsplits.put(newsplitleft, this.getLeft());
		foundsplits.put(newsplitright, this.getRight());
	}

	public void setCutBool(long cutbool) {
		this.cutbool = cutbool;
	}

	public void setCutBoolLowerBound(long cutbool_lower_bound) {
		this.cutbool_lower_bound = cutbool_lower_bound;
	}

	public void setHorizontalParent(VertexSplit<V> horizontalParent) {
		this.horizontalParent = horizontalParent;
	}

	public void setSubTreeUpperBound(long subtree_upper_bound) {
		this.subtree_upper_bound = subtree_upper_bound;
	}

	public void setVertices(PosSubSet<Vertex<V>> vs) {
		setElement(vs);
	}

	// when the node isn't part of decomposition yet
	public void simpleAddLeft() {

	}

	// when the node isn't part of decomposition yet
	public void simpleAddRight() {

	}

	// alias
	public int size() {
		return numVertices();
	}

	@Override
	public String toString() {
		PosSubSet<Vertex<V>> lefts = this.left != null ? this.left.vertices()
				: null;
		PosSubSet<Vertex<V>> rights = this.right != null ? this.right
				.vertices() : null;
				String bag;
				if (lefts == null && rights == null) {
					bag = String.format("%s", vertices());
				} else {
					bag = String.format("%s | %s", lefts, rights);
				}
				return String.format("VSplit(%d): steps=%d, cutbool=%d, bag=%s",
						this.id, this.steps, this.cutbool, bag);
	}

	public void updateCached(PosSet<Vertex<V>> rootset,
			TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>> foundsplits) {
		getCached(rootset, foundsplits);
		setCached(rootset, foundsplits);
	}

	public <E> boolean updateSubTreeUpperBound() {
		assert hasCutBool();
		if (size() > 1) {
			long subtree_upper_bound = Math.max(
					getLeft().getSubTreeUpperBound(), getRight()
					.getSubTreeUpperBound());

			// if the assertion fails, we could have done a random split and
			// fared better,
			// and some code should be optimized to do that.
			// TODO: put back assertion
			// assert subtree_upper_bound <=
			// CutBool.bestGeneralUpperBound(size(),
			// false);

			subtree_upper_bound = Math.max(subtree_upper_bound, getCutBool());
			return updateSubTreeUpperBound(subtree_upper_bound);
		} else {
			return updateSubTreeUpperBound(getCutBool());
		}
	}

	public boolean updateSubTreeUpperBound(long new_subtree_upper_bound) {
		boolean update = false;
		if (new_subtree_upper_bound < getSubTreeUpperBound()) {
			update = true;
			setSubTreeUpperBound(new_subtree_upper_bound);
            // unresolved constructor is just an IntelliJ IDEA problem, not a compile problem
			this.bestcut = new VertexSplit<V>(this.groundSet, vertices(), 0);
			this.bestcut.setLeft(this.left);
			this.bestcut.setRight(this.right);
		}
		return update;
	}

	public PosSubSet<Vertex<V>> vertices() {
		return element();
	}
}
