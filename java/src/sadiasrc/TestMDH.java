
package sadiasrc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import sadiasrc.util.IndexedSet;

import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.io.ControlInput;

import sadiasrc.decomposition.BoolDecomposition;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

public class TestMDH{
		
	public static void main(String[] args)
	{
		
		String fileName =  ControlInput.GRAPHLIB + "prob/pigs.dgf";
		ControlInput cio= new ControlInput();
		IndexGraph G=new IndexGraph();
		G =	cio.getTestGraph(fileName, G);
		//System.out.println(G);
		long start = System.currentTimeMillis();
		DisjointBinaryDecomposition decomp = MinDegreeFillin.DBDbyMDH(G);
		//System.out.println(decomp);
		//System.out.println("Boolean-width is: log("+CutBool.countNeighbourhoods(decomp)+")");
		//System.out.println("Boolean-width is: log("+CutBool.countMIS(decomp)+")");
		ArrayList<IndexVertex> s=MinDegreeFillin.sequence(G);
		System.out.println("Degree Sequence"+s);
		Stack<VSubSet> r1=MinDegreeFillin.greedySeparate(G, s);
		System.out.println("Printing stack");
		BiGraph bg;
		IndexedSet<IndexVertex> groundSet2;
		groundSet2 = new IndexedSet<IndexVertex>(G.vertices());
		
		long maxval=0;
		
		while(!r1.isEmpty())
		{
			VSubSet cut= new VSubSet(groundSet2);
			ArrayList<IndexVertex> left=new ArrayList<IndexVertex>();
			cut=r1.pop();
			//System.out.println("cut"+cut);
			for(IndexVertex z:cut)
				left.add(z);
			if(!cut.isEmpty())
			{
				bg=new BiGraph(left,G);
				long val=BoolDecomposition.boolDimBranch(bg);
				if(val>maxval)
					maxval=val;
				//System.out.println("cut"+cut+"val"+val);
			}
		}
		System.out.printf("cutbool = %.2f\n", Math.log(maxval) / Math.log(2.0));
			

			long end = System.currentTimeMillis();
			System.out.println("UB using mindegree took: "+(end-start)+"ms");
		
		

	}
}



