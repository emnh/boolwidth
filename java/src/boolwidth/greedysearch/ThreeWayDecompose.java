package boolwidth.greedysearch;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Util;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.growNeighbourHood.GrowNeighbourHoodDecompose;
import com.github.krukow.clj_lang.PersistentVector;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by emh on 11/2/2014.
 */
public class ThreeWayDecompose extends BaseDecompose {

    public ThreeWayDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        // decompose top cut 3 way
        HashSet<Vertex<Integer>> remaining = new HashSet<>();
        long totalsize = 0;
        for (Vertex<Integer> v : getGraph().vertices()) {
            totalsize++;
            remaining.add(v);
        }

        long start = System.currentTimeMillis();
        ImmutableBinaryTree lefts1ibt = new ImmutableBinaryTree();
        lefts1ibt = lefts1ibt.addRoot();
        ImmutableBinaryTree lefts2ibt = new ImmutableBinaryTree();
        lefts2ibt = lefts2ibt.addRoot();
        ImmutableBinaryTree remainingibt = new ImmutableBinaryTree();
        remainingibt.addRoot();

        PersistentVector<Vertex<Integer>> lefts1 = Util.createPersistentVector();
        PersistentVector<Vertex<Integer>> lefts2 = Util.createPersistentVector();

        while (!remaining.isEmpty()) {
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;
            ImmutableBinaryTree targetibt = null;
            PersistentVector<Vertex<Integer>> target = null;

            for (Vertex<Integer> v : remaining) {
                PersistentVector<Vertex<Integer>> newlefts1 = lefts1.cons(v);
                //ImmutableBinaryTree newlefts1ibt = trickle(lefts1ibt, null, lefts1ibt.getRoot(), v);
                //long cb1 = getBooleanWidth(newlefts1ibt);
                long cb1 = getCutBool(newlefts1, true);

                PersistentVector<Vertex<Integer>> newlefts2 = lefts2.cons(v);
                //ImmutableBinaryTree newlefts2ibt = trickle(lefts2ibt, null, lefts2ibt.getRoot(), v);
                //long cb2 = getBooleanWidth(newlefts2ibt);
                long cb2 = getCutBool(newlefts2, true);

                long cb3;
                if (remaining.size() > totalsize / 2) {
                    cb3 = Long.MAX_VALUE;
                } else {
                    ArrayList<Vertex<Integer>> vertexIDs3 = new ArrayList<>();
                    for (Vertex<Integer> v2 : remaining) {
                        if (v != v2) {
                            vertexIDs3.add(v2);
                        }
                    }
                    cb3 = getCutBool(vertexIDs3, true);
                }

                if (cb1 < minmove && cb1 < cb3) {
                    minmove = cb1;
                    tomove = v;
                    target = lefts1;
                    //targetibt = newlefts1ibt;
                } else if (cb2 < minmove && cb2 < cb3) {
                    minmove = cb2;
                    tomove = v;
                    target = lefts2;
                    //targetibt = newlefts2ibt;
                }
            }
            System.out.printf("time: %d, sz: [%d,%d,%d:%d], bw: [%.2f, %.2f, %.2f], 2^bw: [%d, %d, %d], lefts1: %s, lefts2: %s\n",
                    System.currentTimeMillis() - start,
                    lefts1.size(), lefts2.size(), remaining.size(), totalsize,
                    getLogBooleanWidth(getCutBool(lefts1, true)),
                    getLogBooleanWidth(getCutBool(lefts2, true)),
                    getLogBooleanWidth(getCutBool(remaining, true)),
                    getCutBool(lefts1, true),
                    getCutBool(lefts2, true),
                    getCutBool(remaining, true),
                    lefts1, lefts2);
            if (target == lefts1) {
                lefts1 = lefts1.cons(tomove);
                remaining.remove(tomove);
                //lefts1ibt = targetibt;
            } else if (target == lefts2) {
                lefts2 = lefts2.cons(tomove);
                remaining.remove(tomove);
                //lefts2ibt = targetibt;
            } else {
                System.out.printf("no valid moves left, cut is balanced, remaining: %s\n", remaining);
                break;
            }
        }
        ArrayList<Vertex<Integer>> list1 = new ArrayList<>();
        lefts1.forEach((node) -> list1.add(node));
        ArrayList<Vertex<Integer>> list2 = new ArrayList<>();
        lefts2.forEach((node) -> list2.add(node));
        ArrayList<Vertex<Integer>> list3 = new ArrayList<>();
        remaining.forEach((node) -> list3.add(node));
        GrowNeighbourHoodDecompose gd = new GrowNeighbourHoodDecompose(getGraph());
        ImmutableBinaryTree ibt1 = gd.decompose(list1);
        ImmutableBinaryTree ibt2 = gd.decompose(list2);
        ImmutableBinaryTree ibt3 = gd.decompose(list3);
        return ibt1.join(ibt2).join(ibt3);
    }
}
