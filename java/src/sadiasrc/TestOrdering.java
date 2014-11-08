import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.heuristic.CreateOrdering;
import sadiasrc.heuristic.GreedyChooser;
import sadiasrc.heuristic.GreedyInitWithStart;
import sadiasrc.heuristic.GreeedyInit;
import sadiasrc.heuristic.GrowNeighborhood;
import sadiasrc.heuristic.GrowNeighborhoodWithStart;
import sadiasrc.heuristic.IChooser;
import sadiasrc.heuristic.MinDegNeighbour;
import sadiasrc.heuristic.Symmdiff;
import sadiasrc.heuristic.SymmdiffWithStart;
import sadiasrc.heuristic.TwoPartition;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sadiasrc.algorithms.IndependentSet;
import sadiasrc.algorithms.Preprocess;

import sadiasrc.decomposition.Caterpiller;

public class TestOrdering {

    public static String logBW(long bw) {
        return String.format("%.2f", Math.log(bw)/Math.log(2.0));
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String fileName =  ControlInput.GRAPHLIB+"delauney/pr299.tsp-pp.dgf";
        //String fileName =  ControlInput.GRAPHLIB+"coloring/queen10_10.dgf";
        //String fileName =  ControlInput.GRAPHLIB+"coloring/david.dgf";
        //String fileName =  ControlInput.GRAPHLIB+"coloring/myciel7.dgf";
        //String fileName =  ControlInput.GRAPHLIB+"coloring/homer.dgf";
		//String fileName =  ControlInput.GRAPHLIB+"prob/alarm.dgf";
		//String fileName =  ControlInput.GRAPHLIB+"coloring/queen8_8.dgf";
		//String fileName =  ControlInput.GRAPHLIB+"prob/barley.dgf";
		String fileName =  ControlInput.GRAPHLIB+"prob2/BN_100.dgf";
	
		//String fileName =  ControlInput.GRAPHLIB+"p_hat/p_hat1500-1.graph.mis.sadia";
		//String fileName =  ControlInput.GRAPHLIB+"DIMACS_PP/gen200_p01.graph.mis.sadia";
		//String fileName =  ControlInput.GRAPHLIB+"DIMACS_PP/MANN_a45.graph.mis.sadia";
		//String fileName =  ControlInput.GRAPHLIB+"DIMACS_PP/brock200_4.graph.mis.sadia";//"Preprocessed/graph04.txt";//"prob/munin4.dgf";//////
				//"coloring/fpsol2.i.1-pp.dgf";
		String dirName = ControlInput.GRAPHLIB + "other/";
		ControlInput cio= new ControlInput();
		ArrayList<File> listOfFiles = (ArrayList<File>) cio.getListOfTestGraphs(dirName);
		long start,start1;//start time for an ordering
		long  end,end1;//end time for an ordering 
		long UBfromCP=0;
		int maxis=0;
		int gridsize=25;
		DecimalFormat df = new DecimalFormat("#.##");
		
	//for(File file : listOfFiles)
		{
		
//			IndexGraph G = IndexGraph.Grid(gridsize);
			IndexGraph G = new IndexGraph();
			
			
			String result=null;
			String[] names;
		
			G =	cio.getTestGraph(fileName, G);
			names=fileName.split("/");

//			G =	cio.getTestGraph(dirName+file.getName(), G);
//			names=file.getName().split("/");
			
			
			result = names[names.length-1];
		
			//System.out.println("here");
			System.out.println(G.numVertices()+ " "+ G.numEdges());
			//System.out.println(G);
			result+= " "+ G.numVertices()+ " "+ G.numEdges()+ " ";
			int taken_in_MIS=0; 
			//taken_in_MIS= Preprocess.Preprocess_for_MIS(G);
			//System.out.println("Already in MIS : "+taken_in_MIS);
			//result+= " "+ "Already in MIS "+taken_in_MIS+" ";
			
			if(G.numVertices()>0 && G.numVertices()<=1000){
	
		
		
				Caterpiller caterpiller= new Caterpiller();
		
				ArrayList<IndexVertex> OrderedSequence;
		
				//System.out.println(G.numVertices()+ " "+ G.numEdges());
				
				Preprocessing p = new Preprocessing(G);
				//p.Preprocess(G);

				//System.out.println("After preprocessing "+G.numVertices()+ " "+ G.numEdges());
				
				//System.out.println(G);
				result+= G.numVertices()+" "+ G.numEdges()+ " ";
				
		
		
	
		
//		IndexVertex startvertex = null;//G.getVertex((int)(Math.random()*G.numVertices()));//null;
//		System.out.println("Random : "+startvertex);
//		startvertex = BasicGraphAlgorithms.BFS(G,G.getVertex((int)(Math.random()*G.numVertices())));
//		startvertex = BasicGraphAlgorithms.BFS(G,startvertex);
//		System.out.println("Startvertex : "+startvertex);
		
			IChooser chooser;
		
			start1=System.currentTimeMillis();
			chooser = new Symmdiff(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end1=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end1-start1)+ " ms");


		//	result += "Symdiff "+ " "+ (end1-start1)+ " ";
			
/*			start=System.currentTimeMillis();
			maxis =IndependentSet.linIS(G, OrderedSequence);
			end=System.currentTimeMillis();
			System.out.println("Independent Set : "+ maxis+ " time taken : "+ (end-start)+ " ms");
			
			result += "MIS"+ " "+ (maxis+taken_in_MIS)+" "+(end-start)+ " ";
			*/

			start=System.currentTimeMillis();
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
			end=System.currentTimeMillis();
			System.out.println("UB : "+logBW(UBfromCP)+" from SymmDiff Time taken: "+ (end-start)+ " ms");
			
			result +=  "Without Start "+UBfromCP;//+ " "+ (end-start)+ " ";
			
			
			start1=System.currentTimeMillis();
			chooser = new SymmdiffWithStart(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end1=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end1-start1)+ " ms");
			
			//result += "Symdiff WS"+ " "+ (end1-start1)+ " ";
			
			
			start=System.currentTimeMillis();
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
			end=System.currentTimeMillis();
			System.out.println("UB : "+logBW(UBfromCP)+" from SymmDiff Time taken: "+ (end-start)+ " ms");
			
			result += "  With Sv mindeg "+ UBfromCP ;//+ " "+ (end-start)+ " ";*/
			
			
			
			
			start1=System.currentTimeMillis();
			chooser = new GrowNeighborhood(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end1=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end1-start1)+ " ms");
			
