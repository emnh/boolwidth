package boolwidth.greedysearch.base;

import com.github.krukow.clj_lang.PersistentHashSet;
import graph.Vertex;

import java.util.ArrayList;

/**
 * Created by emh on 11/15/2014.
 */
public class OrderedSplit extends Split {

    protected ArrayList<Vertex<Integer>> rightOrder = new ArrayList<>();
    protected ArrayList<Vertex<Integer>> leftOrder = new ArrayList<>();

    public OrderedSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, lefts, rights);

        for (Vertex<Integer> v : lefts) {
            this.leftOrder.add(v);
        }
        for (Vertex<Integer> v : rights) {
            this.rightOrder.add(v);
        }
    }

    public ArrayList<Vertex<Integer>> getRightOrder() {
        return rightOrder;
    }

    public ArrayList<Vertex<Integer>> getLeftOrder() {
        return leftOrder;
    }
}
