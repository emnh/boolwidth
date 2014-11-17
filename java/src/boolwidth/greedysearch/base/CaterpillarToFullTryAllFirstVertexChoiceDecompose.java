package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by emh on 11/9/2014.
 */
public class CaterpillarToFullTryAllFirstVertexChoiceDecompose extends BaseDecompose {

    public CaterpillarToFullTryAllFirstVertexChoiceDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split rootSplit0 = createSplit(0, this, getGraph().vertices());

        long minBW = Long.MAX_VALUE;
        ImmutableBinaryTree minIBT = null;
        ArrayList<Vertex<Integer>> minOrdering = null;

        int i = 0;
        ArrayList<Vertex<Integer>> vertices = BasicGraphAlgorithms.BFSAll(getGraph(), getGraph().vertices().iterator().next());
        vertices = BasicGraphAlgorithms.BFSAll(getGraph(), vertices.iterator().next());
        //Collections.shuffle(vertices);

        for (Vertex<Integer> first : vertices) {
            Split split = rootSplit0.decomposeAdvanceFixed(first);
            ArrayList<Vertex<Integer>> ordering = new ArrayList<>();

            ordering.add(first);
            while (!split.done()) {
                split = split.decomposeAdvance();
                ordering.add(split.getLastMoved());
            }
            ArrayList<CaterpillarToFullDecompose> cds = new ArrayList<>();
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (depth == 0) return size / 3;
                    return size / 2;
                }
            });
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (depth == 0) return size / 2;
                    return size / 2;
                }
            });
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (depth == 0) return size * 2 / 3;
                    return size / 2;
                }
            });
            // special case of division by three then linear boolean width
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (depth == 0) {
                        return size / 3;
                    } else {
                        return 1;
                    }
                }
            });
            // special case of double linear boolean width
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (size >= 3) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });
            // special case of linear boolean width
            cds.add(new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    return 1;
                }
            });
            int ci = 0;
            for (CaterpillarToFullDecompose cd : cds) {
                ImmutableBinaryTree ibt = cd.getBinaryIBTFromOrdering(ordering);
                //ImmutableBinaryTree ibt = CaterpillarDecompose.getCaterpillarIBTFromOrdering(ordering);
                long bw = getBooleanWidth(ibt, minBW);
                System.out.printf("i: %d/%d, ci: %d, minBW: %.2f, BW: %.2f\n",
                        i, getGraph().numVertices(), ci,
                        getLogBooleanWidth(minBW), getLogBooleanWidth(bw));
                if (bw != UPPER_BOUND_EXCEEDED && bw < minBW) {
                    minBW = bw;
                    minIBT = ibt;
                    minOrdering = ordering;
                }
                ci++;
            }
            i++;
        }
        // Check more thoroughly to find best top split
        for (i = vertices.size() / 3; i <= vertices.size(); i++) {
            final int topSplitSize = i;
            CaterpillarToFullDecompose cd = new CaterpillarToFullDecompose(getGraph()) {
                @Override
                public int getSplitBoundary(int depth, int size) {
                    if (depth == 0) return topSplitSize;
                    return size / 2;
                }
            };
            ImmutableBinaryTree ibt = cd.getBinaryIBTFromOrdering(minOrdering);
            long bw = getBooleanWidth(ibt, minBW);
            System.out.printf("i_size: %d/%d, minBW: %.2f, BW: %.2f\n",
                    i, getGraph().numVertices(), getLogBooleanWidth(minBW), getLogBooleanWidth(bw));
            if (bw != UPPER_BOUND_EXCEEDED && bw < minBW) {
                minBW = bw;
                minIBT = ibt;
            }
        }
        return minIBT;
    }
}
