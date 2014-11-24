package boolwidth.greedysearch.treewidth;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import graph.Vertex;
import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.ngraph.ListGraph;
import nl.uu.cs.treewidth.ngraph.NGraph;
import nl.uu.cs.treewidth.ngraph.NVertex;

/**
 * Created by emh on 11/24/2014.
 */
public class GraphAndMap<TVertex extends Vertex<V>, V> {
    public BiMap<TVertex, NVertex<GraphInput.InputData>> oldToNewVertex = HashBiMap.create();
    public NGraph<GraphInput.InputData> twGraph = new ListGraph<>();
}
