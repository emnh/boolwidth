package sadiasrc.heuristic;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;

public class MinDegreeSeqBalanceSplit {

	public static DisjointBinaryDecomposition DBDbyMDSplit(IndexGraph G)
	{
		//Start out by making a decomposition with only one bag called the root
		//the root contains all vertices of $G$
		ArrayList<IndexVertex> sequence= MinDegreeFillin.sequence(G);
		System.out.println("Sequence"+sequence);
		DisjointBinaryDecomposition decomp = new DisjointBinaryDecomposition(G);
		//keep a stack of those nodes that need to be split further, initially containing the root
		Stack<DecompNode> s = new Stack<DecompNode>();
		s.push(decomp.root());
		//get the mindegree sequence here
		
		
		while(!s.isEmpty())
		{
			//pick next node from stack to split
			DecompNode p = s.pop();
			
			ArrayList<IndexVertex> seqinNode=new ArrayList<IndexVertex>();
			Collection<IndexVertex> pv=new ArrayList<IndexVertex>();
			int sizeofNode = p.getGraphSubSet().numVertices();
			System.out.println("size"+sizeofNode);
			pv=p.getGraphSubSet().vertices();
			
			if(sizeofNode > 1 )
			{
				//Create sequence for smaller components from the original sequence
				if(!p.equals(decomp.root()))
				{
					for(IndexVertex v:sequence)
						if(pv.contains(v))
							seqinNode.add(v);					
				}
				else
					seqinNode=sequence;
				System.out.println("seq"+seqinNode);

				if(sizeofNode==2)
				{
					decomp.addLeft(p, seqinNode.get(0));
					decomp.addRight(p, seqinNode.get(1));
					
				}
				else
				{
					int inLeft=0;
					while(inLeft< Math.ceil((sizeofNode/3)))
					{
						decomp.addLeft(p, seqinNode.get(inLeft));
						//System.out.println("Added to left"+seqinNode.get(inLeft));
						inLeft++;
									
					}
					for(int i=inLeft;i<sizeofNode;i++)
					{				
						decomp.addRight(p,seqinNode.get(i));
					}
				}
			}//end if sizeofnode
			//push the children on the stack
			if(p.hasLeft())
			{
				System.out.println("Pushing left");
				s.push(p.getLeft());
			}
			if(p.hasRight())
			{
				System.out.println("Pushing rightt");
				s.push(p.getRight());
			}
		}//end while
		return decomp;
	}

}
