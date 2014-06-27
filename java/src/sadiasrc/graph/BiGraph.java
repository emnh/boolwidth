package sadiasrc.graph;

import sadiasrc.exceptions.InvalidPositionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;

public class BiGraph extends IndexGraph implements
		IBiGraph<IndexVertex, IndexEdge<IndexVertex>> {
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
	VertexSet<IndexVertex> leftList;
	VertexSet<IndexVertex> rightList;
	ArrayList<Boolean> isLeft;


	/**
	 * Builds a Graph, labels vertices as left or right
	 */
	public BiGraph(int n) {
		this.leftList = new VertexSet<IndexVertex>(n);
		this.rightList = new VertexSet<IndexVertex>(n);
		this.isLeft = new ArrayList<Boolean>(n);
	}

	
	/**
	 * BiGraph is constructed in O(n + e) time.
	 *
	 * @param left
	 * @param graph
	 */
	public HashMap<IndexVertex, IndexVertex> originalVertex;
	public BiGraph(Collection<IndexVertex> left, IndexGraph graph) {
		this(graph.numVertices());
		HashMap<IndexVertex, IndexVertex> map = new HashMap<IndexVertex, IndexVertex>();
		originalVertex = new HashMap<IndexVertex, IndexVertex>();
		for(IndexVertex v : graph.vertices())
		{
			if(left.contains(v))
			{
				map.put(v, insertLeft());
				originalVertex.put(map.get(v), v);
			}
			else{
				map.put(v, insertRight());
				originalVertex.put(map.get(v), v);
			
			}
		}
		

		// add edges going between left and right
		for (IndexEdge<IndexVertex> e : graph.edges())
		{
			IndexVertex a = map.get(e.endVertices().get(0));
			IndexVertex b = map.get(e.endVertices().get(1));
			if (this.isLeft.get(a.id()) != this.isLeft.get(b.id())) 
				insertEdge(a,b);
		}
	}

	//what is this supposed to do?????
	public BiGraph(int l, int r,int t) {
		super();
		if (l < 0 || r < 0) {
			throw new IllegalArgumentException("Cannot create a BiGraph with "
					+ l + " left vertices and " + r + " right vertices");
		}

		this.leftList = new VertexSet<IndexVertex>(l+r);
		this.rightList = new VertexSet<IndexVertex>(l+r);
		this.isLeft = new ArrayList<Boolean>(t);
	}

	public BiGraph(int l, int r) {
		this(l+r);
		if (l < 0 || r < 0) {
			throw new IllegalArgumentException("Cannot create a BiGraph with "
					+ l + " left vertices and " + r + " right vertices");
		}
		
		for(int i=0;i<l;i++)
			insertLeft();			
		
		for(int i=0;i<r;i++)
			insertRight();
	}

	public BiGraph(BiGraph g) {
		this(g.leftVertices(),(IndexGraph)g);		
	}
	public BiGraph Complement(BiGraph g) {
		BiGraph comp = new BiGraph(g.leftVertices().size(),g.rightVertices().size(),g.leftVertices().size()+g.rightVertices().size());
		for(IndexVertex x:g.leftVertices())
			comp.insertLeft(x);
		for(IndexVertex x:g.rightVertices())
			comp.insertRight(x);
		for(IndexVertex x:g.leftVertices()){
			for(IndexVertex y:g.rightVertices()){
				if(!g.areAdjacent(x,y))
					comp.insertEdge(x, y);
			}
			}
		for(IndexVertex x:g.leftVertices()){
			for(IndexVertex y:g.leftVertices()){
				if(!g.areAdjacent(x,y))
					comp.insertEdge(x, y);
			}
			}
		for(IndexVertex x:g.rightVertices()){
			for(IndexVertex y:g.rightVertices()){
				if(!g.areAdjacent(x,y))
					comp.insertEdge(x, y);
			}
			}
		return comp;	
	}


	@Override
	public IndexVertex insertLeft() {
		IndexVertex c = createVertex();
		if(insertLeft(c))
			return c;
		else
			return null;
	}

	@Override
	public void insertVertex() {
		throw new UnsupportedOperationException("choose left or right");
	}
	/**
	 * O(1)
	 */
	public boolean insertLeft(IndexVertex v) {
		if (super.insertVertex(v)) {
			this.isLeft.add(v.id(),true);
			//System.out.println("Adding"+v.id()+"true");
			this.leftList.add(v);
			return true;
		}
		return false;
	}

	@Override
	public IndexVertex insertRight() {
		IndexVertex c = createVertex();
		if(insertRight(c))
			return c;
		else
			return null;
	}

	/**
	 * O(1)
	 */
	public boolean insertRight(IndexVertex v) {
		if (super.insertVertex(v)) {
			this.isLeft.add(v.id(),false);
		//	System.out.println("Adding"+v.id()+"false");
			this.rightList.add(v);
			return true;
		}
		return false;
	}

	@Override
	public boolean insertVertex(IndexVertex v) {
		throw new UnsupportedOperationException();
	}

	public boolean isLeft(IndexVertex v) throws InvalidPositionException {
		checkVertex(v);
		return this.isLeft.get(v.id());
	}

	public boolean isRight(IndexVertex v) throws InvalidPositionException {
		checkVertex(v);
		return !this.isLeft.get(v.id());
	}
	
	//create Bigraph from vertex set
	public static BiGraph subgraph(Collection<IndexVertex> vs, BiGraph g) {
	    VertexSet<IndexVertex> leftset=new VertexSet<IndexVertex>();
	    for(IndexVertex v:vs)
	    {
	    	if(g.leftList.contains(v))
	    		leftset.add(v);
	    }
	    BiGraph BG = new BiGraph(leftset.size(),vs.size()-leftset.size());
	    for (IndexVertex v : vs) {
				if(leftset.contains(v))
					BG.insertLeft(v);
				else 
					BG.insertRight(v);
			}
		
		// add edges going between left and right
		for (IndexEdge<IndexVertex> e : g.edges()) {
			if (BG.isLeft(e.endVertices().get(0)) && BG.isRight(e.endVertices().get(1))
			 || BG.isLeft(e.endVertices().get(1)) && BG.isRight(e.endVertices().get(0))) 
			{
				BG.insertEdge(e);
			}
		}
		return BG;
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
	public Collection<IndexVertex> leftVertices() {
		return this.leftList;
	}

	public int numLeftVertices() {
		return this.leftList.size();
	}

	public int numRightVertices() {
		return this.rightList.size();
	}

	public static BiGraph random(int l, int r, int m) {
		BiGraph h = new BiGraph(l, r);
		ArrayList<Integer> pairs = new ArrayList<Integer>(l + r);
		ArrayList<IndexVertex> al = new ArrayList<IndexVertex>();
		ArrayList<IndexVertex> ar = new ArrayList<IndexVertex>();
		for (IndexVertex v : h.leftVertices()) {
			al.add(v);
		}
		for (IndexVertex v : h.rightVertices()) {
			ar.add(v);
		}

		for (int a = 0; a < l; a++) {
			for (int b = 0; b < r; b++) {
				pairs.add(a * r + b); // danger for overflow?
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
			//System.out.println("Trying to insert "+al.get(il)+" -> "+ar.get(ir));
			h.insertEdge(al.get(il), ar.get(ir));
		}
		return h;
	}

	@Override
	public boolean removeVertex(IndexVertex v) {
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

		return true;
	}

	//TODO:fix VertexSets after removing and deleting vertices
	/**
	 * @return List containing only right vertices which have a crossing edge
	 *         Works in O(1) time
	 */
	public Collection<IndexVertex> rightVertices() {
		return this.rightList;
	}

	@Override
	protected void toGraphVizNodes(Formatter f) {
		f.format("subgraph cluster_0 {\n");
		f.format("label = \"left\";\n");
		f.format("color = red;\n");

		for (IndexVertex n : leftVertices()) {
			f.format("%s [ label = \"%d\" ];\n", n.id(), n.id());
		}
		f.format("}\n");

		f.format("subgraph cluster_1 {\n");
		f.format("label = \"right\";\n");
		f.format("color = blue;\n");

		for (IndexVertex n : rightVertices()) {
			f.format("%s [ label = \"%d\" ];\n", n.id(), n.id());
		}
		f.format("}\n");
	}

	
}
