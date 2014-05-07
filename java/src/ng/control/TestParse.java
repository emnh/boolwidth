package ng.control;

import control.ControlUtil;
import ng.lib.SimpleGraph;
import ng.lib.SimpleNode;
import ng.lib.Util;

/**
 * Created by emh on 4/26/2014.
 */
public class TestParse {

    public static void main(String[] args) {
        String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        String data = Util.readFile(fileName);
        SimpleGraph sg = SimpleGraph.parseDimacs(data);
        /*for (SimpleNode node : sg.nodes) {
            System.out.println(node.neighbors);
        }*/
    }

}
