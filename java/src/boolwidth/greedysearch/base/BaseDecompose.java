package boolwidth.greedysearch.base;

import boolwidth.CutBool;
import boolwidth.cutbool.CutBoolComparatorCCMIS;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.growNeighbourHood.SplitGrowNeighbourhood;
import boolwidth.opencl.JOCLOpenCLCutBoolComputer;
import graph.BiGraph;
import graph.Vertex;
import interfaces.IGraph;
import sadiasrc.decomposition.CCMIS;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

/**
 * Created by emh on 11/2/2014.
 */
public class BaseDecompose {

    private IGraph<Vertex<Integer>, Integer, String> graph;
    private HashMap<HashSet<Integer>, Long> cache = new HashMap<>();
    private HashMap<HashSet<Integer>, Long> approxCache = new HashMap<>();
    protected Random rnd = new Random();
    public long cacheHits = 0;
    public long cutboolTotalCalls = 0;
    long start;
    long oldPrint;
    static final int PRINT_INTERVAL = 1000;
    static final int SAMPLE_COUNT = 100;

    public BaseDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        this.graph = graph;
        start = System.currentTimeMillis();
        oldPrint = start - PRINT_INTERVAL;
    }

    public long getStart() {
        return start;
    }

    public IGraph<Vertex<Integer>, Integer, String> getGraph() {
        return graph;
    }

    public long getFunkyCutBool(ImmutableBinaryTree ibt, HashSet<Integer> lefts) {
        HashSet<Integer> rights = new HashSet<>(ibt.getAllChildren());
        rights.removeAll(lefts);
        return getCutBool(lefts, rights);
    }

    public long getCutBool(Collection<Integer> leftIDs, Collection<Integer> rightIDs) {
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        for (Integer id : leftIDs) {
            lefts.add(graph.getVertex(id));
        }
        ArrayList<Vertex<Integer>> rights = new ArrayList<>();
        for (Integer id : rightIDs) {
            rights.add(graph.getVertex(id));
        }
        BiGraph<Integer, String> bg = new BiGraph<Integer, String>(lefts, rights, graph);
        //long cb = CCMIS.BoolDimBranch(CutBoolComparatorCCMIS.convertSadiaBiGraph(bg));
        long cb = CutBool.countNeighborhoods(bg);
        return cb;
    }

    public HashSet<Integer> verticesToInts(Collection<Vertex<Integer>> vertices) {
        HashSet<Integer> set = new HashSet<>();
        for (Vertex<Integer> vertex : vertices) {
            set.add(vertex.id());
        }
        return set;
    }

    public long getCutBool(Collection<Vertex<Integer>> vertices, boolean signature_flag) {
        HashSet<Integer> set = new HashSet<>();
        for (Vertex<Integer> vertex : vertices) {
            set.add(vertex.id());
        }
        return getCutBool(set);
    }

    public long getCutBool(Collection<Integer> vertexIDs) {
        cutboolTotalCalls++;
        if (vertexIDs.size() == 0) {
            return 1;
        }
        HashSet<Integer> vids = new HashSet<>(vertexIDs);
        if (cache.containsKey(vids)) {
            cacheHits++;
            return cache.get(vids);
        }
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        for (Integer id : vertexIDs) {
            lefts.add(graph.getVertex(id));
        }
        BiGraph<Integer, String> bg = new BiGraph<>(lefts, graph);
        //long cb = CutBool.countNeighborhoods(bg);
        long cb = CCMIS.BoolDimBranch(CutBoolComparatorCCMIS.convertSadiaBiGraph(bg));
        cache.put(vids, cb);
        return cb;
    }

    public long getApproximateCutBool(Collection<Integer> vertexIDs) {
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        for (Integer id : vertexIDs) {
            lefts.add(graph.getVertex(id));
        }
        HashSet<Integer> vids = new HashSet<>(vertexIDs);
        if (approxCache.containsKey(vids)) {
            return approxCache.get(vids);
        }
        BiGraph<Integer, String> bg = new BiGraph<>(lefts, graph);
        //long cb = CBBackTrackEstimateBinaryFast.estimateNeighborhoods(bg, SAMPLE_COUNT);
        long cb = JOCLOpenCLCutBoolComputer.estimateNeighbourHoods(bg, SAMPLE_COUNT);
        approxCache.put(vids, cb);
        return cb;
    }

    public long getBooleanWidth(ImmutableBinaryTree ibt, ToLongFunction<HashSet<Integer>> fgetCutBool) {
        final long[] maxCutBool = {0};

        ibt.dfs((parent, node) -> {
            HashSet<Integer> vertexIDs = new HashSet<>(ibt.getChildren(parent, node));
            long cutbool = fgetCutBool.applyAsLong(vertexIDs);
            System.out.printf("got cutbool: %d, bw: %d\n", vertexIDs.size(), cutbool);
            if (cutbool > maxCutBool[0]) {
                maxCutBool[0] = cutbool;
            }
        });

        return maxCutBool[0];
    }

    public long getFunkyBooleanWidth(ImmutableBinaryTree ibt) {
        return getBooleanWidth(ibt, (lefts) -> getFunkyCutBool(ibt, lefts));
    }

    public long getBooleanWidth(ImmutableBinaryTree ibt) {
        return getBooleanWidth(ibt, (lefts) -> getCutBool(lefts));
    }

    public long getApproximateBooleanWidth(ImmutableBinaryTree ibt) {
        return getBooleanWidth(ibt, (lefts) -> getApproximateCutBool(lefts));
    }

    public static double getLogBooleanWidth(long bw) {
        return Math.log(bw) / Math.log(2);
    }

    public SimpleNode getMaxCut(ImmutableBinaryTree ibt) {
        final long[] maxCutBool = {0};
        final SimpleNode[] id = {null};

        ibt.dfs((parent, node) -> {
            HashSet<Integer> vertexIDs = new HashSet<>(ibt.getChildren(parent, node));
            long cutbool = getCutBool(vertexIDs);
            if (cutbool > maxCutBool[0]) {
                maxCutBool[0] = cutbool;
                id[0] = node;
            }
        });

        return id[0];
    }

    public SimpleNode getFunkyMaxCut(ImmutableBinaryTree ibt) {
        final long[] maxCutBool = {0};
        final SimpleNode[] id = {null};

        ibt.dfs((parent, node) -> {
            HashSet<Integer> vertexIDs = new HashSet<>(ibt.getChildren(parent, node));
            long cutbool = getFunkyCutBool(ibt, vertexIDs);
            if (cutbool > maxCutBool[0]) {
                maxCutBool[0] = cutbool;
                id[0] = node;
            }
        });

        return id[0];
    }

    public long getCutValueForTrickle(ImmutableBinaryTree ibt, HashSet<Integer> lefts) {
        //return getFunkyCutBool(ibt, lefts);
        return getCutBool(lefts);
    }

    public ImmutableBinaryTree decompose() {
        throw new UnsupportedOperationException("abstract method: decompose");
    }

    public void rateLimitedPrint(Consumer<Long> print) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - oldPrint > PRINT_INTERVAL) {
            print.accept(System.currentTimeMillis() - getStart());
            oldPrint = System.currentTimeMillis();
        }
    }

    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new Split(depth, decomposition, rights);
    }

    public boolean validateDecomposition(ImmutableBinaryTree ibt) {

        HashSet<Integer> children = new HashSet<>(ibt.getAllChildren());
        //children.remove(ImmutableBinaryTree.EMPTY_NODE);
        HashSet<Integer> vertexIds = new HashSet<>();
        for (Vertex<Integer> v : graph.vertices()) {
            vertexIds.add(v.id());
        }

        final boolean[] valid = {vertexIds.equals(children)};
        if (!valid[0]) {
            System.out.println("vertexIDs: " + vertexIds);
            System.out.println("children: " + children);
        }

        ibt.dfs((parent, node) -> {
            HashSet<Integer> dfsparent = new HashSet<>(ibt.getChildren(parent, node));
            dfsparent.remove(ibt.getExternalID(node));
            HashSet<Integer> dfschildren = new HashSet<>();
            for (SimpleNode n : ibt.getNeighbours(node)) {
                if (n != parent) {
                    dfschildren.addAll(ibt.getChildren(node, n));
                }
            }
            valid[0] = valid[0] && dfschildren.equals(dfsparent);
            if (!dfschildren.equals(dfsparent)) {
                System.out.println("dfsparent: " + dfsparent);
                System.out.println("dfschildren: " + dfschildren);
                //dfsparent.removeAll(dfschildren);
                //System.out.println("diff: " + dfsparent);
            }
        });

        return valid[0];
    }
}
