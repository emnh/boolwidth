package boolwidth.greedysearch;

import boolwidth.CutBool;
import boolwidth.cutbool.CutBoolComparatorCCMIS;
import com.github.krukow.clj_lang.PersistentVector;
import graph.BiGraph;
import graph.Vertex;
import interfaces.IGraph;
import sadiasrc.decomposition.CCMIS;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by emh on 11/2/2014.
 */
public class BaseDecompose {

    private IGraph<Vertex<Integer>, Integer, String> graph;
    private HashMap<HashSet<Integer>, Long> cache = new HashMap<>();
    private Random rnd = new Random();
    public long cacheHits = 0;
    public long cutboolTotalCalls = 0;
    long start;
    long oldPrint;
    static final int PRINT_INTERVAL = 1000;

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

    public long getFunkyBooleanWidth(ImmutableBinaryTree ibt) {
        final long[] maxCutBool = {0};

        ibt.dfs((parent, node) -> {
            HashSet<Integer> lefts = new HashSet<>(ibt.getChildren(parent, node));
            long cutbool = getFunkyCutBool(ibt, lefts);
            if (cutbool > maxCutBool[0]) {
                maxCutBool[0] = cutbool;
            }
        });

        return maxCutBool[0];
    }

    public long getBooleanWidth(ImmutableBinaryTree ibt) {
        final long[] maxCutBool = {0};

        ibt.dfs((parent, node) -> {
            HashSet<Integer> vertexIDs = new HashSet<>(ibt.getChildren(parent, node));
            long cutbool = getCutBool(vertexIDs);
            if (cutbool > maxCutBool[0]) {
                maxCutBool[0] = cutbool;
            }
        });

        return maxCutBool[0];
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

    public ImmutableBinaryTree trickle(ImmutableBinaryTree ibt, SimpleNode parent, SimpleNode currentNode, Vertex<Integer> v) {

        ArrayList<SimpleNode> neighbours = new ArrayList<SimpleNode>(ibt.getNeighbours(currentNode));
        if (neighbours.size() < 3) {
            ibt = ibt.addChild(currentNode, v.id());
            return ibt;
        }
        if (parent != null) neighbours.remove(parent);

        long minFBCool = Long.MAX_VALUE;
        SimpleNode minFBNode = null;

        for (SimpleNode n : neighbours) {
            HashSet<Integer> lefts = new HashSet<>(ibt.getChildren(currentNode, n));
            lefts.add(v.id());
            long fbcool = getCutValueForTrickle(ibt, lefts);
            if (fbcool < minFBCool) {
                minFBCool = fbcool;
                minFBNode = n;
            }
        }
        ibt = trickle(ibt, currentNode, minFBNode, v);

        ibt = trickleReRoot(ibt, parent);
        return ibt;
    }

    protected ImmutableBinaryTree trickleReRoot(ImmutableBinaryTree ibt, SimpleNode parent) {
        if (parent == null) {
            SimpleNode maxCut = getMaxCut(ibt);
            assert maxCut != null;
            //System.out.printf("rerooting: %s\n", maxCut != ibt.getRoot());
            ibt = ibt.reRoot(maxCut);
        }
        return ibt;
    }

    public ImmutableBinaryTree retrickleRandom(ImmutableBinaryTree ibt) {
        ArrayList<Integer> ids = new ArrayList<>(ibt.getAllChildren());
        int pick = ids.get(rnd.nextInt(ids.size()));
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), graph.getVertex(pick));
        if (getBooleanWidth(ibt) > getBooleanWidth(oldIbt)) {
        //if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            //System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    public ImmutableBinaryTree retrickleGiven(ImmutableBinaryTree ibt, int externalID) {
        int pick = externalID;
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), graph.getVertex(pick));
        if (getBooleanWidth(ibt) > getBooleanWidth(oldIbt)) {
        //if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            //System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        graph.vertices().forEach((node) -> list.add(node));
        return decompose(list);
    }

    public void rateLimitedPrint(Consumer<Long> print) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - oldPrint > PRINT_INTERVAL) {
            print.accept(System.currentTimeMillis() - getStart());
            oldPrint = System.currentTimeMillis();
        }
    }

    public ImmutableBinaryTree decompose(ArrayList<Vertex<Integer>> vertices) {
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        // doesn't seem to have much impact, leave out while debugging for deterministic results
        // Collections.shuffle(vertices);

        TreeMap<Double, Vertex<Integer>> troubleMakers = new TreeMap<>();

        int i = 0;
        long bw = 1, oldbw = 1;
        double ratio = 0;

        for (Vertex<Integer> v : vertices) {
            i += 1;

            ibt = trickle(ibt, null, ibt.getRoot(), v);
            bw = getBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;

            final int print_i = i;
            final long print_bw = bw;
            final double print_ratio = ratio;
            rateLimitedPrint((time) ->
                System.out.printf("time: %d, trickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                        time, v.id(), print_i, graph.numVertices(),
                        getLogBooleanWidth(print_bw), print_bw, print_ratio));

            for (int j = 0; j < ibt.getAllChildren().size() / 10; j++) {
                ibt = retrickleRandom(ibt);
            }
            bw = getBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;

            final long print_bw2 = bw;
            final double print_ratio2 = ratio;
            rateLimitedPrint((time) ->
                System.out.printf("time: %d, retrickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                        time, v.id(), print_i, graph.numVertices(),
                        getLogBooleanWidth(print_bw2), print_bw2, print_ratio2));
        }
        return ibt;
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
