import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.io.ControlInput;


public class WriteNewFiles {
	public static void main(String[] args)
	{
	String fileName =  ControlInput.GRAPHLIB + "frb35-17-mis/weeduk.dgf";
	String dirName = ControlInput.GRAPHLIB + "CH10/DO/";
	
	ControlInput cio= new ControlInput();
	ArrayList<File> listOfFiles = (ArrayList<File>) cio.getListOfTestGraphs(dirName);
	System.out.println("list"+listOfFiles);
	for(File file : listOfFiles)
	{
		System.out.println(file.getName());
		IndexGraph G=new IndexGraph();
		G =	cio.getTestGraph(dirName+file.getName(), G);
		//writing to file
		try {
			 
			String content = "This is the content to write into file";
 
			File newfile = new File(dirName+file.getName()+".txt");
 
			// if file doesnt exists, then create it
			if (!newfile.exists()) {
				newfile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(newfile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(G.numVertices()+" "+G.numEdges()+"\n");
			for(IndexEdge<IndexVertex> e:G.edges())
			{
				bw.write((e.endVertices().get(0).id()+1)+" "+(e.endVertices().get(1).id()+1)+"\n");
			}
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	}

}
