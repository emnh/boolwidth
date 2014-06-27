package sadiasrc.decomposition;

import sadiasrc.graph.BasicGraphAlgorithms;
import sadiasrc.graph.BiGraph;
import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.*;

import sadiasrc.util.IndexedSet;

	/**
	 * This class implements Bron-Kerbosch dual of IS detection algorithm as it is
	 * described in 
	 */
		public class TomitaMIS
	{
	    //~ Instance fields --------------------------------------------------------

	  //  private final IndexGraph graph;

	    Collection<VSubSet> MaxISs;
	    long total=0;

	    //~ Constructors -----------------------------------------------------------

	    /**
	     * Creates a new IS finder.
	     *
	     * @param graph the graph in which MaxISs are to be found; graph must be
	     * simple
	     */
	    public TomitaMIS(IndexGraph graph)
	    {
	       // this.graph = graph;
	    }

	    public TomitaMIS(BiGraph graph)
	    {
	//        this.graph = graph;
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
	    public long getAllMaximalIS(IndexGraph graph)
	    {
	        MaxISs = new ArrayList<VSubSet>();
	        IndexedSet<IndexVertex> groundSet;
	       
			groundSet = new IndexedSet<IndexVertex>(graph.vertices());
			VSubSet potential_IS = new VSubSet(groundSet);
			VSubSet candidates = new VSubSet(groundSet);
			VSubSet already_found = new VSubSet(groundSet);
			//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
			for(IndexVertex v:graph.vertices())
					candidates.add(v);
			//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
			
			long t=findMIS(graph,potential_IS, candidates, already_found);
	        return t;
	    }
	    public long getAllMaximalIS(BiGraph graph)
	    {
	    	
	        MaxISs = new ArrayList<VSubSet>();
	        IndexedSet<IndexVertex> groundSet;
	       
			groundSet = new IndexedSet<IndexVertex>(graph.vertices());
			VSubSet potential_IS = new VSubSet(groundSet);
			VSubSet candidates = new VSubSet(groundSet);
			VSubSet already_found = new VSubSet(groundSet);
			//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
			for(IndexVertex v:graph.vertices())
					candidates.add(v);
			//System.out.println("potential_IS"+potential_IS+" candidates"+ candidates+ " already_found" +already_found);
			
			long t=findMIS(graph,potential_IS, candidates, already_found);
	        return t;
	    }

	    
	    private long findMIS(BiGraph graph,VSubSet potential_IS,VSubSet candidates,VSubSet already_found)
	    {
	    	//System.out.println("R"+potential_IS+" P"+ candidates+ " X" +already_found);
			long total=0;
	    	IndexedSet<IndexVertex> groundSet;
			groundSet = new IndexedSet<IndexVertex>(graph.vertices());
	        VSubSet candidates_array = new VSubSet(groundSet);
	        for(IndexVertex v:candidates)
				candidates_array.add(v);
	        if (!end(graph,candidates, already_found)) {
	            // for each candidate_node in candidates do
	        	IndexVertex u = graph.MinDegreeVertex(candidates);
	        	//System.out.println("Chosen"+u);
	        	VSubSet p =new VSubSet(groundSet);
	        	
	        	for(IndexVertex x:candidates)
	        	{
	        		if(graph.neighbours(u).contains(x))
	        			p.add(x);
	        		
	        	}
	        	p.add(u);
	        	//System.out.println("pcapN(u)"+p);
	            for (IndexVertex candidate : p) {
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
	                	//System.out.println("Adding"+potential_IS);
	                    //MaxISs.add((potential_IS));
	                	total++;
	                } // of if
	                else {
	                    // recursive call
	                    findMIS(graph,potential_IS,new_candidates,new_already_found);
	                } // of else

	                // move candidate_node from potential_IS to already_found;
	                already_found.add(candidate);
	                potential_IS.remove(candidate);
	            } // of for
	        } // of if
	        return total;
	    }

	    private long findMIS(IndexGraph graph,VSubSet potential_IS,VSubSet candidates,VSubSet already_found)
	    {
	    	long total=0;
	    	//System.out.println("R"+potential_IS+" P"+ candidates+ " X" +already_found);
			
	    	IndexedSet<IndexVertex> groundSet;
			groundSet = new IndexedSet<IndexVertex>(graph.vertices());
	        VSubSet candidates_array = new VSubSet(groundSet);
	        for(IndexVertex v:candidates)
				candidates_array.add(v);
	      //checking connected components
		/*	Boolean con;
			VSubSet all = new VSubSet(groundSet);
			all.addAll(candidates);
			all.addAll(already_found);
			con = BasicGraphAlgorithms.isConnected(graph,all);
			
			if(!con)
			{
				//System.out.println("Got CC");
				long tot=1;
				for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(graph,all))
				{
					VSubSet nall = new VSubSet(groundSet);
					nall.addAll(vs);
					VSubSet nout = nall.clone();
					VSubSet nrest = nall.clone();
					nout.retainAll(already_found);
					nrest.retainAll(candidates);
					//System.out.println("Calling for "+"R"+potential_IS+" P"+ nrest+ " X" +nout);
					tot *= findMIS(graph,potential_IS,nrest,nout);
					//System.out.println("tot = "+tot);
				}

				return tot;
			}*/
	        
	        if (!end(graph,candidates, already_found)) {
	            // for each candidate_node in candidates do
	        	IndexVertex u = graph.MinDegreeVertex(candidates);
	        	//System.out.println("Chosen"+u);
	        	VSubSet p =new VSubSet(groundSet);
	        	
	        	for(IndexVertex x:candidates)
	        	{
	        		if(graph.neighbours(u).contains(x))
	        			p.add(x);
	        		
	        	}
	        	p.add(u);
	        	//System.out.println("pcapN(u)"+p);
	            for (IndexVertex candidate : p) {
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
//	                	System.out.println("Adding"+potential_IS);
//	                    MaxISs.add((potential_IS));
	                	total++;
	                } // of if
	                else {
	                    // recursive call
	                    total+=findMIS(graph,potential_IS,new_candidates,new_already_found);
	                } // of else

	                // move candidate_node from potential_IS to already_found;
	                already_found.add(candidate);
	                potential_IS.remove(candidate);
	            } // of for
	        } // of if
	        return total;
	    }

	    private boolean end(IndexGraph graph,VSubSet candidates, VSubSet already_found)
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
	
		private boolean end(BiGraph graph,VSubSet candidates, VSubSet already_found)
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





