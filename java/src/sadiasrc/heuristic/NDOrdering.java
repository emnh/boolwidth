package sadiasrc.heuristic;

import sadiasrc.io.ControlInput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

public class NDOrdering {
	
	IndexGraph G;
	String fileName;
	
	public NDOrdering(IndexGraph G,String FileName) {
		this.G = G;
		this.fileName= FileName;
	}
	
	public static void makeAdjacencyFile(IndexGraph G,String FileName)
	{
		String dirName = ControlInput.GRAPHLIB + "other/";
		//writing to file
		try {
			 
			String content = "This is the content to write into file";
			System.out.println(content);
 
			File newfile = new File(FileName+".graph");
 
			// if file doesnt exists, then create it
			if (!newfile.exists()) {
				newfile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(newfile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(G.numVertices()+" "+G.numEdges()+"\n");
			for(int i=0;i<G.numVertices();i++)
			{
				IndexVertex u =G.getVertex(i);
				for(IndexVertex v:G.neighbours(u) )
				{
					bw.write((v.id()+1)+" ");
				}
				bw.write("\n");
				
			}
			bw.close();
 
			
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	
	public static ArrayList<IndexVertex> getNDOrdering(IndexGraph G, String FileName) throws FileNotFoundException
	{
		ArrayList<IndexVertex> NDOrder = new ArrayList<IndexVertex>(G.numVertices());
		for(IndexVertex v: G.vertices())
			NDOrder.add(v);
		//makeAdjacencyFile(G,FileName);
		
		try {
		String temp = FileName+".graph.iperm";
		FileReader fw = new FileReader(temp);
			
		System.out.println(temp);
		
		BufferedReader reader=new BufferedReader(fw); 
			
		String line;
		
		line = reader.readLine();
		
		//System.out.println(NDOrder);
		int i=0;
		while(line!=null) 
		{ 
			//System.out.println(line); 
			int index = Integer.valueOf(line);
			//System.out.println(index);
			NDOrder.set(G.numVertices()-1-index, G.getVertex(i));
			i++;
			line=reader.readLine(); 
		} 
		
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return NDOrder;
		
	}
}
