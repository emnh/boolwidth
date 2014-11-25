package boolwidth.greedysearch.base;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.StackDecompose;
import boolwidth.greedysearch.base.StackDecomposeSplitStackItem;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import com.github.krukow.clj_lang.PersistentHashSet;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;
import javafx.geometry.Pos;

import java.util.*;

/**
 * Created by emh on 11/25/2014.
 */
public class LocalSearch extends BaseDecompose {

    public static final int LOCAL_SEARCH_TIME = 60*1000;

    public LocalSearch(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return super.createSplit(depth, decomposition, lefts, rights);
    }

    public ImmutableBinaryTree improve(ImmutableBinaryTree ibt, BaseDecompose recalculate) {

        final Multimap<Split, Split> children = convertToSplits(ibt, recalculate);
        Stack<StackDecomposeSplitStackItem> splits = new Stack<>();

        long start = System.currentTimeMillis();
        System.out.printf("start local search time: %d\n", start);
        while (System.currentTimeMillis() - start < LOCAL_SEARCH_TIME) {
            Split rootSplit = children.get(null).iterator().next();
            splits.push(new StackDecomposeSplitStackItem(null, rootSplit));
            if (!localSearch(splits, children, recalculate)) break;
        }
        System.out.printf("end local search time: %d\n", System.currentTimeMillis());

        ImmutableBinaryTree newIBT = StackDecompose.getImmutableBinaryTree(children);

        if (getBooleanWidth(ibt) < getBooleanWidth(newIBT)) {
            return ibt;
        } else {
            return newIBT;
        }
    }

    protected boolean localSearch(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, BaseDecompose recalculate) {
        long UB = 0;
        long secondLargest = 0;
        boolean better = false;
        StackDecomposeSplitStackItem maxSplit = null;

        // iterate tree and find largest split
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            long leftCutbool = split.measureCutForDecompose(split.getLefts(), null);
            long rightCutbool = split.measureCutForDecompose(split.getRights(), null);
            long maxCutBool = Math.max(leftCutbool, rightCutbool);
            if (maxCutBool > UB) {
                secondLargest = UB;
                UB = maxCutBool;
                maxSplit = splitStackItem;
            }
            initSplitChildren(recalculate, splits, splitChildren, split);
        }

        // do local search on largest split
        for (int i = 0; i < 1; i++) {
            System.out.println("local search");
            Split split = maxSplit.child;
            Split parent = maxSplit.parent;
            Split newSplit = split.localSearch2();
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
            initSplitChildren(recalculate, splits, splitChildren, split);
        }
        //decomposeSplits(splits, splitChildren);
        //return better;
        return true;
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

    private Multimap<Split, Split> convertToSplits(ImmutableBinaryTree ibt, BaseDecompose recalculate) {
        final Multimap<Split, Split> children = ArrayListMultimap.create();
        final HashMap<SimpleNode, Split> ibtToSplits = new HashMap<>();
        //final HashMap<SimpleNode>

        ibt.dfs((parent, node) -> {
            Collection<Integer> externalChildren = ibt.getChildren(parent, node);
            PersistentHashSet<SimpleNode> nodeChildren = ibt.getNeighbours(node).disjoin(parent);
            Iterator<SimpleNode> it = nodeChildren.iterator();
            ArrayList<Vertex<Integer>> leftChildren = new ArrayList<>();
            ArrayList<Vertex<Integer>> rightChildren = new ArrayList<>();
            ArrayList<Vertex<Integer>> allChildren = new ArrayList<>();
            if (it.hasNext()) {
                SimpleNode left = it.next();
                for (int vid : ibt.getChildren(node, left)) {
                    leftChildren.add(getGraph().getVertex(vid));
                }
            }
            if (it.hasNext()) {
                SimpleNode right = it.next();
                for (int vid : ibt.getChildren(node, right)) {
                    rightChildren.add(getGraph().getVertex(vid));
                }
            }
            allChildren.addAll(leftChildren);
            allChildren.addAll(rightChildren);

            Split splitParent = ibtToSplits.get(parent);
            // must split off internal nodes into single lefts
            if (splitParent == null || splitParent.size() >= 2) {
                if (ibt.getExternalID(node) != ibt.EMPTY_NODE) {
                    ArrayList<Vertex<Integer>> singleNode = new ArrayList<>();

                    singleNode.add(getGraph().getVertex(ibt.getExternalID(node)));

                    Split container = recalculate.createSplit(0, this, singleNode, allChildren);
                    children.put(splitParent, container);
                    if (allChildren.size() > 0) {
                        Split singleSplit = recalculate.createSplit(0, this, singleNode);
                        Split split = recalculate.createSplit(0, this, leftChildren, rightChildren);
                        children.put(container, singleSplit);
                        children.put(container, split);
                        ibtToSplits.put(node, split);
                    }

                    //System.out.printf("creating ISplit: %s, %s, %s, %s\n", singleNode, allChildren, leftChildren, rightChildren);


                    //ibtToSplits.put(parent, newSplitParent);
                } else {
                    //System.out.printf("internal: %d\n", ibt.getExternalID(node));

                    //System.out.printf("creating split: %s, %s, %s\n", externalChildren, leftChildren, rightChildren);

                    Split split = recalculate.createSplit(0, this, leftChildren, rightChildren);
                    children.put(splitParent, split);
                    ibtToSplits.put(node, split);
                }
            } else {
                //System.out.printf("COULD NOT ADD: %s, %s, %s\n", externalChildren, leftChildren, rightChildren);
            }
        });
        return children;
    }
}
