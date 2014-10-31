package control;

import boolwidth.cutbool.CutBoolComparatorCCMIS;
import com.github.krukow.clj_lang.PersistentHashSet;
import com.github.krukow.clj_lang.PersistentVector;
import graph.BiGraph;
import graph.Vertex;
import interfaces.IGraph;
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;
import sadiasrc.decomposition.CCMIS;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by emh on 10/30/2014.
 */
class SimpleNode {
    private int treeID;

    SimpleNode(int treeID) {
        this.treeID = treeID;
    }

    public int getTreeID() {
        return treeID;
    }

    public String toString() {
        return "" + treeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleNode)) return false;

        SimpleNode that = (SimpleNode) o;

        if (treeID != that.treeID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return treeID;
    }
}

class Util {
    public static void help() {

    }
}

interface ToJSONPostProcess {
    void accept(JSONObject obj, SimpleNode parent, SimpleNode node);
}

class ImmutableBinaryTree {

    public static final int EMPTY_NODE = -1;
    private int maxid = 0;
    private SimpleNode reference = null; // pointer to last added
    private SimpleNode root = null;
    private PersistentVector<SimpleNode> nodes = PersistentVector.emptyVector();
    private PersistentVector<PersistentHashSet<SimpleNode>> neighbours = PersistentVector.emptyVector();

    /*
    private PersistentVector<SimpleNode> parents = PersistentVector.emptyVector();
    private PersistentVector<SimpleNode> lefts = PersistentVector.emptyVector();
    private PersistentVector<SimpleNode> rights = PersistentVector.emptyVector();
    */

    private PersistentVector<Integer> externalIDs = PersistentVector.emptyVector();

    public ImmutableBinaryTree() {
    }

    public SimpleNode getReference() {
        return reference;
    }

    private ImmutableBinaryTree add(SimpleNode parent, int externalID) {
        ImmutableBinaryTree result = new ImmutableBinaryTree();

        // new node
        SimpleNode child = new SimpleNode(this.maxid);
        result.maxid = this.maxid + 1;
        result.nodes = this.nodes.cons(child);
        result.root = this.root;

        // child properties
        // add parent as neighbour of child
        if (parent != null) {
            result.neighbours = this.neighbours.cons(PersistentHashSet.create(parent));
        } else {
            result.neighbours = this.neighbours.cons(PersistentHashSet.EMPTY);
        }
        result.externalIDs = this.externalIDs.cons(externalID);

        result.reference = child;
        return result;
    }

    public ImmutableBinaryTree copy() {
        ImmutableBinaryTree result = new ImmutableBinaryTree();
        result.maxid = this.maxid;
        result.nodes = this.nodes;
        result.neighbours = this.neighbours;
        result.externalIDs = this.externalIDs;
        result.root = this.root;
        return result;
    }

    public ImmutableBinaryTree reRoot(SimpleNode node) {
        ImmutableBinaryTree result = copy();
        result.root = node;
        return result;
    }

    public ImmutableBinaryTree addRoot() {
        ImmutableBinaryTree result = add(null, EMPTY_NODE);
        result.root = result.reference;
        return result;
    }

    public ImmutableBinaryTree addChild(SimpleNode parent, int externalID) {
        ImmutableBinaryTree result = add(parent, externalID);

        // add child as neighbor of parent
        result.neighbours =
                result.neighbours.assocN(parent.getTreeID(),
                        result.neighbours.nth(parent.getTreeID()).cons(result.reference));
        return result;
    }

    public ImmutableBinaryTree remove(SimpleNode node) {
        ImmutableBinaryTree result = copy();
        result.externalIDs = this.externalIDs.assocN(node.getTreeID(), EMPTY_NODE);
        return result;
    }

    // TODO: slow, replace with hashmap or something
    public SimpleNode find(int externalId) {
        for (int i = 0; i < externalIDs.size(); i++) {
            if (externalIDs.get(i) == externalId) {
                return nodes.get(i);
            }
        }
        throw new NoSuchElementException();
    }

    public void bfs(Consumer<SimpleNode> action, SimpleNode start) {
        Queue<SimpleNode> queue = new LinkedList<>();
        HashSet<SimpleNode> seen = new HashSet<>();
        queue.add(start);
        seen.add(start);

        while(!queue.isEmpty()) {
            SimpleNode root = queue.remove();

            action.accept(root);
            seen.add(root);

            for (SimpleNode n : getNeighbours(root)) {
                if (!seen.contains(n)) {
                    seen.add(n);
                    queue.add(n);
                }
            }
        }
    }

    public void dfs(BiConsumer<SimpleNode, SimpleNode> action) {
        dfs(new HashSet<>(), null, getRoot(), action);
    }

    /*public void dfs(SimpleNode root,
                    BiConsumer<SimpleNode, SimpleNode> action) {

    }*/

