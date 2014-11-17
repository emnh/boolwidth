package boolwidth.greedysearch.spanning;

import graph.Vertex;

/**
 * Created by emh on 11/17/2014.
 */
public class NodePair {
    public Vertex<Integer> a;
    public Vertex<Integer> b;
    public double cost;

    public NodePair(Vertex<Integer> a, Vertex<Integer> b, double cost) {
        this.a = a;
        this.b = b;
        this.cost = cost;
    }
}
