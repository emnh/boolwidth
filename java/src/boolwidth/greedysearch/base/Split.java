package boolwidth.greedysearch.base;

import boolwidth.greedysearch.Util;
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

    public Split(Split old) {
        copy(old);
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
        System.out.printf("time: %d, d: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, last: %d, deg: %d, lefts: %s\n",
                System.currentTimeMillis() - this.decomposition.getStart(),
                depth, lefts.size(), lefts.size() + rights.size(),
                decomposition.getLogBooleanWidth(cutbool), cutbool,
                lastid, lastDegree,
                lefts);
    }

    public Split cons(Vertex<Integer> toadd) {
        Split result = new Split(this);
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

    public Split decomposeAdvance() {
        Split result = new Split(this);
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

    public Split decomposeAdvanceRight() {
        Split result = new Split(this);
        if (done()) {
            return this;
        } else {
            long oldcb = measureCutForDecompose(lefts, null);
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            int i = 0;
            for (Vertex<Integer> v : lefts) {
                i += 1;
                PersistentHashSet<Vertex<Integer>> newlefts = lefts.disjoin(v);
                long cb = measureCutForDecompose(newlefts, v);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                    /*if (cb <= Math.pow(2.0, 13)) {
                        // exit early if we didn't increase
                        System.out.printf("cheated: %d/%d\n", i, rights.size());
                        break;
                    }*/
                }
            }
            result.lefts = result.lefts.disjoin(tomove);
            result.rights = result.rights.cons(tomove);
            result.cutbool = minmove;
            result.reference = tomove;
            result.logStatement();
        }
        return result;
    }
}
