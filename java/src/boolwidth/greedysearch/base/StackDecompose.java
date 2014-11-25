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

    public static final int LOCAL_SEARCH_TIME = 1*1000;

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

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < LOCAL_SEARCH_TIME) {
            rootSplit = splitChildren.get(null).iterator().next();
            splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
            if (!localSearch(splits, splitChildren)) break;
        }

        return getImmutableBinaryTree(splitChildren);
    }

    protected boolean localSearch(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren) {
        long UB = 0;
        long secondLargest = 0;
        boolean better = false;
        StackDecomposeSplitStackItem maxSplit = null;

        // iterate tree and find largest split
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            /*if (!split.isBalanced()) {
                split = initSplit(splits, splitChildren, split, parent);
            }*/
            long leftCutbool = split.measureCutForDecompose(split.getLefts(), null);
            long rightCutbool = split.measureCutForDecompose(split.getRights(), null);
            long maxCutBool = Math.max(leftCutbool, rightCutbool);
            if (maxCutBool > UB) {
                secondLargest = UB;
                UB = maxCutBool;
                maxSplit = splitStackItem;
            }
            initSplitChildren(splits, splitChildren, split);
        }

        // do local search on largest split
        for (int i = 0; i < 1; i++) {
            System.out.println("local search");
            Split split = maxSplit.child;
            Split parent = maxSplit.parent;
            Split newSplit = split.localSearch();
            long leftCutbool2 = split.measureCutForDecompose(newSplit.getLefts(), null);
            long rightCutbool2 = split.measureCutForDecompose(newSplit.getRights(), null);
            long maxCutBool2 = Math.max(leftCutbool2, rightCutbool2);
            System.out.printf("LS: old: %.2f, new: %.2f\n", getLogBooleanWidth(UB), getLogBooleanWidth(maxCutBool2));
            better = maxCutBool2 < UB;
            UB = maxCutBool2;

            splitChildren.remove(parent, split);
            splitChildren.put(parent, newSplit);
            split = newSplit;
            maxSplit = new StackDecomposeSplitStackItem(parent, split);

            if (UB < secondLargest) {
                System.out.printf("break: second: %.2f\n", getLogBooleanWidth(secondLargest));
                break;
            }
        }

        // resplit tree if necessary
        Split rootSplit = splitChildren.get(null).iterator().next();
        splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            if (split.getLefts().size() == 0 || split.getRights().size() == 0) {
                split = initSplit(splits, splitChildren, split, parent);
            }
            initSplitChildren(splits, splitChildren, split);
        }
        //decomposeSplits(splits, splitChildren);
        return better;
    }

    protected void decomposeSplits(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren) {
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            //while (!(2 * split.lefts.size() >= split.rights.size())) {
            split = initSplit(splits, splitChildren, split, parent);
            initSplitChildren(splits, splitChildren, split);
        }
    }

    private Split initSplit(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, Split split, Split parent) {
        while (!split.isBalanced()) {
            Split newSplit = split.decomposeAdvance();
            splitChildren.remove(parent, split);
            splitChildren.put(parent, newSplit);
            split = newSplit;
        }
        return split;
    }

    private void initSplitChildren(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, Split split) {
        if (splitChildren.get(split).isEmpty()) {
            /*if (split.getLefts().size() >= 2) {
                Split leftChild = createSplit(split.getDepth() + 1, this, split.getLefts());
                splitChildren.put(split, leftChild);
                splits.push(new StackDecomposeSplitStackItem(split, leftChild));
            }
            if (split.getRights().size() >= 2) {
                Split rightChild = createSplit(split.getDepth() + 1, this, split.getRights());
                splitChildren.put(split, rightChild);
                splits.push(new StackDecomposeSplitStackItem(split, rightChild));
            }*/
            if (split.size() >= 2) {
                Split leftChild = createSplit(split.getDepth() + 1, this, split.getLefts());
                splitChildren.put(split, leftChild);
                splits.push(new StackDecomposeSplitStackItem(split, leftChild));

                Split rightChild = createSplit(split.getDepth() + 1, this, split.getRights());
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
            //System.out.printf("split: %s, l/r: %s/%s, nodeParent: %s\n",
            //        split.getAll(), split.getLefts(), split.getRights(), nodeParent);

            if (split.size() == 1) {
                ibt = ibt.addChild(nodeParent, Util.getSingle(split.getAll()).id());
                //total.add(Util.getSingle(split.getAll()));
            } else {
                ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                ibtMap.put(split, ibt.getReference());
            }

            for (Split child : splitChildren.get(split)) {
                //System.out.printf("adding child: %s\n", child.getAll());
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