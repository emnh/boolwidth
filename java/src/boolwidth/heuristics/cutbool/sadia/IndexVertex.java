package boolwidth.heuristics.cutbool.sadia;

import graph.Vertex;
import interfaces.IVertexFactory;

/**
 * Created by emh on 6/12/14.
 */
public class IndexVertex extends Vertex<Integer> {

    public IndexVertex(Integer element, int id) {
        super(element, id);
    }

    public static final class Factory implements
            IVertexFactory<IndexVertex, Integer> {

        @Override
        public IndexVertex createNew(Integer element, int id) {
            return new IndexVertex(element, id);
        }
    }
}
