package boolwidth.greedysearch;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by emh on 11/2/2014.
 */

public class TwoWayDecompose extends BaseDecompose {

    public TwoWayDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split split = new Split(0, this, getGraph().vertices());
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        SimpleNode root = ibt.getRoot();
        //ArrayList<Split> childSplits = new ArrayList<Split>();
        HashMap<Split, ArrayList<Split>> children = new HashMap<>();
        ArrayList<Split> processing = new ArrayList<Split>();

        //Split childSplit = new Split(1, this);
        children.put(split, new ArrayList<>());
        children.get(split).add(new Split(1, this));
        SimpleNode last = ibt.getRoot();
        while (!split.done()) {
            final Split oldsplit = split;
            //final Split childSplit2 = childSplit;
            split = oldsplit.decomposeAdvance((newlefts, tomove) -> {
                //final long cb1 = childSplit2.getNewCutBoolWithAddedVertex(tomove);
                long cb2 = getApproximateCutBool(verticesToInts(newlefts)); //measureCut.applyAsLong(lefts, null);
                System.out.printf("bw: %.2f\n", getLogBooleanWidth(cb2));
                //final long cb2 = this.getCutBool(newlefts, true);
                /*rateLimitedPrint((time) ->
                    System.out.printf("time: %d, logcb1, logcb2, cb1, cb2: %.2f, %.2f, %d, %d\n",
                        time, getLogBooleanWidth(cb1), getLogBooleanWidth(cb2), cb1, cb2));
                        */
                return cb2; //Math.max(cb1, cb2);
            });

            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();

            if (split != oldsplit) {
                ArrayList<Split> newParent = new ArrayList<Split>();
                children.put(split, newParent);

                // advance children
                ArrayList<Split> childrenOfPrev = children.get(oldsplit);
                for (Split childSplit : childrenOfPrev) {
                    childSplit = childSplit.cons(split.getLastMoved()); // we always add to right, then maybe move left
                    /*if (!childSplit.isBalanced()) {
                        // TODO: should hint to move split.getLastMoved()
                        childSplit = childSplit.decomposeAdvance((newlefts, tomove) ->
                                        this.getCutBool(newlefts, true)
                        );
                    }*/
                    children.get(split).add(childSplit);
                }
            }

            //ibt = trickle(ibt, null, ibt.getRoot(), split.getLastMoved());
        }
        return ibt;
    }
}
