package sadiasrc.decomposition;

import sadiasrc.io.ControlInput;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import sadiasrc.util.IndexedSet;

import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.CutBool;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public class TestbooldimbranchvsList {

	public static void main(String[] args) throws IOException
	{
		int c = 0;
		double d=0.1;
		BiGraph G = null;
		IndexGraph G1=null;
		//IndexGraph G1=null;
		if(c==0)
		{
			G1 =IndexGraph.random(100,d);	//random(20,20,40);
			//G1=BiGraph.random(100,100,250);
			//System.out.println(G);
//			G1=new IndexGraph(4);
//			G1.insertEdge(0,1);
//			G1.insertEdge(0,2);
//			G1.insertEdge(1,3);
//			G1.insertEdge(2,3);
			
		}
		if(c==1)
		{
			G = new BiGraph(2,2);
			G.insertEdge(0, 2);
			G.insertEdge(0, 3);
			G.insertEdge(1, 2);
			G.insertEdge(1, 3);
			//2^bd(G) = 2
		}
		if(c==2)
		{
			G = new BiGraph(3,3);
			G.insertEdge(0, 3);
			G.insertEdge(1, 3);
			G.insertEdge(1, 4);
			G.insertEdge(2, 4);
			G.insertEdge(2, 5);
			//2^bd(G) = 5
		}
		if(c==3)
		{
			G=BiGraph.random(5,5,10);
			System.out.println(G);
		}
		if(c==4)
		{
			G = new BiGraph(20,20);
//			G.insertEdge(0, 22);
//			G.insertEdge(1, 21);
//			G.insertEdge(1, 31);
			G.insertEdge(2, 37);
//			G.insertEdge(3, 35);
//			G.insertEdge(5, 31);
//			G.insertEdge(6, 35);
			G.insertEdge(7, 26);
			G.insertEdge(8, 26);
//			G.insertEdge(10, 34);
//			G.insertEdge(12, 29);
			G.insertEdge(14, 37);
			G.insertEdge(14, 26);
//			G.insertEdge(15, 25);
//			G.insertEdge(15, 33);
//			G.insertEdge(16, 38);
//			G.insertEdge(16, 35);
//			G.insertEdge(16, 26);
//			G.insertEdge(16, 20);
//			G.insertEdge(19, 32);
		}
		if(c==5)
		{
			G = new BiGraph(2,1);
			G.insertEdge(0, 2);
			G.insertEdge(1, 2);
			
		}
		if(c==6)
		{
			G = new BiGraph(4,2);
			G.insertEdge(0, 4);
			G.insertEdge(1, 5);
			G.insertEdge(2, 5);
			G.insertEdge(3, 4);
			G.insertEdge(3, 5);
			
		}
		if(c==6)
		{
			G = new BiGraph(5,3);
			G.insertEdge(0, 5);
			G.insertEdge(0, 7);
			G.insertEdge(1, 5);
			G.insertEdge(1, 7);
			G.insertEdge(2, 6);
			G.insertEdge(3, 6);
			G.insertEdge(4, 5);
			G.insertEdge(4, 6);
			//should be 5
		}
		
	    G=BiGraph.random(10,10,0);

	    String fileName =  ControlInput.GRAPHLIB_OURS+ "hsugrid/hsu-4x4.dimacs";
        ControlInput cio= new ControlInput();
        IndexGraph G2=new IndexGraph();
        G2 = cio.getTestGraph(fileName, G2);

        long start, end;

        start = System.currentTimeMillis();
        long cvalue=CCMIS.BoolDimBranch(G);
        end = System.currentTimeMillis();
        System.out.printf("CCMIS.BoolDimBranch = log2 %d (%d ms)\n", cvalue, (end-start));

		start = System.currentTimeMillis();
		long bvalue=BoolDecomposition.boolDimBranch(G2);
		end = System.currentTimeMillis();

		System.out.printf("BD.boolDimBranch = log2 %d (%d ms)\n", bvalue, (end-start));
		
//		//String fileName =  ControlInput.GRAPHLIB + "prob/wilson-hugin.dgf";
//		String fileName =  ControlInput.GRAPHLIB + "new/triangle.txt";
//		ControlInput cio= new ControlInput();
//		IndexGraph G2=new IndexGraph();
//		G2=	cio.getTestGraph(fileName, G2);
////		for(IndexEdge<IndexVertex> e:G2.edges())
////		{
////			
////			for(IndexVertex v:e.endVertices())
////			System.out.print(v.id()+" ");
////			System.out.println();
////		}
//		
//		
////		IndexGraph T= new IndexGraph();
////		T=cio.getTestGraph(fileName, T);
//		
//		
//		System.out.print(G2);
//		
//		IndexedSet<IndexVertex> groundSet;
//		groundSet = new IndexedSet<IndexVertex>(G2.vertices());
//		VSubSet cantberemoved = new VSubSet(groundSet);
//		VSubSet vertextoremove = new VSubSet(groundSet);
//		//cantberemoved.addAll((Collection<? extends IndexVertex>) G2.vertices());
//		ArrayList<IndexEdge<IndexVertex>> edgestoremove=new ArrayList<IndexEdge<IndexVertex>>();
//		for(IndexVertex x:G2.vertices())
//		{
//			if((G2.degree(x)==0)||(G2.degree(x)==1))
//			{
//				vertextoremove.add(x);
//				edgestoremove.addAll((Collection<? extends IndexEdge<IndexVertex>>) G2.incidentEdges(x));
//				
//			}
//		}
//		System.out.println(vertextoremove);
////		for(IndexVertex x: vertextoremove)
////			G2.removeVertex(x);
////		
//		
//
//		VSubSet closedNx = new VSubSet(groundSet);
//		VSubSet closedNy = new VSubSet(groundSet);
//		VSubSet Nx = new VSubSet(groundSet);
//		VSubSet Ny = new VSubSet(groundSet);
//
//		
//		for(IndexVertex x:G2.vertices())
//
//		{
//			for(IndexVertex y:G2.vertices())
//
//			{
////				System.out.println("Nx"+G2.closedneighbours(x));
////				System.out.println("Ny"+G2.closedneighbours(y));
//				
//				if(!(x.equals(y)))
//						{
//							if((G2.degree(x)==0)||(G2.degree(x)==1)||(G2.degree(y)==0)||(G2.degree(y)==1))
//								break;
//							
//								
//							
//					//		if((G2.neighbours(x).equals(G2.neighbours(y)))||((G2.closedneighbours(x)).equals(G2.closedneighbours(y))))
//							{
//								
//								System.out.println("x"+x+"y"+y);
//								if(!cantberemoved.contains(x))
//									cantberemoved.add(x);
//								if(!cantberemoved.contains(y))
//								{
//									vertextoremove.add(y);
//									cantberemoved.add(y);
//									System.out.println("Removed"+y);
//								}
//							}
//							
//					
//						}
//			}
//			
//		}
	//	System.out.print("reduced size"+reduced.size()+reduced);
//writing stat to file
/*		PrintWriter out= new PrintWriter(new FileWriter("outputfilBigraph50.txt",true));
		long time = System.currentTimeMillis();
		long bvalue=BoolDecomposition.boolDimBranch(G1);
		time = System.currentTimeMillis()-time;
		System.out.println("Branching maxdegree: "+bvalue+" after "+time+"ms.");
		out.print(G1.numVertices()+" "+G1.numEdges()+" "+bvalue+" " +time);
		long timemid = System.currentTimeMillis();
		long cvalue=BoolDecomposition.boolDimBranchmin(G1);//CutBool.countNeighborhoodsbyListing(G);
		time = System.currentTimeMillis()-timemid;
		out.print(" "+time);
		System.out.println("Branching mindegree: "+cvalue+" after "+time+"ms.");
		out.close();*/
// writing ends
//		long timemid1 = System.currentTimeMillis();
//		long cutvalue=BoolDecomposition.boolDimBranchCutVertex(G1);//CutBool.countNeighborhoodsbyListing(G);
//		time = System.currentTimeMillis()-timemid1;
//		System.out.println("Branching cutvertex: "+cutvalue+" after "+time+"ms.");
//		out.print(" "+time);
//		long timemid2 = System.currentTimeMillis();
//		long listvalue=CutBool.countNeighborhoodsbyListing(G1);
//		time = System.currentTimeMillis()-timemid2;
//		System.out.println("Branching cutvertex: "+listvalue+" after "+time+"ms.");
//		out.println(" "+time);
//		out.close();
		

	}

}
