package control;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;

/**
 * Created by emh on 27.12.2014.
 */
public class DynamicForestGSTest {
    public static void main(String[] args) {

        Graph graph = new DefaultGraph("CC Test");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("AC", "A", "C");

        ConnectedComponents cc = new ConnectedComponents();
        cc.init(graph);
        cc.setCutAttribute("Hide");

        System.out.printf("%d connected component(s) in this graph, so far.%n",
                cc.getConnectedComponentsCount());

        //graph.removeEdge("AC");
        graph.removeNode("B");

        graph.getEdge("AC").setAttribute("Hide");

        System.out.printf("Eventually, there are %d.%n", cc
                .getConnectedComponentsCount());

        graph.getEdge("AC").removeAttribute("Hide");

        System.out.printf("Eventually, there are %d.%n", cc
                .getConnectedComponentsCount());

    }
}