    public void dfs(HashSet<SimpleNode> seen, SimpleNode parent, SimpleNode node,
                    BiConsumer<SimpleNode, SimpleNode> action) {
        seen.add(node);
        action.accept(parent, node);
        //System.out.printf("dfs seen: %s, parent: %s, node: %s, neighbors: %s\n", seen, parent, node, getNeighbours(node));
        for (SimpleNode n : getNeighbours(node)) {
            if (!seen.contains(n)) dfs(seen, node, n, action);
        }
    }

    public Collection<Integer> getAllChildren() {
        return getChildren(null, getRoot());
    }

    public Collection<Integer> getChildren(SimpleNode parent, SimpleNode start) {
        ArrayList<Integer> children = new ArrayList<Integer>();

        Queue<SimpleNode> queue = new LinkedList<>();
        HashSet<SimpleNode> seen = new HashSet<>();
        seen.add(parent);
        seen.add(start);
        if (start != null) queue.add(start);

        while(!queue.isEmpty()) {
            SimpleNode node = queue.remove();

            if (getExternalID(node) != EMPTY_NODE) children.add(getExternalID(node));
            seen.add(node);

            for (SimpleNode n : getNeighbours(node)) {
                if (!seen.contains(n)) {
                    seen.add(n);
                    queue.add(n);
                }
            }
        }
        return children;
    }

    public SimpleNode getRoot()
    {
        return root;
        //return nodes.nth(0);
    }

    public PersistentHashSet<SimpleNode> getNeighbours(SimpleNode root) {
        //System.out.printf("root: %s\n", root);
        return neighbours.nth(root.getTreeID());
    }

    public Integer getExternalID(SimpleNode root) {
        return externalIDs.nth(root.getTreeID());
    }

    public JSONObject toJSON() {
        return toJSON(getRoot());
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON(SimpleNode root) {
        return toJSON(root, (obj, parent, node) -> {});
    }

    public JSONObject toJSON(SimpleNode root, ToJSONPostProcess postProcess) {
        return toJSON(new HashSet<SimpleNode>(), null, root, postProcess);
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON(HashSet<SimpleNode> seen, SimpleNode parent, SimpleNode root, ToJSONPostProcess postProcess) {
        seen.add(root);
        JSONObject obj = new JSONObject();
        obj.put("id", root.getTreeID());
        obj.put("value", getExternalID(root));

        ArrayList<SimpleNode> neighbours = new ArrayList<>(getNeighbours(root));
        ArrayList<JSONObject> jsNeighbours = new ArrayList<>();
        for (SimpleNode n : neighbours) {
            if (!seen.contains(n)) {
                jsNeighbours.add(toJSON(seen, root, n, postProcess));
            }
        }
        obj.put("children", jsNeighbours);

        postProcess.accept(obj, parent, root);

        return obj;
    }
}

class GreedyDecomposition {

    private IGraph<Vertex<Integer>, Integer, String> graph;
    private HashMap<HashSet<Integer>, Long> cache = new HashMap<>();
    private Random rnd = new Random();
    long cacheHits = 0;
    long cutboolTotalCalls = 0;

    public GreedyDecomposition(IGraph<Vertex<Integer>, Integer, String> graph) {
        this.graph = graph;
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
        long cb = CCMIS.BoolDimBranch(CutBoolComparatorCCMIS.convertSadiaBiGraph(bg));
        return cb;
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
            long fbcool = getFunkyCutBool(ibt, lefts);
            if (fbcool < minFBCool) {
                minFBCool = fbcool;
                minFBNode = n;
            }
        }
        ibt = trickle(ibt, currentNode, minFBNode, v);

        /*if (parent == null) {
            SimpleNode maxCut = getFunkyMaxCut(ibt);
            assert maxCut != null;
            System.out.printf("rerooting: %s\n", maxCut != ibt.getRoot());
            ibt = ibt.reRoot(maxCut);
        }*/
        return ibt;
    }

    public ImmutableBinaryTree retrickleRandom(ImmutableBinaryTree ibt) {
        ArrayList<Integer> ids = new ArrayList<>(ibt.getAllChildren());
        int pick = ids.get(rnd.nextInt(ids.size()));
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), graph.getVertex(pick));
        if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    public ImmutableBinaryTree retrickleGiven(ImmutableBinaryTree ibt, int externalID) {
        int pick = externalID;
        ImmutableBinaryTree oldIbt = ibt;
        ibt = ibt.remove(ibt.find(pick));
        ibt = trickle(ibt, null, ibt.getRoot(), graph.getVertex(pick));
        if (getFunkyBooleanWidth(ibt) > getFunkyBooleanWidth(oldIbt)) {
            System.out.println("worse result");
            ibt = oldIbt;
        }
        return ibt;
    }

