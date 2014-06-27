import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.MinDegreeFillin;

import java.util.ArrayList;

import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.decomposition.SimpleTreedecomposition;


public class BoolDecompFromTreeDecomp {
	
	
	public static DisjointBinaryDecomposition BoolWfromTW(IndexGraph G, SimpleTreedecomposition t)
	{
		long start=System.currentTimeMillis();
//		ArrayList<IndexVertex> seq = MinDegreeFillin.sequence(G);
//		SimpleTreedecomposition t = new SimpleTreedecomposition(G, seq);
//		//System.out.println("decomp:");
//System.out.println(t);
//		System.out.println("Treewidth : "+(t.treewidth()-1));
//		if((System.currentTimeMillis()-start)>30*60*1000)
//			return 0;
		t.removeRed();	
		System.out.println("decomp:After Redundant vertex reduction");
		//System.out.println(t);
//		if((System.currentTimeMillis()-start)>30*60*1000)
//			return 0;
		t.makealleaf();
		System.out.println("decomp:After making all leaves");
		//System.out.println(t);
//		if((System.currentTimeMillis()-start)>30*60*1000)
//			return 0;
		t.cleanUp();
		System.out.println("decomp:After clean up");
		//System.out.println(t);
	//	if((System.currentTimeMillis()-start)>30*60*1000)
	//		return 0;
		DisjointBinaryDecomposition decomp = t.createBinDecomp();
		System.out.println("decomp:Disjoint Binary");
		//System.out.println(decomp);
	//	if((System.currentTimeMillis()-start)>30*60*1000)
	//		return 0;
		
		return decomp;
	}


}
