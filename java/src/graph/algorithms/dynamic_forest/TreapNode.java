package graph.algorithms.dynamic_forest;

/**
 * Created by emh on 26.12.2014.
 * Translated from https://github.com/mikolalysenko/dynamic-forest/blob/master/lib/treap.js.
 * The MIT License (MIT):
 * https://github.com/mikolalysenko/dynamic-forest/blob/master/LICENSE
 *
 *
 */

//This is a custom binary tree data structure
//The reason for using this instead of an array or some generic search tree is that:
//
//    * Nodes are ordered by position not sorted by key
//    * On average tree height is O(log(number of nodes))
//    * Concatenation and splitting both take O(log(N))
//    * Has augmentations for size and edge level incidence flag
//    * Node references are not invalidated during updates
//    * Has threaded pointers for fast sequential traversal
//
public class TreapNode {

    public EulerObject value;
    public boolean flag;
    public boolean flagAggregate;
    public int count;
    public double priority;
    public TreapNode parent;
    public TreapNode left;
    public TreapNode right;
    public TreapNode next;
    public TreapNode prev;

    private static int countOfValue(EulerObject v) {
        if (v == null) {
            return 1;
        } else {
            return v instanceof EulerVertex ? 1 : 0;
        }
    }

    public TreapNode(
            EulerObject value,
            boolean flag,
            boolean flagAggregate,
            int count,
            double priority,
            TreapNode parent,
            TreapNode left,
            TreapNode right,
            TreapNode next,
            TreapNode prev) {
        this.value = value;
        this.flag = flag;
        this.flagAggregate = flagAggregate;
        this.count = count;
        this.priority = priority;
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.next = next;
        this.prev = prev;
    }

    public void bubbleUp() {
        while(true) {
            TreapNode p = this.parent;
            if(p == null || p.priority < this.priority) {
                break;
            }
            if(this == p.left) {
                TreapNode b = this.right;
                p.left = b;
                if (b != null) {
                    b.parent = p;
                }
                this.right = p;
            } else {
                TreapNode b = this.left;
                p.right = b;
                if (b != null) {
                    b.parent = p;
                }
                this.left = p;
            }
            p.update();
            this.update();
            TreapNode gp = p.parent;
            p.parent = this;
            this.parent = gp;
            if(gp != null) {
                if(gp.left == p) {
                    gp.left = this;
                } else {
                    gp.right = this;
                }
            }
        }
        TreapNode p = this.parent;
        while(p != null) {
            p.update();
            p = p.parent;
        }
    }

    public TreapNode root() {
        TreapNode n = this;
        while (n.parent != null) {
            n = n.parent;
        }
        return n;
    }

    public TreapNode first() {
        TreapNode l = this.root();
        while (l.left != null) {
            l = l.left;
        }
        return l;
    }

    public TreapNode last() {
        TreapNode r = this.root();
        while (r.right != null) {
            r = r.right;
        }
        return r;
    }

    public TreapNode insert(EulerObject value) {
        if(this.right == null) {
            TreapNode nn = this.right = new TreapNode(value, false, false, countOfValue(value), Math.random(), this, null, null, this.next, this);
            if(this.next != null) {
                this.next.prev = nn;
            }
            this.next = nn;
            nn.bubbleUp();
            return nn;
        }
        TreapNode v = this.next;
        TreapNode nn = v.left = new TreapNode(value, false, false, countOfValue(value), Math.random(), v, null, null, v, this);
        v.prev = nn;
        this.next = nn;
        nn.bubbleUp();
        return nn;
    }

