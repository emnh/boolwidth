package boolwidth.greedysearch.reorder;

import boolwidth.greedysearch.base.*;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.growNeighbourHood.SplitGrowNeighbourhood;
import boolwidth.greedysearch.symdiff.SplitSymDiff;
import com.github.krukow.clj_lang.PersistentHashSet;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import graph.BasicGraphAlgorithms;
import graph.Vertex;
import graph.VertexLabel;
import interfaces.IGraph;

import java.util.*;

/**
 * Created by emh on 11/26/2014.
 */
public class BFSDecompose extends StackDecompose {

    public BFSDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        //return super.createSplit(depth, decomposition, lefts, rights);
        return new SplitSymDiff(depth, decomposition, lefts, rights);
        //return new SplitGrowNeighbourhood(depth, decomposition, lefts, rights);
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<TVertex> getShortestPath(IGraph<TVertex, V, E> G,
                                                                                                HashSet<TVertex> subGraph,
                                                                                                TVertex start,
                                                                                                TVertex end) {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();
        HashMap<TVertex, TVertex> parents = new HashMap<>();

        TVertex root = start;
        vertexQueue.add(root);
        visited[root.id()] = true;

        TVertex current;
        while (!vertexQueue.isEmpty()) {
            current = vertexQueue.remove();

            for (TVertex child : G.incidentVertices(current)) {
                if (subGraph.contains(child) && !visited[child.id()]) {
                    parents.put(child, current);
                    if (child == end) {
                        vertexQueue.clear();
                    } else {
                        vertexQueue.add(child);
                        visited[child.id()] = true;
                    }
                }
            }
        }

        ArrayList<TVertex> path = new ArrayList<>();
        current = end;
        path.add(current);
        while (parents.containsKey(current)) {
            current = parents.get(current);
            path.add(current);
        }
        return path;
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<ArrayList<TVertex>> getComponents(IGraph<TVertex, V, E> G,
                                                                                                HashSet<TVertex> subGraph,
                                                                                                ArrayList<TVertex> removed) {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();

        ArrayList<TVertex> vertices = new ArrayList<>();
        vertices.addAll(BasicGraphAlgorithms.getAllVertices(G));

        ArrayList<ArrayList<TVertex>> components = new ArrayList<>();
        int componentCount = 0;

        for (TVertex v : removed) {
            visited[v.id()] = true;
        }

        for (TVertex root : subGraph) {
            if (visited[root.id()] == false) {
                componentCount++;
                ArrayList<TVertex> component = new ArrayList<>();
                components.add(component);
                vertexQueue.add(root);
                visited[root.id()] = true;

                TVertex current = root;

                while (!vertexQueue.isEmpty()) {
                    current = vertexQueue.remove();
                    component.add(current);

                    for (TVertex child : G.incidentVertices(current)) {
                        if (subGraph.contains(child) && !visited[child.id()]) {
                            vertexQueue.add(child);
                            visited[child.id()] = true;
                        }
                    }
                }
            }
        }

        return components;
    }

    public ImmutableBinaryTree decomposeRest(ArrayList<Vertex<Integer>> vertices) {
        CaterpillarDecompose cd = new CaterpillarDecompose(getGraph()) {
            @Override
            public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
                return new SplitSymDiff(depth, decomposition, lefts, rights);
            }
        };
        return cd.decompose(vertices);
    }

