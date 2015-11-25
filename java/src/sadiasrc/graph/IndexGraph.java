package sadiasrc.graph;

import sadiasrc.exceptions.InvalidPositionException;

import java.util.ArrayList;

import sadiasrc.util.HashIndexSet;
import sadiasrc.util.IndexedSet;

/**
 * A realization of a graph according to adjacency list structure where the
 * element of the vertices is an integer and the edge element is a string.
 * 
 */

public class IndexGraph extends
		AdjacencyListGraph<IndexVertex, IndexEdge<IndexVertex>> {
	IndexedSet<IndexVertex> vertexset = new IndexedSet<IndexVertex>(vertices());

	/**
	 * Constructs a graph with no vertices and no edges runningtime O(1)
	 */
	public IndexGraph() {
		super();
		this.vList = new HashIndexSet();//VertexSet<IndexVertex>();
	}
	//TO DO
	/**
	 * Constructs a COPY OF THE graph with SAME vertices and SAME edges runningtime O(n+m)
	 */
	public IndexGraph copy() {
		
		IndexGraph t=new IndexGraph(this.numVertices());
				
		for(IndexEdge<IndexVertex> e:this.edges())
			t.insertEdge(e);
		return t;
		
	}

    @Override
    protected int getIndex(IndexVertex v) {
        return v.id();
    }

	/**
	 * Construct a graph with specified number of default vertices nodes have 0
	 * based indexes
	 *
	 * @param nodes
	 *            number of nodes to be inserted into the graph runningtime
	 *            O(nodes)
	 */
	public IndexGraph(int nodes) {
		this();
		for (int a = 0; a < nodes; a++) {
			insertVertex();
		}
	}

	public IndexGraph(IGraph<IndexVertex,? extends IEdge<IndexVertex>> g) {
		this(g.numVertices());
		for(IEdge<IndexVertex> e : g.edges())
		{
			insertEdge(e.endVertices().get(0).id(),e.endVertices().get(1).id());
		}
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see graph.AdjacencyListGraph#containsEdge(interfaces.IEdge) Runningtime
	 * O(1)
	 */
	@Override
	public boolean containsEdge(IndexEdge<IndexVertex> e) {
		return this.eList.get(e.id()).equals(e);
	}

	public boolean insertEdge(IndexVertex a, IndexVertex b)
			throws InvalidPositionException {
		if (a.id() >= numVertices() || b.id() >= numVertices()) {
			return false;
		}
		if (a == b || areAdjacent(a, b)) {
			return false;
		}
		//System.out.println("Adding edge "+a+" "+ b);
		return insertEdge(new IndexEdge<IndexVertex>(this, nextEdgeID(), a, b));
	}

	public boolean insertEdge(int v, int w) throws InvalidPositionException {
		if (v >= numVertices() || w >= numVertices()) {
			return false;
		}
		IndexVertex a = this.vList.get(v);
		IndexVertex b = this.vList.get(w);
		if (a != null && b != null) {
			return insertEdge(a, b);
		}
		return false;
	}

	public void insertVertex() {
		insertVertex(createVertex());
	}
	
	
	public static IndexGraph Grid(int n)
	{
		IndexGraph G = new IndexGraph(n*n);
	
		//edges in rows
		for(int i=0; i<n*n; )
		{
			for(int j=i; j<i+n-1; j++)
			{
				//System.out.println("Inserting edge "+j+" to "+(j+1));
				G.insertEdge(j, j+1);
			}
		    i=i+n;
		}
		
		//edges in cols
		for(int i=0; i<n;i++ )
		{
			int k=i;
			for(int j=0; j<n-1;j++ )
			{
				//System.out.println("Inserting edge "+k+" to "+(k+n));
				G.insertEdge(k, k+n);
				k=k+n;
			}
		    
		}
			
		return G;
	}
	public static IndexGraph random(int n, int m)
	{
		IndexGraph G = new IndexGraph(n);
		ArrayList<Integer> edges = new ArrayList<Integer>();
		for(int i=0; i<n; i++)
			for(int j=0; j<i; j++)
				edges.add(j*n+i);

		for(int i=0; i<m; i++)
		{
			int index = (int)(Math.random()*edges.size());
			int e = edges.remove(index);
			G.insertEdge(e%n, e/n);
			G.insertEdge(e/n, e%n);
		}
		return G;
	}

	public static IndexGraph random(int n, double p)
	{
		IndexGraph G = new IndexGraph(n);
		ArrayList<Integer> edges = new ArrayList<Integer>();
		for(int i=0; i<n; i++)
			for(int j=0; j<i; j++)
			{
				if(Math.random()<=p)
				{	G.insertEdge(i, j);
					G.insertEdge(j, i);
				}
			}
		return G;
	}

	public IndexVertex createVertex()
	{
		return new IndexVertex(this, numVertices());
	}
	//TO DO: Update
	public int degree(IndexVertex v,Iterable<IndexVertex> vs) {
		checkVertex(v);
		int d=0;
		for(IndexVertex w : vs)
		{
			if(this.areAdjacent(v, w))
				d++;
		}
		return d;
	}

	 /* O(vs) */
	public IndexVertex maxDegreeVertex(Iterable<IndexVertex> vs)
	{
		int maxDeg = -1;
		IndexVertex v = null;
		for(IndexVertex w : vs) {
			// System.out.println(w+ "has degree "+this.degree(w));
			int t = this.degree(w);
			if(t > maxDeg)
			{
				v = w;
				maxDeg = t;
			}
		}
		return v;
	}

	/*O(n)*/
	public boolean isPendant(IndexVertex v)
	{
		if(this.degree(v)==1){
			
			for(IndexVertex y:this.neighbours(v))
				if(this.degree(y)>1)
					return true;
		}
		
		return false;
		
	}
	
	//O(1)
	public boolean inMatchedEdge(IndexVertex u,IndexVertex v)
	{
		if(this.degree(v)==1 && this.degree(u)==1 && this.areAdjacent(u, v)&&this.numEdges()==1){
			return true;
		}
		
		return false;
		
	}
	
	
	//O(1)
	public boolean isMatched(IndexEdge<IndexVertex> e)
	{
		
		ArrayList<IndexVertex> ends = new ArrayList<IndexVertex>();
		for(IndexVertex x:this.endVertices(e))
		{
			ends.add(x);
		}
		return inMatchedEdge(ends.get(0),ends.get(1));
	
	}
	
	//O(vs)
	public IndexVertex MinDegreeVertex(Iterable<IndexVertex> vs)
	{
		int minDeg=Integer.MAX_VALUE;
		IndexVertex v = null;
		for(IndexVertex w : vs)
		{
			if(this.degree(w)<minDeg)
			{
				v=w;
				minDeg=this.degree(w);
			}
		}
		
		return v;
	}
	
	public int nextEdgeID() {
		return this.eList.size();
	}

	public int nextVertexID() {
		return this.vList.size();
	}

	@Override
	protected boolean removeEdgeOneWay(IndexEdge<IndexVertex> e,
			IndexVertex removed) throws InvalidPositionException {
		boolean s = this.adjacencyList.get(opposite(removed, e).id()).remove(e);
		if(s) eList.remove(e);
		// TODO: check consistency
		
		return s;
	}

	
	
	@Override
	//remove the given vertex and its incident edges.
	//update indexes of other vertices
	public boolean removeVertex(IndexVertex v) throws InvalidPositionException {
		checkVertex(v);
		boolean change = false;
		for (IndexEdge<IndexVertex> e : incidentEdges(v)) {
			change = removeEdgeOneWay(e, v) | change;
		}

		int last = this.vList.size() - 1;
		IndexVertex tempv = this.vList.get(last);
//		System.out.println("removing: "+v+"\nfrom: "+ vList);
//		System.out.println("size = "+vList.size());
		this.vList.remove(last);
//		System.out.println("size = "+vList.size());
		ArrayList<IndexEdge<IndexVertex>> templ = this.adjacencyList.remove(last);
		if (v.id() != last) {
//			System.out.println("size = "+vList.size());
			this.vList.replace(v.id(), tempv);
//			System.out.println("size = "+vList.size());
			this.adjacencyList.set(v.id(), templ);
			tempv.setId(v.id());
//			System.out.println("size = "+vList.size());
		}
//		System.out.println("after: "+vList);
		//System.out.println("vlist"+vList+"after removing"+v);
		return true;
	}
	
	public ArrayList<ArrayList<IndexVertex>> neighbourhoods()
	{
		ArrayList<ArrayList<IndexVertex>> ns = new ArrayList<ArrayList<IndexVertex>>();
		for(IndexVertex v : vertices())
		{
			VSubSet vs = new VSubSet(vertexset);
			for(IndexVertex x:neighbours(v))
				vs.add(x);
			//System.out.println(neighbours(v));
			//System.out.println(vs);
			ns.add((ArrayList<IndexVertex>) neighbours(v));
		}
		return ns;
	}

	@Override
	public String toString() {
		String s = "" + numVertices() + " nodes and " + numEdges()
				+ " edges.\n";
		for (IndexVertex v : vertices()) {
			boolean first = true;
			s += "" + v + ": ";
			for (IndexVertex n : incidentVertices(v)) {
				if (!first) {
					s += ",";
				}

				s += n;
				first = false;
			}
			s += "\n";
		}
		return s;
	}
}
/*
 * @Override public int getId(V v) { return v.id(); }
 *
 * public int getNextID() { return this.vList.size(); } public V getVertex(int
 * i) { return this.vList.get(i); } public Iterable<E> incidentEdges(V v) throws
 * InvalidPositionException { checkVertex(v); return
 * this.adjacencyList.get(v.id()); } public Iterable<V> incidentVertices(V v)
 * throws InvalidPositionException { ArrayList<V> vertices = new ArrayList<V>();
 * for (E edge : incidentEdges(v)) { vertices.add(edge.opposite(v)); } return
 * vertices; }
 *
 * // Auxiliary methods
 *
 * /** Insert and return a new edge with a given element between two vertices
 * Running time: O(1)
 *
 * public E insertEdge(V v, V w, E o) throws InvalidPositionException {
 * checkVertex(v); checkVertex(w); Edge<TVertex, V, E> ee = new Edge<TVertex, V,
 * E>(o, v, w, this.eList .size()); this.adjacencyList.get(v.id()).add(ee);
 * this.adjacencyList.get(w.id()).add(ee); this.eList.add(ee); return ee; }
 *
 * protected TVertex insertVertex(TVertex v) { if (this.vList.size() != v.id())
 * { throw new InvalidPositionException("incorrect id"); } this.vList.add(v);
 * ArrayList<Edge<TVertex, V, E>> al = new ArrayList<Edge<TVertex, V, E>>();
 * this.adjacencyList.add(al); return v; }
 *
 * /** Insert and return a new vertex with a given element. Running time: O(1)
 *
 * public TVertex insertVertex(V v) { // TVertex vv = new TVertex(v,
 * vList.size()); TVertex vv = createVertex(v, this.vList.size()); return
 * insertVertex(vv); }
 *
 * /** Default constructor that creates an empty graph // constructing generic
 * factory from vertex class was too slow // public AdjacencyListGraph(Class<?>
 * vertexcls) { // vList = new ArrayList<TVertex>(); // eList = new
 * ArrayList<Edge<TVertex, V, E>>(); // adjacencyList = new
 * ArrayList<ArrayList<Edge<TVertex, V, E>>>(); // vertexFactory = new
 * GenericFactory<TVertex>( // vertexcls, // Object.class, // int.class);
 *
 * public int[] intAdjacencyMatrix() { assert numVertices() <= 30; // 31?
 *
 * int[] m = new int[numVertices()]; Arrays.fill(m, 0); // int rowidx = 0; for
 * (TVertex v : vertices()) { // assert v.id() == rowidx; // int row = 0; for
 * (TVertex n : incidentVertices(v)) { m[v.id()] |= 1 << n.id(); } // m[rowidx]
 * = row; // rowidx++; }
 *
 * return m; }
 *
 * @Override public Iterator<TVertex> iterator() { return this.vList.iterator();
 * }
 *
 * /** Return number of edges
 *
 * @Override public int numEdges() { return this.eList.size(); }
 *
 * /** Return number of vertices
 *
 * @Override public int numVertices() { return this.vList.size(); }
 *
 * /** Return the other endvertex of an incident edge. Running time: O(1) public
 * TVertex opposite(TVertex v, Edge<TVertex, V, E> e) throws
 * InvalidPositionException { checkVertex(v); checkEdge(e); return
 * e.opposite(v); }
 *
 * public boolean remove(TVertex v) { if (this.vList.contains(v)) { return
 * false; } else { removeVertex(v); return true; } }
 *
 * /** Remove an edge and return its element. Running time: O(n) public E
 * removeEdge(Edge<TVertex, V, E> e) throws InvalidPositionException {
 * checkEdge(e); // System.out.printf("LR: %s %s\n", e.left(), e.right()); //
 * checkVertex(e.left()); // checkVertex(e.right());
 * this.adjacencyList.get(e.left().id()).remove(e);
 * this.adjacencyList.get(e.right().id()).remove(e); int last =
 * this.eList.size() - 1; Edge<TVertex, V, E> temp = this.eList.remove(last); if
 * (e.id() == last) { return temp.element(); } temp.setId(e.id());
 * this.eList.set(e.id(), temp); return e.element(); }
 *
 * /** Remove an edge and return its element. Running time: O(m) protected E
 * removeEdgeOneWay(Edge<TVertex, V, E> e, TVertex removed) throws
 * InvalidPositionException { checkEdge(e); TVertex other = opposite(removed,
 * e);
 *
 * this.adjacencyList.get(other.id()).remove(e); int last = this.eList.size() -
 * 1; Edge<TVertex, V, E> temp = this.eList.remove(last); if (e.id() == last) {
 * return temp.element(); } temp.setId(e.id()); this.eList.set(e.id(), temp);
 * return e.element(); // TODO: check consistency }
 *
 * /** Remove a vertex and all its incident edges and return the element stored
 * at the removed vertex. Running time: O(n^2)
 *
 *
 * /** Replaces the element of edge p with element o. Returns the former
 * element. Running time: O(1)
 *
 * public E replace(Edge<TVertex, V, E> p, E o) throws InvalidPositionException
 * { checkEdge(p); E temp = p.element(); p.setElement(o); return temp; }
 *
 * /** Replaces the element of vertex p with element o. Returns the former
 * element. Running time: O(1).
 *
 * public V replace(TVertex p, V o) throws InvalidPositionException {
 * checkVertex(p); V temp = p.element(); p.setElement(o);
 *
 * return temp; }
 *
 * @Override public int size() { return numVertices(); }
 *
 * @Override public V test() { return null; }
 *
 * @Override public String toDimacs() { StringBuffer s = new StringBuffer();
 * Formatter f = new Formatter(s); f.format("p edge %d %d\n", numVertices(),
 * numEdges()); for (Edge<TVertex, V, E> e : edges()) { f.format("e %s %s\n",
 * e.left().id() + 1, e.right().id() + 1); } return s.toString(); }
 *
 * @Override public String toGraphViz(String label) { StringBuffer s = new
 * StringBuffer(); Formatter f = new Formatter(s); f.format("graph {\n"); label
 * = label.replaceAll("\n", "\\n"); // what to do if nodes overlap
 * f.format("overlap = scale;\n"); // curve edges around nodes
 * f.format("splines = true;\n"); // node shape
 * f.format("node [shape=circle];\n"); // graph title
 * f.format("label = \"%s\";\n", label); toGraphVizNodes(f); toGraphVizEdges(f);
 * f.format("}\n"); return s.toString(); }
 *
 * protected void toGraphVizEdges(Formatter f) { for (Edge<TVertex, V, E> e :
 * edges()) { f.format("\"%s\" -- \"%s\";\n", e.left().hashCode(), e.right()
 * .hashCode()); } }
 *
 * protected void toGraphVizNodes(Formatter f) { // int i = 0; for (TVertex n :
 * vertices()) { // f.format("%s [ label = \"%d\" ];\n", n.hashCode(), ++i);
 * f.format("%s [ label = \"%d,d=%d\",height=%.2f ];\n", n.hashCode(), n.id(),
 * degree(n), 0.1 * degree(n)); } }
 *
 * /** Returns a string representation of the vertex and edge lists, separated
 * by a newline.
 *
 * @Override public String toString() { return this.vList.toString() + "\n" +
 * this.eList.toString(); }
 *
 * /** Return an iterator over the elements of all the vertices. Running time:
 * O(n)
 *
 * @Override public Iterable<V> vertexElements() { ArrayList<V> al = new
 * ArrayList<V>(this.vList.size()); for (TVertex e : this.vList) {
 * al.add(e.elem); } return al;
 *
 * }
 *
 * /** Return an iterator over the vertices of the graph
 *
 * @Override public Iterable<TVertex> vertices() { return this.vList; }
 *
 * }
 */
