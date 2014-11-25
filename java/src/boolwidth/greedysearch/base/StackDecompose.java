package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import interfaces.IGraph;

import java.util.*;

/**
 * Created by emh on 11/9/2014.
 */


public class StackDecompose extends BaseDecompose {

    public StackDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    /*@Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new Split(depth, decomposition, rights);
    }*/

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        Split rootSplit = createSplit(0, this, vertices);
        Stack<StackDecomposeSplitStackItem> splits = new Stack<>();
        Multimap<Split, Split> splitChildren = ArrayListMultimap.create();

        splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
        decomposeSplits(splits, splitChildren);

        return getImmutableBinaryTree(splitChildren);
    }

    protected void decomposeSplits(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren) {
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            //while (!(2 * split.lefts.size() >= split.rights.size())) {
            split = initSplit(splits, splitChildren, split, parent);
            initSplitChildren(this, splits, splitChildren, split);
        }
    }

    protected static Split initSplit(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, Split split, Split parent) {
        while (!split.isBalanced()) {
            Split newSplit = split.decomposeAdvance();
            splitChildren.remove(parent, split);
            splitChildren.put(parent, newSplit);
            split = newSplit;
        }
        return split;
    }

    protected static void initSplitChildren(BaseDecompose decompose, Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, Split split) {
        if (splitChildren.get(split).isEmpty()) {
            if (split.size() >= 2) {
                Split leftChild = decompose.createSplit(split.getDepth() + 1, decompose, split.getLefts());
                splitChildren.put(split, leftChild);
                splits.push(new StackDecomposeSplitStackItem(split, leftChild));

                Split rightChild = decompose.createSplit(split.getDepth() + 1, decompose, split.getRights());
                splitChildren.put(split, rightChild);
                splits.push(new StackDecomposeSplitStackItem(split, rightChild));
            }
        } else {
            for (Split child : splitChildren.get(split)) {
                splits.push(new StackDecomposeSplitStackItem(split, child));
            }
        }
    }

    public static ImmutableBinaryTree getImmutableBinaryTree(Multimap<Split, Split> splitChildren) {
        Stack<StackDecomposeSplitStackItem> splits = new Stack<>();
        Split rootSplit = splitChildren.get(null).iterator().next();
        splits.push(new StackDecomposeSplitStackItem(null, rootSplit));

        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        HashMap<Split, SimpleNode> ibtMap = new HashMap<>();
        ibtMap.put(null, ibt.getRoot());

        //HashSet<Vertex<Integer>> total = new HashSet<>();
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split splitParent = splitStackItem.parent;
            SimpleNode nodeParent = ibtMap.get(splitParent);
            //System.out.printf("split: %s, l/r: %s/%s, splitParent: %s, nodeParent: %s\n",
            //        split.getAll(), split.getLefts(), split.getRights(), splitParent, nodeParent);

            if (split.size() == 1) {
                ibt = ibt.addChild(nodeParent, Util.getSingle(split.getAll()).id());
                //total.add(Util.getSingle(split.getAll()));
            } else {
                ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                ibtMap.put(split, ibt.getReference());
            }

            for (Split child : splitChildren.get(split)) {
                //System.out.printf("adding child: %s, %s\n", split, child.getAll());
                splits.push(new StackDecomposeSplitStackItem(split, child));
            }
        }
        //System.out.printf("total size: %d\n", total.size());
        return ibt;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> vertices = BasicGraphAlgorithms.BFSAll(getGraph(), getGraph().vertices().iterator().next());
        vertices = BasicGraphAlgorithms.BFSAll(getGraph(), vertices.iterator().next());
        //Collections.shuffle(vertices);

        return decompose(vertices);
    }
}