    public void swapNodes(TreapNode a, TreapNode b) {
        double p = a.priority;
        a.priority = b.priority;
        b.priority = p;
        TreapNode t = a.parent;
        a.parent = b.parent;
        if(b.parent != null) {
            if(b.parent.left == b) {
                b.parent.left = a;
            } else {
                b.parent.right = a;
            }
        }
        b.parent = t;
        if (t != null) {
            if(t.left == a) {
                t.left = b;
            } else {
                t.right = b;
            }
        }
        t = a.left;
        a.left = b.left;
        if(b.left != null) {
            b.left.parent = a;
        }
        b.left = t;
        if (t != null) {
            t.parent = b;
        }
        t = a.right;
        a.right = b.right;
        if(b.right != null) {
            b.right.parent = a;
        }
        b.right = t;
        if (t != null) {
            t.parent = b;
        }
        t = a.next;
        a.next = b.next;
        if(b.next != null) {
            b.next.prev = a;
        }
        b.next = t;
        if (t != null) {
            t.prev = b;
        }
        t = a.prev;
        a.prev = b.prev;
        if(b.prev != null) {
            b.prev.next = a;
        }
        b.prev = t;
        if (t != null) {
            t.next = b;
        }
        int c = a.count;
        a.count = b.count;
        b.count = c;
        boolean f = a.flag;
        a.flag = b.flag;
        b.flag = f;
        f = a.flagAggregate;
        a.flagAggregate = b.flagAggregate;
        b.flagAggregate = f;
    }

    public void update() {
        int c = countOfValue(this.value);
        boolean f = this.flag;
        if (this.left != null) {
            c += this.left.count;
            f = f || this.left.flagAggregate;
        }
        if (this.right != null) {
            c += this.right.count;
            f = f || this.right.flagAggregate;
        }
        this.count = c;
        this.flagAggregate = f;
    }

    // Set new flag state and propagate up tree
    public void setFlag(boolean f) {
        this.flag = f;
        for(TreapNode v = this; v != null; v = v.parent) {
            boolean pstate = v.flagAggregate;
            v.update();
            if(pstate == v.flagAggregate) {
                break;
            }
        }
    }

    public void remove() {
        TreapNode node = this;
        if(node.left != null && node.right != null) {
            TreapNode other = node.next;
            swapNodes(other, node);
        }
        if(node.next != null) {
            node.next.prev = node.prev;
        }
        if(node.prev != null) {
            node.prev.next = node.next;
        }
        TreapNode r = null;
        if(node.left != null) {
            r = node.left;
        } else {
            r = node.right;
        }
        if(r != null) {
            r.parent = node.parent;
        }
        if(node.parent != null) {
            if(node.parent.left == node) {
                node.parent.left = r;
            } else {
                node.parent.right = r;
            }
            // Update all ancestor counts
            TreapNode p = node.parent;
            while(p != null) {
                p.update();
                p = p.parent;
            }
        }
        //Remove all pointers from detached node
        node.parent = node.left = node.right = node.prev = node.next = null;
        node.count = 1;
    }

    public TreapNode split() {
        TreapNode node = this;
        TreapNode s = node.insert(null);
        s.priority = Double.NEGATIVE_INFINITY;
        s.bubbleUp();
        TreapNode l = s.left;
        TreapNode r = s.right;
        if (l != null) {
            l.parent = null;
        }
        if (r != null) {
            r.parent = null;
        }
        if(s.prev != null) {
            s.prev.next = null;
        }
        if(s.next != null) {
            s.next.prev = null;
        }
        return r;
    }

    public TreapNode concatRecurse(TreapNode a, TreapNode b) {
        if(a == null) {
            return b;
        } else if(b == null) {
            return a;
        } else if(a.priority < b.priority) {
            a.right = concatRecurse(a.right, b);
            a.right.parent = a;
            a.update();
            return a;
        } else {
            b.left = concatRecurse(a, b.left);
            b.left.parent = b;
            b.update();
            return b;
        }
    }

    public TreapNode concat(TreapNode other) {
        if(other == null) {
            return null;
        }
        TreapNode ra = this.root();
        TreapNode ta = ra;
        while (ta.right != null) {
            ta = ta.right;
        }
        TreapNode rb = other.root();
        TreapNode sb = rb;
        while (sb.left != null) {
            sb = sb.left;
        }
        ta.next = sb;
        sb.prev = ta;
        TreapNode r = concatRecurse(ra, rb);
        r.parent = null;
        return r;
    }

    public static TreapNode createTreap(EulerObject value) {
        return new TreapNode(value, false, false, countOfValue(value), Math.random(), null, null, null, null, null);
    }

}
