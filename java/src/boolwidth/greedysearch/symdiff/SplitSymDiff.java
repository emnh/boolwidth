package boolwidth.greedysearch.symdiff;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.Util;
import boolwidth.greedysearch.growNeighbourHood.SplitGrowNeighbourhood;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;

import java.util.TreeSet;

/**
 * Created by emh on 11/3/2014.
 */
public class SplitSymDiff extends Split {

    public SplitSymDiff() {
    }

    @Override
    public SplitSymDiff create(Split old) {
        SplitSymDiff result = new SplitSymDiff();
        result.copy(old);
        return result;
    }

    public SplitSymDiff(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    public SplitSymDiff(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, lefts, rights);
    }

    @Override
    public SplitSymDiff decomposeAdvance() {
        SplitSymDiff result = create(this);
        if (done()) {
            return this;
        } else {
            IGraph<Vertex<Integer>, Integer, String> graph = this.getDecomposition().getGraph();
            PosSet<Vertex<Integer>> all = new PosSet<>(graph.vertices());
            TreeSet<PosSubSet<Vertex<Integer>>> nodeHoods = new TreeSet<>();
            PosSubSet<Vertex<Integer>> N_LEFT = new PosSubSet<Vertex<Integer>>(all);

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
                        N_LEFT = N_LEFT.union(neighbors);
                    }
                }
            }

            //long oldcb = this.getDecomposition
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            //long cb2 = this.getDecomposition().getApproximateCutBool(this.getDecomposition().verticesToInts(lefts)); //measureCut.applyAsLong(lefts, null);
            //System.out.printf("bw: %.2f\n", this.decomposition.getLogBooleanWidth(cb2));

            if (lefts.size() == 0) {
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), Util.getFirst(rights), rights);
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), tomove, rights);
                //System.out.println("was empty, did BFS");
            }

            if (tomove == null) {
                int i = 0;
                for (Vertex<Integer> v : N_LEFT) {
                    i += 1;

                    PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                    PosSubSet<Vertex<Integer>> neighbors_plus_V = new PosSubSet<>(all);
                    neighbors_plus_V.add(v);
                    int rightCount = 0;
                    int rightNoNeighborInLeft = 0;
                    for (Vertex<Integer> u : graph.incidentVertices(v)) {
                        if (rights.contains(u)) {
                            neighbors.add(u);
                            neighbors_plus_V.add(u);
                            rightCount++;
                            if (!N_LEFT.contains(u)) {
                                rightNoNeighborInLeft++;
                            }
                        }
                    }
                    long cb = rightNoNeighborInLeft; // neighbors.size();

                    // isolated node
                    if (rightCount == 0 && N_LEFT.contains(v)) {
                        minmove = cb;
                        tomove = v;
                        break;
                    }
                    // twin node
                    if (nodeHoods.contains(neighbors) || nodeHoods.contains(neighbors_plus_V)) {
                        minmove = cb;
                        tomove = v;
                        break;
                    }
                    if (cb < minmove) {
                        minmove = cb;
                        tomove = v;
                    }
                }
            }
            if (tomove == null) {
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), Util.getFirst(rights), rights);
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), tomove, rights);
                //System.out.println("was empty, did BFS");
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
