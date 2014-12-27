package graph.algorithms.dynamic_forest;

import java.util.ArrayList;

/**
 * Created by emh on 26.12.2014.
 */
public class DynamicForest {
    public int KEY_COUNTER = 0;

    // Raise the level of an edge, optionally inserting into higher level trees
    public void raiseLevel(DynamicEdge edge) {
        DynamicVertex s = edge.s;
        DynamicVertex t = edge.t;

        // Update position in edge lists
        removeEdge(s, edge);
        removeEdge(t, edge);
        edge.level += 1;
        EdgeList.insertEdge(s.adjacent, edge);
        EdgeList.insertEdge(t.adjacent, edge);

        // Update flags for s
        if(s.euler.size() <= edge.level) {
            s.euler.add(createEulerVertex(s));
        }
        EulerVertex es = s.euler.get(edge.level);
        es.setFlag(true);

        // Update flags for t
        if(t.euler.size() <= edge.level) {
            t.euler.add(createEulerVertex(t));
        }
        EulerVertex et = t.euler.get(edge.level);
        et.setFlag(true);

        // Relink if necessary
        if(edge.euler != null) {
            edge.euler.add(es.link(et, edge));
        }
    }

    // Remove edge from list and update flags
    public void removeEdge(DynamicVertex vertex, DynamicEdge edge) {
        ArrayList<DynamicEdge> adj = vertex.adjacent;
        int idx = EdgeList.index(adj, edge);
        adj.remove(idx);
        //Check if flag needs to be updated
        if(!((idx < adj.size() && adj.get(idx).level == edge.level) ||
                (idx > 0 && adj.get(idx-1).level == edge.level))) {
            vertex.euler.get(edge.level).setFlag(false);
        }
    }

    // Add an edge to all spanning forests with level <= edge.level
    public void linkDynamicForest(DynamicForest.DynamicEdge edge) {
        ArrayList<EulerVertex> es = edge.s.euler;
        ArrayList<EulerVertex> et = edge.t.euler;
        ArrayList<EulerHalfEdge> euler = new ArrayList<>(edge.level+1);
        for(int i = 0; i < edge.level + 1; ++i) {
            if(es.size() <= i) {
                es.add(createEulerVertex(edge.s));
            }
            if(et.size() <= i) {
                et.add(createEulerVertex(edge.t));
            }
            euler.add(i, es.get(i).link(et.get(i), edge));
        }
        edge.euler = euler;
    }

    public DynamicVertex createVertex(Object value) {
        ArrayList<EulerVertex> euler = new ArrayList<>();
        DynamicVertex v = new DynamicVertex(value, euler, new ArrayList<>());
        euler.add(createEulerVertex(v));
        return v;
    }

    public static EulerVertex createEulerVertex(DynamicVertex value) {
        return EulerVertex.createVertex(value);
    }

    /**
     * Created by emh on 26.12.2014.
     */
    public class DynamicVertex {

        // TODO: generify
        public Object value;
        public ArrayList<EulerVertex> euler;
        public ArrayList<DynamicEdge> adjacent;

        public DynamicVertex(
                Object value,
                ArrayList<EulerVertex> euler,
                ArrayList<DynamicEdge> adjacent) {
            this.value = value;
            this.euler = euler;
            this.adjacent = adjacent;
        }

        public boolean connected(DynamicVertex other) {
            return this.euler.get(0).path(other.euler.get(0));
        }

        public DynamicEdge link(DynamicVertex other, Object value) {
            DynamicEdge e = new DynamicEdge(value, (KEY_COUNTER++), this, other, 0, null);
            if(!this.euler.get(0).path(other.euler.get(0))) {
                linkDynamicForest(e);
            }
            this.euler.get(0).setFlag(true);
            other.euler.get(0).setFlag(true);
            EdgeList.insertEdge(this.adjacent, e);
            EdgeList.insertEdge(other.adjacent, e);
            return e;
        }

        public Object valueOf() {
            return this.value;
        }

        // Returns the number of vertices in this connected component
        public int componentSize() {
            return this.euler.get(0).count();
        }

        // Removes the vertex from the graph
        public void cut() {
            while(this.adjacent.size() > 0) {
                this.adjacent.get(this.adjacent.size()-1).cut();
            }
        }

        public DynamicComponentIterator component() {
            return new DynamicComponentIterator(this.euler.get(0).node);
        }
    }

    public class DynamicEdge {

        public Object value;
        public int key;
        public DynamicVertex s;
        public DynamicVertex t;
        public int level;
        public ArrayList<EulerHalfEdge> euler;

        public DynamicEdge(
                Object value,
                int key,
                DynamicVertex s,
                DynamicVertex t,
                int level,
                ArrayList<EulerHalfEdge> euler) {
            this.value = value;
            this.key = key; // Used to sort edges in list
            this.s = s;
            this.t = t;
            this.level = level;
            this.euler = euler;
        }

        public Object valueOf() {
            return this.value;
        }

        // Search over tv for edge connecting to tw
        public boolean visit(int level, TreapNode node) {
            if(node.flag) {
                DynamicVertex v = node.value.value;
                ArrayList<DynamicEdge> adj = v.adjacent;
                for(int ptr = EdgeList.levelIndex(adj, level);
                    ptr < adj.size() && adj.get(ptr).level == level;
                    ++ptr) {
                    DynamicEdge e = adj.get(ptr);
                    DynamicVertex es = e.s;
                    DynamicVertex et = e.t;
                    if (es.euler.get(level).path(et.euler.get(level))) {
                        raiseLevel(e);
                        ptr -= 1;
                    } else {
                        // Found the edge, relink components
                        linkDynamicForest(e);
                        return true;
                    }
                }
            }
            if(node.left != null && node.left.flagAggregate) {
                if(visit(level, node.left)) {
                    return true;
                }
            }
            if(node.right != null && node.right.flagAggregate) {
                if(visit(level, node.right)) {
                    return true;
                }
            }
            return false;
        }

        public void cut() {
            int level;

            //Don't double cut an edge
            if(this.s == null) {
                return;
            }

            removeEdge(this.s, this);
            removeEdge(this.t, this);
            if (this.euler != null && this.euler.size() > 0) {
                // Cut edge from tree
                for(int i=0; i < this.euler.size(); ++i) {
                    this.euler.get(i).cut();
                }

                // Find replacement, looping over levels
                for(int i = this.level; i >= 0; --i) {
                    TreapNode tv = this.s.euler.get(i).node.root();
                    TreapNode tw = this.t.euler.get(i).node.root();
                    level = i;
                    if(tv.count > tw.count) {
                        visit(level, tw);
                    } else {
                        visit(level, tv);
                    }
                }
            }
            this.s = this.t = null;
            this.euler = null;
            this.level = 32;
        }
    }

}
