package sadiasrc.decomposition;

import sadiasrc.graph.IndexEdge;
import sadiasrc.graph.IndexGraph;
import sadiasrc.graph.IndexVertex;
import sadiasrc.graph.VSubSet;

import java.util.*;

import sadiasrc.util.IndexedSet;

	/**
	 * This class implements Bron-Kerbosch clique detection algorithm as it is
	 * described in 
	 */
		public class BronKerBoschCliqueFinder
	{
	    //~ Instance fields --------------------------------------------------------

	    private final IndexGraph graph;

	    Collection<VSubSet> cliques;

	    //~ Constructors -----------------------------------------------------------

	    /**
	     * Creates a new clique finder.
	     *
	     * @param graph the graph in which cliques are to be found; graph must be
	     * simple
	     */
	    public BronKerBoschCliqueFinder(IndexGraph graph)
	    {
	        this.graph = graph;
	    }

	    //~ Methods ----------------------------------------------------------------

	    /**
	     * Finds all maximal cliques of the graph. A clique is maximal if it is
	     * impossible to enlarge it by adding another vertex from the graph. Note
	     * that a maximal clique is not necessarily the biggest clique in the graph.
	     *
	     * @return Collection of cliques (each of which is represented as a Set of
	     * vertices)
	     */
	    public Collection<VSubSet> getAllMaximalCliques()
	    {
	        cliques = new ArrayList<VSubSet>();
	        IndexedSet<IndexVertex> groundSet;
	       
			groundSet = new IndexedSet<IndexVertex>(graph.vertices());
			VSubSet potential_clique = new VSubSet(groundSet);
			VSubSet candidates = new VSubSet(groundSet);
			VSubSet already_found = new VSubSet(groundSet);
			//System.out.println("potential_clique"+potential_clique+" candidates"+ candidates+ " already_found" +already_found);
			for(IndexVertex v:graph.vertices())
					candidates.add(v);
			//System.out.println("potential_clique"+potential_clique+" candidates"+ candidates+ " already_found" +already_found);
			findCliques(potential_clique, candidates, already_found);
	        return cliques;
	    }

	    
	    private void findCliques(
	        VSubSet potential_clique,
	        VSubSet candidates,
	        VSubSet already_found)
	    {
	    	//System.out.println("potential_clique"+potential_clique+" candidates"+ candidates+ " already_found" +already_found);
			
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

	                // move candidate node to potential_clique
	                potential_clique.add(candidate);
	                candidates.remove(candidate);
	                //System.out.println("potential_clique"+potential_clique+" candidates"+ candidates+ " already_found" +already_found);
	    			

	                // create new_candidates by removing nodes in candidates not
	                // connected to candidate node
	                for (IndexVertex new_candidate : candidates) {
	                    if (graph.areAdjacent(candidate, new_candidate)) {
	                        new_candidates.add(new_candidate);
	                    } // of if
	                } // of for

	                // create new_already_found by removing nodes in already_found
	                // not connected to candidate node
	                for (IndexVertex new_found : already_found) {
	                    if (graph.areAdjacent(candidate, new_found)) {
	                        new_already_found.add(new_found);
	                    } // of if
	                } // of for

	                // if new_candidates and new_already_found are empty
	                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
	                    // potential_clique is maximal_clique
	                    cliques.add((potential_clique));
	                    System.out.println("Adding clique"+potential_clique);
	                } // of if
	                else {
	                    // recursive call
	                    findCliques(
	                        potential_clique,
	                        new_candidates,
	                        new_already_found);
	                } // of else

	                // move candidate_node from potential_clique to already_found;
	                already_found.add(candidate);
	                potential_clique.remove(candidate);
	            } // of for
	        } // of if
	    }

	    private boolean end(VSubSet candidates, VSubSet already_found)
	    {
	        // if a node in already_found is connected to all nodes in candidates
	        boolean end = false;
	        int edgecounter;
	        for (IndexVertex found : already_found) {
	            edgecounter = 0;
	            for (IndexVertex candidate : candidates) {
	                if (graph.areAdjacent(found, candidate)) {
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

	// End BronKerboschCliqueFinder.java



