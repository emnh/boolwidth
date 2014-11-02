package control;

import boolwidth.greedysearch.*;
import graph.Vertex;
import interfaces.IGraph;
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;

public class GreedySearch {

    public static void test() {
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        SimpleNode root = ibt.getRoot();
        ibt = ibt.addChild(root, 2);
        SimpleNode child = ibt.getReference();
        ibt = ibt.addChild(root, 3);
        ibt = ibt.addChild(child, 4);
        ibt = ibt.addChild(child, 5);
        final ImmutableBinaryTree ibt2 = ibt;
        ibt2.dfs((parent, node) ->
            System.out.printf("%s (parent=%s): %s\n", node, parent, ibt2.getChildren(parent, node))
        );
        System.out.println(ibt.toJSON());
    }

    /*public Decomposition<Vertex<Integer>, Integer, String> oldStyleDecomposition() {

    }*/

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {

        /*test();
        System.exit(1);
        */
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen6_6.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen7_7.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen11_11.dgf";

        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.1.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/link.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph02-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph04-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph07-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        String fileName = ControlUtil.GRAPHLIB + "delauney/a280.tsp.dgf";
        if (args.length > 0) {
            fileName = args[0];
        }
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        //BaseDecomposition gd = new BaseDecomposition(graph);
        //BaseDecomposition gd = new TwoWayDecomposition(graph);
        BaseDecomposition gd = new ThreeWayDecomposition(graph);
        //BaseDecomposition gd = new RandomDecomposition(graph);

        long decomposeStart = System.currentTimeMillis();
        final ImmutableBinaryTree ibt = gd.decompose();
        //final ImmutableBinaryTree ibt = gd.decomposeTopCut3Way();

        long decomposeEnd = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        result.put("valid", gd.validateDecomposition(ibt));
        System.out.println("computing boolean width");
        long computeWidthStart = System.currentTimeMillis();
        long bw = gd.getBooleanWidth(ibt);
        long computeWidthEnd = System.currentTimeMillis();
        result.put("cache hits", (double) gd.cacheHits / gd.cutboolTotalCalls);
        result.put("decompose time", decomposeEnd - decomposeStart);
        result.put("compute width time", computeWidthEnd - computeWidthStart);
        result.put("boolean-width", BaseDecomposition.getLogBooleanWidth(bw));
        result.put("2^boolean-width", bw);
        String jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
            if (node != ibt.getRoot()) {
                obj.put("cutbool", gd.getCutBool(ibt.getChildren(parent, node)));
            }
        }).toString();

        System.out.println(JsonWriter.formatJson(jsonDecomposition));
        System.out.println(JsonWriter.formatJson(result.toString()));
    }
}
