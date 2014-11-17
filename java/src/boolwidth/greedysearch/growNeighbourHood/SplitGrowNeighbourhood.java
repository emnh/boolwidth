package boolwidth.greedysearch.growNeighbourHood;

import boolwidth.greedysearch.base.Util;
import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import com.github.krukow.clj_lang.PersistentHashSet;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;

import java.util.TreeSet;

/**
 * Created by emh on 11/3/2014.
 */
public class SplitGrowNeighbourhood extends Split {

    public SplitGrowNeighbourhood() {
        super();
    }

    public SplitGrowNeighbourhood(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    public SplitGrowNeighbourhood(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, lefts, rights);
    }

    @Override
    public SplitGrowNeighbourhood create(Split old) {
        SplitGrowNeighbourhood result = new SplitGrowNeighbourhood();
        result.copy(old);
        return result;
    }

    @Override
    public SplitGrowNeighbourhood decomposeAdvance() {
        SplitGrowNeighbourhood result = create(this);
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
            double minmove_E = Double.MAX_VALUE;
            Vertex<Integer> tomove = null;
            Vertex<Integer> tomove_E = null;

            //long cb2 = this.getDecomposition().getApproximateCutBool(this.getDecomposition().verticesToInts(lefts)); //measureCut.applyAsLong(lefts, null);
            //System.out.printf("bw: %.2f\n", this.decomposition.getLogBooleanWidth(cb2));

            if (tomove == null && lefts.size() == 0) {
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), Util.getFirst(rights), rights);
                tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), tomove, rights);
            }

            // First check for isolated nodes
            if (tomove == null) {
                for (Vertex<Integer> v : N_LEFT) {
                    int rightNeighboursOfVCount = 0;
                    for (Vertex<Integer> u : graph.incidentVertices(v)) {
                        if (rights.contains(u)) {
                            rightNeighboursOfVCount++;
                        }
                    }
                    // isolated node
                    if (rightNeighboursOfVCount == 0) {
                        tomove = v;
                        break;
                    }
                }
            }

            // Then check for twin nodes
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
                    // twin node
                    if (nodeHoods.contains(neighbors) || nodeHoods.contains(neighbors_plus_V)) {
                        tomove = v;
                        break;
                    }
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
                        //System.out.println("chose by ratio");
                        minmove = ratio;
                        tomove = v;
                    }
                    if (external < minmove_E) {
                        minmove_E = external;
                        tomove_E = v;
                    }
                    minmove = ratio;
                    tomove = v;
                    break;
                }
            }
            if (tomove != null && tomove_E != null) {
                //System.out.println("tomove_E");
                tomove = tomove_E;
            }
            if (tomove == null) {
                tomove = Util.getFirst(rights);
                //tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), Util.getFirst(rights), rights);
                //tomove = BasicGraphAlgorithms.BFS(getDecomposition().getGraph(), tomove, rights);
                //System.out.println("was empty, did BFS");
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
