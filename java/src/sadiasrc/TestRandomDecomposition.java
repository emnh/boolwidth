import java.util.ArrayList;

import sadiasrc.io.ControlInput;
import sadiasrc.decomposition.CountMIS;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.heuristic.MinDegreeSeqBalanceSplit;
import sadiasrc.heuristic.RandomDecomposition;

public class TestRandomDecomposition {

	public static void main(String[] args) 
	{
		int n = 50;
		IndexGraph G = IndexGraph.random(n, 0.15);
//		IndexGraph G = new IndexGraph(9);
//		G.insertEdge(0,2); G.insertEdge(6,8);
//		for(int i=1;i<=8;i++) G.insertEdge(i-1,i);
//		String fileName =  ControlInput.GRAPHLIB + "prob/weeduk.dgf";
//		ControlInput cio= new ControlInput();
//		IndexGraph G=new IndexGraph();
//		G =	cio.getTestGraph(fileName, G);
		//System.out.println(G);
		
		String fileName =  ControlInput.GRAPHLIB+ "protein/1ail_graph.dimacs";
		
		ControlInput cio= new ControlInput();
		
		
		G = new IndexGraph();
		G=cio.getTestGraph(fileName, G);
		
		long mid1=System.currentTimeMillis();
		long cvalue=CountMIS.countMIS(G);
		long mid2=System.currentTimeMillis();
		System.out.println("#MaxIS"+cvalue+"after"+(mid2-mid1)+" ms ");
	
		long start = System.currentTimeMillis();
		DisjointBinaryDecomposition decomp = RandomDecomposition.randomDBD(G);
		//System.out.println(decomp);
		long bw = CutBool.countMIS(decomp);
		long end = System.currentTimeMillis();
		System.out.println("Boolean-width is: "+Math.log(bw)/Math.log(2)+" in time(msec) : "+ (end-start));
		
//		ArrayList<IndexVertex> sequence= MinDegreeFillin.sequence(G);
//		System.out.println("Sequence"+sequence);
//		DisjointBinaryDecomposition decomp1 = MinDegreeSeqBalanceSplit.DBDbyMDSplit(G);
//		System.out.println(decomp1);
//		System.out.println("Boolean-width is: log("+CutBool.countMIS(decomp)+")");
//		System.out.println("Boolean-width is: log("+CutBool.countMIS(decomp1)+")");
//		decomposition.TreeDecomposition td = RandomDecomposition.randomTD(G);
//		System.out.println(td);
//		System.out.println("Treewidth is: "+td.treewidth());
	}
}
