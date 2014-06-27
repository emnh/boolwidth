import java.io.File;

import sadiasrc.modularDecomposition.*;
import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sadiasrc.util.IndexedSet;
import sadiasrc.decomposition.BKMIS;
import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.BranchonBKMIS;
import sadiasrc.decomposition.BronKerBoschCliqueFinder;
import sadiasrc.decomposition.CountMIS;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.decomposition.SimpleTreedecomposition;
import sadiasrc.decomposition.TomitaMIS;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.BasicGraphAlgorithms;

public class TestBFS{
		
	public static void main(String[] args)
	{
		
		String fileName =  ControlInput.GRAPHLIB+ "freq/celar08-wpp.dgf";
		
		ControlInput cio= new ControlInput();
		
		
		IndexGraph G = new IndexGraph();
		G=cio.getTestGraph(fileName, G);
		Preprocessing p = new Preprocessing(G);
		p.Preprocess(G);
//		String filen= ControlInput.GRAPHLIB+ "other/1.txt"; 
//		String adjlist=new String();
//		
//		//			BiGraph G ;
////			G = BiGraph.random(25, 25, 200);
//		
//			try {
//				System.out.println("Writing to file.."+filen);
//				FileWriter fstream = new FileWriter(filen);
//				BufferedWriter out = new BufferedWriter(fstream);
//				//BufferedWriter out = new BufferedWriter(new FileWriter(filen, true));
//				for(IndexVertex v:G.vertices())
//					{
//						
//					    System.out.print(v.id()+"->");
//					    adjlist+=v.id()+"->";
//						out.write(v.id()+"->");
//						int nc=0;
//						for(IndexVertex n:G.neighbours(v))
//						{
//							nc++;
//							
//							System.out.print(n.id());
//							adjlist+=n.id();
//							out.write(n.id());
//							if(nc<G.degree(v)){
//								System.out.print(",");
//								adjlist+=",";
//								out.write(",");
//							}
//						}
//						out.write("\n");
//						System.out.print("\n");
//						adjlist+="\n";
//					}
//
//				out.close();
//				}
//			catch (IOException e) { //System.err.println("Error: " + e.getMessage()); 
//				
//			}
//			System.out.println("adj"+adjlist);
//			Graph g = new Graph(adjlist);
//			System.out.println(g.getMDTree());
////		System.out.println(G.numVertices()+" "+G.numEdges());
//		for(IndexEdge<IndexVertex> e:G.edges())
//			System.out.println(e.endVertices().get(0).id()+" "+e.endVertices().get(1).id());

		ArrayList<IndexVertex> seq = MinDegreeFillin.sequence(G);
		
//		for(IndexVertex v : seq)
//			System.out.println(v+",");
		System.out.println("seq");
		SimpleTreedecomposition t = new SimpleTreedecomposition(G, seq);
		System.out.println("decomp:");
		System.out.println(t);
		System.out.println("Treewidth is: "+(t.treewidth()-1));
		t.removeRed();	
		System.out.println("decomp:After redundent vertex reduction");
		System.out.println(t);
		t.makealleaf();
		System.out.println("decomp:After making all leaves");
		System.out.println(t);
		t.cleanUp();
		System.out.println("decomp:After clean up");
		System.out.println(t);
		DisjointBinaryDecomposition decomp = t.createBinDecomp();
		System.out.println("decomp:Disjoint Binary");
		System.out.println(decomp);
		long bw = CutBool.countMIS(decomp);
		System.out.println("Boolean-width is: log("+bw+")="+Math.log(bw)/Math.log(2));
		
//		long tn = System.currentTimeMillis();
//		BiGraph G;
		
//		for(int i=0;i<1;i++)
//		{
////			G = BiGraph.random(500, 500 ,200 );
////		 IndexGraph G = IndexGraph.random(600,0.9);
//		CountMIS cmis= new CountMIS();
//		long t1 = System.currentTimeMillis();
//		long val= cmis.countMIS(G);
//		 System.out.println("Edges"+G.numEdges());
//		long t2 = System.currentTimeMillis();
//		System.out.println("Val countmis = "+val+" after "+(t2-t1)+ "msec");
//		//long bvalue=BoolDecomposition.boolDimBranchmin(G);
//		long t3 = System.currentTimeMillis();
//	//	System.out.println("Val branchmisMin= "+bvalue +" after "+(t3-t2)+ "msec");
//		long bmaxvalue = BoolDecomposition.boolDimBranch(G);
//		long tn = System.currentTimeMillis();
//		System.out.println("Val branchmisMax = "+bmaxvalue +" after "+(tn-t3)+ "msec");
//		
//		TomitaMIS b=new TomitaMIS(G);
//		long t4 = System.currentTimeMillis();
//		long m = b.getAllMaximalIS(G);
//		long t5 = System.currentTimeMillis();
//		System.out.println("Val tomitamis = "+m +" after "+(t5-t4)+ "msec");
//		
//		BKMIS bk=new BKMIS(G);
//		long t6 = System.currentTimeMillis();
//		long n = bk.getAllMaximalIS();
////		long n=CutBool.countNeighborhoodsbyListing(G);
//		long t7 = System.currentTimeMillis();
//		System.out.println("Val bkmis = "+n +" after "+(t7-t6)+ "msec");
//		}
//		BiGraph G=BiGraph.random(30,30,200);
//		G = new BiGraph(2,2);
//		G.insertEdge(0, 2);
//		G.insertEdge(1, 3);
//		G.insertEdge(0, 3);
//		G.insertEdge(1, 2);
		
//		G = new BiGraph(2,4);
//		G.insertEdge(0, 2);
//		G.insertEdge(0, 3);
//		G.insertEdge(1, 2);
//		G.insertEdge(1, 3);
//		G.insertEdge(1, 4);
//		G.insertEdge(1, 5);
		
		//System.out.println(G);
//		BronKerBoschCliqueFinder b= new BronKerBoschCliqueFinder(G);
//		Collection<VSubSet> cliques = b.getAllMaximalCliques();
//		System.out.println(cliques.size());
//		BiGraph G;
//		for(int j=0;j<10;j++)
//		{
//		for(int i=10;i<51;i++)
//		{
//		BiGraph G = BiGraph.random(10 ,10, 50);
//		//System.out.println("here");
//		long start=System.currentTimeMillis();
//		long bvalue=BoolDecomposition.boolDimBranch(G);
//		long mid=System.currentTimeMillis();
//	//	System.out.print(j+" th run "+ i+" "+"200");
//		System.out.println(" after "+(mid-start)+" ms CB by branching "+bvalue);
//		
		
//		long cvalue=CutBool.countNeighborhoodsbyListing(G);
//		long end=System.currentTimeMillis();
//		System.out.print("after "+(end-mid)+ "ms"+ cvalue);
		//System.out.println(" ms and "+(end-mid)+" ms");
		//System.out.println(G);
//		long start = System.currentTimeMillis();
//		long bvalue=BoolDecomposition.boolDimBranchmin(G);
//		long mid = System.currentTimeMillis();
//		//long cvalue=CutBool.countNeighborhoodsbyListing(G);
//		System.out.println("branch value = " +bvalue+" = "+ "after"+(mid-start)+"ms");
//		BKMIS b=new BKMIS(G);
//		long m = b.getAllMaximalIS();
//		long mid1 = System.currentTimeMillis();
//		System.out.println("BKMIS = "+m+"after"+(mid1-mid)+"ms");
		
//		BranchonBKMIS c=new BranchonBKMIS(G);
//		long bval=c.BKMISCon(G);
//		System.out.println("="+bval);
//		BiGraph compG=G.Complement(G);
//		System.out.println(compG);
//		BronKerBoschCliqueFinder b1=new BronKerBoschCliqueFinder(compG);
//		cliques = b1.getAllMaximalCliques();
//		System.out.println(cliques.size());
				
		//System.out.println(G);
		
		}
		}