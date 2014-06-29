package sadiasrc.decomposition;

/**
 * Created by emh on 6/29/14.
 */

import sadiasrc.graph.*;
import sadiasrc.util.IndexedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class CCMISStack {

    static class State {
        VSubSet all;
        VSubSet out;
        VSubSet rest;
    }

    //stores neighborhood in bitsets for faster intersection and union
    private static ArrayList<VSubSet> neighbourhoods;
    //groundset for bitset
    private static IndexedSet<IndexVertex> groundSet;
    private static BiGraph graph;

    public static long BoolDimBranch(BiGraph G)
    {
//		System.out.println("Bigraph is");
//		System.out.println(G);
        graph = G;
        neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
        groundSet = new IndexedSet<IndexVertex>(G.vertices());
        for(int i=0; i<G.numVertices(); i++)
        {
            neighbourhoods.add(new VSubSet(groundSet, G.neighbours(G.getVertex(i))));
        }


        VSubSet all = new VSubSet(groundSet);
        VSubSet out = new VSubSet(groundSet);
        VSubSet rest = new VSubSet(groundSet);
        //all=rest U out
        all.addAll((Collection<? extends IndexVertex>) G.vertices());
        //rest =P
        rest.addAll((Collection<? extends IndexVertex>) G.vertices());
        //out =X
        Stack<CCMISStack.State> stack = new Stack<>();
        CCMISStack.State topState = new CCMISStack.State();
        topState.all = all;
        topState.out = out;
        topState.rest = rest;
        stack.push(topState);
        return boolDimBranch(stack);
    }

    public static long boolDimBranch(Stack<CCMISStack.State> stateStack)
    {
        //checking termination conditions

        long misCount = 0;

        while (!stateStack.isEmpty()) {

            State state = stateStack.pop();
            VSubSet all = state.all;
            VSubSet out = state.out;
            VSubSet rest = state.rest;

            //System.out.printf("all: %s\n", all);
            //System.out.printf("out: %s\n", out);
            //System.out.printf("rest: %s\n", rest);

            //check if P and X are empty
            if (rest.isEmpty()) {
                if (out.isEmpty()) {
                    misCount += 1;
                }
                continue;
            }

            //check to see if the graph is disconneced
            boolean isConnected = true;
            //if (all.size() >= 10) {
            isConnected = BasicGraphAlgorithms.isConnected(graph, all, neighbourhoods);
            //}

            //If not connected then call for components and multiply
            if (!isConnected) {

                long innerTotal = 1;
                //returns list of components
                for (ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(graph, all)) {
                    VSubSet nall = new VSubSet(groundSet);
                    nall.addAll(vs);
                    VSubSet nout = nall.clone();
                    VSubSet nrest = nall.clone();
                    nout.retainAll(out);
                    nrest.retainAll(rest);

                    Stack<CCMISStack.State> stack = new Stack<>();
                    CCMISStack.State topState = new CCMISStack.State();
                    topState.all = nall;
                    topState.out = nout;
                    topState.rest = nrest;
                    stack.push(topState);
                    long next = boolDimBranch(stack);

                    if (next == 0) {
                        innerTotal = 0;
                        break;
                    }
                    innerTotal *= next;
                    //				System.out.println("total = "+total);
                }

                misCount += innerTotal;
                continue;
            }

            // Find a vertex to branch on
            IndexVertex v = graph.maxDegreeVertex(rest);
            //System.out.printf("maxvertex: %s, degree: %s, neighbours: %s\n\n", v, G.degree(v), G.neighbours(v));

            VSubSet oldAll = all.clone();
            VSubSet oldRest = rest.clone();
            VSubSet oldOut = out.clone();

            //Moving v to out
            rest.remove(v);
            out.add(v);
            if (checkValid(all, out, rest)) {
                CCMISStack.State newState = new CCMISStack.State();
                newState.all = all.clone();
                newState.out = out.clone();
                newState.rest = rest.clone();
                stateStack.push(newState);
            }

            // move vertices back
            out.cloneInPlace(oldOut);
            all.cloneInPlace(oldAll);
            rest.cloneInPlace(oldRest);

            // try v in
            putIn(all, out, rest, v);
            if (checkValid(all, out, rest)) {
                CCMISStack.State newState = new CCMISStack.State();
                newState.all = all.clone();
                newState.out = out.clone();
                newState.rest = rest.clone();
                stateStack.push(newState);
            }

            // move vertices back
            out.cloneInPlace(oldOut);
            all.cloneInPlace(oldAll);
            rest.cloneInPlace(oldRest);
        }
        return misCount;
    }

    private static boolean checkValid(VSubSet all, VSubSet out, VSubSet rest) {
        // look for vertices that can not be dominated
        // and vertices that have to be in
        boolean valid = true;
        boolean changed = true;
        //0=from out 1=from rest

        Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

        // check if there will be a vertex that cannot be dominated if v is OUT then valid=false otherwise call
        while (changed) {
            changed = false;

            for (IndexVertex w : all) {
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
                if (intersection.isEmpty()) {
                    if (out.contains(w)) {
                        valid = false;
                        break;
                    } else {
                        if (!toAdd.contains(w))
                            toAdd.push(w);
                    }
                } else {
                    if (out.contains(w) && (intersection.size() == 1)) {
                        IndexVertex u = intersection.first();
                        if (u != null) {
                            if (!toAdd.contains(u))
                                toAdd.push(u);
                        }
                    }
                }
            }
            while (valid && !toAdd.isEmpty()) {
                IndexVertex x = toAdd.pop();
                if (rest.contains(x)) {
                    putIn(all, out, rest, x);
                } else {
                    valid = false;
                }
                changed = true;
            }
        }
        return valid;
    }

    private static void putIn(VSubSet all,VSubSet out, VSubSet rest, IndexVertex v)
    {
        //System.out.println("Before adding "+v);
        //System.out.println(all+", "+out+", "+rest);
        all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
    }

}
