package boolwidth.greedysearch.ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by emh on 11/8/2014.
 */
public class ImmutableBitSetGraph {
    private BitSetCollection bitSetCollection;
    private BSCBitSet[] neighbours;
    private int numNodes;

    public ImmutableBitSetGraph(int numNodes) {
        bitSetCollection = new BitSetCollection(numNodes);
        neighbours = new BSCBitSet[numNodes];
        this.numNodes = numNodes;
    }

    public BitSetCollection getBitSetCollection() {
        return bitSetCollection;
    }

    public ImmutableBitSetGraph(int numNodes, HashMap<Integer, ArrayList<Integer>> neighbours) {
        this.numNodes = numNodes;
        for (int node : getNodes()) {
            int[] nodeNeighbours = new int[neighbours.size()];
            int i = 0;
            for (int nodeNeighbour : neighbours.get(node)) {
                nodeNeighbours[i++] = nodeNeighbour;
            }
            this.neighbours[node] = bitSetCollection.createBSCBitSet(nodeNeighbours);
        }
    }

    public int getNumNodes() {
        return numNodes;
    }

    public Collection<Integer> getNodes() {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(i);
        }
        return nodes;
    }

    public BSCBitSet getNeighbours(int nodeID) {
        return neighbours[nodeID];
    }
}
