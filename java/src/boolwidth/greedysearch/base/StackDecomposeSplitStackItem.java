package boolwidth.greedysearch.base;

/**
 * Created by emh on 11/9/2014.
 */
class StackDecomposeSplitStackItem {
    public Split parent;
    public Split child;
    public boolean isLeft;

    public StackDecomposeSplitStackItem(Split parent, Split child, boolean isLeft) {
        this.child = child;
        this.parent = parent;
        this.isLeft = isLeft;
    }
}
