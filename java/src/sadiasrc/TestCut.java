import java.io.File;

	import sadiasrc.modularDecomposition.*;
	import sadiasrc.heuristic.*;
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
import sadiasrc.decomposition.MaximumMatching;
import sadiasrc.decomposition.SimpleTreedecomposition;
import sadiasrc.decomposition.TomitaMIS;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.graph.BasicGraphAlgorithms;

	public class TestCut{
			
		public static void main(String[] args)
		{
			
			String fileName =  ControlInput.GRAPHLIB+ "other/g.txt";
			
			ControlInput cio= new ControlInput();
			
			
			IndexGraph G = new IndexGraph();
			G=cio.getTestGraph(fileName, G);
//			long start=System.currentTimeMillis();
			System.out.println(G.numVertices()+ " "+ G.numEdges());
			System.out.println();
			BiGraph g;

			g = new BiGraph(5,5);
			g.insertEdge(0, 5);
			g.insertEdge(0, 6);
			g.insertEdge(1, 5);
			g.insertEdge(1, 6);
			g.insertEdge(2, 6);
			g.insertEdge(2, 7);
			g.insertEdge(3, 7);
			g.insertEdge(3, 8);
			g.insertEdge(3, 9);
			g.insertEdge(4, 7);
			g.insertEdge(4, 8);
			g.insertEdge(4, 9);
			System.out.println(g);
			System.out.println(g.vertices());
			System.out.println("UB Exact LB");
			//for(int i=0;i<626;i+=5)
			{
				//for(int j=0;j<5;j++)
				{
			//g = BiGraph.random(25, 25 , i);
			Preprocessing p = new Preprocessing(g);
			p.Preprocess(g);
//			System.out.println(g);
//			System.out.println(g.vertices());
//			MaximumMatching M=new MaximumMatching();
//			int mm=M.maximumMatching(g);
//			System.out.println("mm="+mm);
            
			//long ub= CutBool.CutUB(g);
			//long lb = CutBool.CutLB(g);
			long bd = BoolDecomposition.BoolDimBranch(g);
			//System.out.print(ub);
			
			System.out.print("Cut-bool is : "+bd);
			//System.out.println(" "+lb);
				}
			}
			//create sequence
/*			GreedyChooser chooser = new MinDegNeighbour(G);
//			ArrayList<IndexVertex> seq =TwoPartition.buildSequence(G, chooser);
			ArrayList<IndexVertex> seq =TwoPartition.CreateSequenceSYMDiff(G);
			
			System.out.println("Seq :"+seq);
			
			
			
			ArrayList<IndexVertex> left = new ArrayList<IndexVertex>(G.numVertices());
			IndexedSet<IndexVertex> groundSet = new IndexedSet<IndexVertex>(G.vertices());
			VSubSet Left = new VSubSet(groundSet);
			long ub=0,max=0,min =Integer.MAX_VALUE;
			
			//compute cutbool for every cut in the sequence //caterpillar
			for(IndexVertex v: seq)
			{
				Left.add(v);
				VSubSet c= new VSubSet(Left);
			    ub = CutBool.countMIS(G,c);
			    max=Math.max(max, ub);
			    if(Left.size()>=(G.numVertices()/3) && Left.size()<=(2*(G.numVertices()/3))){
			    	min=Math.min(min, ub);
			    	left = new ArrayList<IndexVertex>(G.numVertices());
			    	for(IndexVertex x: Left)
			    		left.add(x);
			   // System.out.println("Left"+left+"UB"+ub);
			    }
			}
			long end = System.currentTimeMillis();
			System.out.println("Cutbool="+max+" after "+(end-start)+" ms from caterpillar");
//			System.out.println("Min="+min);
//			System.out.println("Left"+left);
			DisjointBinaryDecomposition decomp= TwoPartition.BisectionfromSymmetricDiff(G, seq);
			System.out.println(decomp);
			System.out.println("Boolean-width is: log("+CutBool.countMIS(decomp)+")");*/
			
			//System.out.println("here");
//			long start=System.currentTimeMillis();
//			
//			
//			long cvalue=CountMIS.countMIS(G);
//			long mid1=System.currentTimeMillis();
//			System.out.println("#MaxIS "+cvalue+" after "+(mid1-start)+" ms ");
//////			System.out.println(G);
///*			BKMIS b=new BKMIS(G);
//			long mid2=System.currentTimeMillis();
//			long BKMIS = b.getAllMaximalIS();
//			long mid3 = System.currentTimeMillis();
//			System.out.println("BKMIS "+BKMIS + " after "+(mid3-mid2)+" ms ");*/
//			
//			TomitaMIS t=new TomitaMIS(G);
//			long mid4=System.currentTimeMillis();
//			long TomitaMIS = t.getAllMaximalIS(G);
//			long mid5 = System.currentTimeMillis();
//			System.out.println("TomitaMIS "+TomitaMIS + " after "+(mid5-mid4)+" ms ");
		}
			
		
}
