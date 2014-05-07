package ng.lib;

import java.util.ArrayList;

/**
 * Created by emh on 4/26/2014.
 */
public class SimpleNode {
    public int index;
    public String label;
    public ArrayList<Integer> neighbors;

    public SimpleNode(int index) {
        this.index = index;
        this.label = "";
        neighbors = new ArrayList<>();
    }
}