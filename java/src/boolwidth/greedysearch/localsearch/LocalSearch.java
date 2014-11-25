package boolwidth.greedysearch.localsearch;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.StackDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import com.github.krukow.clj_lang.PersistentHashSet;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import graph.Vertex;
import interfaces.IGraph;

import java.util.*;

/**
 * Created by emh on 11/25/2014.
 */
public class LocalSearch extends BaseDecompose {

    public LocalSearch(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return super.createSplit(depth, decomposition, lefts, rights);
    }

    public ImmutableBinaryTree improve(ImmutableBinaryTree ibt, BaseDecompose recalculate) {
        final ArrayList<Split> splits = new ArrayList<>();
        final Multimap<Split, Split> children = ArrayListMultimap.create();

        final HashMap<SimpleNode, Split> ibtToSplits = new HashMap<>();
        //final HashMap<SimpleNode>

        ibt.dfs((parent, node) -> {
            Collection<Integer> externalChildren = ibt.getChildren(parent, node);
            PersistentHashSet<SimpleNode> nodeChildren = ibt.getNeighbours(node).disjoin(parent);
            Iterator<SimpleNode> it = nodeChildren.iterator();
            ArrayList<Vertex<Integer>> leftChildren = new ArrayList<>();
            ArrayList<Vertex<Integer>> rightChildren = new ArrayList<>();
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
            Split splitParent = ibtToSplits.get(parent);
            // must split off internal nodes into single lefts
            if (ibt.getExternalID(node) != ibt.EMPTY_NODE) {
                ArrayList<Vertex<Integer>> singleNode = new ArrayList<>();
                ArrayList<Vertex<Integer>> allChildren = new ArrayList<>();
                allChildren.addAll(leftChildren);
                allChildren.addAll(rightChildren);
                singleNode.add(getGraph().getVertex(ibt.getExternalID(node)));
                Split newSplitParent = createSplit(0, this, singleNode, allChildren);

                splits.add(newSplitParent);
                children.put(splitParent, newSplitParent);
                ibtToSplits.put(parent, newSplitParent);

                splitParent = newSplitParent;
            }

            System.out.printf("creating split: %d, %s, %s, %s\n", nodeChildren.size(), externalChildren, leftChildren, rightChildren);

            Split split = createSplit(0, this, leftChildren, rightChildren);
            splits.add(split);
            ibtToSplits.put(node, split);

            children.put(splitParent, split);
        });

        ImmutableBinaryTree newIBT = StackDecompose.getImmutableBinaryTree(children);

        return newIBT;
    }
}
