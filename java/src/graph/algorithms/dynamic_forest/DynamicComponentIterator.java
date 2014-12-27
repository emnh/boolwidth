package graph.algorithms.dynamic_forest;

/**
 * Created by emh on 26.12.2014.
 */
public class DynamicComponentIterator {

    private TreapNode node;

    public DynamicComponentIterator(TreapNode node) {
        this.node = node;
    }

    public DynamicForest.DynamicVertex vertex() {
        return this.node.value.value;
    }

    public int size() {
        return this.node.root().count;
    }

    public boolean valid() {
        return this.node == null;
    }

    public Object valueOf() {
        if (this.node != null) {
            return this.vertex().value;
        }
        return null;
    }

    public TreapNode next() {
        TreapNode n = this.node;
        if (n != null) {
            n = n.next;
        }
        while (n != null) {
            if (n.value instanceof EulerVertex) {
                break;
            }

            n = n.next;
        }
        this.node = n;
        return n;
    }

    public boolean hasNext() {
        TreapNode n = this.node;
        if (n != null) {
            n = n.next;
        }
        while (n != null) {
            if (n.value instanceof EulerVertex) {
                break;
            }

            n = n.next;
        }
        return n == null;
    }

    public TreapNode prev() {
        TreapNode n = this.node;
        if (n != null) {
            n = n.prev;
        }
        while (n != null) {
            if(n.value instanceof EulerVertex) {
                break;
            }

            n = n.prev;
        }
        this.node = n;
        return n;
    }

    public boolean hasPrev() {
        TreapNode n = this.node;
        if (n != null) {
            n = n.prev;
        }
        while (n != null) {
            if(n.value instanceof EulerVertex) {
                break;
            }

            n = n.prev;
        }
        return n == null;
    }

    public TreapNode first() {
        if (this.node != null) {
            this.node = this.node.first();
            if(!(this.node.value instanceof EulerVertex)) {
                this.next();
            }
        }
        return null;
    }

    public TreapNode last() {
        if (this.node != null) {
            this.node = this.node.last();
            if(!(this.node.value instanceof EulerVertex)) {
                this.prev();
            }
        }
        return null;
    }

}
