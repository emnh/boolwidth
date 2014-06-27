package sadiasrc.heuristic;

import java.util.Collection;

import sadiasrc.graph.IndexVertex;

public interface GreedyChooser {

	//Input: left of the ordering (so far generated) as parameter
	//Output : next vertex chosen from right
	public IndexVertex next(Collection<IndexVertex> A);
	
	//Input: A - left of the ordering (so far generated)
	//		 B - right of the ordering (rest) 
	//       C - N(left) of the ordering in right 
	//Output : next vertex chosen from N(left) or right
	
	public IndexVertex next(Collection<IndexVertex> A,Collection<IndexVertex> B,Collection<IndexVertex> C);
}
