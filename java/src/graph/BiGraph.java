package graph;

import exceptions.InvalidPositionException;
import interfaces.IBiGraph;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;

/**
 * 
 * @author oty094
 * 
 */
public class BiGraph<V, E> extends AdjacencyListGraph.D<V, E> implements
IBiGraph<V, E> {

	// We use the left/right lists to keep track of used vertexes in this graph
	PosSet<Vertex<V>> leftList;
	PosSet<Vertex<V>> rightList;
	ArrayList<Boolean> isLeft;

	//	public static String ORIGINALFIELD = "BiGraph_orig";
	//	public static String REVERSEFIELD = null;

	/**
	 * Generates a random bipartite graph
	 * 
	 * @param l
	 *            number of nodes on left side
	 * @param r
	 *            number of nodes on right side
	 * @param m
	 *            number of edges in the graph
	 */
	public static BiGraph<Integer, String> random(int l, int r, int m) {
		BiGraph<Integer, String> h = new BiGraph<Integer, String>(l, r);
		ArrayList<Integer> pairs = new ArrayList<Integer>(l + r);
		ArrayList<Vertex<Integer>> al = new ArrayList<Vertex<Integer>>();
		ArrayList<Vertex<Integer>> ar = new ArrayList<Vertex<Integer>>();
		for (int a = 0; a < l; a++) {
			al.add(h.insertLeft(a * 2));
		}
		for (int a = 0; a < r; a++) {
			ar.add(h.insertRight(a * 2 + 1));
		}

		for (int a = 0; a < l; a++) {
			for (int b = 0; b < r; b++) {
				pairs.add(a * r + b); // potential for overflow?
			}
		}
		for (int a = 0; a < m; a++) {
			int i = (int) (Math.random() * pairs.size());
			int il = pairs.get(i) / r;
			int ir = pairs.get(i) % r;
			if (i < pairs.size() - 1) {
				pairs.set(i, pairs.remove(pairs.size() - 1));
			} else {
				pairs.remove(i);
			}
			h.insertEdge(al.get(il), ar.get(ir), "-");
		}
		return h;
	}

	/**
	 * BiGraph is constructed in O(n + e) time.
	 * 
	 * @param left
	 * @param graph
	 */
	public BiGraph(Collection<Vertex<V>> left, IGraph<Vertex<V>, V, E> graph) {
		this(left.size(), Math.max(0, graph.numVertices() - left.size()));

		// add left and right vertices
		for (Vertex<V> v : graph.vertices()) {
			if (left.contains(v)) {
				insertLeft(v);
			} else {
				insertRight(v);
			}
		}

		// add edges going between left and right
		for (Edge<Vertex<V>, V, E> e : graph.edges()) {
			int a = e.endVertices().get(0).id();
			int b = e.endVertices().get(1).id();
			if (this.isLeft.get(a) != this.isLeft.get(b)) {
				Vertex<V> va = this.vList.get(a);
				Vertex<V> vb = this.vList.get(b);
				insertEdge(va, vb, e.element());
			}
		}
	}

    /**
     * Convert graph to bigraph.
     * Duplicate nodes in a graph to both left and right.
     * @param graph
     */
    public BiGraph(IGraph<Vertex<V>, V, E> graph) {
        this(graph.numVertices(), graph.numVertices());

        // add left and right vertices
        for (Vertex<V> v : graph.vertices()) {
            Vertex<V> newVertexLeft = createVertex(v.element(), getNextID());
            insertLeft(newVertexLeft);
        }
        for (Vertex<V> v : graph.vertices()) {
            Vertex<V> newVertexRight = createVertex(v.element(), getNextID());
            insertRight(newVertexRight); // maybe dup it?
        }

        // add edges going between left and right
        for (Edge<Vertex<V>, V, E> e : graph.edges()) {
            int a = e.endVertices().get(0).id();
            int b = e.endVertices().get(1).id();
            // add right offset
            b += graph.numVertices();
            if (this.isLeft.get(a) != this.isLeft.get(b)) {
                Vertex<V> va = this.vList.get(a);
                Vertex<V> vb = this.vList.get(b);
                insertEdge(va, vb, e.element());
            }
        }
    }

	/**
	 * Builds a Graph, labels vertices as left or right
	 * 
	 */
	public BiGraph(int n) {
		this.leftList = new PosSet<Vertex<V>>(n);
		this.rightList = new PosSet<Vertex<V>>(n);
		this.isLeft = new ArrayList<Boolean>(n);
	}

	public BiGraph(int l, int r) {
		super();
		if (l < 0 || r < 0) {
			throw new IllegalArgumentException("Cannot create a BiGraph with "
					+ l + " left vertices and " + r + " right vertices");
		}

		this.leftList = new PosSet<Vertex<V>>(l);
		this.rightList = new PosSet<Vertex<V>>(r);
		this.isLeft = new ArrayList<Boolean>(r + l);
	}

	//	@SuppressWarnings("unchecked")
	//	public Vertex<V> getOriginal(Vertex<V> bi_v) {
	//		return (Vertex<V>) bi_v.getAttr(ORIGINALFIELD);
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	public Vertex<V> getReverse(Vertex<V> v) {
	//		return (Vertex<V>) v.getAttr(REVERSEFIELD);
	//	}

	/**
	 * O(1)
	 */
	@Deprecated
	public Vertex<V> insertLeft(V o) {
		Vertex<V> v = super.insertVertex(o);
		this.isLeft.add(true);
		this.leftList.add(v);
		return v;
	}

	/**
	 * O(1)
	 */
	public Vertex<V> insertLeft(Vertex<V> v) {
		v = super.insertVertex(v);
		this.isLeft.add(true);
		this.leftList.add(v);
		return v;
	}

	/**
	 * O(1)
	 */
	@Deprecated
	public Vertex<V> insertRight(V o) {
		Vertex<V> v = super.insertVertex(o);
		this.isLeft.add(false);
		this.rightList.add(v);
		return v;
	}

	/**
	 * O(1)
	 */
	public Vertex<V> insertRight(Vertex<V> v) {
		v = super.insertVertex(v);
		this.isLeft.add(false);
		this.rightList.add(v);
		return v;
	}

	@Override
	public Vertex<V> insertVertex(V o) {
		throw new UnsupportedOperationException();
	}

	public boolean isLeft(Vertex<V> v) throws InvalidPositionException {
		checkVertex(v);
		return this.isLeft.get(v.id());
	}

	public boolean isRight(Vertex<V> v) throws InvalidPositionException {
		checkVertex(v);
		return !this.isLeft.get(v.id());
	}

	/**
	 * Makes object be collected earlier by the JVM GC (if we are using very big
	 * graphs)
	 */
	public void killGraph() {
		this.vList.clear();
		this.eList.clear();
		this.adjacencyList.clear();
	}

	/**
	 * @return List containing only left vertices which have a crossing edge
	 *         Works in O(1) time
	 */
	public Iterable<Vertex<V>> leftVertices() {
		return this.leftList;
	}

	public int numLeftVertices() {
		return this.leftList.size();
	}

	public int numRightVertices() {
		return this.rightList.size();
	}

	@Override
	public V removeVertex(Vertex<V> v) {
		if (isLeft(v)) {
			this.leftList.remove(v);
		}

		if (isRight(v)) {
			this.rightList.remove(v);
		}

		super.removeVertex(v);

		if (this.isLeft.size() - 1 == v.id()) {
			this.isLeft.remove(v.id());
		} else {
			this.isLeft.set(v.id(), this.isLeft.remove(this.isLeft.size() - 1));
		}

		return v.element();
	}

	/**
	 * @return List containing only right vertices which have a crossing edge
	 *         Works in O(1) time
	 */
	public Iterable<Vertex<V>> rightVertices() {
		return this.rightList;
	}

	//	public void setOriginal(Vertex<V> bi_v, Vertex<V> v) {
	//		bi_v.setAttr(ORIGINALFIELD, v);
	//	}
	//
	//	public void setReverse(Vertex<V> v, Vertex<V> bi_v) {
	//		v.setAttr(REVERSEFIELD, bi_v);
	//	}

    public int[][] getAdjacencyMatrix() {
        BiGraph<V,E> g = this;
        int[][] mat = new int[g.numLeftVertices()][g.numRightVertices()];

        // use left/right iteration order as new matrix index
        int[] fromGraphToBiGraphNodeIndexLeft = new int[g.numVertices() + 1];
        int[] fromGraphToBiGraphNodeIndexRight = new int[g.numVertices() + 1];
        int leftid = 0;
        for (Vertex<V> v : g.leftVertices()) {
            fromGraphToBiGraphNodeIndexLeft[v.id()] = leftid;
            leftid++;
        }
        int rightid = 0;
        for (Vertex<V> v : g.rightVertices()) {
            fromGraphToBiGraphNodeIndexRight[v.id()] = rightid;
            rightid++;
        }

        for (Edge<Vertex<V>, V, E> edge : g.edges()) {
            Vertex<V> left = edge.left();
            Vertex<V> right = edge.right();
            //System.out.printf("e: %d %d\n", left.id(), right.id() - g.numLeftVertices());
            leftid = fromGraphToBiGraphNodeIndexLeft[left.id()];
            rightid = fromGraphToBiGraphNodeIndexRight[right.id()];
            //System.out.printf("%b", this.isLeft.get(left.id()) != this.isLeft.get(right.id()));
            mat[leftid][rightid] = 1;
        }

        return mat;
    }

    public void printAdjacencyMatrix(int[][] mat) {
        int leftid = 0;
        for (Vertex<V> v1 : leftVertices()) {
            int rightid = 0;
            for (Vertex<V> v2 : rightVertices()) {
                System.out.printf("%d ", mat[leftid][rightid]);
                rightid++;
            }
            leftid++;
            System.out.println("");
        }
    }

	@Override
	protected void toGraphVizNodes(Formatter f) {
		f.format("subgraph cluster_0 {\n");
		f.format("label = \"left\";\n");
		f.format("color = red;\n");

		for (Vertex<V> n : leftVertices()) {
			f.format("%s [ label = \"%d\" ];\n", n.id(), n.id());
		}
		f.format("}\n");

		f.format("subgraph cluster_1 {\n");
		f.format("label = \"right\";\n");
		f.format("color = blue;\n");

		for (Vertex<V> n : rightVertices()) {
			f.format("%s [ label = \"%d\" ];\n", n.id(), n.id());
		}
		f.format("}\n");
	}
}
