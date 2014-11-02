package control;

import boolwidth.greedysearch.*;
import control.http.HTTPResultsServer;
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
        String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen11_11.dgf";

        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.1.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/link.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph02-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph04-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph07-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/a280.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_26.dgf";
        if (args.length > 0) {
            fileName = args[0];
        }
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        BaseDecompose gd = null;

        switch (2) {
            case 0:
                gd = new BaseDecompose(graph);
                break;
            case 1:
                gd = new RandomDecompose(graph);
                break;
            case 2:
                gd = new TwoWayDecompose(graph);
                break;
            case 3:
                gd = new ThreeWayDecompose(graph);
                break;
        }

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
        result.put("boolean-width", BaseDecompose.getLogBooleanWidth(bw));
        result.put("2^boolean-width", bw);
        final BaseDecompose gd2 = gd;
        JSONObject jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
            if (node != ibt.getRoot()) {
                obj.put("cutbool", gd2.getCutBool(ibt.getChildren(parent, node)));
            }
        });

        System.out.println(JsonWriter.formatJson(jsonDecomposition.toString()));
        System.out.println(JsonWriter.formatJson(result.toString()));

        HTTPResultsServer hrServer = new HTTPResultsServer();
        hrServer.addResult("decomposition", jsonDecomposition);
        hrServer.addResult("result", result);
    }
}
