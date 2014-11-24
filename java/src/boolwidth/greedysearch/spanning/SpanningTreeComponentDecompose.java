package boolwidth.greedysearch.spanning;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.subsets.PosSubSet;
import interfaces.IGraph;
import javafx.geometry.Pos;

import java.util.*;

/**
 * Created by emh on 11/16/2014.
 */



public class SpanningTreeComponentDecompose extends BaseDecompose {

    private ArrayList<PosSubSet<Vertex<Integer>>> hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());

    public SpanningTreeComponentDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
        hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());
    }

    public Collection<Vertex<Integer>> getIncidentVertices(Vertex<Integer> v) {
        return getGraph().incidentVertices(v);
    }

    public double getCost(PosSubSet<Vertex<Integer>> a, PosSubSet<Vertex<Integer>> b) {
        // FIRST GOOD
        double cost = -(double) a.intersection(b).size() / a.union(b).size();
        // SAME SAME
        //double cost = -(double) a.intersection(b).size() / a.symmetricDifference(b).size();

        // SECOND GOOD
        //double cost = a.symmetricDifference(b).size();

        // EXPERIMENTAL
        //double cost = b.subtract(a).size();
        return cost;
    }

    public double getCost(Vertex<Integer> a, Vertex<Integer> b) {
        double cost = getCost(hoods.get(a.id()), hoods.get(b.id()));
        return cost;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        //Multimap<Vertex<Integer>, Vertex<Integer>> spanningTree = ArrayListMultimap.create();
        ArrayList<ArrayList<Vertex<Integer>>> spanningTreeNeighbours = new ArrayList<>();
        ArrayList<ArrayList<Vertex<Integer>>> components = new ArrayList<>();
        ArrayList<PosSubSet<Vertex<Integer>>> N_LEFTs = new ArrayList<>();
        PriorityQueue<NodePair> edges = new PriorityQueue<>(getGraph().numEdges(), Comparator.comparingDouble((n) -> n.cost));

        // Initialize edges with costs
        for (Vertex<Integer> a : getGraph().vertices()) {
            ArrayList<Vertex<Integer>> component =  new ArrayList<>();
            component.add(a);
            N_LEFTs.add(hoods.get(a.id()));
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

        // Kruskal's algorithm for minimum spanning tree
        while (!edges.isEmpty()) {
            NodePair np = edges.poll();
            //System.out.printf("cost: %.2f\n", np.cost);

            if (components.get(np.a.id()) != components.get(np.b.id())) {
                //System.out.printf("setting %s component to %s component\n", np.a.id(), np.b.id());
                ArrayList<Vertex<Integer>> aComponent = components.get(np.a.id());
                PosSubSet<Vertex<Integer>> N_LEFT = N_LEFTs.get(np.a.id());

                /*PosSubSet<Vertex<Integer>> N_INTERNAL = new PosSubSet<>(N_LEFT.groundSet);
                for (Vertex<Integer> v : aComponent) {
                    N_INTERNAL.add(v);
                }
                for (Vertex<Integer> v : components.get(np.b.id())) {
                    N_INTERNAL.add(v);
                }*/
                N_LEFT = N_LEFT.union(N_LEFTs.get(np.b.id()));
                //N_LEFT = N_LEFT.subtract(N_INTERNAL);

                for (Vertex<Integer> v : aComponent) {
                    N_LEFTs.set(v.id(), N_LEFT);
                }
                for (Vertex<Integer> v : components.get(np.b.id())) {
                    components.set(v.id(), aComponent);
                    N_LEFTs.set(v.id(), N_LEFT);
                    aComponent.add(v);
                }
                spanningTreeNeighbours.get(np.a.id()).add(np.b);
                spanningTreeNeighbours.get(np.b.id()).add(np.a);

                // update edge priorities based on new component
                for (Vertex<Integer> u : aComponent) {
                    for (Vertex<Integer> v : getIncidentVertices(u)) {
                        if (aComponent != components.get(v.id())) {
                            ArrayList<NodePair> removeList = new ArrayList<>();
                            for (NodePair e : edges) {
                                if(e.a == u && e.b == v || e.a == v && e.b == u) {
                                    removeList.add(e);
                                }
                            }
                            edges.removeAll(removeList);
                            PosSubSet<Vertex<Integer>> N_RIGHT = N_LEFTs.get(v.id());
                            double cost = getCost(N_LEFT, N_RIGHT);
                            NodePair npNew = new NodePair(u, v, cost);
                            edges.add(npNew);
                        }
                    }
                }
            } else {
                // not different components, so not part of spanning tree
            }
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
