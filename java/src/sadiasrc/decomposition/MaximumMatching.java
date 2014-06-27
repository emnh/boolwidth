package sadiasrc.decomposition;

import java.util.Arrays;
import java.util.HashMap;

import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexVertex;

public class MaximumMatching {
	int m, n;
    boolean[][] graph;
    boolean seen[];
    int matchL[];   //What left vertex i is matched to (or -1 if unmatched)
    int matchR[];   //What right vertex j is matched to (or -1 if unmatched)
 
    public int maximumMatching(BiGraph G) {
        //Read input and populate graph[][]
        //Set m to be the size of L, n to be the size of R
    	HashMap<Integer, Integer> VertexIDtoInt = new HashMap<Integer, Integer>();;
    	int VertexCounter=0;
    	for(IndexVertex x:G.leftVertices())
    	{    	
    		VertexIDtoInt.put(x.id(), VertexCounter);
		    VertexCounter++;
    	}
		for(IndexVertex y:G.rightVertices())
		{
			VertexIDtoInt.put(y.id(), VertexCounter);
		    VertexCounter++;
		}
    	m=G.numLeftVertices();
    	n=G.numRightVertices();
    	graph= new boolean[m][n];
    	matchL = new int[m];
    	matchR = new int[n];
    	seen =new boolean[m+n];
    	for (int i = 0; i < m; i++)
    		for (int j = 0; j < n; j++)
    			graph[i][j]=false;
    	for(IndexVertex x:G.leftVertices())
    		for(IndexVertex y:G.rightVertices())
    		{
    			if(G.areAdjacent(x, y))
    			graph[VertexIDtoInt.get(x.id())][VertexIDtoInt.get(y.id())-m]=true;
    		}
    	for (int i = 0; i < m; i++)
    		Arrays.fill(matchL, -1);
    	for (int i = 0; i < n; i++)
    		Arrays.fill(matchR, -1);
 
        int count = 0;
        for (int i = 0; i < m; i++) {
            Arrays.fill(seen, false);
            if (bpm(i)) count++;
        }
        return count;
    }
 
    boolean bpm(int u) {
        //try to match with all vertices on right side
        for (int v = 0; v < n; v++) {
            if (!graph[u][v] || seen[v]) continue;
            seen[v] = true;
            //match u and v, if v is unassigned, or if v's match on the left side can be reassigned to another right vertex
            if (matchR[v] == -1 || bpm(matchR[v])) {
                matchL[u] = v;
                matchR[v] = u;
                return true;
            }
        }
        return false;
    }

}
