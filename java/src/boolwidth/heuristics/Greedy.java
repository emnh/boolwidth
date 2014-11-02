package boolwidth.heuristics;

import boolwidth.CutBool;
import boolwidth.cutbool.CutBoolComparator;
import graph.subsets.PosSubSet;
import graph.Vertex;
import util.Util;

import java.util.ArrayList;

/**
 * Created by emh on 10/29/2014.
 */
public class Greedy<V, E> {

    private LSDecomposition.D<V, E> decomposition;
    private CutBoolComparator<V, E> cutboolComparator;
    private boolean useInitGreedyExitEarly = true;

    public Greedy(LSDecomposition.D<V, E> decomposition,
                  CutBoolComparator<V, E> cutboolComparator,
                  boolean useInitGreedyExitEarly) {
        this.decomposition = decomposition;
        this.cutboolComparator = cutboolComparator;
        this.useInitGreedyExitEarly = useInitGreedyExitEarly;
    }

    // TODO: use graph upper bound
    public VertexSplit<V> initGreedy(VertexSplit<V> bag, boolean toplevel) {
        ArrayList<VertexSplit<V>> newSplit = new ArrayList<>(1);
        newSplit.add(null);

        long[] minNewBoolwidth = { Long.MAX_VALUE };

        // it's smart to use "allover" true, because it's faster to move nodes over
        // with small cuts than it is to count potential large cuts resulting from random
        boolean allover = true; //this.useActives;

        if (allover || toplevel) {
            bag.setLeft(this.decomposition.createVertex(bag.vertices(), this.decomposition.getNextID()));
            bag.setRight(
                    this.decomposition.createVertex(
                            new PosSubSet<Vertex<V>>(bag.vertices().getSet()), this.decomposition.getNextID()));
        } else {
            this.decomposition.splitRandom(bag, (bag.size() + 1) / 2);
            assert bag.checkNode() : String.format("BAG=\"%s\"\n", bag.toString());
        }

        VertexSplit<V> swapSplit = bag;

        assert bag.getLeft().size() > 0;

        for (int i = 0; i < bag.getLeft().size(); i++) {
            if (initGreedyCheck(bag, swapSplit, newSplit, minNewBoolwidth)) {
                /*int minsize2 = 0;
                if (toplevel) {
                    minsize2 = Math.max(Util.divRoundUp(swapSplit.size(), 3), 1);
                } else {
                    minsize2 = Math.max(swapSplit.size() / 2, 1);
                }*/
                if (this.useInitGreedyExitEarly
                        //swapSplit.getLeft().size() >= minsize2 &&
                        //swapSplit.getRight().size() >= minsize2
                        ) {
                    return newSplit.get(0);
                }
            }
            System.out.printf("Greedy init: %d/%d\n", i, bag.getLeft().size());
            swapSplit = swapGreedyLeft(swapSplit);
        }
        initGreedyCheck(bag, swapSplit, newSplit, minNewBoolwidth);

        assert newSplit.get(0) != null;

        return newSplit.get(0);
    }

    /**
     * Check if we want to use this cut
     * @param bag
     * @param swapSplit
     * @return
     */
    public boolean initGreedyCheck(VertexSplit<V> bag, VertexSplit<V> swapSplit,
                                   ArrayList<VertexSplit<V>> newSplit, long[] minNewBoolwidth) {
        if (SwapConstraints.isValid(swapSplit)) {
            long boolwidth = this.cutboolComparator.maxLeftRightCutBool(swapSplit, minNewBoolwidth[0]);
            //int boolwidth = this.cmp.maxLeftRightCutBool(swapSplit);
            if (boolwidth != CutBool.BOUND_EXCEEDED) {
                if (boolwidth < minNewBoolwidth[0]) {
                    newSplit.set(0, swapSplit);
                    minNewBoolwidth[0] = boolwidth;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Try all 1-node swaps and pick best one
     * @param bag
     * @return
     */
    public VertexSplit<V> swapGreedyLeft(VertexSplit<V> bag) {
        VertexSplit<V> newsplit = null;
        long minNewBoolwidth = Long.MAX_VALUE;

        assert bag.getLeft().size() > 0;
        int i = 0;
        for (Vertex<V> v : bag.getLeft().vertices()) {
            i++;
            ArrayList<Vertex<V>> toswap = new ArrayList<Vertex<V>>(1);
            toswap.add(v);
            VertexSplit<V> swapSplit = this.decomposition.swapNodes(bag, toswap, null);

            long boolwidth = this.cutboolComparator.maxLeftRightCutBool(swapSplit, minNewBoolwidth);
            if (boolwidth != CutBool.BOUND_EXCEEDED) {
                if (boolwidth < minNewBoolwidth) {
                    newsplit = swapSplit;
                    minNewBoolwidth = boolwidth;
                    System.out.printf("switching greedy %d/%d, new min: %d\n", i, bag.getLeft().numVertices(), boolwidth);
                } else if (newsplit == null) {
                    assert false;
                }
            }
        }
        assert newsplit != null;
        assert newsplit.checkNode();
        return newsplit;
    }

}
