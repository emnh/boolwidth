package graph;

import com.github.krukow.clj_lang.PersistentHashSet;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

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

    public static <TVertex extends Vertex<V>, V, E> ArrayList<TVertex> BFSAll(IGraph<TVertex, V, E> G, TVertex root)
    {
        boolean[] visited = new boolean[G.numVertices()];
        Queue<TVertex> vertexQueue =  new LinkedList<TVertex>();
        vertexQueue.add(root);
        visited[root.id()] = true;
        ArrayList<TVertex> resultList = new ArrayList<>();

        TVertex current = root;

        while(!vertexQueue.isEmpty())
        {
            current = vertexQueue.remove();
            resultList.add(current);

            for(TVertex child : G.incidentVertices(current))
            {
                if(!visited[child.id()])
                {
                    vertexQueue.add(child);
                    visited[child.id()] = true;

                }
            }
        }
        Collections.reverse(resultList);

        return resultList;
    }
}
