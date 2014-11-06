package boolwidth.greedysearch.ds;

import graph.Vertex;

/**
 * Created by emh on 11/3/2014.
 */
public class TreeElement implements Comparable<TreeElement> {
    public long cutbool;
    public Vertex<Integer> vertex;

    public TreeElement(long cutbool, Vertex<Integer> vertex) {
        this.cutbool = cutbool;
        this.vertex = vertex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeElement)) return false;

        TreeElement that = (TreeElement) o;

        if (cutbool != that.cutbool) return false;
        if (!vertex.equals(that.vertex)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (cutbool ^ (cutbool >>> 32));
        result = 31 * result + vertex.hashCode();
        return result;
    }

    @Override
    public int compareTo(TreeElement o) {
        // support multiset
        if (this.cutbool == o.cutbool) {
            return Integer.compare(this.vertex.id(), o.vertex.id());
        }
        return Long.compare(this.cutbool, o.cutbool);
    }

    public String toString() {
        //return String.format("%d: %d", this.cutbool, this.vertex.id());
        return String.format("%d", this.vertex.id());
    }
}
