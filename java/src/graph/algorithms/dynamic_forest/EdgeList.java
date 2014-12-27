package graph.algorithms.dynamic_forest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by emh on 26.12.2014.
 */
public class EdgeList {
    public static int compareEdges(DynamicForest.DynamicEdge a, DynamicForest.DynamicEdge b) {
        int d = a.level - b.level;
        if (d != 0) {
            return d;
        }
        return a.key - b.key;
    }

    public static int compareLevel(DynamicForest.DynamicEdge a, int i) {
        return a.level - i;
    }

    public static void insertEdge(ArrayList<DynamicForest.DynamicEdge> list, DynamicForest.DynamicEdge e) {
        //list.splice(bounds.gt(list, e, compareEdges), 0, e);
        int idx = index(list, e);
        idx = -(idx + 1);
        list.add(idx, e);
    }

    public static int index(ArrayList<DynamicForest.DynamicEdge> list, DynamicForest.DynamicEdge e) {
        int index = Collections.binarySearch(list, e, new Comparator<DynamicForest.DynamicEdge>() {
            @Override
            public int compare(DynamicForest.DynamicEdge o1, DynamicForest.DynamicEdge o2) {
                return compareEdges(o1, o2);
            }
        });
        return index;
    }

    public static void removeEdge(ArrayList<DynamicForest.DynamicEdge> list, DynamicForest.DynamicEdge e) {
        int idx = index(list, e);
        if(idx >= 0) {
            list.remove(idx);
        }
    }

    public static int levelIndex(ArrayList<DynamicForest.DynamicEdge> list, int i) {
        //return bounds.ge(list, i, compareLevel)
        int index = Collections.binarySearch(list, null, new Comparator<DynamicForest.DynamicEdge>() {
            @Override
            public int compare(DynamicForest.DynamicEdge o1, DynamicForest.DynamicEdge o2) {
                assert o2 == null;
                return compareLevel(o1, i);
            }
        });
        return index;
    }
}
