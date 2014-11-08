package control;

import graph.Vertex;
import interfaces.IGraph;
import io.DiskGraph;

import java.io.File;

/**
 * Created by emh on 11/8/2014.
 */
public class TestGraphReader {
    public static void main(String[] args) {
        String path = ControlUtil.GRAPHLIB;

        int i = 0;
        for (File file : DiskGraph.iterateOver(path, true)) {
            boolean success = false;
            try {
                IGraph<Vertex<Integer>, Integer, String> graph;
                graph = ControlUtil.getTestGraph(file.toString());
                success = true;
            } catch (Exception e) {
                System.out.println(e);
            }
            if (!success) {
                System.out.printf("%s parse success: %s\n", file, success);
            }
            i++;
        }
    }
}
