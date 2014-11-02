package boolwidth.greedysearch;

import com.github.krukow.clj_lang.PersistentHashSet;
import graph.Vertex;

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


    public Split(Split old) {
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
        System.out.printf("time: %d, d: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, lefts: %s\n",
                System.currentTimeMillis() - this.decomposition.getStart(),
                depth, lefts.size(), lefts.size() + rights.size(),
                decomposition.getLogBooleanWidth(cutbool), cutbool, lefts);
    }

    public Split cons(Vertex<Integer> toadd) {
        Split result = new Split(this);
        result.rights = result.rights.cons(toadd);
        return result;
    }

    public boolean isBalanced() {
        return lefts.size() >= rights.size();
    }

    public Split decomposeAdvance(MeasureCut measureCut) {
        Split result = new Split(this);
        if (done()) {
            return this;
        } else {
            long oldcb = measureCut.applyAsLong(lefts, null);
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            int i = 0;
            for (Vertex<Integer> v : rights) {
                i += 1;
                PersistentHashSet<Vertex<Integer>> newlefts = lefts.cons(v);
                long cb = measureCut.applyAsLong(newlefts, v);
                //decomposition.getCutBool(newlefts, true);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                    if (cb <= oldcb) {
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
}
