package boolwidth.greedysearch.spanning;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.subsets.PosSubSet;
import interfaces.IGraph;

import java.util.*;

/**
 * Created by emh on 11/16/2014.
 */



public class GreedyMergeDecompose extends BaseDecompose {

    private ArrayList<PosSubSet<Vertex<Integer>>> hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());

    public GreedyMergeDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
        hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());
    }

    public Collection<Vertex<Integer>> getIncidentVertices(Vertex<Integer> v) {
        return getGraph().incidentVertices(v);
    }

    public double getCost(Vertex<Integer> a, Vertex<Integer> b) {
        double cost = -(double) hoods.get(a.id()).intersection(hoods.get(b.id())).size() / hoods.get(a.id()).union(hoods.get(b.id())).size();
        //double cost = -(double) hoods.get(a.id()).subtract(hoods.get(b.id())).size() / hoods.get(a.id()).size(); //union(hoods.get(b.id())).size();
        return cost;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        //Multimap<Vertex<Integer>, Vertex<Integer>> spanningTree = ArrayListMultimap.create();
        ArrayList<ArrayList<Vertex<Integer>>> spanningTreeNeighbours = new ArrayList<>();
        ArrayList<ArrayList<Vertex<Integer>>> components = new ArrayList<>();
        PriorityQueue<NodePair> edges = new PriorityQueue<>(getGraph().numEdges(), Comparator.comparingDouble((n) -> n.cost));

        // Initialize edges with costs
        for (Vertex<Integer> a : getGraph().vertices()) {
            ArrayList<Vertex<Integer>> component =  new ArrayList<>();
            component.add(a);
            components.add(component);
            spanningTreeNeighbours.add(new ArrayList<>());
            //for (Vertex<Integer> b : getGraph().incidentVertices(a)) {
            for (Vertex<Integer> b : getIncidentVertices(a)) {
                if (a.id() < b.id()) {
                    NodePair np = new NodePair(a, b, getCost(a, b));
                    edges.add(np);
                    //System.out.printf("adding edge: %s - %s\n", np.a, np.b);
                }
            }
        }

        // Minimum merge tree
        int componentCount = components.size();
        while (componentCount > 1) {
            //System.out.printf("cost: %.2f\n", np.cost);

            long mincb = Long.MAX_VALUE;
            Vertex<Integer> mina = null, minb = null;
            for (Vertex<Integer> a : getGraph().vertices()) {
                for (Vertex<Integer> b : getGraph().incidentVertices(a)) {
                    if (components.get(a.id()) != components.get(b.id())) {
                        //System.out.printf("setting %s component to %s component\n", np.a.id(), np.b.id());
                        ArrayList<Vertex<Integer>> newComponent = new ArrayList<>();
                        newComponent.addAll(components.get(a.id()));
                        newComponent.addAll(components.get(b.id()));
                        long cb = getCutBool(newComponent, true);
                        if (cb < mincb) {
                            mincb = cb;
                            mina = a;
                            minb = b;
                        }
                    } else {
                        // not different components, so not part of spanning tree
                    }
                }
            }
            if (mincb == Long.MAX_VALUE) break;
            //System.out.printf("connected: %d, new merge cutbool: %.2f\n", componentCount, getLogBooleanWidth(mincb));
            ArrayList<Vertex<Integer>> aComponent = components.get(mina.id());
            for (Vertex<Integer> v : components.get(minb.id())) {
                components.set(v.id(), aComponent);
                aComponent.add(v);
            }
            spanningTreeNeighbours.get(mina.id()).add(minb);
            spanningTreeNeighbours.get(minb.id()).add(mina);
            componentCount--;
        }

        // Appoint root as vertex with index 0
        Vertex<Integer> root = getGraph().getVertex(0);

        // Connect components, just add all to vertex 0, which will be root
        for (Vertex<Integer> v : getGraph().vertices()) {
            //System.out.printf("component: %s\n", components.get(v.id()));
            if (components.get(v.id()) != components.get(root.id())) {
                //System.out.printf("disconnected component for %d\n", v.id());
                spanningTreeNeighbours.get(root.id()).add(v);
                spanningTreeNeighbours.get(v.id()).add(root);
            }
        }

        // Convert to binary spanning tree
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        HashMap<Vertex<Integer>, SimpleNode> ibtMap = new HashMap<>();

        ibt = ibt.addChild(ibt.getRoot(), root.id()); // root.id() == 0 yes
        ibtMap.put(root, ibt.getReference());

        Stack<Vertex<Integer>> vertices = new Stack<>();
        boolean[] seen = new boolean[getGraph().numVertices()];
        vertices.add(root);
        seen[root.id()] = true;

        while (!vertices.isEmpty()) {
            Vertex<Integer> v = vertices.pop();
            Stack<Vertex<Integer>> children = new Stack<>();
            for (Vertex<Integer> v2 : spanningTreeNeighbours.get(v.id())) {
                if (!seen[v2.id()]) {
                    children.add(v2);
                    vertices.add(v2);
                    seen[v2.id()] = true;
                }
            }
            SimpleNode nodeParent = ibtMap.get(v);
            //System.out.printf("nodeParent of %s = %s\n", v, nodeParent);
            ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
            while (!children.isEmpty()) {
                // add left/right child
                Vertex<Integer> child = children.pop();
                ibt = ibt.addChild(nodeParent, child.id());
                ibtMap.put(child, ibt.getReference());

                // add extra internal right child if more than 2 children
                if (children.size() > 1) {
                    // insert extra internal node
                    ibt = ibt.addChild(nodeParent, ImmutableBinaryTree.EMPTY_NODE);
                    nodeParent = ibt.getReference();
                }
            }
        }

        return ibt;
    }
}