    public ImmutableBinaryTree decomposeTopCut() {
        HashSet<Vertex<Integer>> remaining = new HashSet<>();
        long totalsize = 0;
        for (Vertex<Integer> v : graph.vertices()) {
            totalsize++;
            remaining.add(v);
        }

        long start = System.currentTimeMillis();
        PersistentVector<Vertex<Integer>> lefts = PersistentVector.EMPTY;
        while (!remaining.isEmpty()) {
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            for (Vertex<Integer> v : remaining) {
                PersistentVector<Vertex<Integer>> newlefts = lefts.cons(v);
                HashSet<Integer> vertexIDs = new HashSet<>();
                for (Vertex<Integer> v2 : newlefts) {
                    vertexIDs.add(v2.id());
                }
                long cb = getCutBool(vertexIDs);
                if (cb < minmove) {
                    minmove = cb;
                    tomove = v;
                }

            }
            System.out.printf("time: %d, sz: %d/%d, bw: %.2f, 2^bw: %d, lefts: %s\n",
                    System.currentTimeMillis() - start,
                    lefts.size(), totalsize,
                    getLogBooleanWidth(minmove), minmove, lefts);
            lefts = lefts.cons(tomove);
            remaining.remove(tomove);
        }
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        lefts.forEach((node) -> list.add(node));
        return decompose(list);
    }

    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> list = new ArrayList<>();
        graph.vertices().forEach((node) -> list.add(node));
        return decompose(list);
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

        long start = System.currentTimeMillis();
        final int PRINT_INTERVAL = 1000;
        long oldPrint = start - PRINT_INTERVAL;

        for (Vertex<Integer> v : vertices) {
            i += 1;

            ibt = trickle(ibt, null, ibt.getRoot(), v);
            bw = getFunkyBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;
            //troubleMakers.put(ratio, v);

            final ImmutableBinaryTree ibt2 = ibt;
            //if (System.currentTimeMillis() - oldPrint > PRINT_INTERVAL) {

            /*String jsonDecomposition = ibt.toJSON(ibt.getRoot(), (obj, parent, node) -> {
                if (node != ibt2.getRoot()) {
                    //obj.clear();
                    obj.put("cb", getCutBool(ibt2.getChildren(parent, node)));
                }
            }).toString();
            System.out.println(jsonDecomposition);
            */

            /*try {
                System.out.println(JsonWriter.formatJson(jsonDecomposition));
            } catch (IOException e) {
                System.out.println("DAMN!");
                System.exit(-1);
            }*/
            System.out.printf("trickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                    v.id(), i, graph.numVertices(),
                    getLogBooleanWidth(bw), bw, ratio);
            //    oldPrint = System.currentTimeMillis();
            //}

            /*int retid = troubleMakers.firstEntry().getValue().id();
            troubleMakers.remove(troubleMakers.firstEntry().getKey());
            ibt = retrickleGiven(ibt, retid);
            bw = getFunkyBooleanWidth(ibt);
            ratio = (double) bw / oldbw;
            oldbw = bw;
            troubleMakers.put(ratio, graph.getVertex(retid));
            */
            for (int j = 0; j < 2; j++) {
                ibt = retrickleRandom(ibt);
            }
            //bw = getFunkyBooleanWidth(ibt);

            /*System.out.printf("retrickling id=%d, %d/%d, funky bw: %.2f, 2^bw: %d, ratio: %f\n",
                    v.id(), i, graph.numVertices(),
                    getLogBooleanWidth(bw), bw, ratio);*/

        }

        /*i = 0;
        ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();
        for (Map.Entry<Double, Vertex<Integer>> v : troubleMakers.entrySet()) {
            i++;
            System.out.printf("retrickling vertex %f %d, %d/%d\n", v.getKey(), v.getValue().id(), i, graph.numVertices());
            System.out.printf("bw: %d\n", getBooleanWidth(ibt));
            ibt = trickle(ibt, ibt.getRoot(), v.getValue());
        }*/

        /*
        i = 0;
        for (Map.Entry<Double, Vertex<Integer>> v : troubleMakers.descendingMap().entrySet()) {
            i++;
            System.out.printf("retrickling vertex %f %d, %d/%d\n", v.getKey(), v.getValue().id(), i, graph.numVertices());
            bw = getBooleanWidth(ibt);
            System.out.printf("bw: %.2f, 2^bw: %d\n", getLogBooleanWidth(bw), bw);
            ibt = retrickleGiven(ibt, v.getValue().id());
        }*/

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

    public static void main(String[] args) throws Exception {

        /*test();
        System.exit(1);
        */

        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf";
        String fileName = ControlUtil.GRAPHLIB + "coloring/fpsol2.i.1.dgf";
        //String fileName = ControlUtil.GRAPHLIB_OURS + "cycle/c5.dimacs";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "coloring/queen9_9.dgf";
        //String fileName = ControlUtil.GRAPHLIB + "delauney/a280.tsp.dgf";
        if (args.length > 0) {
            fileName = args[0];
        }
        IGraph<Vertex<Integer>, Integer, String> graph;
        graph = ControlUtil.getTestGraph(fileName);

        GreedyDecomposition gd = new GreedyDecomposition(graph);

        //gd.decomposeTopCut();
        //System.exit(-1);

        long decomposeStart = System.currentTimeMillis();
        //final ImmutableBinaryTree ibt = gd.decomposeTopCut();
        final ImmutableBinaryTree ibt = gd.decompose();

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
        result.put("boolean-width", GreedyDecomposition.getLogBooleanWidth(bw));
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
