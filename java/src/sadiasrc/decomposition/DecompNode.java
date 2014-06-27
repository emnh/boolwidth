package sadiasrc.decomposition;

import java.util.HashMap;
import java.util.Set;

import sadiasrc.util.IndexedSet;

import sadiasrc.graph.IVertex;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.InducedSubgraph;

public class DecompNode extends BinBag<DecompNode>
{
	boolean isLeaf;
	IndexVertex v;
	HashMap<String, Object> attributes = new HashMap<String, Object>();

  
	public void setAttr(String key, Object value) {
		this.attributes.put(key, value);
	}

    @SuppressWarnings("unchecked")
	public <T> T getAttr(String key) {
		if (this.attributes.containsKey(key)) {
			return (T) this.attributes.get(key);
		} else {
			// TODO: get default from configuration
			return null;
		}
	}

	public DecompNode(BinaryDecomposition owner, int index) {
		super(owner, index);
		this.gss = new InducedSubgraph(owner.graph());
	}

	//gets the node mapped to a leaf
	//this method is not neccesarry as one can use getGraphSubset()
	IndexVertex getNode()
	{
		if(isLeaf)
			return v;
		else
			//TODO: throw new NotLeafException();
			return null;
	}
	public DecompNode left(DecompNode n)
	{
		if(n.isLeaf)
			return null;
		else
			return n.getLeft();
		
	}
	public DecompNode right(DecompNode n)
	{
		if(n.isLeaf)
			return null;
		else
			return n.getRight();
		
	}
	public DecompNode parent(DecompNode n)
	{
		if(n.hasParent())
			return n.getParent();
		else
			return null;
		
	}
	public Set<IndexVertex> subSet(DecompNode n)
	{
		Set<IndexVertex> vertexSet = new IndexedSet<IndexVertex>(n.getGraphSubSet().vertices().size());
		for(IndexVertex v: n.getGraphSubSet().vertices())
			vertexSet.add(v);		
		return vertexSet;		
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id="+id()+",{");
		for(IndexVertex v : gss.vertices())
			sb.append(v.toString());
		sb.append('}');
		return sb.toString();
	}
}
