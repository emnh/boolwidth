package boolwidth.cutbool.ccmis_trial;

import graph.subsets.PosSet;

/**
 * Created by emh on 6/12/14.
 */
public class IndexedSet<V extends IndexVertex> extends PosSet<V> {

    public IndexedSet() {
    }

    public IndexedSet(int n) {
        super(n);
    }

    public IndexedSet(Iterable<V> it) {
        super(it);
    }
}
