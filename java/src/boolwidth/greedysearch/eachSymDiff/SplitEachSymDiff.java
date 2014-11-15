package boolwidth.greedysearch.eachSymDiff;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.Util;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by emh on 11/3/2014.
 */
public class SplitEachSymDiff extends Split {

    public SplitEachSymDiff() {
    }

    @Override
    public SplitEachSymDiff create(Split old) {
        SplitEachSymDiff result = new SplitEachSymDiff();
        result.copy(old);
        return result;
    }

    public SplitEachSymDiff(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    @Override
    public SplitEachSymDiff decomposeAdvance() {
        SplitEachSymDiff result = create(this);
        if (done()) {
            return this;
        } else {
            IGraph<Vertex<Integer>, Integer, String> graph = this.getDecomposition().getGraph();
            PosSet<Vertex<Integer>> all = new PosSet<>(graph.vertices());
            TreeSet<PosSubSet<Vertex<Integer>>> leftNodeHoodsSet = new TreeSet<>();
            TreeMap<Vertex<Integer>, PosSubSet<Vertex<Integer>>> leftNodeHoods = new TreeMap<>();
            PosSubSet<Vertex<Integer>> rightHoodAll = new PosSubSet<Vertex<Integer>>(all);

            for (Vertex<Integer> node : graph.vertices()) {
                if (lefts.contains(node)) {
                    PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                    for (Vertex<Integer> v : graph.incidentVertices(node)) {
                        if (rights.contains(v)) {
                            neighbors.add(v);
                        }
                    }
                    leftNodeHoods.put(node, neighbors);
                    if (neighbors.size() > 0) {
                        leftNodeHoodsSet.add(neighbors);
                        rightHoodAll = rightHoodAll.union(neighbors);
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

            // First check for isolated or twin nodes
            if (tomove == null) {
                for (Vertex<Integer> v : rights) {
                    int rightNeighboursOfVCount = 0;
                    PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                    PosSubSet<Vertex<Integer>> neighbors_plus_V = new PosSubSet<>(all);
                    for (Vertex<Integer> u : graph.incidentVertices(v)) {
                        if (rights.contains(u)) {
                            neighbors.add(u);
                            rightNeighboursOfVCount++;
                        }
                    }
                    neighbors_plus_V = neighbors.clone();
                    neighbors_plus_V.add(v);
                    // isolated node
                    if (rightNeighboursOfVCount == 0) {
                        tomove = v;
                        break;
                    }
                    // twin node
                    if (leftNodeHoodsSet.contains(neighbors) || leftNodeHoodsSet.contains(neighbors_plus_V)) {
                        tomove = v;
                        break;
                    }
                }
            }

            if (tomove == null) {
                int i = 0;
                for (Vertex<Integer> v : rights) {
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
                            if (!rightHoodAll.contains(u)) {
                                rightNoNeighborInLeft++;
                            }
                        }
                    }
                    int differenceCount = 0;
                    for (Vertex<Integer> left : lefts) {
                        PosSubSet<Vertex<Integer>> leftNeighbours = leftNodeHoods.get(left);
                        if (leftNeighbours.size() > 0 && neighbors.size() > 0 &&
                                !(leftNeighbours.isSubset(neighbors) || neighbors.isSubset(leftNeighbours))) {
                            differenceCount++;
                        }
                    }
                    long cb = differenceCount; // neighbors.size();

                    if (cb < minmove) {
                        minmove = cb;
                        tomove = v;
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
