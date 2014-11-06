package boolwidth.greedysearch.ds;

import java.util.Comparator;

/**
 * Created by emh on 11/3/2014.
 */
public class TreeElementComparator implements Comparator<TreeElement> {

    @Override
    public int compare(TreeElement o1, TreeElement o2) {
        if (o1.cutbool == o2.cutbool) {
            return Integer.compare(o1.vertex.id(), o2.vertex.id());
        }
        return Long.compare(o1.cutbool, o2.cutbool);
    }
}
