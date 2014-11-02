package boolwidth.greedysearch;

import com.github.krukow.clj_lang.PersistentHashSet;
import com.github.krukow.clj_lang.PersistentVector;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by emh on 11/2/2014.
 */
public class ImmutableBinaryTree {

    public static final int EMPTY_NODE = -1;
    private int maxid = 0;
    private SimpleNode reference = null; // pointer to last added
    private SimpleNode root = null;
    private PersistentVector<SimpleNode> nodes = PersistentVector.create();
    private PersistentVector<PersistentHashSet<SimpleNode>> neighbours = Util.createPersistentVector();

    private PersistentVector<Integer> externalIDs = PersistentVector.create();

    public ImmutableBinaryTree() {
    }

    public SimpleNode getReference() {
        return reference;
    }

    public ImmutableBinaryTree join(ImmutableBinaryTree other) {
        ImmutableBinaryTree[] result = {new ImmutableBinaryTree()};
        result[0] = result[0].addRoot();
        final HashMap<SimpleNode, SimpleNode> thisToResult = new HashMap<>();
        //thisToResult.put(this.getRoot(), result[0].getRoot());
        this.dfs((parent, node) -> {
            SimpleNode newParent;
            if (parent == null) {
                newParent = result[0].getRoot();
            } else {
                newParent = thisToResult.get(parent);
            }
            //System.out.printf("parent, newParent, resroot, node: %s, %s, %s, %s\n", parent, newParent, result[0].getRoot(), node);
            result[0] = result[0].addChild(newParent, this.getExternalID(node));
            thisToResult.put(node, result[0].getReference());
        });
        //thisToResult.put(other.getRoot(), result[0].getRoot());
        other.dfs((parent, node) -> {
            SimpleNode newParent;
            if (parent == null) {
                newParent = result[0].getRoot();
            } else {
                newParent = thisToResult.get(parent);
            }
            result[0] = result[0].addChild(newParent, other.getExternalID(node));
            thisToResult.put(node, result[0].getReference());
        });
        return result[0];
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
            result.neighbours = this.neighbours.cons(PersistentHashSet.create());
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
