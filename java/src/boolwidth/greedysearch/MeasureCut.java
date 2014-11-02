package boolwidth.greedysearch;

import graph.Vertex;

import java.util.Collection;

/**
 * Created by emh on 11/2/2014.
 */
public interface MeasureCut {

    public long applyAsLong(Collection<Vertex<Integer>> newlefts, Vertex<Integer> tomove);

}
