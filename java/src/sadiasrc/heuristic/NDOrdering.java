package sadiasrc.heuristic;

import sadiasrc.io.ControlInput;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;

public class NDOrdering {
	
	IndexGraph G;
	String fileName;
    private static String METIS_PATH = "C:\\cygwin64\\bin\\ndmetis.exe";
    private static boolean DEBUG = false;
	
	public NDOrdering(IndexGraph G,String FileName) {
		this.G = G;
		this.fileName= FileName;
	}

    public static ArrayList<IndexVertex> computeNDOrdering(IndexGraph G) {
        try {
            File tempFile = File.createTempFile("metis-graph", "");
            String fileName = tempFile.getAbsolutePath();
            // System.out.println("fileName: " + fileName);
            makeAdjacencyFile(G, fileName);
            runMetis(fileName + ".graph");
            return getNDOrdering(G, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

	public static void runMetis(String fileName) {
        try {
            Process process = new ProcessBuilder(METIS_PATH, fileName).start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            InputStream eis = process.getErrorStream();
            InputStreamReader eisr = new InputStreamReader(eis);
            BufferedReader ebr = new BufferedReader(eisr);

            String line;

            // System.out.println("Current path:" + new File(".").getAbsolutePath());
            if (DEBUG) {
                while ((line = br.readLine()) != null) {
                    System.out.println("Output: " + line);
                }
            }

            while ((line = ebr.readLine()) != null) {
                System.out.println("Error: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
	
	public static void makeAdjacencyFile(IndexGraph G, String FileName)
	{
		String dirName = ControlInput.GRAPHLIB + "other/";
		//writing to file
		try {
			File newFile = new File(FileName + ".graph");
 
			// if file doesnt exists, then create it
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
 
			FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(G.numVertices() + " " + G.numEdges() + "\n");
			for (int i=0; i < G.numVertices(); i++) {
				IndexVertex u = G.getVertex(i);
				for(IndexVertex v : G.neighbours(u)) {
					bw.write((v.id() + 1) + " ");
				}
				bw.write("\n");
				
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
            System.exit(-1);
		}
	}
	
	
	public static ArrayList<IndexVertex> getNDOrdering(IndexGraph G, String FileName) throws FileNotFoundException
	{
		ArrayList<IndexVertex> NDOrder = new ArrayList<IndexVertex>(G.numVertices());
		for (IndexVertex v : G.vertices()) {
            NDOrder.add(null);
        }
		
		try {
            String temp = FileName + ".graph.iperm";
            FileReader fw = new FileReader(temp);

            BufferedReader reader = new BufferedReader(fw);

            String line;

            line = reader.readLine();

            int i = 0;
            while (line != null) {
                int index = Integer.valueOf(line);
                NDOrder.set(G.numVertices() - 1 - index, G.getVertex(i));
                i++;
                line = reader.readLine();
            }
		} catch (IOException e) {
			e.printStackTrace();
            System.exit(-1);
		} 

		return NDOrder;
		
	}
}
