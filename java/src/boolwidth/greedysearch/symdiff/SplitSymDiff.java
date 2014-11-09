package boolwidth.greedysearch.symdiff;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;

import java.util.TreeSet;

/**
 * Created by emh on 11/3/2014.
 */
public class SplitSymDiff extends Split {

    public SplitSymDiff(SplitSymDiff old) {
        copy(old);
    }

    public SplitSymDiff(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    @Override
    public SplitSymDiff decomposeAdvance() {
        SplitSymDiff result = new SplitSymDiff(this);
        if (done()) {
            return this;
        } else {
            IGraph<Vertex<Integer>, Integer, String> graph = this.getDecomposition().getGraph();
            PosSet<Vertex<Integer>> all = new PosSet<>(graph.vertices());
            TreeSet<PosSubSet<Vertex<Integer>>> nodeHoods = new TreeSet<>();
            PosSubSet<Vertex<Integer>> rightHoodAll = new PosSubSet<Vertex<Integer>>(all);

            for (Vertex<Integer> node : graph.vertices()) {
                if (lefts.contains(node)) {
                    PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                    for (Vertex<Integer> v : graph.incidentVertices(node)) {
                        if (rights.contains(v)) {
                            neighbors.add(v);
                        }
                    }
                    if (neighbors.size() > 0) {
                        nodeHoods.add(neighbors);
                        rightHoodAll = rightHoodAll.union(neighbors);
                    }
                }
            }

            //long oldcb = this.getDecomposition
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            //long cb2 = this.getDecomposition().getApproximateCutBool(this.getDecomposition().verticesToInts(lefts)); //measureCut.applyAsLong(lefts, null);
            //System.out.printf("bw: %.2f\n", this.decomposition.getLogBooleanWidth(cb2));

            int i = 0;
            for (Vertex<Integer> v : rights) {
                i += 1;

                PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                int rightCount = 0;
                int rightNoNeighborInLeft = 0;
                for (Vertex<Integer> u : graph.incidentVertices(v)) {
                    if (rights.contains(u)) {
                        neighbors.add(u);
                        rightCount++;
                        if (!rightHoodAll.contains(u)) {
                            rightNoNeighborInLeft++;
                        }
                    }
                }

                long cb = rightNoNeighborInLeft; // neighbors.size();

                // isolated node
                if (rightCount == 0) {
                    minmove = cb;
                    tomove = v;
                    break;
                }
                // twin node
                if (nodeHoods.contains(neighbors)) {
                    minmove = cb;
                    tomove = v;
                    break;
                }
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
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
