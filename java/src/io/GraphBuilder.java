package io;

import exceptions.InvalidGraphFileFormatException;
import graph.AdjacencyListGraph;
import graph.IntegerGraph;
import graph.Vertex;
import graph.VertexLabel;
import interfaces.IGraph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Builds an undirected graph with only single edges between any pair of nodes.
 */
public class GraphBuilder<TVertex extends Vertex<V>, V, E> {

	protected int nodesNum;
	protected int edgesNum;
	protected int maxNodeNum;
	protected String fileName;
	private static final boolean TOLERANT = false;
	protected ArrayList<int[]> edgeList;
	private final HashMap<Integer, String> revNodeLabelMap;

	public GraphBuilder(GraphFileReader r) {
		this.nodesNum = r.getNodesNum();
		this.edgesNum = r.getEdgeNum();
		this.edgeList = r.getEdgeList();
		this.fileName = r.getFileName();
		this.maxNodeNum = r.getMaxNodeNum();
		this.revNodeLabelMap = new HashMap<Integer, String>();
		if (r.getNodeLabelMap() != null) {
			for (Entry<String, Integer> e : r.getNodeLabelMap().entrySet()) {
				this.revNodeLabelMap.put(e.getValue(), e.getKey());
			}
		}
	}

	/**
	 * Builds an adjacency list graph where both vertices and edges holds a
	 * string element.
	 * 
	 * TODO: rewrite once new graph library is in place
	 */
	public AdjacencyListGraph.D<V, E> buildAdjListGraph(ArrayList<V> vElements,
			ArrayList<E> eElements) throws FileNotFoundException {

		AdjacencyListGraph.D<V, E> graph = new AdjacencyListGraph.D<V, E>();
		ArrayList<Vertex<V>> nodes = new ArrayList<Vertex<V>>();

		for (int i = 0; i < this.nodesNum; i++) {
			Vertex<V> v = graph.insertVertex(vElements.get(i));
			if (!this.revNodeLabelMap.isEmpty()) {
				VertexLabel.setLabel(v, this.revNodeLabelMap.get(i));
			}
			nodes.add(v);
		}

		int node1;
		int node2;
		for (int i = 0; i < this.edgesNum; i++) {
			node1 = this.edgeList.get(i)[0];
			node2 = this.edgeList.get(i)[1];
			graph.insertEdge(nodes.get(node1), nodes.get(node2), eElements
					.get(i));
		}
		return graph;
	}

	/**
	 * Builds a graph where both vertices and edges holds a null element.
	 *
	 * TODO: rewrite once new graph library is in place
	 * 
	 * @param graph
	 *            An empty graph.
	 */
	public IGraph<TVertex, V, E> buildNullGraph(IGraph<TVertex, V, E> graph)
	throws FileNotFoundException {

		// AdjacencyListGraph<TVertex, V, E> graph = new
		// AdjacencyListGraph<TVertex, V, E>();
		ArrayList<TVertex> nodes = new ArrayList<TVertex>();

		for (int i = 0; i < this.nodesNum; i++) {
			TVertex v = graph.insertVertex(null);
			if (!this.revNodeLabelMap.isEmpty()) {
				VertexLabel.setLabel(v, this.revNodeLabelMap.get(i));
			}
			nodes.add(v);
		}

		boolean[][] setEdges = new boolean[this.nodesNum][this.nodesNum];
		boolean warned = false;

		for (int[] edge : this.edgeList) {
			int node1 = edge[0]; // this.edgeList.get(i)[0];
			int node2 = edge[1]; // this.edgeList.get(i)[1];
			if (node1 > node2) {
				int tmp = node1;
				node1 = node2;
				node2 = tmp;
			}
			if (setEdges[node1][node2] == true) {
				if (warned == false) {
					System.out.printf(
							"warning: \"%s\" has duplicate edge (or is directed)\n",
							this.fileName);
					warned = true;
				}
				continue;
			}
			setEdges[node1][node2] = true;
			if (node1 >= nodes.size() || node2 >= nodes.size()) {
				if (TOLERANT) {

				} else {
					throw new InvalidGraphFileFormatException(
					"declared number of nodes is too low");
				}
			}
			graph.insertEdge(nodes.get(node1), nodes.get(node2), null);
		}
		return graph;
	}

	public IntegerGraph constructIntGraph() throws FileNotFoundException {
		IntegerGraph graph = new IntegerGraph(this.nodesNum);

		for (int i = 0; i < this.edgesNum; i++) {
			graph.insertEdge(this.edgeList.get(i)[0], this.edgeList.get(i)[1]);
		}
		return graph;
	}
}
