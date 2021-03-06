package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Reads a graph from file. Number of nodes, number of edges and end-vertices of
 * each edge are registered. The file must declare the number of nodes and
 * number of edges with labels on an empty line. Edges must be declared with a
 * label and its end-vertices as integers.
 * 
 * Example: c FILE: knights8_8.dgf c Squares of the chessboard with an edge when
 * reachable by a knight's move:
 * 
 * <pre>
 * p edge 64 168
 * e 1 11
 * e 2 12
 * ...
 * </pre>
 * 
 * Labels can be specified.
 */
public class GraphFileReader {

	protected Scanner sc;
	protected int nodesNum;
	protected int edgeNum;
	protected ArrayList<int[]> edgeList = new ArrayList<int[]>();
	private HashMap<String, Integer> nodeLabelMap = null;
	protected int minNodeNum;
	protected int maxNodeNum;
	public boolean emptyGraph;
	private String fileName;

	// Reads all graphs in our graphLib
	public GraphFileReader(String fileName) throws FileNotFoundException {
		this(fileName, "p", "edge", "n", "e");
	}

	public GraphFileReader(String fileName, String numNodesLabel,
			String numEdgesLabel, String nodeLabel, String edgeLabel)
	throws FileNotFoundException {
		this.sc = setFile(fileName);
		this.nodesNum = 0;
		this.edgeNum = 0;
		this.maxNodeNum = 0;
		this.minNodeNum = Integer.MAX_VALUE;
		this.emptyGraph = false;
		this.setFileName(fileName);
		read(numNodesLabel, numEdgesLabel, nodeLabel, edgeLabel);
	}

	public ArrayList<int[]> getEdgeList() {
		return this.edgeList;
	}

	public int getEdgeNum() {
		return this.edgeNum;
	}

	public String getFileName() {
		return this.fileName;
	}

	public int getMaxNodeNum() {
		return this.maxNodeNum;
	}

	protected HashMap<String, Integer> getNodeLabelMap() {
		return this.nodeLabelMap;
	}

	// Due to a problem with undeclared node labels in some graph files we return this.maxNodeNum + 1,
	// the maximum number of a node in an edge instead of declared node number in file.
	// The graph will be a bit bigger than necessary.
	// An alternative that compresses the graph is to make up the node labels based on the edges and 0-indexing.
	public int getNodesNum() {
		return this.maxNodeNum + 1;
	}

	public void read(String numNodesLabel, String numEdgesLabel,
			String nodeLabel, String edgeLabel) throws InputMismatchException {

		boolean found_header = true;

		// read header containing node and edge count
		while (found_header && this.sc.hasNext()) {
			if (this.sc.hasNext(numNodesLabel)) {
				this.sc.next();
				if (this.sc.next().startsWith(numEdgesLabel)) {
					this.nodesNum = this.sc.nextInt();
					this.edgeNum = this.sc.nextInt();
					// System.out.printf("nodes: %d, edges: %d\n", nodesNum,
					// edgeNum);
					this.edgeList = new ArrayList<int[]>(this.edgeNum);
					found_header = false;
				}
			} else {
				this.sc.nextLine();
			}
		}

		// read nodes and edges
		int nodectr = 0;
		while (this.sc.hasNextLine()) {
			String line = this.sc.nextLine();
			Scanner lineScanner = new Scanner(line);

			if (lineScanner.hasNext(nodeLabel)) {
				lineScanner.next();
				if (getNodeLabelMap() == null) {
					setNodeLabelMap(new HashMap<String, Integer>());
				}

				String label = lineScanner.next();

				// read weight. not used
				if (lineScanner.hasNextDouble()) {
					lineScanner.nextDouble();
				}

				// map from node label to index
				getNodeLabelMap().put(label, nodectr);
				nodectr++;
			}
			if (lineScanner.hasNext(edgeLabel)) {
				lineScanner.next();
				int[] tab;
				if (getNodeLabelMap() == null) {
					tab = new int[] { lineScanner.nextInt(), lineScanner.nextInt() };
				} else {
					// assumes that all nodes have labels if at least one does
					String a = lineScanner.next();
					String b = lineScanner.next();
					tab = new int[] { getNodeLabelMap().get(a), getNodeLabelMap().get(b) };
				}
				this.minNodeNum = Math.min(this.minNodeNum, tab[0]);
				this.minNodeNum = Math.min(this.minNodeNum, tab[1]);
				this.maxNodeNum = Math.max(this.maxNodeNum, tab[0]);
				this.maxNodeNum = Math.max(this.maxNodeNum, tab[1]);
				this.edgeList.add(tab);
			}
			lineScanner.close();
		}
		this.sc.close();

		// convert to 0 indexing
		for (int[] e : this.edgeList) {
			e[0] -= this.minNodeNum;
			e[1] -= this.minNodeNum;
		}

		if (this.nodesNum == 0) {
			this.emptyGraph = true;
		}
	}

	public Scanner setFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (file == null) {
			throw new FileNotFoundException("File not chosen");
		} else {
			return new Scanner(file);
		}
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	protected void setNodeLabelMap(HashMap<String, Integer> nodeLabelMap) {
		this.nodeLabelMap = nodeLabelMap;
	}

}
