import sadiasrc.io.ControlInput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.CutBool;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;


public class Testcutboolbranch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//BiGraph G;
		double p=0.05;
		int avg_deg=0;
		int A=30;
		int B=30;
		String result=null;
		
		String fileName =  ControlInput.GRAPHLIB+ "prob2/BN_100.graph";
		
		ControlInput cio= new ControlInput();
		
		
		IndexGraph G = new IndexGraph();
		G=cio.getTestGraph(fileName, G);
		
		ArrayList<IndexVertex> Left= new ArrayList<IndexVertex>();
		int r=0;
		for(IndexVertex v: G.vertices())
		{
			r= (int) (Math.random()*G.numVertices()/2);
		}
		 
		
		//while(avg_deg<=A)
		{
		/*	avg_deg++;
			for(int j=0;j<5;j++)
			{
			result=null;
			long avg_time_List=0;
			long avg_time_MIS=0;
			for(int i=0;i<5;i++)
			{
			
			G = BiGraph.random(A, B ,avg_deg*A);
			System.out.println("here");
			long start=System.currentTimeMillis();
			long bvalue=BoolDecomposition.BoolDimBranch(G);
			long mid=System.currentTimeMillis();
			avg_time_MIS+= (mid-start);
			long cvalue=CutBool.countNeighborhoodsbyListing(G);
			long end=System.currentTimeMillis();
			avg_time_List+= (end-mid);
			System.out.print(bvalue+" = "+ cvalue);
			System.out.println(" after"+(mid-start)+" ms and "+(end-mid)+" ms");
			
			}
			avg_time_List= avg_time_List/5;
			avg_time_MIS= avg_time_MIS/5;
			result =avg_deg+ " "+ "List"+ " "+ avg_time_List+ " "+ "CCMIS"+ " "+ avg_time_MIS+ " ";*/
		
		//System.out.println(G);
		
		try {			
			
			File newfile = new File(ControlInput.GRAPHLIB + "output/"+"Ch4AvgDeg2.txt");
			 
			// if file doesnt exists, then create it
			if (!newfile.exists()) {
				newfile.createNewFile();
			}
			FileWriter fw = new FileWriter(newfile.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(result+"\n");
			bw.close();
			}
			 catch (IOException e) {
					e.printStackTrace();
			}
			
		}//while
	}

}