			//result += "Grwnghbr "+ " "+ (end1-start1)+ " ";
			
			start=System.currentTimeMillis();
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
			end=System.currentTimeMillis();
			System.out.println("UB : "+logBW(UBfromCP)+" from GrowNeighbourhood Time taken: "+ (end-start)+ " ms");
			
			result += UBfromCP+ " "+ (end-start)+ " ";
			start1=System.currentTimeMillis();
			chooser = new GrowNeighborhoodWithStart(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end1=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end1-start1)+ " ms");
			
			//result += "Grwnghbr "+ " "+ (end1-start1)+ " ";
			
			start=System.currentTimeMillis();
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
			end=System.currentTimeMillis();
			System.out.println("UB : "+logBW(UBfromCP)+" from GrowNeighbourhood Time taken: "+ (end-start)+ " ms");
			
			result += UBfromCP+ " "+ (end-start)+ " ";
			
			
			/*start=System.currentTimeMillis();
			maxis =IndependentSet.linIS(G, OrderedSequence);
			end=System.currentTimeMillis();
			System.out.println("Independent Set : "+ maxis+ " time taken : "+ (end-start)+ " ms");
			
			result += "MIS"+ " "+ (maxis+taken_in_MIS)+" "+(end-start)+ " ";*/		
			
		/*	start=System.currentTimeMillis();
			//OrderedSequence = makeFixedOrder(G);
			//maxis =IndependentSet.linIS(G, OrderedSequence);
			maxis =IndependentSet.linIS(G, G.getVertex(0));
			end=System.currentTimeMillis();
			System.out.println("Independent Set from runtime ordering: "+ maxis+ " time taken : "+ (end-start)+ " ms");
			
			result += "MIS_Runtime_DP"+ " "+ (maxis+taken_in_MIS)+" "+(end-start)+ " ";	*/		
			
		
			
			
			start=System.currentTimeMillis();
			chooser = new GreeedyInit(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end-start)+ " ms");
			
			result += "Greedy "+ " "+ (end-start)+ " ";
			UBfromCP=chooser.getUB();
			System.out.println("UB : "+logBW(UBfromCP)+" from GreedyInit Time taken: "+ (end-start)+ " ms");
			
			result += UBfromCP;
			
			
			start=System.currentTimeMillis();
			chooser = new GreedyInitWithStart(G);
			OrderedSequence = CreateOrdering.BuildSequence(G, chooser);
			System.out.println("Ordered Sequence: "+OrderedSequence);	
			end=System.currentTimeMillis();
			System.out.println("Time taken: "+ (end-start)+ " ms");
			
			result += "Greedy "+ " "+ (end-start)+ " ";
			UBfromCP=chooser.getUB();
			System.out.println("UB : "+logBW(UBfromCP)+" from GreedyInit Time taken: "+ (end-start)+ " ms");
			
			result += UBfromCP;
		
			
		/*	start=System.currentTimeMillis();
			maxis =IndependentSet.linIS(G, OrderedSequence);
			end=System.currentTimeMillis();
			System.out.println("Independent Set : "+ maxis+ " time taken : "+ (end-start)+ " ms");
			
			result += "MIS"+ " "+ (maxis+taken_in_MIS)+" "+(end-start)+ " ";*/	
			
			start=System.currentTimeMillis();
			//choose start vertex
			IndexVertex choosen;
			choosen = BasicGraphAlgorithms.BFS(G,G.vertices().iterator().next());
			choosen = BasicGraphAlgorithms.BFS(G,choosen);
			OrderedSequence =IndependentSet.runtimeOrder(G, choosen);
			end=System.currentTimeMillis();
			System.out.println("Runtime order time taken : "+ (end-start)+ " ms");
			
			//result += "Runtime order MIS"+ " "+ (maxis+taken_in_MIS)+" "+df.format((end-start))+ " ";	
			
			start=System.currentTimeMillis();
			UBfromCP=caterpiller.getLinearBooleanWidth(OrderedSequence, G);
			end=System.currentTimeMillis();
			System.out.println("UB : "+logBW(UBfromCP)+" from runtimeOrder Time taken: "+ (end-start)+ " ms");
			
			result += UBfromCP+ " "+ (end-start)+ " ";
			
			//System.out.println();
			//System.out.println("Order"+OrderedSequence);
			start=System.currentTimeMillis();
			maxis =IndependentSet.linIS2(G, OrderedSequence);
			end=System.currentTimeMillis();
			System.out.println("Independent Set : "+ maxis+ " time taken : "+ (end-start)+ " ms");
			
			result += "MIS"+ " "+ (maxis+taken_in_MIS)+" "+(end-start)+ " ";		
		
			}//end if
			//System.out.println(result);
			//create output file
			try {			
			
			File newfile = new File(ControlInput.GRAPHLIB + "output/"+"SymmdifWmin.txt");
			 
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
		}//End for files
		
	}

	public static ArrayList<IndexVertex> makeFixedOrder(IndexGraph g)
	{
		int[] list = {0,1,2,4,5,6,7,8,9,10,11,12,13,14,255,247,260,262,266,256,269,258,264,268,257,267,263,261,265,270,48,56,57,45,54,47,59,55,53,50,51,58,49,52,46,25,282,281,277,271,279,272,275,278,273,276,280,274,283,284,
				174,172,178,165,170,171,90,169,168,175,176,167,177,173,166,179,100,194,96,98,99,104,122,134,97,130,125,133,129,128,127,120,132,131,121,123,124,126,187,86,193,192,182,183,94,180,92,190,91,184,95,101,93,191,
				186,181,188,102,103,185,26,19,189,22,85,15,16,20,84,88};
		ArrayList<IndexVertex> al = new ArrayList<IndexVertex>();
		for(int i:list)
			al.add(g.getVertex(i));
		
		for(IndexVertex v: g.vertices())
		{
			if(!(al.contains(v)))
				al.add(v);
		}
		System.out.println("Fixed order"+al+"\n of size :"+al.size());
		
		return al;
	}
	
}
