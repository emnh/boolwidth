package boolwidth.cutbool;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.util.IndexedSet;

import java.util.*;

/**
 * Created by emh on 11/28/2015.
 */
public class CBIncremental {

    private static ArrayList<VSubSet> neighbourhoods;
    private static IndexedSet<IndexVertex> groundSet;

    private static class ReturnValue {
        public long size;
        public TreeSet<VSubSet> neighbourHoods;
        public IndexVertex chosen;
    }

    public static long GreedyCaterpillarWidth(IndexGraph G, String fileName)	{
        neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
        groundSet = new IndexedSet<IndexVertex>(G.vertices());

        for(int i = 0; i < G.numVertices(); i++) {
            IndexVertex v = G.getVertex(i);
            neighbourhoods.add(new VSubSet(groundSet, G.neighbours(v)));
        }

        VSubSet left = new VSubSet(groundSet);
        VSubSet right = new VSubSet(groundSet);
        for(int i = 0; i < G.numVertices(); i++) {
            right.add(G.getVertex(i));
        }

        VSubSet empty = new VSubSet(groundSet);
        TreeSet<VSubSet> previousNeighbourHoods = new TreeSet<>();
        previousNeighbourHoods.add(empty);
        long maxValue = 0;
        for (int i = 0; i < G.numVertices(); i++) {
            ReturnValue returnValue = countNeighbourHoodsIncremental(G, previousNeighbourHoods, left, right);
            left.add(returnValue.chosen);
            right.remove(returnValue.chosen);
            previousNeighbourHoods = returnValue.neighbourHoods;
            //System.out.printf("%s: moving %d from right(%d) to left(%d), new size: %d\n",
            //        fileName, returnValue.chosen.id(), right.size(), left.size(), previousNeighbourHoods.size());
            if (previousNeighbourHoods.size() > maxValue) {
                maxValue = previousNeighbourHoods.size();
            }
        }

        return maxValue;
    }

    public static ReturnValue countNeighbourHoodsIncremental(IndexGraph G, TreeSet<VSubSet> previousNeighbourHoods, VSubSet left, VSubSet right) {
        long minSize = Long.MAX_VALUE;
        ReturnValue returnValue = new ReturnValue();

        VSubSet selection = right; /*new VSubSet(groundSet);
        long maxDegree = 0;
        IndexVertex maxVertex = null;
        for (IndexVertex v : left) {
            VSubSet rightHood = neighbourhoods.get(v.id()).intersection(right);
            selection = selection.union(rightHood);
        }
        for (IndexVertex v : selection) {
            long degree = neighbourhoods.get(v.id()).intersection(right).size();
            if (degree > maxDegree) {
                maxDegree = degree;
                maxVertex = v;
            }
        }
        if (maxVertex != null) {
            selection = new VSubSet(groundSet);
            selection.add(maxVertex);
        } else {
            selection.add(right.first());
        }
        if (selection.isEmpty()) {
            selection.add(right.first());
        }
        */

        VSubSet neighbourHoodOfLeft = new VSubSet(groundSet);
        for (IndexVertex v : left) {
            VSubSet rightHood = neighbourhoods.get(v.id()).intersection(right);
            neighbourHoodOfLeft = neighbourHoodOfLeft.union(rightHood);
        }

        ArrayList<VSubSet> alPrevious = new ArrayList<>(previousNeighbourHoods);

        VSubSet backups = new VSubSet(groundSet);
        for (IndexVertex v : selection) {
            TreeSet<VSubSet> newNeighbourHoods = new TreeSet<>();
            VSubSet rightNeighbourHood = neighbourhoods.get(v.id()).intersection(right);
            // redundant since no self-loops allowed but just in case
            rightNeighbourHood.remove(v);
            if (rightNeighbourHood.subtract(neighbourHoodOfLeft).size() > 0) {
                // size will probably be near to 2 * previousNeighbourHoods
                backups.add(v);
            } else {
                for (VSubSet hood : alPrevious) {
                    VSubSet newHood = hood.clone();
                    newHood.remove(v);
                    newNeighbourHoods.add(newHood);
                    VSubSet newHoodUnion = newHood.union(rightNeighbourHood);
                    newNeighbourHoods.add(newHoodUnion);
                    if (newNeighbourHoods.size() > minSize) {
                        break;
                    }
                }
                if (newNeighbourHoods.size() < minSize) {
                    minSize = newNeighbourHoods.size();
                    returnValue.size = minSize;
                    returnValue.chosen = v;
                    returnValue.neighbourHoods = newNeighbourHoods;
                }
            }
        }
        if (minSize == Long.MAX_VALUE) {
            // System.out.println("resorting to backups");
            for (IndexVertex v : backups) {
                TreeSet<VSubSet> newNeighbourHoods = new TreeSet<>();
                VSubSet rightNeighbourHood = neighbourhoods.get(v.id()).intersection(right);
                // redundant since no self-loops allowed but just in case
                rightNeighbourHood.remove(v);

                for (VSubSet hood : alPrevious) {
                    VSubSet newHood = hood.clone();
                    newHood.remove(v);
                    newNeighbourHoods.add(newHood);
                    VSubSet newHoodUnion = newHood.union(rightNeighbourHood);
                    newNeighbourHoods.add(newHoodUnion);
                    if (newNeighbourHoods.size() > minSize) {
                        break;
                    }
                }
                if (newNeighbourHoods.size() < minSize) {
                    minSize = newNeighbourHoods.size();
                    returnValue.size = minSize;
                    returnValue.chosen = v;
                    returnValue.neighbourHoods = newNeighbourHoods;
                }
            }
        }

        /*
        ArrayList<TreeSet<VSubSet>> newNeighbourHoodsList = new ArrayList<>(G.numVertices());
        for(int i = 0; i < G.numVertices(); i++) {
            newNeighbourHoodsList.add(null);
        }
        for (IndexVertex v : right) {
            TreeSet<VSubSet> newNeighbourHoods = new TreeSet<>();
            newNeighbourHoodsList.set(v.id(), newNeighbourHoods);
        }
        for (VSubSet hood : previousNeighbourHoods) {
            for (IndexVertex v : right) {
                TreeSet<VSubSet> newNeighbourHoods = newNeighbourHoodsList.get(v.id());
                VSubSet rightNeighbourHood = neighbourhoods.get(v.id()).intersection(right);
                // redundant since no self-loops allowed but just in case
                rightNeighbourHood.remove(v);

                VSubSet newHood = hood.clone();
                newHood.remove(v);
                newNeighbourHoods.add(newHood);
                VSubSet newHoodUnion = newHood.union(rightNeighbourHood);
                newNeighbourHoods.add(newHoodUnion);
            }
        }
        for (IndexVertex v : right) {
            TreeSet<VSubSet> newNeighbourHoods = newNeighbourHoodsList.get(v.id());
            if (newNeighbourHoods.size() < minSize) {
                minSize = newNeighbourHoods.size();
                returnValue.size = minSize;
                returnValue.chosen = v;
                returnValue.neighbourHoods = newNeighbourHoods;
            }
        }*/

        return returnValue;
    }
}
