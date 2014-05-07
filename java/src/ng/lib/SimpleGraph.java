package ng.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by emh on 4/26/2014.
 *
 * Maintains both adjacency list and bipartite graph.
 */

public class SimpleGraph {
    public ArrayList<SimpleNode> nodes = new ArrayList<SimpleNode>();

    public static SimpleGraph parseDimacs(String data) {
        String[] lines = data.split("\\r?\\n");
        Pattern pat = Pattern.compile("^e ([\\d]+) ([\\d]+)");
        SimpleGraph sg = new SimpleGraph();
        HashSet<Integer> nodeLabels = new HashSet<>();

        ArrayList<STuple<Integer>> edges = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = pat.matcher(line);
            if (matcher.matches()) {
                int n1 = Integer.parseInt(matcher.group(1));
                int n2 = Integer.parseInt(matcher.group(2));
                nodeLabels.add(n1);
                nodeLabels.add(n2);
                edges.add(new STuple<>(n1, n2));
            }
        }

        HashMap<Integer, Integer> label2index = new HashMap<>();
        int i = 0;
        for (int label : nodeLabels) {
            label2index.put(label, i);
            SimpleNode node = new SimpleNode(i);
            node.label = Integer.toString(label);
            sg.nodes.add(node);
            i++;
        }
        for (STuple<Integer> edge : edges) {
            int leftIndex = label2index.get(edge.left);
            int rightIndex = label2index.get(edge.right);
            sg.nodes.get(leftIndex).neighbors.add(rightIndex);
            sg.nodes.get(rightIndex).neighbors.add(leftIndex);
        }
        return sg;
    }
}