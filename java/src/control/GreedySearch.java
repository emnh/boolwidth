package control;

import boolwidth.greedysearch.*;
import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.FixedOrderingDecompose;
import boolwidth.greedysearch.base.StackDecompose;
import boolwidth.greedysearch.base.TrickleDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.growNeighbourHood.GrowNeighbourHoodDecompose;
import boolwidth.greedysearch.base.LocalSearch;
import boolwidth.greedysearch.memory.MemoryDecompose;
import boolwidth.greedysearch.spanning.*;
import boolwidth.greedysearch.symdiff.SymDiffDecompose;
import boolwidth.greedysearch.treewidth.TreeWidthGreedyFillinDecompose;
import graph.Vertex;
import interfaces.IGraph;
import com.cedarsoftware.util.io.JsonWriter;
import io.DiskGraph;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
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

    public static ArrayList<String> getUnbeatFileNames() throws IOException {
        ArrayList<String> fileNames = new ArrayList<>();

        fileNames.add(DiskGraph.getMatchingGraph("**rl1323.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**d657.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**rd400.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pr1002.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**graph12pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**u574.tsp.dgf"));

        fileNames.add(DiskGraph.getMatchingGraph("**munin2.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**munin_kgo_complete.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pigs.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_44.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_45.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**munin3.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**pigs-wpp.dgf"));


        fileNames.add(DiskGraph.getMatchingGraph("**pr226.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**link-wpp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**link-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**link.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**p654.tsp-pp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**p654.tsp.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_46.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_42.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**BN_43.dgf"));
        fileNames.add(DiskGraph.getMatchingGraph("**munin2-wpp.dgf"));



        fileNames.add(DiskGraph.getMatchingGraph("**fl417.tsp-pp.dgf"));

        fileNames.add(DiskGraph.getMatchingGraph("**BN_42-pp.dgf"));
        return fileNames;
    }

    public static void processFiles(ArrayList<String> fileNames, Function<IGraph<Vertex<Integer>, Integer, String>, BaseDecompose> getDecomposer) throws IOException {
        ArrayList<String> results = new ArrayList<>();
        for (String file : fileNames) {
            System.out.printf("processing: %s\n", file);

            IGraph<Vertex<Integer>, Integer, String> graph;
            graph = ControlUtil.getTestGraph(file);
            BaseDecompose gd = getDecomposer.apply(graph);
            JSONObject result = processGraph(file, graph, gd);

            String resultStr = result.toString();
            System.out.printf("result: %s\n", resultStr); // for parsing
            results.add(resultStr);

            System.out.println("");
            for (String result2 : results) {
                System.out.printf("result: %s\n", result2); // for parsing
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = DiskGraph.getMatchingGraph("**d493.tsp.dgf");
        //String fileName = DiskGraph.getMatchingGraph("**u574.tsp.dgf");
        String fileName = DiskGraph.getMatchingGraph("**munin_kgo_complete.dgf");

        String cls = "";
        if (args.length > 0) {
            cls = "boolwidth.greedysearch." + args[0];
            fileName = args[1];
        }
        System.out.printf("filename: %s\n", fileName);
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        BaseDecompose gd = null;

        if (cls != "") {
            Class<?> clazz = Class.forName(cls);
            Constructor<?> ctor = clazz.getConstructor(IGraph.class);
            gd = (BaseDecompose) ctor.newInstance(new Object[]{graph});
        } else {
            switch (-7) {
                case -9:
                    long minbw = Long.MAX_VALUE;
                    for (int i = 0; i < 100; i++ ) {
                        gd = new GreedyMergeDecompose(graph);
                        ImmutableBinaryTree ibt = gd.decompose();
                        long bw = gd.getBooleanWidth(ibt, minbw);
                        System.out.printf("bw: %.2f\n", gd.getLogBooleanWidth(bw));
                        if (bw != gd.UPPER_BOUND_EXCEEDED && bw < minbw) {
                            minbw = bw;
                            System.out.printf("minbw: %.2f\n", gd.getLogBooleanWidth(minbw));
                        }
                    }
                    return;
                    //gd = new GreedyMergeDecompose(graph);
                    //break;
                    //processFiles(getUnbeatFileNames(), (g) -> new GreedyMergeDecompose(g));
                    //return;
                case -8:
                    gd = new TrickleDecompose(graph);
                    break;
                case -7:
                    gd = new TreeWidthGreedyFillinDecompose(graph);
                    break;
                    //processFiles(getUnbeatFileNames(), (g) -> new TreeWidthGreedyFillinDecompose(g));
                    //return;
                case -6:
                    gd = new GreedyMergeDecompose(graph);
                    break;
                case -5:
                    //gd = new SpanningTreeDecompose(graph);
                    //gd = new SpanningTreeCostSymDiffDecompose(graph);
                    //gd = new SpanningTreeComponentDecompose(graph);
                    gd = new SpanningTreeComponentAverageDecompose(graph);
                    //processFiles(getLargeFileNames(), (g) -> new SpanningTreeComponentAverageDecompose(g));
                    //return;
                    break;
                case -4:
                    gd = new GrowNeighbourHoodDecompose(graph);
                    break;
                case -3:
                    gd = new SymDiffDecompose(graph);
                    break;
                case -2:
                    gd = new SpanningTreeDecompose(graph);
                    break;
                case -1:
                    processFiles(getLargeFileNames(), (g) -> new SpanningTreeComponentDecompose(g));
                    return;
                case 0:
                    gd = new FixedOrderingDecompose(graph);
                    break;
                case 1:
                    gd = new RandomDecompose(graph);
                    break;
                case 2:
                    //gd = new GreedyMergeDecompose(graph);
                    //break;
                    processFiles(getLargeFileNames(), (g) -> new GreedyMergeDecompose(g));
                    return;
                case 3:
                    gd = new ThreeWayDecompose(graph);
                    break;
                case 4:
                    gd = new MemoryDecompose(graph);
                    break;
                case 5:
                    gd = new StackDecompose(graph);
                    break;
                case 7:
                    processFiles(getSmallFileNames(), (g) -> new SymDiffDecompose(g));
                    return;
                case 8:
                    processFiles(getLargeFileNames(), (g) -> new GrowNeighbourHoodDecompose(g));
                    return;
                case 9:
                    processFiles(getSmallFileNames(), (g) -> new StackDecompose(g));
                    return;
            }
        }

        processGraph(fileName, graph, gd);

        /*if (args.length == 0) {
            HTTPResultsServer hrServer = new HTTPResultsServer();
            hrServer.addResult("decomposition", jsonDecomposition);
            hrServer.addResult("result", result);
        }*/
        //hrServer.openBrowser("static/decomposition.html");
    }

    private static JSONObject processGraph(String fileName, IGraph<Vertex<Integer>, Integer, String> graph, BaseDecompose gd) throws IOException {
        long decomposeStart = System.currentTimeMillis();
        final ImmutableBinaryTree ibt = gd.decompose();
        long decomposeEnd = System.currentTimeMillis();

        boolean valid = gd.validateDecomposition(ibt);

        System.out.println("computing boolean width");
        long computeWidthStart = System.currentTimeMillis();
        boolean overflow = false;
        long bw = 0;
        try {
            bw = gd.getBooleanWidth(ibt);
        } catch (ArithmeticException e) {
            overflow = true;
        }
        long computeWidthEnd = System.currentTimeMillis();

        ImmutableBinaryTree ibt2 = ibt;
        System.out.printf("bw: %.2f, improving with local search\n", gd.getLogBooleanWidth(bw));
        LocalSearch ls = new LocalSearch(graph);
        ibt2 = ls.improve(ibt, gd);
        valid = ls.validateDecomposition(ibt2);

        System.out.println("computing boolean width of LS decomposition");
        computeWidthStart = System.currentTimeMillis();
        overflow = false;
        bw = 0;
        try {
            bw = gd.getBooleanWidth(ibt2);
        } catch (ArithmeticException e) {
            overflow = true;
        }
        computeWidthEnd = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        result.put("valid", valid);
        result.put("cacheHits", (double) gd.cacheHits / gd.cutboolTotalCalls);
        result.put("decomposeTime", decomposeEnd - decomposeStart);
        result.put("computeWidthTime", computeWidthEnd - computeWidthStart);
        if (overflow) {
            result.put("booleanWidth", "overflow");
            result.put("2^booleanWidth", "overflow");
        } else {
            result.put("booleanWidth", BaseDecompose.getLogBooleanWidth(bw));
            result.put("2^booleanWidth", bw);
        }
        result.put("graph", fileName);
        result.put("v", graph.numVertices());
        result.put("e", graph.numEdges());
        if (ibt.creatorName != null) {
            result.put("heuristic", ibt.creatorName);
        } else {
            result.put("heuristic", gd.getClass().getName());
        }
        final BaseDecompose gd2 = gd;

        final boolean overflow2 = overflow;
        JSONObject jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
            if (node != ibt.getRoot()) {
                if (!overflow2) {
                    obj.put("cutBool", gd2.getCutBool(ibt.getChildren(parent, node)));
                }
            }
        });

        //System.out.println(JsonWriter.formatJson(jsonDecomposition.toString()));
        System.out.printf("result: %s\n", result.toString()); // for parsing
        System.out.println(JsonWriter.formatJson(result.toString()));

        return result;
    }
}
