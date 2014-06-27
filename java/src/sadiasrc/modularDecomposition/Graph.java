package sadiasrc.modularDecomposition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/* 
 * A simple, undirected graph.
 */
public class Graph {
	
	// The delimiter separating vertices from their list of neighbours in
	// the input file format for graphs.
	private static final String VERTEX_DELIM = "->";
	
	// The delimiter separating a vertex's neighbours from one another in 
	// the input file format for graphs.
	private static final String NEIGH_DELIM = ",";
	
	// The graph's vertices, keyed by their label.
	private Hashtable<String,Vertex> vertices;

	
	private Hashtable<String,Vertex> buildFromList(ArrayList<ArrayList<Integer>> G) {
		
		Hashtable<String,Vertex> vertices = new Hashtable<String,Vertex>();
		
		for(int i=0; i<G.size();i++)
		{   
			if (!vertices.containsKey(""+i)) {
        		Vertex vertex = new Vertex(""+i);
				vertices.put(""+i,vertex);
        	}
		}
		
		// fill in edges
		// Create vertices for each of its neighbours (if they haven't already
        // been created) and add them as neigbhours of this vertex.
        for (int i = 0; i < G.size(); i++) {
        	for(int j=0;j<G.get(i).size();j++){
        		if (vertices.containsKey(""+i))         		
        			vertices.get(""+i).addNeighbour(vertices.get(G.get(i).get(j))); 
        	
        		else {
        			Vertex unseenNeighbour = new Vertex(""+vertices.get(G.get(i).get(j)));
        			vertices.put(""+vertices.get(G.get(i).get(j)),unseenNeighbour);
        			vertices.get(""+i).addNeighbour(unseenNeighbour);
        			}
        	}
        }
		return vertices;
	}

	
	/* 
	 * Constructs a graph from the file whose name is that specified.  The 
	 * format of the file must be as follows:
	 * - each line in the file specifies a vertex;
	 * - each vertex is specified by its label, followed by VERTEX_DELIM, 
	 * followed by a list of its neighbours separated by NEIGH_DELIM (without
	 * spaces between these tokens);
	 * - the file must correctly specify a graph in that each vertex appearing
	 * as a neighbour is specified by a line in the file, and the neighbourhood
	 * lists are symmetric in that an entry for x as a neighbour of y implies 
	 * an entry for y as a neighbour of x.
	 * @param file The name of the input file specifying the graph.
	 */
	public Graph(String file) {		
		vertices = buildFromFile(file);
	}

	/*
	 * Does the work of reading the file and populating the graph with 
	 * vertices according to the contents of the file.  See 'Graph(String )'
	 * for the required input file format.
	 * @param file The name of the input file specifying the graph. 
	 */
	private Hashtable<String,Vertex> buildFromFile(String file) {
		
		Hashtable<String,Vertex> vertices = new Hashtable<String,Vertex>();
		
		BufferedReader inputStream = null;
		
      //  try {
            //inputStream = new BufferedReader(new FileReader(file));

            String[] line = file.split("\n");
            for(int j=0;j<line.length;j++)
            {
                            	
                // Determine the current vertex's label.
            	String[] vertexAndNeighbours = line[j].split(VERTEX_DELIM);
                String vertexLabel = new String(vertexAndNeighbours[0]);
                
                // Determine the current vertex's neighbours.
                String[] neighbourLabels = vertexAndNeighbours[1].split(NEIGH_DELIM);
                   
                
                // Create this vertex if it hasn't already been created (from
                // appearing as a neighbour of an earlier vertex).
                Vertex vertex;
                if (vertices.containsKey(vertexLabel)) {
                	vertex = vertices.get(vertexLabel);
                }
                else {
                	vertex = new Vertex(vertexLabel);
                	vertices.put(vertexLabel,vertex);
                }
                       
                // Create vertices for each of its neighbours (if they haven't already
                // been created) and add them as neigbhours of this vertex.
                for (int i = 0; i < neighbourLabels.length; i++) {
                	if (vertices.containsKey(neighbourLabels[i])) {
                		vertex.addNeighbour(vertices.get(neighbourLabels[i])); 
                	}
                	else {
                		Vertex unseenNeighbour = new Vertex(neighbourLabels[i]);
                		vertices.put(neighbourLabels[i],unseenNeighbour);
                		vertex.addNeighbour(unseenNeighbour);
                	}
                } 
            }
       /* } 
        catch (IOException e) {
        	System.out.println(e);
        }        		
                        
        if (inputStream != null) {
        	try {
        		inputStream.close();
        	}
        	catch (IOException e) {
        		System.out.println(e);
        	}
        }
        */
        return vertices;
	}
	

	/* Returns this graph's vertices. */
	public Collection<Vertex> getVertices() {
		return vertices.values();	
	}

	
	/* Returns the number of this graph's vertices. */
	public int getNumVertices() {		
		return vertices.values().size();
	}

	
	/* Returns the modular decomposition tree for this graph. */
	public MDTree getMDTree() {
		return new MDTree(this);
	}
	
	
	/* 
	 * Returns a string representation of this graph.  The representation
	 * is a list of the graph's vertices.
	 * @return A string representation of the graph.
	 */
	public String toString() {
		return vertices.values().toString();
	}
}
