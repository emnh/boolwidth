package graph.algorithms.dynamic_forest;

/**
 * Created by emh on 26.12.2014.
 * translated from https://github.com/mikolalysenko/dynamic-forest/blob/master/lib/euler.js
 * The MIT License (MIT):
 * https://github.com/mikolalysenko/dynamic-forest/blob/master/LICENSE
 */
public class EulerHalfEdge extends EulerObject {

    //public Object value;
    public DynamicForest.DynamicEdge value;
    public EulerVertex s;
    public EulerVertex t;
    public TreapNode node;
    public EulerHalfEdge opposite;
    //public String type = "edge";

    public EulerHalfEdge(
            DynamicForest.DynamicEdge value,
            EulerVertex s,
            EulerVertex t,
            TreapNode node,
            EulerHalfEdge opposite) {
        this.value = value;
        this.s = s;
        this.t = t;
        this.node = node;
        this.opposite = opposite;
    }

    public void cleanup() {
        TreapNode v = this.node;
        v.remove();
        v.value = null;
        this.node = null;
        this.opposite = null;
        this.s = null;
        this.t = null;
    }

    public void cut() {

        EulerHalfEdge other = this.opposite;

        //Split into parts
        TreapNode a = this.node;
        TreapNode b = a.split();
        TreapNode c = other.node;
        TreapNode d = c.split();

        //Pull out the roots
        if(d != null && a.root() != d.root()) {
            // a comes before c:
            // [a, bc, d]
            a.concat(d);
        } else if(b != null && c.root() != b.root()) {
            // c comes before a:
            // [c, da, b]
            c.concat(b);
        }

        // Clean up mess
        this.cleanup();
        other.cleanup();
    }
}
