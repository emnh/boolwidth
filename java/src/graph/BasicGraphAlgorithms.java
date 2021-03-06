package graph;

import com.github.krukow.clj_lang.PersistentHashSet;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import interfaces.IGraph;
import sadiasrc.graph.BasicGraphAlgorithm;

import java.util.*;

/**
 * Created by emh on 11/10/2014.
 */
public class BasicGraphAlgorithms {
    public static <TVertex extends Vertex<V>, V, E> Vertex<V> BFS(IGraph<TVertex, V, E> G, TVertex root, PersistentHashSet<TVertex> valids)
    {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();
        vertexQueue.add(root);
        visited[root.id()] = true;

        TVertex current = root;
        TVertex result = root;

        while(!vertexQueue.isEmpty())
        {
            current = vertexQueue.remove();
            if (valids.contains(current)) {
                result = current;
            }
            for(TVertex child : G.incidentVertices(current))
            {
                if(!visited[child.id()])
                {
                    vertexQueue.add(child);
                    visited[child.id()] = true;

                }
            }
        }

        return result;
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<TVertex> BFSGrid(IGraph<TVertex, V, E> G, TVertex start)
    {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();

        ArrayList<TVertex> vertices = new ArrayList<>();
        vertices.add(start);
        vertices.addAll(BasicGraphAlgorithms.getAllVertices(G));
        ArrayList<ArrayList<TVertex>> layers = new ArrayList<>();
        int layerIndex = 0;
        ArrayList<TVertex> resultList = new ArrayList<>();
        HashSet<TVertex> resultHash = new HashSet<>();

        for (TVertex root : vertices) {
            if (visited[root.id()] == false) {
                vertexQueue.add(root);
                visited[root.id()] = true;

                ArrayList<TVertex> layer = new ArrayList<>();
                layer.add(root);
                layers.add(layer);

                while (layers.get(layerIndex).size() > 0) {
                    ArrayList<TVertex> nextLayer = new ArrayList<>();
                    layers.add(nextLayer);
                    ArrayList<TVertex> corners = new ArrayList<>();
                    for (TVertex current : layers.get(layerIndex)) {
                        resultList.add(current);
                        resultHash.add(current);
                    }
                    // add corners
                    for (TVertex current : layers.get(layerIndex)) {
                        for (TVertex child : G.incidentVertices(current)) {
                            int resultNeighbours = 0;
                            for (TVertex n : G.incidentVertices(current)) {
                                if (resultHash.contains(n)) {
                                    resultNeighbours++;
                                }
                            }
                            if (!visited[child.id()] && resultNeighbours >= 3) {
                                corners.add(child);
                                visited[child.id()] = true;
                            }
                        }
                    }
                    layers.get(layerIndex).addAll(corners);
                    for (TVertex current : corners) {
                        resultList.add(current);
                        resultHash.add(current);
                    }
                    // add normal
                    for (TVertex current : layers.get(layerIndex)) {
                        for (TVertex child : G.incidentVertices(current)) {
                            if (!visited[child.id()]) {
                                nextLayer.add(child);
                                visited[child.id()] = true;
                            }
                        }
                    }
                    layerIndex++;
                }
            }
        }
        Collections.reverse(resultList);

        return resultList;
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<TVertex> BFSAll(IGraph<TVertex, V, E> G, TVertex start)
    {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();

        ArrayList<TVertex> vertices = new ArrayList<>();
        vertices.add(start);
        vertices.addAll(BasicGraphAlgorithms.getAllVertices(G));
        ArrayList<TVertex> resultList = new ArrayList<>();

        for (TVertex root : vertices) {
            if (visited[root.id()] == false) {
                vertexQueue.add(root);
                visited[root.id()] = true;

                TVertex current = root;

                while (!vertexQueue.isEmpty()) {
                    current = vertexQueue.remove();
                    resultList.add(current);

                    for (TVertex child : G.incidentVertices(current)) {
                        if (!visited[child.id()]) {
                            vertexQueue.add(child);
                            visited[child.id()] = true;

                        }
                    }
                }
            }
        }
        Collections.reverse(resultList);

        return resultList;
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

    protected static <TVertex extends Vertex<V>, V, E> void depthFirstComponent(IGraph<TVertex, V, E> graph, TVertex v, ArrayList<TVertex> component, ArrayList<ArrayList<TVertex>> components) {
        if (components.get(graph.getId(v)) == null) {
            component.add(v);
            components.set(graph.getId(v), component);
            for (TVertex neighbor : graph.incidentVertices(v)) {
                depthFirstComponent(graph, neighbor, component, components);
            }
        }
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<ArrayList<TVertex>> connectedComponents(IGraph<TVertex, V, E> graph) {
        ArrayList<ArrayList<TVertex>> components = new ArrayList<>(graph.numVertices());
        for (TVertex v : graph.vertices()) {
            components.add(null);
        }
        for (TVertex v : graph.vertices()) {
            if (components.get(graph.getId(v)) == null) {
                ArrayList<TVertex> component = new ArrayList<TVertex>();
                depthFirstComponent(graph, v, component, components);
            }
        }
        return components;
    }

    public static <TVertex extends Vertex<V>, V, E> ArrayList<PosSubSet<TVertex>> getNeighbourHoods(IGraph<TVertex, V, E> graph) {
        PosSet<TVertex> all = new PosSet<>(graph.vertices());
        ArrayList<PosSubSet<TVertex>> nodeHoods = new ArrayList<>();
        for (TVertex v : graph.vertices()) {
            PosSubSet<TVertex> hood = new PosSubSet<>(all, graph.incidentVertices(v));
            nodeHoods.add(hood);
        }
        return nodeHoods;
    }

    public static <TVertex extends Vertex<V>, V, E> Collection<TVertex> getAllVertices(IGraph<TVertex, V, E> graph) {
        ArrayList<TVertex> vertices = new ArrayList<>();
        for (TVertex v : graph.vertices()) {
            vertices.add(v);
        }
        return vertices;
    }
}
