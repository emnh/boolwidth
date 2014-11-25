package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by emh on 11/9/2014.
 */
public class CaterpillarToFullDecompose extends BaseDecompose {

    public CaterpillarToFullDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    public int getSplitBoundary(int depth, int size) {
        int splitBoundary = size / 2;
        if (depth == 0) splitBoundary = size * 2 / 3;
        return splitBoundary;
    }

    public OrderedSplit createSplitGivenOrdering(int depth, ArrayList<Vertex<Integer>> localVertexOrdering) {
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        ArrayList<Vertex<Integer>> rights = new ArrayList<>();
        int i = 0;
        for (i = 0; i < getSplitBoundary(depth, localVertexOrdering.size()); i++) {
            lefts.add(localVertexOrdering.get(i));
        }
        for (int j = i; j < localVertexOrdering.size(); j++) {
            rights.add(localVertexOrdering.get(j));
        }
        OrderedSplit split = new OrderedSplit(depth, this, lefts, rights);
        return split;
    }

    public ImmutableBinaryTree getBinaryIBTFromOrdering(ArrayList<Vertex<Integer>> vertexOrdering) {
        Stack<CaterpillarToFullDecomposeStackItem> splits = new Stack<>();
        Multimap<Split, Split> splitChildren = ArrayListMultimap.create();

        OrderedSplit rootSplit = createSplitGivenOrdering(0, vertexOrdering);
        splits.push(new CaterpillarToFullDecomposeStackItem(null, rootSplit));
        splitChildren.put(null, rootSplit);

        while (!splits.isEmpty()) {
            CaterpillarToFullDecomposeStackItem splitStackItem = splits.pop();
            OrderedSplit split = splitStackItem.child;
            OrderedSplit parent = splitStackItem.parent;

            if (split.size() >= 2) {
                OrderedSplit leftChild = createSplitGivenOrdering(split.getDepth() + 1, split.getLeftOrder());
                splitChildren.put(split, leftChild);
                splits.push(new CaterpillarToFullDecomposeStackItem(split, leftChild));

                OrderedSplit rightChild = createSplitGivenOrdering(split.getDepth() + 1, split.getRightOrder());
                splitChildren.put(split, rightChild);
                splits.push(new CaterpillarToFullDecomposeStackItem(split, rightChild));
            }
        }

        return StackDecompose.getImmutableBinaryTree(splitChildren);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split split = createSplit(0, this, getGraph().vertices());
        ArrayList<Vertex<Integer>> ordering = new ArrayList<>();

        while (!split.done()) {
            split = split.decomposeAdvance();
            ordering.add(split.getLastMoved());
        }
        ImmutableBinaryTree ibt = getBinaryIBTFromOrdering(ordering);

        return ibt;
    }
}
