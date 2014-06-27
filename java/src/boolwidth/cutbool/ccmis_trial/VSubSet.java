package boolwidth.cutbool.ccmis_trial;

import graph.PosSubSet;
import interfaces.IPosSet;

/**
 * Created by emh on 6/12/14.
 */
public class VSubSet extends PosSubSet<IndexVertex> {


    public VSubSet(IPosSet<IndexVertex> set) {
        super(set);
    }

    public VSubSet(IPosSet<IndexVertex> set, Iterable<IndexVertex> subset) {
        super(set, subset);
    }

    public VSubSet(IPosSet<IndexVertex> groundSet, long[] w) {
        super(groundSet, w);
    }

    @Override
    public VSubSet clone() {
        return new VSubSet(this.groundSet, this.words.clone());
    }

    public IndexVertex oneIntersectElement(VSubSet set) {
        PosSubSet<IndexVertex> intersection = this.intersection(set);
        if (intersection.size() == 1) {
            return intersection.first();
        }
        return null;
    }
}
