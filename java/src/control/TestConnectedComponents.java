package control;

import graph.AdjacencyListGraph;
import graph.SubsetGraph;
import graph.Vertex;
import io.DiskGraph;

import java.util.ArrayList;

/**
 * Created by emh on 5/22/14.
 */
public class TestConnectedComponents {

    public static void main(String[] args) {

        //String fileName = ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs";
        String fileName = ControlUtil.GRAPHLIB_OURS + "test/disconnected.dimacs";
        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);

        /*ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);
        */
        ArrayList<SubsetGraph<Vertex<Integer>, Integer, String>> components = graph.connectedComponents();
        System.out.println(components.size());

        /*
        int bw = CutBool.countNeighborhoods(bigraph);
        System.out.printf("exact %d\n", bw);
        long est = MISBackTrack.countNeighborhoods(bigraph);
        System.out.printf("estimate %d", est);
        */

    }
}
