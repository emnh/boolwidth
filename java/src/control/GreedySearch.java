package control;

import boolwidth.greedysearch.*;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import control.http.HTTPResultsServer;
import graph.Vertex;
import interfaces.IGraph;
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;

import java.util.ArrayList;

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

    public static ArrayList<String> getFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> fileNames2 = new ArrayList<>();

        fileNames.add("prob/alarm.dgf");
        fileNames.add("prob/barley.dgf");
        fileNames.add("prob/pigs-pp.dgf");
        fileNames.add("prob2/BN_100.dgf");
        fileNames.add("delauney/eil76.tsp.dgf");
        fileNames.add("coloring/david.dgf");
        fileNames.add("protein/1jhg_graph.dimacs");
        fileNames.add("protein/1aac_graph.dimacs");
        /*fileNames.add("");
        fileNames.add("");
        fileNames.add("");
        fileNames.add("");
        fileNames.add("");
        fileNames.add("");
        fileNames.add("");*/

        for (String f : fileNames) {
            fileNames2.add(ControlUtil.GRAPHLIB + f);
        }

        return fileNames2;
    }

    public static void processFiles() {
        ArrayList<String> results = new ArrayList<>();
        for (String file : getFileNames()) {
            IGraph<Vertex<Integer>, Integer, String> graph;
            graph = ControlUtil.getTestGraph(file);
            BaseDecompose gd = new SymDiffDecompose(graph);
            ImmutableBinaryTree ibt = gd.decompose();
            long bw = gd.getBooleanWidth(ibt);
            String result = String.format("%s: bw: %.2f", file, BaseDecompose.getLogBooleanWidth(bw));
            results.add(result);
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        /*test();
        System.exit(1);
        */
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen6_6.dgf";
        String fileName = ControlUtil.GRAPHLIB + "coloring/queen16_16.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen7_7.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen16_16.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen11_11.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/myciel7.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_65.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/homer.dgf";

        //String fileName = ControlUtil.GRAPHLIB + "prob/alarm.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/david.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.1.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/link.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph02-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph04-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph07-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/a280.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/pr439.tsp.dgf";

        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_26.dgf";
        if (args.length > 0) {
            fileName = args[0];
        }
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        BaseDecompose gd = null;

        switch (6) {
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
            case 4:
                gd = new MemoryDecompose(graph);
                break;
            case 5:
                gd = new TwoStepsForthOneBackDecompose(graph);
                break;
            case 6:
                gd = new SymDiffDecompose(graph);
                break;
            case 7:
                processFiles();
                return;
        }

        long decomposeStart = System.currentTimeMillis();
        final ImmutableBinaryTree ibt = gd.decompose();
        //final ImmutableBinaryTree ibt = gd.decomposeTopCut3Way();

        long decomposeEnd = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        result.put("valid", gd.validateDecomposition(ibt));
        System.out.println("computing boolean width");
        long computeWidthStart = System.currentTimeMillis();
        long bw = gd.getApproximateBooleanWidth(ibt);
        long computeWidthEnd = System.currentTimeMillis();
        result.put("cache hits", (double) gd.cacheHits / gd.cutboolTotalCalls);
        result.put("decompose time", decomposeEnd - decomposeStart);
        result.put("compute width time", computeWidthEnd - computeWidthStart);
        result.put("boolean-width", BaseDecompose.getLogBooleanWidth(bw));
        result.put("2^boolean-width", bw);
        final BaseDecompose gd2 = gd;
        JSONObject jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
            if (node != ibt.getRoot()) {
                obj.put("cutbool", gd2.getApproximateCutBool(ibt.getChildren(parent, node)));
            }
        });

        //System.out.println(JsonWriter.formatJson(jsonDecomposition.toString()));
        System.out.println(JsonWriter.formatJson(result.toString()));

        HTTPResultsServer hrServer = new HTTPResultsServer();
        hrServer.addResult("decomposition", jsonDecomposition);
        hrServer.addResult("result", result);
        //hrServer.openBrowser("static/decomposition.html");
    }
}
