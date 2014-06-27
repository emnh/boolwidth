package sadiasrc.heuristic;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.Collection;

public class MinDegNeighbour implements GreedyChooser {

	IndexGraph G;
	public MinDegNeighbour(IndexGraph G) {
		this.G = G;
	}
	@Override
	public IndexVertex next(Collection<IndexVertex> A) {
		BiGraph H = new BiGraph(A, G);
		
		IndexVertex minv = null;
		for(IndexVertex v : H.leftVertices())
		{
			if(H.degree(v)>0)
			{
				if(minv==null || H.degree(v) < H.degree(minv))
					minv = v;
			}
		}
		
		if(minv==null)
		{
			int mindeg = G.numVertices();
			for(IndexVertex v : H.rightVertices())
			{
				if(G.degree(H.originalVertex.get(v))<mindeg)
					minv=v;
			}
			
		}
		else
		{
			minv = H.neighbours(minv).iterator().next();
		}
		
		return H.originalVertex.get(minv);
	}
	@Override
	public IndexVertex next(Collection<IndexVertex> A,
			Collection<IndexVertex> B, Collection<IndexVertex> C) {
		// TODO Auto-generated method stub
		return null;
	}

}
