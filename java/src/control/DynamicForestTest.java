package control;

import graph.algorithms.dynamic_forest.DynamicForest;

/**
 * Created by emh on 27.12.2014.
 */
public class DynamicForestTest {
    public static void main(String[] args) {
        DynamicForest df = new DynamicForest();
        DynamicForest.DynamicVertex a = df.createVertex("a");
        DynamicForest.DynamicVertex b = df.createVertex("b");
        DynamicForest.DynamicVertex c = df.createVertex("c");
        DynamicForest.DynamicVertex d = df.createVertex("d");

        // Print out connectivity between a and c
        System.out.println(a.componentSize());
        System.out.println(c.componentSize());
        System.out.println(a.connected(c)); // Prints out "false"

        // Link vertices together in a cycle
        DynamicForest.DynamicEdge ab = a.link(b, "");
        DynamicForest.DynamicEdge bc = b.link(c, "");
        DynamicForest.DynamicEdge cd = c.link(d, "");
        DynamicForest.DynamicEdge da = d.link(a, "");
        DynamicForest.DynamicEdge da2 = a.link(d, "");

        // Vertices are now connected
        System.out.println(a.componentSize());
        System.out.println(c.componentSize());
        System.out.println(a.connected(c));   //Prints out "true"

        // Cut the edge between b and c
        bc.cut();

        // Still connected
        System.out.println(a.componentSize());
        System.out.println(c.componentSize());
        System.out.println(a.connected(c));   //Prints out "true"

        // Cut edge between a and d
        //da.cut();
        //da2.cut();
        d.cut();

        // Finally a and c are disconnected
        System.out.println(a.componentSize());
        System.out.println(c.componentSize());
        System.out.println(a.connected(c));   //Prints out "false"
    }
}
