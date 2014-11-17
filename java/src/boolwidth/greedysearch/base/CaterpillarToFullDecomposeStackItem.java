package boolwidth.greedysearch.base;

/**
 * Created by emh on 11/9/2014.
 */
class CaterpillarToFullDecomposeStackItem {
    public OrderedSplit parent;
    public OrderedSplit child;

    public CaterpillarToFullDecomposeStackItem(OrderedSplit parent, OrderedSplit child) {
        this.child = child;
        this.parent = parent;
    }
}
