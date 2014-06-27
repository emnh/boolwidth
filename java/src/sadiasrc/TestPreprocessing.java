import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.io.ControlInput;
import sadiasrc.io.Preprocessing;



public class TestPreprocessing {
	public static void main(String[] args)
	{
		
		String fileName =  ControlInput.GRAPHLIB + "coloring/zeroin.i.3.dgf";
		String dirName = ControlInput.GRAPHLIB + "TWLIB/";
		
		ControlInput cio= new ControlInput();
		ArrayList<File> listOfFiles = (ArrayList<File>) cio.getListOfTestGraphs(dirName);
		System.out.println("list"+listOfFiles);
		for(File file : listOfFiles)
		{
			System.out.println(file.getName());
			IndexGraph G=new IndexGraph();
			G =	cio.getTestGraph(dirName+file.getName(), G);
		
			Preprocessing p = new Preprocessing(G);
			p.Preprocess(G);
			System.out.println(G.numVertices()+ " "+G.numEdges());
			
			if(G.numVertices()<=500)
			{
				try {
					 
					String content = "This is the content to write into file";
		 
					File newfile = new File(dirName+"Preprocessed500/"+file.getName());
		 
					// if file doesnt exists, then create it
					if (!newfile.exists()) {
						newfile.createNewFile();
					}
		 
					FileWriter fw = new FileWriter(newfile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("p edges "+G.numVertices()+" "+G.numEdges()+"\n");
					for(IndexEdge<IndexVertex> e:G.edges())
					{
						bw.write("e "+(e.endVertices().get(0).id())+" "+(e.endVertices().get(1).id())+"\n");
					}
					bw.close();
		 
					System.out.println("Done");
		 
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		}
		
	
		
	}
	

}
