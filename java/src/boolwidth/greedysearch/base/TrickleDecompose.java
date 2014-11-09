package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by emh on 11/9/2014.
 */
public class TrickleDecompose extends BaseDecompose {

    public TrickleDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    public ImmutableBinaryTree trickle(ImmutableBinaryTree ibt, SimpleNode parent, SimpleNode currentNode, Vertex<Integer> v) {

        ArrayList<SimpleNode> neighbours = new ArrayList<SimpleNode>(ibt.getNeighbours(currentNode));
        if (neighbours.size() < 3) {
            ibt = ibt.addChild(currentNode, v.id());
            return ibt;
        }
        if (parent != null) neighbours.remove(parent);

        long minFBCool = Long.MAX_VALUE;
        SimpleNode minFBNode = null;

        for (SimpleNode n : neighbours) {
            HashSet<Integer> lefts = new HashSet<>(ibt.getChildren(currentNode, n));
            lefts.add(v.id());
            long fbcool = getCutValueForTrickle(ibt, lefts);
            if (fbcool < minFBCool) {
                minFBCool = fbcool;
                minFBNode = n;
            }
        }
        ibt = trickle(ibt, currentNode, minFBNode, v);

        ibt = trickleReRoot(ibt, parent);
        return ibt;
    }

    protected ImmutableBinaryTree trickleReRoot(ImmutableBinaryTree ibt, SimpleNode parent) {
        if (parent == null) {
            SimpleNode maxCut = getMaxCut(ibt);
            assert maxCut != null;
            //System.out.printf("rerooting: %s\n", maxCut != ibt.getRoot());
            ibt = ibt.reRoot(maxCut);
        }
        return ibt;
    }

    public ImmutableBinaryTree retrickleRandom(ImmutableBinaryTree ibt) {
        ArrayList<Integer> ids = new ArrayList<>(ibt.getAllChildren());
        int pick = ids.get(rnd.nextInt(ids.size()));
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), getGraph().getVertex(pick));
        if (getBooleanWidth(ibt) > getBooleanWidth(oldIbt)) {
            //if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            //System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    public ImmutableBinaryTree retrickleGiven(ImmutableBinaryTree ibt, int externalID) {
        int pick = externalID;
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), getGraph().getVertex(pick));
        if (getBooleanWidth(ibt) > getBooleanWidth(oldIbt)) {
            //if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            //System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        this.getGraph().vertices().forEach((node) -> list.add(node));
        return decompose(list);
    }

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        // doesn't seem to have much impact, leave out while debugging for deterministic results
        // Collections.shuffle(vertices);

        TreeMap<Double, Vertex<Integer>> troubleMakers = new TreeMap<>();

        int i = 0;
        long bw = 1, oldbw = 1;
        double ratio = 0;

        for (Vertex<Integer> v : vertices) {
            i += 1;

            ibt = trickle(ibt, null, ibt.getRoot(), v);
            bw = getBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;

            final int print_i = i;
            final long print_bw = bw;
            final double print_ratio = ratio;
            rateLimitedPrint((time) ->
                    System.out.printf("time: %d, trickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                            time, v.id(), print_i, getGraph().numVertices(),
                            getLogBooleanWidth(print_bw), print_bw, print_ratio));

            for (int j = 0; j < ibt.getAllChildren().size() / 10; j++) {
                ibt = retrickleRandom(ibt);
            }
            bw = getBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;

            final long print_bw2 = bw;
            final double print_ratio2 = ratio;
            rateLimitedPrint((time) ->
                    System.out.printf("time: %d, retrickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                            time, v.id(), print_i, getGraph().numVertices(),
                            getLogBooleanWidth(print_bw2), print_bw2, print_ratio2));
        }
        return ibt;
    }
}
