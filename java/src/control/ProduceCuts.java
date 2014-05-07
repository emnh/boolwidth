package control;

import graph.Vertex;
import interfaces.IGraph;
import io.DiskGraph;
import io.GraphViz;

import java.io.File;

/**
 * Created by emh on 5/7/2014.
 */
public class ProduceCuts {
    public static void main(String[] args) throws Exception {

        String path = ControlUtil.GRAPHLIB;

        int i = 0;
        for (File file : DiskGraph.iterateOver(path, true)) {
            //jobManager.execute(new HeuristicJob<V, E>(file));
            i++;
            IGraph<Vertex<Integer>, Integer, String> graph;
            String fileName = file.getAbsolutePath();
            System.out.printf("Processing %s\n", fileName);
            graph = ControlUtil.getTestGraph(fileName);

            

            /*HeuristicTest<Integer, String> ht = new HeuristicTest<Integer, String>();
            ht.doHeuristic(graph);
            if (ht.decomposition != null) {
                GraphViz.saveGraphDecomposition(fileName, graph, ht.bw,
                        ht.decomposition, ht.time);
            }*/
        }
    }
}
