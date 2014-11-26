package boolwidth.greedysearch.treewidth;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.CaterpillarDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.spanning.SpanningTreeDecompose;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import graph.Vertex;
import interfaces.IGraph;
import nl.uu.cs.treewidth.algorithm.*;
import nl.uu.cs.treewidth.input.GraphInput;
import nl.uu.cs.treewidth.ngraph.*;

import java.util.*;


/**
 * Created by emh on 11/24/2014.
 */
public class TreeWidthGreedyFillinDecompose extends BaseDecompose{

    public TreeWidthGreedyFillinDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    public static <TVertex extends Vertex<V>, V, E> GraphAndMap<TVertex, V> toTreeWidthGraph(IGraph<TVertex, V, E> graph) {
        NVertex<GraphInput.InputData> vertexPrototype = new ListVertex<GraphInput.InputData>();
        GraphAndMap<TVertex, V> result = new GraphAndMap<>();
        for (TVertex v : graph.vertices()) {
            NVertex<GraphInput.InputData> newVertex = vertexPrototype.newOfSameType(new GraphInput.InputData(v.id(), ""));
            result.twGraph.addVertex(newVertex);
            result.oldToNewVertex.put(v, newVertex);
        }
        for (TVertex v1 : graph.vertices()) {
            for (TVertex v2 : graph.incidentVertices(v1)) {
                NVertex<GraphInput.InputData> newVertex1 = result.oldToNewVertex.get(v1);
                NVertex<GraphInput.InputData> newVertex2 = result.oldToNewVertex.get(v2);
                if (v1.id() < v2.id()) {
                    result.twGraph.addEdge(newVertex1, newVertex2);
                }
            }
        }
        return result;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        GraphAndMap<Vertex<Integer>, Integer> graphAndMap = toTreeWidthGraph(getGraph());

        Permutation<GraphInput.InputData> p = new LexBFS<>(); //The upperbound algorithm
        int upperBound1 = Integer.MAX_VALUE;
        try {
            PermutationToTreeDecomposition<GraphInput.InputData> pttd = new PermutationToTreeDecomposition<>(p);
            pttd.setInput(graphAndMap.twGraph);
            pttd.run();
            upperBound1 = pttd.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("warning! treewidthlib bug!");
        }

        GreedyFillIn<GraphInput.InputData> ubAlgo2 = new GreedyFillIn<>();
        int upperBound2 = Integer.MAX_VALUE;
        try {
            ubAlgo2.setInput(graphAndMap.twGraph);
            ubAlgo2.run();
            upperBound2 = ubAlgo2.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("warning! treewidthlib bug!");
        }

        int upperBound3 = Integer.MAX_VALUE;
        GreedyDegree<GraphInput.InputData> ubAlgo3 = new GreedyDegree<>();
        try {
            ubAlgo3.setInput(graphAndMap.twGraph);
            ubAlgo3.run();
            upperBound3 = ubAlgo3.getUpperBound();
        } catch (ConcurrentModificationException e) {
            System.out.println("warning! treewidthlib bug!");
        } catch (NullPointerException e) {
            System.out.println("warning! treewidthlib bug!");
        }

        int UB = Math.min(Math.min(upperBound1, upperBound2), upperBound3);

        NVertexOrder<GraphInput.InputData> permutation = null;
        if (UB == Integer.MAX_VALUE) {
            return new SpanningTreeDecompose(getGraph()).decompose();
        } else if (UB == upperBound1) {
            permutation = p.getPermutation();
        } else if (UB == upperBound2) {
            permutation = ubAlgo2.getPermutation();
        } else if (UB == upperBound3) {
            permutation = ubAlgo3.getPermutation();
        }

        System.out.printf("UB: %d [%d, %d, %d]\n",
                UB,
                upperBound1,
                upperBound2,
                upperBound3);

        PermutationToTreeDecomposition<GraphInput.InputData> convertor = new PermutationToTreeDecomposition<>(permutation);
        convertor.setInput(graphAndMap.twGraph);
        convertor.run();
        NGraph<NTDBag<GraphInput.InputData>> decomposition = convertor.getDecomposition();

        ImmutableBinaryTree ibt = treeWidthDecompositionToBooleanDecomposition(graphAndMap, decomposition);

        return ibt;
    }

    private ImmutableBinaryTree treeWidthDecompositionToBooleanDecomposition(GraphAndMap<Vertex<Integer>, Integer> graphAndMap, NGraph<NTDBag<GraphInput.InputData>> decomposition) {
        ArrayList<Vertex<Integer>> ordering = new ArrayList<>();
        HashSet<Vertex<Integer>> seen = new HashSet<>();
        BiMap<HashSet<Vertex<Integer>>, NVertex<NTDBag<GraphInput.InputData>>> bagMap = HashBiMap.create();

        Stack<NVertex<NTDBag<GraphInput.InputData>>> bags = new Stack<>();
        HashSet<NVertex<NTDBag<GraphInput.InputData>>> seenBags = new HashSet<>();
        bags.push(decomposition.getVertex(0));
        seenBags.add(decomposition.getVertex(0));

        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        HashMap<NVertex<NTDBag<GraphInput.InputData>>, SimpleNode> ibtMap = new HashMap<>();
        ibtMap.put(decomposition.getVertex(0), ibt.getRoot());

        while (!bags.isEmpty()) {
            NVertex<NTDBag<GraphInput.InputData>> bag = bags.pop();

            HashSet<Vertex<Integer>> vertices = new HashSet<>();
            for (NVertex<GraphInput.InputData> newVertex : bag.data.vertices) {
                Vertex<Integer> v = graphAndMap.oldToNewVertex.inverse().get(newVertex);
                if (!seen.contains(v)) {
                    ordering.add(v);
                    seen.add(v);
                    vertices.add(v);
                }
            }
            bagMap.put(vertices, bag);

            Stack<NVertex<NTDBag<GraphInput.InputData>>> childBags = new Stack<>();
            Iterator<NVertex<NTDBag<GraphInput.InputData>>> it = bag.getNeighbors();
            while (it.hasNext()) {
                NVertex<NTDBag<GraphInput.InputData>> bag2 = it.next();
                if (!seenBags.contains(bag2)) {
                    bags.push(bag2);
                    seenBags.add(bag2);
                    childBags.add(bag2);
                }
            }

            // add isolated bags
            if (bags.isEmpty()) {
                for (NVertex<NTDBag<GraphInput.InputData>> isolatedBag : decomposition) {
                    if (!seenBags.contains(isolatedBag)) {
                        seenBags.add(isolatedBag);
                        childBags.push(isolatedBag);
                        bags.push(isolatedBag);
                    }
                }
            }

            SimpleNode nodeParent = ibtMap.get(bag);
            //System.out.printf("nodeParent of %s = %s\n", v, nodeParent);
            //ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
            int i = 0;
            for (Vertex<Integer> v : vertices) {
                i++;
                // add left/right child
                ibt = ibt.addChild(nodeParent, v.id());

                // add extra internal right child if more than 2 children
                if (vertices.size() + childBags.size() - i > 1) {
                    // insert extra internal node
                    ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                    nodeParent = ibt.getReference();
                }
            }



            while (!childBags.isEmpty()) {
                // add left/right child
                NVertex<NTDBag<GraphInput.InputData>> child = childBags.pop();
                ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                ibtMap.put(child, ibt.getReference());

                // add extra internal right child if more than 2 children
                if (childBags.size() > 1) {
                    // insert extra internal node
                    ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                    nodeParent = ibt.getReference();
                }
            }
        }
        return ibt;
    }
}
