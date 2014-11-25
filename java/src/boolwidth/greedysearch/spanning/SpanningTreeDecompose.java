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



public class SpanningTreeDecompose extends BaseDecompose {

    protected ArrayList<PosSubSet<Vertex<Integer>>> hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());

    public SpanningTreeDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
        hoods = BasicGraphAlgorithms.getNeighbourHoods(getGraph());
    }

    public Collection<Vertex<Integer>> getIncidentVertices(Vertex<Integer> v) {
        return getGraph().incidentVertices(v);
    }

    public double getCost(Vertex<Integer> a, Vertex<Integer> b) {
        // FIRST GOOD
        double cost = -(double) hoods.get(a.id()).intersect(hoods.get(b.id())).size() / hoods.get(a.id()).union(hoods.get(b.id())).size();
        // SAME SAME
        //double cost = -(double) hoods.get(a.id()).intersect(hoods.get(b.id())).size() / hoods.get(a.id()).symmetricDifference(hoods.get(b.id())).size();

        // SECOND GOOD
        //double cost = hoods.get(a.id()).symmetricDifference(hoods.get(b.id())).size();

        // EXPERIMENTAL
        //double cost = hoods.get(b.id()).size() - hoods.get(a.id()).size();
        return cost;
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> vertices = new ArrayList<>(BasicGraphAlgorithms.getAllVertices(getGraph()));
        return decompose(vertices);
    }

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> allVerticesList) {
        //Multimap<Vertex<Integer>, Vertex<Integer>> spanningTree = ArrayListMultimap.create();
        ArrayList<ArrayList<Vertex<Integer>>> spanningTreeNeighbours = new ArrayList<>();
        ArrayList<ArrayList<Vertex<Integer>>> components = new ArrayList<>();
        PriorityQueue<NodePair> edges = new PriorityQueue<>(getGraph().numEdges(), Comparator.comparingDouble((n) -> n.cost));

        HashSet<Vertex<Integer>> allVertices = new HashSet<>(allVerticesList);

        // Initialize edges with costs
        for (Vertex<Integer> a : getGraph().vertices()) {
            ArrayList<Vertex<Integer>> component =  new ArrayList<>();
            component.add(a);
            components.add(component);
            spanningTreeNeighbours.add(new ArrayList<>());
            //for (Vertex<Integer> b : getGraph().incidentVertices(a)) {
            if (allVertices.contains(a)) {
                for (Vertex<Integer> b : getIncidentVertices(a)) {
                    if (a.id() < b.id() && allVertices.contains(b)) {
                        NodePair np = new NodePair(a, b, getCost(a, b));
                        edges.add(np);
                        //System.out.printf("adding edge: %s - %s\n", np.a, np.b);
                    }
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
                for (Vertex<Integer> v : components.get(np.b.id())) {
                    components.set(v.id(), aComponent);
                    aComponent.add(v);
                }

                //long cb = getCutBool(aComponent, true);

                spanningTreeNeighbours.get(np.a.id()).add(np.b);
                spanningTreeNeighbours.get(np.b.id()).add(np.a);
            } else {
                // not different components, so not part of spanning tree
            }
        }

        // Appoint root as vertex with index 0
        Vertex<Integer> root = allVerticesList.iterator().next();
        connectComponents(allVerticesList, spanningTreeNeighbours, components, root);
        return getImmutableBinaryTree(spanningTreeNeighbours, root);
    }

    protected void connectComponents(ArrayList<Vertex<Integer>> allVerticesList, ArrayList<ArrayList<Vertex<Integer>>> spanningTreeNeighbours, ArrayList<ArrayList<Vertex<Integer>>> components, Vertex<Integer> root) {
        // Connect components, just add all to vertex 0, which will be root
        for (Vertex<Integer> v : allVerticesList) {
            //System.out.printf("component: %s\n", components.get(v.id()));
            if (components.get(v.id()) != components.get(root.id())) {
                //System.out.printf("disconnected component for %d\n", v.id());
                spanningTreeNeighbours.get(root.id()).add(v);
                spanningTreeNeighbours.get(v.id()).add(root);
            }
        }
    }

    protected ImmutableBinaryTree getImmutableBinaryTree(ArrayList<ArrayList<Vertex<Integer>>> spanningTreeNeighbours, Vertex<Integer> root) {
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
