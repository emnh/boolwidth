package boolwidth.cutbool;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.util.IndexedSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by emh on 5/10/2014.
 */

public class CBBacktrackInOutRest {

    private static class State {
        //stores neighborhood in bitsets for faster intersection and union
        private static ArrayList<VSubSet> neighbourhoods;
        //groundset for bitset
        private static IndexedSet<IndexVertex> groundSet;
    }

    public CBBacktrackInOutRest() {

    }

    public static long count(State state, BiGraph G,
                             VSubSet inLeft, VSubSet outLeft, VSubSet restLeft,
                             VSubSet inRight, VSubSet outRight, VSubSet restRight) {

        VSubSet in = inLeft.union(inRight);
        VSubSet rest = restLeft.union(restRight);
        VSubSet allConnected = in.union(rest);

        if (restRight.size() == 0) {
            VSubSet total = new VSubSet(state.groundSet);
            for (IndexVertex v : inLeft.union(restLeft)) {
                VSubSet vNeighbourHood = state.neighbourhoods.get(v.id());
                total = total.union(vNeighbourHood);
            }
            if (total.equals(inRight)) {
                return 1;
            } else {
                return 0;
            }
        }

        boolean isConnected = BasicGraphAlgorithms.isConnected(G, allConnected, state.neighbourhoods);

        if(!isConnected)
        {
            long total=1;
            // returns list of components
            for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G, allConnected))
            {
                VSubSet newAll = new VSubSet(state.groundSet);
                newAll.addAll(vs);
                
                VSubSet newInLeft = newAll.intersection(inLeft);
                VSubSet newOutLeft = newAll.intersection(outLeft);
                VSubSet newRestLeft = newAll.intersection(restLeft);

                VSubSet newInRight = newAll.intersection(inRight);
                VSubSet newOutRight = newAll.intersection(outRight);
                VSubSet newRestRight = newAll.intersection(restRight);

                long next = count(state, G,
                        newInLeft, newOutLeft, newRestLeft,
                        newInRight, newOutRight, newRestRight);
                if (next == 0) return 0;
                total = Math.multiplyExact(total, next);
            }

            return total;
        }

        //IndexVertex v = G.maxDegreeVertex(rest);
        IndexVertex v = G.maxDegreeVertex(restRight);
        //IndexVertex v = restRight.first();

        // add v and branch
        boolean vinValid = true;
        VSubSet vinInLeft = inLeft.clone();
        VSubSet vinOutLeft = outLeft.clone();
        VSubSet vinRestLeft = restLeft.clone();
        VSubSet vinInRight = inRight.clone();
        VSubSet vinOutRight = outRight.clone();
        VSubSet vinRestRight = restRight.clone();

        if (G.isRight(v)) {
            vinInRight.add(v);
            vinRestRight.remove(v);

            VSubSet vLeftNeighbours = restLeft.intersection(state.neighbourhoods.get(v.id()));
            if (vLeftNeighbours.size() == 0) {
                vinValid = false;
            }
            if (vLeftNeighbours.size() == 1) {
                // if it is the only left neighbour it must be in
                IndexVertex first = vLeftNeighbours.first();
                vinInLeft.add(first);
                vinRestLeft.remove(first);

                // and then all right neighbours of this one must be in as well
                VSubSet right = state.neighbourhoods.get(first.id());
                if (outRight.intersection(right).size() > 0) {
                    vinValid = false;
                } else {
                    vinInRight = vinInRight.union(right);
                    vinRestRight = vinRestRight.subtract(right);
                }
            }

            // for all (neighbours of v) in left, if N(v) subset of inRight, add v to inLeft
            if (vinValid) {
                for (IndexVertex u : vLeftNeighbours) {
                    VSubSet uNeighbourHood = state.neighbourhoods.get(u.id());
                    if (uNeighbourHood.isSubSet(vinInRight)) {
                        vinInLeft.add(u);
                        vinRestLeft.remove(u);
                    }
                }
            }
        }

        long total = 0;

        if (vinValid) {
            total = count(state, G,
                    vinInLeft, vinOutLeft, vinRestLeft,
                    vinInRight, vinOutRight, vinRestRight);
        }

        // remove v and branch
        boolean voutValid = true;
        VSubSet voutInLeft = inLeft.clone();
        VSubSet voutOutLeft = outLeft.clone();
        VSubSet voutRestLeft = restLeft.clone();
        VSubSet voutInRight = inRight.clone();
        VSubSet voutOutRight = outRight.clone();
        VSubSet voutRestRight = restRight.clone();

        if (G.isRight(v)) {
            voutOutRight.add(v);
            voutRestRight.remove(v);

            // remove all neighbours of v
            VSubSet vLeftNeighbours = state.neighbourhoods.get(v.id());
            if (inLeft.intersection(vLeftNeighbours).size() > 0) {
                voutValid = false;
            } else {
                voutOutLeft = voutOutLeft.union(vLeftNeighbours);
                voutRestLeft = voutRestLeft.subtract(vLeftNeighbours);
            }
        }
        if (voutValid) {
            total += count(state, G,
                    voutInLeft, voutOutLeft, voutRestLeft,
                    voutInRight, voutOutRight, voutRestRight);
        }

        return total;
    }

    public static <V, E> long countNeighborhoods(BiGraph G) {
        State state = new State();

        state.neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
        state.groundSet = new IndexedSet<IndexVertex>(G.vertices());
        for(int i=0; i<G.numVertices(); i++)
        {
            state.neighbourhoods.add(new VSubSet(state.groundSet, G.neighbours(G.getVertex(i))));
        }

        VSubSet inLeft = new VSubSet(state.groundSet);
        VSubSet outLeft = new VSubSet(state.groundSet);
        VSubSet restLeft = new VSubSet(state.groundSet);
        restLeft.addAll((Collection<? extends IndexVertex>) G.leftVertices());

        VSubSet inRight = new VSubSet(state.groundSet);
        VSubSet outRight = new VSubSet(state.groundSet);
        VSubSet restRight = new VSubSet(state.groundSet);
        restRight.addAll((Collection<? extends IndexVertex>) G.rightVertices());
        
        return count(state, G,
                inLeft, outLeft, restLeft,
                inRight, outRight, restRight);
    }
}
