package sadiasrc.decomposition;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.TreeMap;

import sadiasrc.util.IndexedSet;

public class BranchonBKMIS {
	//~ Instance fields --------------------------------------------------------
	private static IndexGraph graph;

    static Collection<VSubSet> MaxISs;

	private static TreeMap<VSubSet,Boolean> isConnected;
	private static int numCon=0;
	private static int numElse=0;
	static int small;
	private static ArrayList<VSubSet> neighbourhoods;
	private static IndexedSet<IndexVertex> groundSet;
	//~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IS finder.
     *
     * @param graph the graph in which MaxISs are to be found; graph must be
     * simple
     */
    public BranchonBKMIS(IndexGraph graph)
    {
        this.graph = graph;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Finds all maximal ISs of the graph. A IS is maximal if it is
     * impossible to enlarge it by adding another vertex from the graph. Note
     * that a maximal IS is not necessarily the biggest IS in the graph.
     *
     * @return Collection of MaxISs (each of which is represented as a Set of
     * vertices)
     */
	public static long BKMISCon(IndexGraph G)
	{
		
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
		for(int i=0; i<G.numVertices(); i++)
		{
			neighbourhoods.add(new VSubSet(groundSet,G.neighbours(G.getVertex(i))));
		}

		isConnected = new TreeMap<VSubSet, Boolean>();
		VSubSet potential_IS = new VSubSet(groundSet);
		VSubSet candidates = new VSubSet(groundSet);
		VSubSet already_found = new VSubSet(groundSet);
		//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
		for(IndexVertex v:graph.vertices())
				candidates.add(v);
		//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
		Boolean con=true;
		con = BasicGraphAlgorithms.isConnected(graph,candidates);
		
		long total;
		if(!con && !(candidates.isEmpty()))
		{
			numCon++;
		    total=1;
			
			VSubSet nalready_found = new VSubSet(groundSet);
			VSubSet np_mis = new VSubSet(groundSet);
			
			if(!candidates.isEmpty())
			{for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(graph,candidates))
			{
				VSubSet ncandidates = new VSubSet(groundSet);
				ncandidates.addAll(vs);
//				nalready_found = ncandidates.clone();
//				np_mis = ncandidates.clone();
//				nalready_found.retainAll(already_found);
//				np_mis.retainAll(potential_IS);
				MaxISs = new ArrayList<VSubSet>();
				total*=findMIS(np_mis, ncandidates, nalready_found);
			}
			
			}
			System.out.println("total="+total);			
		}
		else
		{
			MaxISs = new ArrayList<VSubSet>();
			total=findMIS(potential_IS, candidates, already_found);
		}
        return total;
	}

	    private static long findMIS(VSubSet potential_IS,VSubSet candidates,VSubSet already_found)
	    {
	    	System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
	    	
	    	//check to see if the graph is disconneced
			
			 
				 IndexedSet<IndexVertex> groundSet;
				 groundSet = new IndexedSet<IndexVertex>(graph.vertices());
				 VSubSet candidates_array = new VSubSet(groundSet);
				 for(IndexVertex v:candidates)
					 candidates_array.add(v);
				 if (!end(candidates, already_found)) {
	            // for each candidate_node in candidates do
	            for (IndexVertex candidate : candidates_array) {
	                VSubSet new_candidates = new VSubSet(groundSet);
	                VSubSet new_already_found = new VSubSet(groundSet);
	             // System.out.println("potential_MIS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
	    			
	                // move candidate node to potential_IS
	                potential_IS.add(candidate);
	                candidates.remove(candidate);
	                

	                // create new_candidates by removing nodes in candidates not
	                // connected to candidate node
	                for (IndexVertex new_candidate : candidates) {
	                    if (!graph.areAdjacent(candidate, new_candidate)) {
	                        new_candidates.add(new_candidate);
	                    } // of if
	                } // of for

	                // create new_already_found by removing nodes in already_found
	                // not connected to candidate node
	                for (IndexVertex new_found :already_found) {
	                    if (!graph.areAdjacent(candidate, new_found)) {
	                        new_already_found.add(new_found);
	                    } // of if
	                } // of for

	                // if new_candidates and new_already_found are empty
	                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
	                    // potential_IS is maximal_IS
	                	System.out.println("Adding"+potential_IS);
	                    MaxISs.add((potential_IS));	    
	                    
	                     } // of if
	                else {
	                    // recursive call
	                    findMIS(potential_IS,new_candidates,new_already_found);
	                } // of else

	                // move candidate_node from potential_IS to already_found;
	                already_found.add(candidate);
	                potential_IS.remove(candidate);
	            } // of for
	        } // of if
			return MaxISs.size();	 
			}
			
			
			
			
	

	    private static boolean end(VSubSet candidates, VSubSet already_found)
	    {
	        // if a node in already_found is connected to all nodes in candidates
	        boolean end = false;
	        int edgecounter;
	        for (IndexVertex found : already_found) {
	            edgecounter = 0;
	            for (IndexVertex candidate : candidates) {
	                if (!graph.areAdjacent(found, candidate)) {
	                	edgecounter++;
	                } // of if
	            } // of for
	            if (edgecounter == candidates.size()) {
	                end = true;
	                
	            }
	        } // of for
	        return end;
	    }
	}

	// End BronKerboschMaxIS.java






