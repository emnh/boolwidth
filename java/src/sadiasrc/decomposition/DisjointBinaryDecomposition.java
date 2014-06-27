package sadiasrc.decomposition;

import java.util.Iterator;

import sadiasrc.graph.IGraphElement;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexGraphSubSet;
import sadiasrc.graph.IndexVertex;

/**
 * A DisjointBinaryDecomposition has the invariant that for every DecompNode p
 * with children l and r we have.
 * getSubSet(l).intersects(getSubSet(r)) = false
 * In addition it follows from BinaryDecomposition that 
 * getSubSet(l).union(getSubSet(r)) is a subset of getSubSet(p) 
 * @author MaVa
 *
 */
public class DisjointBinaryDecomposition extends BinaryDecomposition {

	//creates a decomposition with one bag and all vertices of G are placed in that bag
	public DisjointBinaryDecomposition(IndexGraph G) {
		super();
		this.graph=G;
		//create a root containing all vertices of G
		DecompNode root = newBag();
		for(IndexVertex v : G)
		{
			root.gss.add(v);
		}
		//add the root
		addRoot(root);
	}

	public boolean splitBag(DecompNode b)
	{
		if(b==null)
		{	System.out.println("Found null");
			return false;
		}
		if(b.getGraphSubSet().vertices().isEmpty())
		{	System.out.println("Found empty");
			return false;
		}
		int i=0;
		if(b.getGraphSubSet().vertices().size()==1)
			return true;
		boolean change = false;
		for(IndexVertex v : b.getGraphSubSet().vertices())
		{
			if(i%2==0)
				addLeft(b, v);
			else
				addRight(b,v);
			i++;
		}
		return splitBag(b.left) && splitBag(b.right);
	}

	protected boolean canAddVertex(DecompNode w, IndexVertex v) {
		//Can not add vertices that does not belong to the graph we are decomposing
		if (!this.graph().contains(v))
		{
			return false;
		}
		//can not add to nodes that are not part of this decomposition
		if(!contains(w))
			return false;
		//Can not add to a bag that already have the vertex
		if(root().equals(w) || w.getGraphSubSet().contains(v))
			return false;

		//find the first ancestor that has the vertex v
		DecompNode p = parent(w);
		DecompNode child = w;
		while(!getSubSet(p).contains(v))
		{
			child = p;
			p=parent(p);
		}
		//if there is only one child of p there can not be any conflict
		if(!hasLeft(p) || !hasRight(p))
			return true;
		//the other child of p
		DecompNode child2;
		if(left(p).equals(child))
			child2 = right(p);
		else
			child2 = left(p);
		//if already added to child2 we can not add to child
		if(getSubSet(child2).contains(v))
			return false;
		return true;
	}
	
	@Override
	protected boolean canAddLeft(DecompNode p, IndexVertex v) {
		//if there already is a left child we check directly
		if(hasLeft(p))
		{
			return canAddVertex(left(p), v);
		}
		//if v already has been added to the right side we can not add it to the left side
		if (hasRight(p) && right(p).getGraphSubSet().contains(v)) {
			return false;
		}
		//if v is added to p we can safely add v
		if(p.getGraphSubSet().contains(v))
			return true;
		//else we have to check if v can be added to p
		return canAddVertex(p, v);
	}

	@Override
	protected boolean canAddRight(DecompNode p, IndexVertex v) {
		//if there already is a right child we check directly
		if(hasRight(p))
		{
			return canAddVertex(right(p), v);
		}
		//if v already has been added to the left side we can not add it to the right side
		if (hasLeft(p) && left(p).getGraphSubSet().contains(v)) {
			return false;
		}
		//if v is added to p we can safely add v
		if(p.getGraphSubSet().contains(v))
			return true;
		//else we have to check if v can be added to p
		return canAddVertex(p, v);
	}

	@Override
	public boolean isComplete() {
		for(DecompNode bag : vertices())
		{
			int nw = bag.getGraphSubSet().numVertices();
			//if a leaf has more than one vertex the decomposition is not complete
			if(isExternal(bag) && nw>1)
				return false;
			else
			{
				//TODO: should we allow complete decompoitions to have vertices with only 1 child?
				if(!hasLeft(bag) || !hasRight(bag))
					return false; 
				int nl = left(bag).getGraphSubSet().numVertices();
				int nr = right(bag).getGraphSubSet().numVertices();
				//checking it the union of the two children is disjoint
				if(nl+nr!=nw)
					return false;
			}
		}
		//found nothing wrong
		return true;
	}

	/** Running time: O (n log n) */
	public static boolean isDisjoint(IndexGraphSubSet set1, IndexGraphSubSet set2) {
		boolean contains = false;
		Iterator<IGraphElement> it = set2.iterator();
		while (it.hasNext()) {
			contains |= set1.contains(it.next());
			if (contains) {
				return !contains;
			}
		}
		return !contains;
	}

	public static boolean isSubset(IndexGraphSubSet set, IndexGraphSubSet sub) {
		return set.containsAll(sub);
	}
}
