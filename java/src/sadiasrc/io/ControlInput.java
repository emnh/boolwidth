package sadiasrc.io;

import sadiasrc.exceptions.FatalHandler;
import sadiasrc.exceptions.InvalidGraphFileFormatException;
import sadiasrc.exceptions.InvalidPositionException;
import sadiasrc.graph.IndexGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class ControlInput {	
	
	public static final String OUTPUT_DIR = "output/";
	public static final String GRAPHLIB = "data/graphLib/";
	public static final String GRAPHLIB_OURS = "data/graphLib_ours/";

	public static String getOutputDir(String inputFile) {
		String outputDir = OUTPUT_DIR + inputFile + "/";
		// make parent directory
		new File(outputDir).mkdirs();
		return outputDir;
	}
	
	@SuppressWarnings("unused")
	public static Scanner setFile(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (file == null) {
			throw new FileNotFoundException("File not chosen");
		} else {
			return new Scanner(file);
		}
	}
	
	public static IndexGraph buildIndexGraph(GraphContent gcon,String filename)
	throws FileNotFoundException,InvalidGraphFileFormatException {

		IndexGraph graph = new IndexGraph(gcon.nodesNum);
		
		// convert to 0 indexing
		for (int[] e : gcon.edgeList) {
			e[0] -= gcon.minNodeNum;
			e[1] -= gcon.minNodeNum;
		}

		boolean warned = false;
		String s="";
		try {
			for (int[] edge : gcon.edgeList) {
				s="("+edge[0]+","+edge[1]+")";
				//System.out.println(s);
				if(graph.areAdjacent(edge[0],edge[1]))
					warned = true;
				
				graph.insertEdge(edge[0], edge[1]);
			}
		} catch (InvalidPositionException e) {
			System.out.println(s+" n="+gcon.nodesNum);
			e.printStackTrace();
			throw new InvalidGraphFileFormatException(
			"Graph has egde between nodes out of range");
		}
				if (warned == true) {
					System.out.printf(
							"warning: \"%s\" has duplicate edge (or is directed)\n",
							filename);
				}
		return graph;
	}


	public IndexGraph getTestGraph(String fileName,IndexGraph graph) {
		try {
			
			GraphContent gc= new GraphContent();
			gc.getGraphContent(fileName);
			graph = buildIndexGraph(gc,fileName);
			return graph;
		} catch (FileNotFoundException e) {
			FatalHandler.handle(e);
		}
		return null;
	}
	// If input is a folder then return list of graphs
	public List<File> getListOfTestGraphs(String dirname)
	{
		System.out.println(dirname);
		try {
			DiskGraph dg=new DiskGraph();
			List<File> filelist =dg.getGraphs(dirname);
			return filelist;
		} catch (FileNotFoundException e) {
			FatalHandler.handle(e);
		}
		return null;
		
	}
}
