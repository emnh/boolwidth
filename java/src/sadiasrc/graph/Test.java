package sadiasrc.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import sadiasrc.util.IndexedSet;

import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.io.ControlInput;

import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.CutBool;
import sadiasrc.graph.BiGraph;

public class Test{
		
	public static void main(String[] args)
	{
		
		String fileName =  ControlInput.GRAPHLIB + "prob2/BN_95.dgf";
		ControlInput cio= new ControlInput();
		IndexGraph G=new IndexGraph();
		G =	cio.getTestGraph(fileName, G);
		//System.out.println(G);
		ArrayList<IndexVertex>s=MinDegreeFillin.sequence(G);
		System.out.println(s);
		Stack<VSubSet> r1=MinDegreeFillin.greedySeparate(G, s);
		//System.out.println("Printing stack");
		BiGraph bg;
		IndexedSet<IndexVertex> groundSet2;
		groundSet2 = new IndexedSet<IndexVertex>(G.vertices());
		
		long maxval=0;
		
		while(!r1.isEmpty())
		{
			VSubSet cut= new VSubSet(groundSet2);
			ArrayList<IndexVertex> left=new ArrayList<IndexVertex>();
			cut=r1.pop();
			for(IndexVertex z:cut)
				left.add(z);
			if(!cut.isEmpty())
			{
				bg=new BiGraph(left,G);
				long val=BoolDecomposition.boolDimBranch(bg);
				if(val>maxval)
					maxval=val;
				System.out.println("cut"+cut+"val"+val);
			}
			System.out.println("cutbool = "+maxval);
		}
		
//		long start = System.currentTimeMillis();
//		long b2value=BoolDecomposition.boolDimBranch2(G);
//		long mid = System.currentTimeMillis();
//		System.out.println("branching with mindegree used: "+(mid-start)+","+b2value);
//		long bvalue=BoolDecomposition.boolDimBranch(G);
//		long mid1 = System.currentTimeMillis();
//		System.out.println("branching with maxdegree used: "+(mid1-mid)+","+bvalue);
//		long cvalue=CutBool.countNeighborhoods(G);
//		long end = System.currentTimeMillis();
//		System.out.println("listing used: "+(end-mid1));
////		long rvalue= BoolDecomposition.BK1(G);
////		System.out.println("Recursive value"+rvalue);
//		System.out.println(bvalue+" = "+ cvalue);	
	}
}
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		int count_num_files=0;
//		String fileName =  ControlInput.GRAPHLIB + "coloring/anna-pp.dgf";
//		String dirName = "graphLib/protein";
//		ControlInput cio= new ControlInput();
//		IndexGraph testGraph=new IndexGraph();
//		//cio.getTestGraph(fileName, testGraph);
//		List<File> fileList= cio.getListOfTestGraphs(dirName);
//		 //print out all file names, in the the order of File.compareTo()
//	    for(File file : fileList ){
//	      System.out.println(file);
//	      count_num_files++;
//	      IndexGraph tempGraph=new IndexGraph();
//		  cio.getTestGraph(file.toString(), tempGraph);
//	      
//	    }
//	    System.out.println(count_num_files);
//
//	}


