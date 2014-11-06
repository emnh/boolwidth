package boolwidth.greedysearch.ds;

/**
 * Created by emh on 10/30/2014.
 */
public class SimpleNode {
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