    @Override
    protected Split initSplit(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren, Split split, Split parent) {
        Split newSplit = null;
        double bestBalance = Double.MAX_VALUE;
        Vertex<Integer> minU = null, minV = null;
        ImmutableBinaryTree ibtLeft = null, ibtRight = null;
        ArrayList<ArrayList<Vertex<Integer>>> baseComponents = getComponents(getGraph(), new HashSet<>(split.getAll()), new ArrayList<>());
        System.out.printf("entering initSplit: %s, %s\n", split.getLefts(), split.getRights());
        if (baseComponents.size() >= 2) {
            ArrayList<Vertex<Integer>> lefts = baseComponents.get(0);
            ArrayList<Vertex<Integer>> rights = new ArrayList<>();
            for (ArrayList<Vertex<Integer>> connectedSubGraph : baseComponents.subList(1, baseComponents.size())) {
                rights.addAll(connectedSubGraph);
            }
            newSplit = createSplit(0, this, lefts, rights);
            System.out.printf("connected components >= 2, l/r: %s / %s\n", lefts, rights);
        } else if (split.size() >= getGraph().numVertices()) {
            ArrayList<Vertex<Integer>> connectedSubGraph = baseComponents.get(0);
            boolean done = false;
            HashSet<Vertex<Integer>> hashSubGraph = new HashSet<>(connectedSubGraph);
            for (Vertex<Integer> u : connectedSubGraph) {
                Stack<Vertex<Integer>> separators = new Stack<>();
                HashSet<Vertex<Integer>> seen = new HashSet<>();

                // move along the perimeter
                separators.push(BasicGraphAlgorithms.BFS(getGraph(), u, PersistentHashSet.create(hashSubGraph)));
                while (!separators.isEmpty()) {
                    Vertex<Integer> v = separators.pop();
                    if (u != v) {
                        ArrayList<Vertex<Integer>> path = getShortestPath(getGraph(), hashSubGraph, u, v);
                        ArrayList<ArrayList<Vertex<Integer>>> components = getComponents(getGraph(), hashSubGraph, path);
                        //if (components.size() >= 2 &&
                                //components.get(0).size() + path.size() >= connectedSubGraph.size() / 3 &&
                                //components.get(1).size() >= connectedSubGraph.size() / 3) {
                        if (components.size() >= 2) {
                            for (Vertex<Integer> v2 : getGraph().incidentVertices(v)) {
                                if (!seen.contains(v2)) {
                                    separators.push(v2);
                                    seen.add(v2);
                                }
                            }
                            if (components.get(0).size() + path.size() >= connectedSubGraph.size() / 3 &&
                                    components.get(1).size() >= connectedSubGraph.size() / 3) {
                                ArrayList<Vertex<Integer>> lefts = components.get(0);
                                lefts.addAll(path);
                                ArrayList<Vertex<Integer>> rights = new ArrayList<>();
                                for (ArrayList<Vertex<Integer>> csg : components.subList(1, components.size())) {
                                    rights.addAll(csg);
                                }

                                ImmutableBinaryTree ibt1 = decomposeRest(lefts);
                                ImmutableBinaryTree ibt2 = decomposeRest(rights);
                                long cb1 = getBooleanWidth(ibt1); //getCutBool(lefts, true);
                                long cb2 = getBooleanWidth(ibt2); //getCutBool(rights, true);
                                //double balance = path.size(); //Math.abs(lefts.size() - rights.size());
                                double balance = Math.max(cb1, cb2);
                                if (balance <= bestBalance) {
                                    System.out.printf("cbLeft: %.2f, cbRight: %.2f, u, v: %s, %s: %s\n",
                                            getLogBooleanWidth(cb1), getLogBooleanWidth(cb2), u, v, path);
                                    bestBalance = balance;
                                    minU = u;
                                    minV = v;
                                    ibtLeft = ibt1;
                                    ibtRight = ibt2;
                                    newSplit = createSplit(0, this, lefts, rights);
                                }
                            }
                        }
                    }
                    if (done) break;
                }
                if (done) break;
            }
        }
        if (newSplit == null) {
            if (split.size() <= 1) {
                newSplit = split;
            } else {
                System.out.printf("no separator found\n", newSplit);
                newSplit = super.initSplit(splits, splitChildren, split, parent);
                //newSplit = split.decomposeAdvance();
            }
        } else {
            System.out.printf("newSplit: %s, %s : %s, %s\n", minU, minV, newSplit.getLefts(), newSplit.getRights());
        }
        /*if (ibtLeft != null && ibtRight != null) {
            Split leftChild = createSplit(split.getDepth() + 1, this, newSplit.getLefts());
            splitChildren.put(newSplit, leftChild);
            //splits.push(new StackDecomposeSplitStackItem(split, leftChild));

            Split rightChild = createSplit(split.getDepth() + 1, this, newSplit.getRights());
            splitChildren.put(newSplit, rightChild);
            //splits.push(new StackDecomposeSplitStackItem(split, rightChild));

            Multimap<Split, Split> leftChildren = LocalSearch.convertToSplits(ibtLeft, this);
            for (Split key : leftChildren.keys()) {
                Split newKey = key;
                if (key == null) {
                    newKey = leftChild;
                }
                splitChildren.putAll(newKey, leftChildren.get(key));
            }
            Multimap<Split, Split> rightChildren = LocalSearch.convertToSplits(ibtRight, this);
            for (Split key : rightChildren.keys()) {
                Split newKey = key;
                if (key == null) {
                    newKey = rightChild;
                }
                splitChildren.putAll(newKey, rightChildren.get(key));
            }
        }*/
        return newSplit;
    }

    @Override
    protected void decomposeSplits(Stack<StackDecomposeSplitStackItem> splits, Multimap<Split, Split> splitChildren) {
        while (!splits.isEmpty()) {
            StackDecomposeSplitStackItem splitStackItem = splits.pop();
            Split split = splitStackItem.child;
            Split parent = splitStackItem.parent;
            //while (!(2 * split.lefts.size() >= split.rights.size())) {
            Split newSplit = initSplit(splits, splitChildren, split, parent);
            splitChildren.remove(parent, split);
            splitChildren.put(parent, newSplit);
            split = newSplit;
            initSplitChildren(this, splits, splitChildren, split);
        }
    }

    @Override
    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {

        //Vertex<Integer> start = BasicGraphAlgorithms.BFS(getGraph(), vertices.iterator().next(), PersistentHashSet.create(vertices));
        //Vertex<Integer> end = BasicGraphAlgorithms.BFS(getGraph(), start, );

        return super.decompose(vertices);
    }
}
