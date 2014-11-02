package boolwidth.greedysearch;

import com.github.krukow.clj_lang.PersistentVector;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by emh on 11/2/2014.
 */
public class TwoWayDecomposition extends BaseDecomposition {

    public TwoWayDecomposition(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        HashSet<Vertex<Integer>> remaining = new HashSet<>();
        long totalsize = 0;
        for (Vertex<Integer> v : getGraph().vertices()) {
            totalsize++;
            remaining.add(v);
        }

        long start = System.currentTimeMillis();
        PersistentVector<Vertex<Integer>> lefts = Util.createPersistentVector();
        while (!remaining.isEmpty()) {
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            for (Vertex<Integer> v : remaining) {
                PersistentVector<Vertex<Integer>> newlefts = lefts.cons(v);
                long cb = getCutBool(newlefts, true);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                }

            }
            System.out.printf("time: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, lefts: %s\n",
                    System.currentTimeMillis() - start,
                    lefts.size(), totalsize,
                    getLogBooleanWidth(minmove), minmove, lefts);
            lefts = lefts.cons(tomove);
            remaining.remove(tomove);
        }
        // only uses the order
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        lefts.forEach((node) -> list.add(node));
        return decompose(list);
    }
}
