package boolwidth.greedysearch.growNeighbourHood;

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
public class SplitGrowNeighbourhood extends Split {

    public SplitGrowNeighbourhood(SplitGrowNeighbourhood old) {
        copy(old);
    }

    public SplitGrowNeighbourhood(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    @Override
    public SplitGrowNeighbourhood decomposeAdvance() {
        SplitGrowNeighbourhood result = new SplitGrowNeighbourhood(this);
        if (done()) {
            return this;
        } else {
            IGraph<Vertex<Integer>, Integer, String> graph = this.getDecomposition().getGraph();
            PosSet<Vertex<Integer>> all = new PosSet<>(graph.vertices());
            TreeSet<PosSubSet<Vertex<Integer>>> nodeHoods = new TreeSet<>();
            PosSubSet<Vertex<Integer>> N_LEFT = new PosSubSet<>(all);

            int minLeftHoodInRightCount = Integer.MAX_VALUE;
            PosSubSet<Vertex<Integer>> minLeftHoodInRight = new PosSubSet<>(all);
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
                        if (neighbors.size() <= minLeftHoodInRightCount) {
                            minLeftHoodInRightCount = neighbors.size();
                            minLeftHoodInRight = neighbors;
                        }
                    }
                }
            }

            /*for (Vertex<Integer> v : lefts) {

            }*/

            //long oldcb = this.getDecomposition
            double minmove = Double.MAX_VALUE;
            Vertex<Integer> tomove = null;

            //long cb2 = this.getDecomposition().getApproximateCutBool(this.getDecomposition().verticesToInts(lefts)); //measureCut.applyAsLong(lefts, null);
            //System.out.printf("bw: %.2f\n", this.decomposition.getLogBooleanWidth(cb2));


            // TODO: what if rightHoodAll is empty and there are more nodes in right?

            // First check for isolated or twin nodes
            for (Vertex<Integer> v : rights) {
                int rightNeighboursOfVCount = 0;
                PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                for (Vertex<Integer> u : graph.incidentVertices(v)) {
                    if (rights.contains(u)) {
                        neighbors.add(u);
                        rightNeighboursOfVCount++;
                    }
                }
                // isolated node
                if (rightNeighboursOfVCount == 0) {
                    tomove = v;
                    break;
                }
                // twin node
                if (nodeHoods.contains(neighbors)) {
                    tomove = v;
                    break;
                }
            }

            // Now choose by ratio
            int i = 0;
            if (tomove == null) {
                for (Vertex<Integer> v : minLeftHoodInRight) {
                    i += 1;

                    PosSubSet<Vertex<Integer>> neighbors = new PosSubSet<>(all);
                    int rightNeighboursOfVCount = 0;
                    int rightNoNeighborInN_LEFTCount = 0;   // external
                    int external = rightNoNeighborInN_LEFTCount;
                    int internal = rightNeighboursOfVCount - external;
                    for (Vertex<Integer> u : graph.incidentVertices(v)) {
                        if (rights.contains(u)) {
                            neighbors.add(u);
                            rightNeighboursOfVCount++;
                            if (!N_LEFT.contains(u)) {
                                rightNoNeighborInN_LEFTCount++;
                            }
                        }
                    }
                    double ratio = 0.0;
                    if (internal == 0) {
                        ratio = external / (internal + 0.1);
                    } else {
                        ratio = external / internal;
                    }
                    if (ratio < minmove) {
                        minmove = ratio;
                        tomove = v;
                    }
                }
            }
            if (tomove == null) {
                for (Vertex<Integer> v : rights) {
                    System.out.println("was empty, just took first");
                    // TODO: BFS
                    tomove = v;
                    break;
                }
            }

            result.lefts = result.lefts.cons(tomove);
            result.rights = result.rights.disjoin(tomove);
            result.cutbool = 0;
            result.reference = tomove;
            result.logStatement();
        }
        return result;
    }
}
