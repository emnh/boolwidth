import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DecompNode;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.decomposition.SimpleTreedecomposition;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.heuristic.MinDegreeSeqBalanceSplit;
import sadiasrc.heuristic.RandomDecomposition;

public class TestTDtoBooD  {

	public static void main(String[] args) 
	{
		int n = 40;
		double p=0.5;
		String result=null;

		String fileName =  ControlInput.GRAPHLIB + "prob/pigs.dgf";
		//String dirName = ControlInput.GRAPHLIB + "TableCH6_1/";
		ControlInput cio= new ControlInput();
		ArrayList<File> listOfFiles = new ArrayList<>();
		listOfFiles.add(new File(fileName));
		//(ArrayList<File>) cio.getListOfTestGraphs(dirName);
		long start,start1;//start time for an ordering
		long  end,end1;//end time for an ordering 
		long UBfromCP=0;
		int maxis=0;
		int gridsize=25;
		DecimalFormat df = new DecimalFormat("#.##");
        
		
	for(File file : listOfFiles)

	{
		

		
		IndexGraph G=new IndexGraph();
//		G =	cio.getTestGraph(fileName, G);
//		result=fileName;
		
		result=null;
		String[] names;
//		
		
		G =	cio.getTestGraph(file.toString(), G);
		names=file.getName().split("/");
////		
		
		result = names[names.length-1];
		//System.out.println(G);
		
		
		//System.out.println(G);
		
		//for(n=5;n<200;n+=5) 
		{
			//IndexGraph G = IndexGraph.random(n, p);
			
			result=" "+G.numVertices()+" "+G.numEdges()+" ";
			Preprocessing PP = new Preprocessing(G);
			PP.Preprocess(G);
			System.out.println("After preprocessing "+G.numVertices()+ " "+ G.numEdges());
			result+= G.numVertices()+" "+ G.numEdges()+ " ";
			
			start = System.currentTimeMillis();
			ArrayList<IndexVertex> seq = MinDegreeFillin.sequence(G);
			System.out.println("EO"+ seq);
			SimpleTreedecomposition t = new SimpleTreedecomposition(G, seq);
			end = System.currentTimeMillis();
			System.out.println("decomp:");
			//System.out.println(t);
			int tw= t.treewidth()-1;
			System.out.println("Treewidth   : "+(tw)+ " time taken : "+(end-start)+" ms");
			result+=  "tw "+ tw+ " time "+ (end-start) +" ";
		
			start = System.currentTimeMillis();
			DisjointBinaryDecomposition decomp=BoolDecompFromTreeDecomp.BoolWfromTW(G,t);
//			DisjointBinaryDecomposition decomp = RandomDecomposition.randomDBD(G);
//			long boolwfrmRdbd= CutBool.countMIS(decomp);
			end = System.currentTimeMillis();
			System.out.println("TDtoBD time taken : "+(end-start)+ " ms");
		
			result+=  "TDtoBD "+ (end-start) +" ";
			
			start = System.currentTimeMillis();
			long boolwfrmTW = CutBool.countMIS(decomp);
			end = System.currentTimeMillis();
			result+=  "boolw "+ boolwfrmTW+ " time "+ (end-start) +" ";
			System.out.println(result);
			//write result to file
			try {			
				
				File newfile = new File(ControlInput.GRAPHLIB + "output/"+"resultch6_1_PP.txt");
				 
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

		}
	}
	}
}

