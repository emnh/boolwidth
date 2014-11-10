package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by emh on 11/9/2014.
 */


public class StackDecompose extends BaseDecompose {

    public StackDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new Split(depth, decomposition, rights);
    }

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        Split rootSplit = createSplit(0, this, vertices);
        Stack<StackDecomposeSplitStackItem> splits = new Stack<>();
        Multimap<Split, Split> splitChildren = ArrayListMultimap.create();

        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        SimpleNode last = ibt.getRoot();

        splits.push(new StackDecomposeSplitStackItem(null, rootSplit, true));
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            //while (!(2 * split.lefts.size() >= split.rights.size())) {
            while (!split.isBalanced()) {
                Split newSplit = split.decomposeAdvance();
                splitChildren.remove(parent, split);
                splitChildren.put(parent, newSplit);
                split = newSplit;
            }
            if (split.getLefts().size() >= 2) {
                Split leftChild = createSplit(split.getDepth() + 1, this, split.getLefts());
                splitChildren.put(split, leftChild);
                splits.push(new StackDecomposeSplitStackItem(split, leftChild, true));
            }
            if (split.getRights().size() >= 2) {
                Split rightChild = createSplit(split.getDepth() + 1, this, split.getRights());
                splitChildren.put(split, rightChild);
                splits.push(new StackDecomposeSplitStackItem(split, rightChild, false));
            }
        }

        rootSplit = splitChildren.get(null).iterator().next();
        splits.push(new StackDecomposeSplitStackItem(null, rootSplit, true));
        HashMap<Split, SimpleNode> ibtMap = new HashMap<>();
        ibtMap.put(null, ibt.getRoot());
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split splitParent = splitStackItem.parent;
            SimpleNode nodeParent = ibtMap.get(splitParent);
            if (split.getLefts().size() >= 2) {
                ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                ibtMap.put(split, ibt.getReference());
            } else if (split.getLefts().size() == 1) {
                ibt = ibt.addChild(nodeParent, Util.getSingle(split.getLefts()).id());
            }
            if (split.getRights().size() >= 2) {
                ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                ibtMap.put(split, ibt.getReference());
            } else if (split.getRights().size() == 1) {
                ibt = ibt.addChild(nodeParent, Util.getSingle(split.getRights()).id());
            }
            for (Split child : splitChildren.get(split)) {
                // true for isLeft is bogus, but we don't need it in this case
                splits.push(new StackDecomposeSplitStackItem(split, child, true));
            }
        }
        return ibt;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        this.getGraph().vertices().forEach((node) -> list.add(node));
        return decompose(list);
    }
}