import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sadiasrc.decomposition.Caterpiller;
import sadiasrc.decomposition.CutBool;
import sadiasrc.decomposition.DisjointBinaryDecomposition;
import sadiasrc.util.IndexedSet;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;
import sadiasrc.heuristic.ChooseFromSmallestComp;
import sadiasrc.heuristic.CreateOrdering;
import sadiasrc.heuristic.Degeneracy;
import sadiasrc.heuristic.GreedyChooser;
import sadiasrc.heuristic.GreeedyInit;
import sadiasrc.heuristic.GrowNeighborhood;
import sadiasrc.heuristic.MinDegNeighbour;
import sadiasrc.heuristic.MinDegreeFillin;
import sadiasrc.heuristic.SymDiffwithN_vinLeft;
import sadiasrc.heuristic.TwoPartition;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;


public class TestDiffVertexOrdering {
	public static void main(String[] args)
	{
		
		String fileName =  ControlInput.GRAPHLIB+ "" +
				"coloring/fpsol2.i.1-pp.dgf";//diabetes-pp-002.dgf";
		String dirName = ControlInput.GRAPHLIB + "prob2/";
		ControlInput cio= new ControlInput();
		ArrayList<File> listOfFiles = (ArrayList<File>) cio.getListOfTestGraphs(dirName);
		
		
		//for(File file : listOfFiles)
		{
		IndexGraph G = new IndexGraph();
		String result=null;
		String[] names;
		//G.random(500, 0.99);
		G =	cio.getTestGraph(fileName, G);
		names=fileName.split("/");
	//	G =	cio.getTestGraph(dirName+file.getName(), G);
	//	names=file.getName().split("/");
		result = names[names.length-1];
		
		if(G.numVertices()>0 && G.numVertices()<=400){
			
			
		System.out.println(G.numVertices()+ " "+ G.numEdges());
		result+= " "+ G.numVertices()+ " "+ G.numEdges()+ " ";
		Preprocessing p = new Preprocessing(G);
		//p.Preprocess(G);

		System.out.println("After preprocessing "+G.numVertices()+ " "+ G.numEdges());
		
		//System.out.println(G);
		result+= G.numVertices()+" "+ G.numEdges()+ " ";
		
		Caterpiller caterpiller= new Caterpiller();
		
		long start;//start time for an ordering
		long  end;//end time for an ordering including evaluating caterpillar
		long UBfromCP=0;
		GreedyChooser chooser;
		ArrayList<IndexVertex> OrderedSequence;
		//try different vertex ordering
		
		//Algorithm 1:create sequence from greedy initialization
		
/*		GreeedyInit GI= new GreeedyInit(G);
		start=System.currentTimeMillis();		
		UBfromCP =GreeedyInit.seqFromGreedyInit(G,UBfromCP);		
//		System.out.println("Ordered Sequence: "+OrderedSequence);	
//		if(OrderedSequence==null)
//			UBfromCP=0;
//		else
//			UBfromCP = GreeedyInit.Upperbound;
//			//UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from GreedyInitcaterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";
		
		
		//Algorithm 1b:create sequence from greedy initialization but choose from neighbourhood of left
		GI= new GreeedyInit(G);
		start=System.currentTimeMillis();		
		//UBfromCP =GreeedyInit.GreedyInitInSubset(G,UBfromCP);		
//		System.out.println("Ordered Sequence: "+OrderedSequence);	
//		if(OrderedSequence==null)
//			UBfromCP=0;
//		else
//			UBfromCP = GreeedyInit.Upperbound;
//			//UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from GreedyInit from N(left) caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";
*/	
		//Algorithm 2:create sequence from symmetric difference . Choose v from Right which has
		//            least neighborhood diff with any of vertex u in Left
		
/*		start=System.currentTimeMillis();
		OrderedSequence =SymDiffwithN_vinLeft.CreateSequenceSYMDiff(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from symdiffWithAny caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";
	*/	
		//Algorithm 3:create sequence from Growing neighborhood. Choose u from Left with minimum 
		          //  number of neighbors in Right. choose  v from N(U) in right.
				
	/*	start=System.currentTimeMillis();			
		OrderedSequence =CreateOrdering.GrowNeighborhood(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from neighborhoodGrow caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";*/
		
		//Algorithm 4:create sequence from symmetric difference . Choose v from Right which has
		//            least neighborhood diff with N(Left).
		
		start=System.currentTimeMillis();
		OrderedSequence =TwoPartition.CreateSequenceSYMDiff(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from symdiff caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";
		
		//Algorithm 5:create sequence from symmetric difference . Choose v from N(left) which has
				//            least neighborhood diff with N(Left).
				
		/*start=System.currentTimeMillis();
		OrderedSequence =TwoPartition.CreateSequenceSYMDiffchosfromNLeft(G);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from symdiffchsFromN(left) caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";		
			
		//Algorithm 6:create sequence from symmetric difference . Choose neighbor of v where v has minimum degree 
		        //    in the bipartite graph Left/Right.
				
		start=System.currentTimeMillis();
		chooser = new MinDegNeighbour(G);
		OrderedSequence =TwoPartition.buildSequence(G, chooser);
		System.out.println("Ordered Sequence: "+OrderedSequence);
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from MinDegNeighbour caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";
	*/
		//Algorithm 7:create sequence from choosing from smallest component. Choose v from the smallest component in Right.
				//    If connected then min degree vertex.

		
	/*	start=System.currentTimeMillis();
		chooser = new ChooseFromSmallestComp(G);
		OrderedSequence =TwoPartition.buildSequence(G, chooser);
		System.out.println("Ordered Sequence: "+OrderedSequence);
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from select from smallestcomponent caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";*/
		
		//Algorithm 8:create reverse degeneracy sequence 
/*		start=System.currentTimeMillis();
		GrowNeighborhood chooser1 = new GrowNeighborhood(G);
		OrderedSequence =(ArrayList<IndexVertex>) CreateOrdering.BuildSequence(G, chooser1);
		System.out.println("Ordered Sequence: "+OrderedSequence);	
		if(OrderedSequence==null)
			UBfromCP=0;
		else
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
		end = System.currentTimeMillis();
		System.out.println("Cutbool="+UBfromCP+" after "+(end-start)+" ms from grow neighborhood caterpillar");
		result += UBfromCP+ " "+ (end-start)+ " ";*/
		
		//Algorithm 9:create mindgereefillin sequence. Then generate Tree Decomposition.
		       //	  finally BoolDecomposition from TD.
		
//		start=System.currentTimeMillis();
//		long boolwfrmTW=BoolDecompFromTreeDecomp.BoolWfromTW(G);
//		end = System.currentTimeMillis();
//		System.out.println("Cutbool= " +boolwfrmTW +" after "+(end-start)+" ms from TD");
//		result += boolwfrmTW+ " "+ (end-start)+ " ";
		
		System.out.println(result);
		//create output file
		try {			
		
		File newfile = new File(ControlInput.GRAPHLIB + "output/"+"result_Growneighborhood.txt");
		 
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
		}//end if
		}//end for
		
	}

}
