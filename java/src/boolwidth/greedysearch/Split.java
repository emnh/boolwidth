package boolwidth.greedysearch;

import com.github.krukow.clj_lang.PersistentHashSet;
import com.github.krukow.clj_lang.PersistentVector;
import graph.Vertex;

import java.util.ArrayList;

/**
 * Created by emh on 11/2/2014.
 */
public class Split {
    private BaseDecomposition decomposition;

    public PersistentHashSet<Vertex<Integer>> getRemaining() {
        return remaining;
    }

    private PersistentHashSet<Vertex<Integer>> remaining = Util.createPersistentHashSet();
    private PersistentVector<Vertex<Integer>> lefts = Util.createPersistentVector();
    long cutbool = 0;

    public Split(Split old) {
        this.remaining = old.remaining;
        this.lefts = old.lefts;
    }

    public Split(BaseDecomposition decomposition, ArrayList<Vertex<Integer>> remaining) {
        this.decomposition = decomposition;
    }

    public boolean done() {
        return this.remaining.isEmpty();
    }

    public void logStatement() {
        /*System.out.printf("time: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, lefts: %s\n",
                System.currentTimeMillis() - this.decomposition.getStart(),
                lefts.size(), totalsize,
                decomposition.getLogBooleanWidth(minmove), minmove, lefts);*/
    }

    public Split cons(Vertex<Integer> toadd) {
        Split result = new Split(this);
        result.remaining = result.remaining.cons(toadd);
        return result;
    }

    public Split decomposeAdvance() {
        Split result = new Split(this);
        if (done()) {
            return this;
        } else {
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            for (Vertex<Integer> v : remaining) {
                PersistentVector<Vertex<Integer>> newlefts = lefts.cons(v);
                long cb = decomposition.getCutBool(newlefts, true);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                }
            }

            result.lefts = result.lefts.cons(tomove);
            result.remaining = result.remaining.disjoin(tomove);
        }
        return result;
    }
}
