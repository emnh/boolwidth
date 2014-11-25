package boolwidth.greedysearch.base;

import com.github.krukow.clj_lang.PersistentHashSet;
import graph.Vertex;

import java.util.Collection;

/**
 * Created by emh on 11/2/2014.
 */
public class Split {
    protected BaseDecompose decomposition;
    protected long cutbool = 0;
    protected int depth = 0;
    protected PersistentHashSet<Vertex<Integer>> rights = Util.createPersistentHashSet();
    protected PersistentHashSet<Vertex<Integer>> lefts = Util.createPersistentHashSet();
    protected Vertex<Integer> reference = null; // last moved or added

    protected Split() {

    }

    public Split create(Split old) {
        Split result = new Split();
        result.copy(old);
        return result;
    }

    protected void copy(Split old) {
        this.decomposition = old.decomposition;
        this.cutbool = old.cutbool;
        this.rights = old.rights;
        this.lefts = old.lefts;
        this.reference = old.reference;
        this.depth = old.depth;
    }

    public Split(int depth, BaseDecompose decomposition) {
        this.depth = depth;
        this.decomposition = decomposition;
    }

    public Split(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        this.decomposition = decomposition;
        this.depth = depth;
        for (Vertex<Integer> v : rights) {
            this.rights = this.rights.cons(v);
        }
    }

    public Split(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        this.decomposition = decomposition;
        this.depth = depth;
        for (Vertex<Integer> v : lefts) {
            this.lefts = this.lefts.cons(v);
        }
        for (Vertex<Integer> v : rights) {
            this.rights = this.rights.cons(v);
        }
    }

    public long getNewCutBoolWithAddedVertex(Vertex<Integer> tomove) {
        long cbLeftAddRight = this.getDecomposition().getCutBool(lefts, true);
        long cbRightAddLeft = this.getDecomposition().getCutBool(rights, true);
        long cbLeftAddLeft = this.getDecomposition().getCutBool(lefts.cons(tomove), true);
        long cbRightAddRight = this.getDecomposition().getCutBool(rights.cons(tomove), true);
        return Math.min(Math.max(cbLeftAddRight, cbRightAddRight), Math.max(cbLeftAddLeft, cbRightAddLeft));
    }

    public PersistentHashSet<Vertex<Integer>> getRights() {
        return rights;
    }

    public PersistentHashSet<Vertex<Integer>> getLefts() {
        return lefts;
    }

    public PersistentHashSet<Vertex<Integer>> getAll() {
        PersistentHashSet<Vertex<Integer>> newSet = lefts;
        for (Vertex<Integer> r : rights) {
            newSet = newSet.cons(r);
        }
        return newSet;
    }

    public Vertex<Integer> getLastMoved() {
        return reference;
    }

    public BaseDecompose getDecomposition() {
        return decomposition;
    }

    public int getDepth() {
        return depth;
    }

    public int size() {
        return lefts.size() + rights.size();
    }

    public boolean done() {
        return this.rights.isEmpty();
    }

    public void logStatement() {
        int lastid = -1;
        int lastDegree = -1;
        if (getLastMoved() != null) {
            lastid = getLastMoved().id();
            lastDegree = this.getDecomposition().getGraph().degree(getLastMoved());
        }
        if (false) {
            System.out.printf("time: %d, d: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, last: %d, deg: %d, lefts: %s\n",
                    System.currentTimeMillis() - this.decomposition.getStart(),
                    depth, lefts.size(), lefts.size() + rights.size(),
                    decomposition.getLogBooleanWidth(cutbool), cutbool,
                    lastid, lastDegree,
                    lefts);
        }
    }

    public Split cons(Vertex<Integer> toadd) {
        Split result = create(this);
        result.rights = result.rights.cons(toadd);
        return result;
    }

