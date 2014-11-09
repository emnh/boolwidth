package boolwidth.greedysearch.ds;

/**
 * Created by emh on 10/30/2014.
 */
public class SimpleNode {
    private int graphID;

    SimpleNode(int graphID) {
        this.graphID = graphID;
    }

    public int getGraphID() {
        return graphID;
    }

    public String toString() {
        return "" + graphID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleNode)) return false;

        SimpleNode that = (SimpleNode) o;

        if (graphID != that.graphID) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return graphID;
    }
}
