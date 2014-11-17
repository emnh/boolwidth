package control;

import boolwidth.greedysearch.*;
import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.FixedOrderingDecompose;
import boolwidth.greedysearch.base.StackDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.eachSymDiff.EachSymDiffDecompose;
import boolwidth.greedysearch.growNeighbourHood.GrowNeighbourHoodDecompose;
import boolwidth.greedysearch.memory.MemoryDecompose;
import boolwidth.greedysearch.reorder.ExperimentalDecompose;
import boolwidth.greedysearch.spanning.SpanningTreeAllDecompose;
import boolwidth.greedysearch.spanning.SpanningTreeDecompose;
import boolwidth.greedysearch.symdiff.SymDiffDecompose;
import control.http.HTTPResultsServer;
import graph.Vertex;
import interfaces.IGraph;
import com.cedarsoftware.util.io.JsonWriter;
import io.DiskGraph;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

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

    public static ArrayList<String> getSmallFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();
        ArrayList<String> fileNames2 = new ArrayList<>();

        // Small test graphs from Sadia's Thesis
        fileNames.add("prob/alarm.dgf");
        fileNames.add("prob/barley.dgf");
        fileNames.add("prob/pigs-pp.dgf");
        fileNames.add("prob2/BN_100.dgf");
        fileNames.add("delauney/eil76.tsp.dgf");
        fileNames.add("coloring/david.dgf");
        fileNames.add("protein/1jhg_graph.dimacs");
        fileNames.add("protein/1aac_graph.dimacs");
        fileNames.add("freq/celar04-pp.dgf");
        fileNames.add("protein/1a62_graph.dimacs");
        fileNames.add("protein/1bkb_graph-pp.dimacs");
        fileNames.add("coloring/miles250.dgf");
        fileNames.add("coloring/miles1500.dgf");
        fileNames.add("protein/1dd3_graph.dimacs");
        fileNames.add("freq/celar10-pp.dgf");
        fileNames.add("coloring/anna.dgf");
        fileNames.add("delauney/pr152.tsp.dgf");
        fileNames.add("prob/munin2-pp.dgf");
        fileNames.add("coloring/mulsol.i.5.dgf");
        fileNames.add("coloring/zeroin.i.2.dgf");
        fileNames.add("prob/boblo.dgf");
        fileNames.add("coloring/fpsol2.i.1-pp.dgf");
        fileNames.add("prob/munin4-wpp.dgf");

        //fileNames.add("coloring/homer.dgf");

        for (String f : fileNames) {
            fileNames2.add(ControlUtil.GRAPHLIB + f);
        }

        return fileNames2;
    }

    public static ArrayList<String> getLargeFileNames() throws IOException {
        // large graphs from Sadia's thesis
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(DiskGraph.getMatchingGraph("**link-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**diabetes-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**link-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**celar10.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**celar11.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**rd400.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**diabetes.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**fpsol2.i.3.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pigs.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**celar08.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**d493.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**homer.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**rat575.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**u724.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**inithx.i.1.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**munin2.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**vm1084.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_24.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_25.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_23.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_26.dgf"));
        return fileNames;
    }

    public static void processFiles(ArrayList<String> fileNames, Function<IGraph<Vertex<Integer>, Integer, String>, BaseDecompose> getDecomposer) {
        ArrayList<String> results = new ArrayList<>();
        for (String file : fileNames) {
            System.out.printf("processing: %s\n", file);
            IGraph<Vertex<Integer>, Integer, String> graph;
            graph = ControlUtil.getTestGraph(file);
            BaseDecompose gd = getDecomposer.apply(graph);

            long decomposeStart = System.currentTimeMillis();
            ImmutableBinaryTree ibt = gd.decompose();
            long decomposeEnd = System.currentTimeMillis();

            boolean valid = gd.validateDecomposition(ibt);

            System.out.println("computing boolean width");
            long computeWidthStart = System.currentTimeMillis();
            long bw = gd.getBooleanWidth(ibt);
            long computeWidthEnd = System.currentTimeMillis();

            JSONObject result = new JSONObject();
            result.put("valid", valid);
            result.put("cacheHits", (double) gd.cacheHits / gd.cutboolTotalCalls);
            result.put("decomposeTime", decomposeEnd - decomposeStart);
            result.put("computeWidthTime", computeWidthEnd - computeWidthStart);
            result.put("booleanWidth", BaseDecompose.getLogBooleanWidth(bw));
            result.put("2^booleanWidth", bw);
            result.put("graph", file);
            result.put("v", graph.numVertices());
            result.put("e", graph.numEdges());
            result.put("heuristic", gd.getClass().getName());

            String resultStr = result.toString();
            System.out.printf("result: %s\n", resultStr); // for parsing
            results.add(resultStr);
        }
        System.out.println("");
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
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen16_16.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen16_16.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen11_11.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/myciel7.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_65.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/homer.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/alarm.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/david.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.1.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/link.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/link-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/diabetes-wpp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_26.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/celar11.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/rd400.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.3.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.3.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/vm1084.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/u724.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob/diabetes.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph02-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph04-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "freq/graph07-pp.dgf";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/a280.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/pr439.tsp.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "prob2/BN_26.dgf";

        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = DiskGraph.getMatchingGraph("**d493.tsp.dgf");
        String fileName = DiskGraph.getMatchingGraph("**d493.tsp.dgf");

        if (args.length > 0) {
            fileName = args[0];
        }
        System.out.printf("filename: %s\n", fileName);
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        BaseDecompose gd = null;

        switch (2) {
            case -1:
                gd = new ExperimentalDecompose(graph);
                break;
            case 0:
                gd = new FixedOrderingDecompose(graph);
                break;
            case 1:
                gd = new RandomDecompose(graph);
                break;
            case 2:
                gd = new SpanningTreeDecompose(graph);
                break;
            //processFiles(getLargeFileNames(), (g) -> new SpanningTreeAllDecompose(g));
            //return;
            case 3:
                gd = new ThreeWayDecompose(graph);
                break;
            case 4:
                gd = new MemoryDecompose(graph);
                break;
            case 5:
                gd = new StackDecompose(graph);
                break;
            case 6:
                gd = new SymDiffDecompose(graph);
                break;
            case 7:
                processFiles(getSmallFileNames(), (g) -> new SymDiffDecompose(g));
                return;
            case 8:
                gd = new GrowNeighbourHoodDecompose(graph);
                break;
            case 9:
                processFiles(getLargeFileNames(), (g) -> new GrowNeighbourHoodDecompose(g));
                return;
            case 10:
                processFiles(getSmallFileNames(), (g) -> new StackDecompose(g));
                return;
        }

        long decomposeStart = System.currentTimeMillis();
        final ImmutableBinaryTree ibt = gd.decompose();
        long decomposeEnd = System.currentTimeMillis();

        boolean valid = gd.validateDecomposition(ibt);

        System.out.println("computing boolean width");
        long computeWidthStart = System.currentTimeMillis();
        long bw = gd.getBooleanWidth(ibt);
        long computeWidthEnd = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        result.put("valid", valid);
        result.put("cacheHits", (double) gd.cacheHits / gd.cutboolTotalCalls);
        result.put("decomposeTime", decomposeEnd - decomposeStart);
        result.put("computeWidthTime", computeWidthEnd - computeWidthStart);
        result.put("booleanWidth", BaseDecompose.getLogBooleanWidth(bw));
        result.put("2^booleanWidth", bw);
        result.put("graph", fileName);
        result.put("v", graph.numVertices());
        result.put("e", graph.numEdges());
        result.put("heuristic", gd.getClass().getName());
        final BaseDecompose gd2 = gd;
        JSONObject jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
            if (node != ibt.getRoot()) {
                obj.put("cutBool", gd2.getCutBool(ibt.getChildren(parent, node)));
            }
        });

        //System.out.println(JsonWriter.formatJson(jsonDecomposition.toString()));
        System.out.printf("result: %s\n", result.toString()); // for parsing
        System.out.println(JsonWriter.formatJson(result.toString()));

        HTTPResultsServer hrServer = new HTTPResultsServer();
        hrServer.addResult("decomposition", jsonDecomposition);
        hrServer.addResult("result", result);
        //hrServer.openBrowser("static/decomposition.html");
    }
}