    public boolean isBalanced() {
        return lefts.size() >= rights.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Split split = (Split) o;

        if (!lefts.equals(split.lefts)) return false;
        if (!rights.equals(split.rights)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rights.hashCode();
        result = 31 * result + lefts.hashCode();
        return result;
    }

    public long measureCutForDecompose(Collection<Vertex<Integer>> newLefts, Vertex<Integer> toMove){
        return this.getDecomposition().getCutBool(newLefts, true);
    }

    public Split decomposeAdvanceFixed(Vertex<Integer> tomove) {
        Split result = create(this);
        result.lefts = result.lefts.cons(tomove);
        result.rights = result.rights.disjoin(tomove);
        result.cutbool = 0;
        result.reference = tomove;
        result.logStatement();
        return result;
    }

    public Split localSearch() {
        Split result = create(this);
        if (done()) {
            return this;
        } else {
            long oldCutBoolLeft = measureCutForDecompose(lefts, null);
            long oldCutBoolRight = measureCutForDecompose(rights, null);
            long minMove = Math.max(oldCutBoolLeft, oldCutBoolRight);
            Vertex<Integer> toMove = null;

            if (2 * (lefts.size() - 1) >= (rights.size() + 1)) {
                for (Vertex<Integer> v : lefts) {
                    PersistentHashSet<Vertex<Integer>> newRights = rights.cons(v);
                    PersistentHashSet<Vertex<Integer>> newLefts = lefts.disjoin(v);
                    long cb1 = measureCutForDecompose(newRights, v);
                    long cb2 = measureCutForDecompose(newLefts, v);
                    long cb = Math.max(cb1, cb2);
                    if (cb < minMove) {
                        minMove = cb;
                        toMove = v;
                    }
                }
            }
            if (2 * (rights.size() - 1) >= (lefts.size() + 1)) {
                for (Vertex<Integer> v : rights) {
                    PersistentHashSet<Vertex<Integer>> newLefts = lefts.cons(v);
                    PersistentHashSet<Vertex<Integer>> newRights = rights.disjoin(v);
                    long cb1 = measureCutForDecompose(newRights, v);
                    long cb2 = measureCutForDecompose(newLefts, v);
                    long cb = Math.max(cb1, cb2);
                    if (cb < minMove) {
                        minMove = cb;
                        toMove = v;
                    }
                }
            }
            if (rights.contains(toMove)) {
                result.lefts = result.lefts.cons(toMove);
                result.rights = result.rights.disjoin(toMove);
            } else if (lefts.contains(toMove)) {
                result.lefts = result.lefts.disjoin(toMove);
                result.rights = result.rights.cons(toMove);
            }
            result.cutbool = minMove;
            result.reference = toMove;
            result.logStatement();
        }
        return result;
    }

    public Split decomposeAdvanceBase() {
        Split result = create(this);
        if (done()) {
            return this;
        } else {
            long oldcb = measureCutForDecompose(lefts, null);
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            int i = 0;
            for (Vertex<Integer> v : rights) {
                i += 1;
                PersistentHashSet<Vertex<Integer>> newlefts = lefts.cons(v);
                long cb = measureCutForDecompose(newlefts, v);
                //decomposition.getCutBool(newlefts, true);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                    if (cb <= oldcb) {
                        //if (cb <= Math.pow(2, 20)) {
                        //if (cb <= Math.pow(2.0, 13)) {
                        // exit early if we didn't increase
                        System.out.printf("cheated: %d/%d\n", i, rights.size());
                        break;
                    }
                }
            }
            result.lefts = result.lefts.cons(tomove);
            result.rights = result.rights.disjoin(tomove);
            result.cutbool = minmove;
            result.reference = tomove;
            result.logStatement();
        }
        return result;
    }

    public Split decomposeAdvance() {
        return decomposeAdvanceBase();
    }

    public Split decomposeAdvanceRight() {
        Split result = create(this);

        // swap lefts and rights
        PersistentHashSet<Vertex<Integer>> temp = result.getLefts();
        result.lefts = result.rights;
        result.rights = temp;

        // advance
        result = result.decomposeAdvance();

        // swap back
        temp = result.getLefts();
        result.lefts = result.rights;
        result.rights = temp;

        return result;
    }
}
