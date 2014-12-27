package graph.algorithms.dynamic_forest;

/**
 * Created by emh on 26.12.2014.
 * translated from https://github.com/mikolalysenko/dynamic-forest/blob/master/lib/euler.js
 * The MIT License (MIT):
 * https://github.com/mikolalysenko/dynamic-forest/blob/master/LICENSE
 */
public class EulerVertex extends EulerObject {
    //public DynamicVertex value;
    public TreapNode node;
    //public String type = "vertex";

    public EulerVertex(DynamicForest.DynamicVertex value, TreapNode node) {
        this.value = value;
        this.node = node;
    }

    // If flag is set, then this vertex has incident edges of at least level v
    public void setFlag(boolean f) {
        this.node.setFlag(f);
    }

    public boolean path(EulerVertex other) {
        return this.node.root() == other.node.root();
    }

    public void makeRoot() {
        TreapNode a = this.node;
        TreapNode b = a.split();
        if (b != null) {
            b.concat(a);
        }
    }

    public EulerHalfEdge link(EulerVertex other, DynamicForest.DynamicEdge value) {
        // Move both vertices to root
        this.makeRoot();
        other.makeRoot();

        // Create half edges and link them to each other
        EulerHalfEdge st = new EulerHalfEdge(value, this, other, null, null);
        EulerHalfEdge ts = new EulerHalfEdge(value, other, this, null, st);
        st.opposite = ts;

        // Insert entries in Euler tours
        st.node = this.node.insert(st);
        ts.node = other.node.insert(ts);

        // Link tours together
        this.node.concat(other.node);

        // Return half edge
        return st;
    }

    public int count() {
        return this.node.root().count;
    }

    public void cleanup() {
        this.node.remove();
        this.node.value = null;
        this.node = null;
    }

    public static EulerVertex createVertex(DynamicForest.DynamicVertex value) {
        EulerVertex v = new EulerVertex(value, null);
        v.node = TreapNode.createTreap(v);
        return v;
    }
}
