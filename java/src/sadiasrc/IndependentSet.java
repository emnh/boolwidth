import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VertexSet;

public class IndependentSet {

    @SuppressWarnings("unchecked")
	public static int linIS(IndexGraph g, List<IndexVertex> order)
	{
		Map<VertexSet<IndexVertex>,Integer> sizes = new HashMap<VertexSet<IndexVertex>, Integer>();
		sizes.put(new VertexSet<IndexVertex>(), 0);
		VertexSet<IndexVertex> seen = new VertexSet<IndexVertex>();
		int maxIS = 0;
		
		for(IndexVertex v : order)
		{
			seen.add(v);
			Map<VertexSet<IndexVertex>,Integer> newsizes = new HashMap<VertexSet<IndexVertex>, Integer>();
			for(Entry<VertexSet<IndexVertex>, Integer> e : sizes.entrySet())
			{
				VertexSet<IndexVertex> set = e.getKey();
				int size = e.getValue();
				if(set.contains(v))
				{
					maxIS = Math.max(maxIS, size);
					set.remove(v);
					if(!newsizes.containsKey(set) || newsizes.get(set)<size)
						newsizes.put(set, size);
				}
				else
				{
					if(!newsizes.containsKey(set) || newsizes.get(set)<size)
						newsizes.put(set, size);

					VertexSet<IndexVertex> newset = (VertexSet) set.clone();
					size++;
					maxIS = Math.max(maxIS, size);

					for(IndexVertex u : g.neighbours(v))
					{
						if(!seen.contains(u))
							newset.add(u);
					}
					if(!newsizes.containsKey(newset) || newsizes.get(newset)<size)
						newsizes.put(newset, size);
				}
			}
			sizes = newsizes;
		}
		
		int maxsize = 0;
		for(int size : sizes.values())
		{	
			maxsize = Math.max(maxsize, size);
		}
		return maxsize;
	}
}

