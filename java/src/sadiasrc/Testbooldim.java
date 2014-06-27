import sadiasrc.io.ControlInput;

import java.util.Collection;

import sadiasrc.decomposition.BKMIS;
import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.BronKerBoschCliqueFinder;
import sadiasrc.decomposition.CountMIS;
import sadiasrc.decomposition.CutBool;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.VSubSet;

public class Testbooldim {
	
	public static void main()
	{
		String fileName =  ControlInput.GRAPHLIB+ "other/hsu-2x2.dimacs";
		
		ControlInput cio= new ControlInput();
		
		
		IndexGraph G = new IndexGraph();
		G=cio.getTestGraph(fileName, G);
//		BiGraph G;
//		G = new BiGraph(4,2);
//		G.insertEdge(0, 4);
//		G.insertEdge(1, 5);
//		G.insertEdge(2, 5);
//		G.insertEdge(3, 4);
//		G.insertEdge(3, 5);
//		G = BiGraph.random(25, 25 , 200);
		
		System.out.println("here");
		long start=System.currentTimeMillis();
		
		long mid1=System.currentTimeMillis();
		long cvalue=CountMIS.countMIS(G);
		long mid2=System.currentTimeMillis();
		System.out.println("#MaxIS"+cvalue+"after"+(mid1-start)+" ms ");
		System.out.println(G);
		BKMIS b=new BKMIS(G);
		long BKMIS = b.getAllMaximalIS();
		long end = System.currentTimeMillis();
		System.out.println("BKMIS"+BKMIS + "after"+(end-mid2)+" ms ");
		
	}

}
