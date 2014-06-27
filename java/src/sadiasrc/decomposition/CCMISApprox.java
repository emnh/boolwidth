package sadiasrc.decomposition;

import sadiasrc.graph.*;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;


import sadiasrc.util.IndexedSet;

public class CCMISApprox {

    //stores neighborhood in bitsets for faster intersection and union
    private static ArrayList<VSubSet> neighbourhoods;
    //groundset for bitset
    private static IndexedSet<IndexVertex> groundSet;


    public static long BoolDimBranch(BiGraph G, int sampleCount)
    {
//		System.out.println("Bigraph is");
//		System.out.println(G);
        neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
        groundSet = new IndexedSet<IndexVertex>(G.vertices());
        for(int i=0; i<G.numVertices(); i++)
        {
            neighbourhoods.add(new VSubSet(groundSet, G.neighbours(G.getVertex(i))));
        }


        VSubSet all = new VSubSet(groundSet);
        VSubSet out = new VSubSet(groundSet);
        VSubSet rest = new VSubSet(groundSet);
        //all=rest U out
        all.addAll((Collection<? extends IndexVertex>) G.vertices());
        //rest =P
        rest.addAll((Collection<? extends IndexVertex>) G.vertices());
        //out =X
        long t = 0;
        for (int i = 0; i < sampleCount; i++) {
            t += boolDimBranch(G, all, out, rest);
        }
        return t / sampleCount;
    }

    /**
     * @param G The bipartite graph of which we want to compute the boolean dimension
     * @param all = (out union rest)
     * @param out =X the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
     * @param rest =P the set of vertices not yet considered
     * @return
     * @throws InvalidAlgorithmParameterException
     */

    public static long boolDimBranch(BiGraph G, VSubSet all, VSubSet out, VSubSet rest)
    {
        //checking termination conditions

//		System.out.println("out"+out);
//		System.out.println("rest"+rest);

        //check if P and X are empty
        if(rest.isEmpty())
        {
            if(out.isEmpty()) {
                return 1;
            } else {
                System.out.println("returning 0");
                return 0;
            }
        }

        //check to see if the graph is disconneced
        boolean isConnected = true;
        if (all.size() >= 10) {
            isConnected = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);
        }
        /* INCORRECT
        if (rest.size() < 16 && out.size() == 0) {
            BiGraph bg = new BiGraph(G.numLeftVertices(), G.numRightVertices());
            for (IndexEdge<IndexVertex> e : G.edges()) {
                IndexVertex v1 = e.endVertices().get(0);
                IndexVertex v2 = e.endVertices().get(1);
                if (rest.contains(v1) && rest.contains(v2)) {
                    int id1 = v1.id();
                    int id2 = v2.id();
                    bg.insertEdge(id1, id2);
                }
            }
            return CutBool.countNeighborhoods(bg);
        }*/

        //If not connected then call for components and multiply
        if(!isConnected)
        {

            long total=1;
            //returns list of components
            for(ArrayList<IndexVertex> vs : BasicGraphAlgorithms.connectedComponents(G,all))
            {
                VSubSet nall = new VSubSet(groundSet);
                nall.addAll(vs);
                VSubSet nout = nall.clone();
                VSubSet nrest = nall.clone();
                nout.retainAll(out);
                nrest.retainAll(rest);

                long next = boolDimBranch(G,nall,nout,nrest);
                if (next == 0) return 0;
                total *= next;
//				System.out.println("total = "+total);
            }

            return total;
        }

        //Find a vertex to branch on

        IndexVertex v = G.maxDegreeVertex(rest);

        //if v is out

        //look for vertices that can not be dominated
        //and vertices that have to be in
        boolean outValid = true;
        boolean changed = true;
        //0=from out 1=from rest

        //Moving v to out
        rest.remove(v);
        out.add(v);

        VSubSet oldAll = all.clone();
        VSubSet oldRest = rest.clone();
        VSubSet oldOut = out.clone();

        Stack<IndexVertex> toAdd = new Stack<IndexVertex>();

        //check if there will be a vertex that cannot be dominated if v is OUT then outvalid=false otherwise call
        while(changed)
        {
            changed = false;

            for(IndexVertex w : all)
            {
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
                if(intersection.isEmpty())
                {
                    if(out.contains(w)){
                        outValid=false;
                        break;
                    }
                    else
                    {
                        if(!toAdd.contains(w))
                            toAdd.push(w);
                    }
                }
                else
                {
                    if(out.contains(w) && (intersection.size() == 1))
                    {
                        //IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
                        IndexVertex u = intersection.first();
                        if(u!=null)
                        {
                            if(!toAdd.contains(u))
                                toAdd.push(u);
                        }
                    }
                }
            }
            while(outValid && !toAdd.isEmpty())
            {
                IndexVertex x = toAdd.pop();
                if(rest.contains(x)) {
                    putIn(G, all, out, rest, x);
                } else {
                    outValid = false;
                }
                changed=true;
            }
        }

        long total = 0;
        VSubSet outSave = null;
        VSubSet restSave = null;
        VSubSet allSave = null;
        if(outValid) {
            outSave = out.clone();
            restSave = rest.clone();
            allSave = all.clone();
        }

        // move vertices back
        out.cloneInPlace(oldOut);
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);

        // try v in
        putIn(G, all, out, rest, v);
        toAdd = new Stack<IndexVertex>();
        boolean inValid = true;
        changed = true;

        // check if there will be a vertex that cannot be dominated if v is IN then invalid=false otherwise call
        while(changed)
        {
            changed = false;
            for(IndexVertex w : all)
            {
                VSubSet intersection = rest.intersection(neighbourhoods.get(w.id()));
                //if(!rest.intersects(neighbourhoods.get(w.id())))
                if(intersection.isEmpty())
                {
                    if(out.contains(w)){
                        inValid=false;
                        break;
                    }
                    else
                    {
                        if(!toAdd.contains(w))
                            toAdd.push(w);
                    }
                }
                else
                {
                    if(out.contains(w) && (intersection.size() == 1))
                    {
                        //IndexVertex u = rest.oneIntersectElement(neighbourhoods.get(w.id()));
                        IndexVertex u = intersection.first();
                        if(u!=null)
                        {
                            if(!toAdd.contains(u))
                                toAdd.push(u);
                        }
                    }
                }
            }
            while(inValid && !toAdd.isEmpty())
            {
                if(rest.contains(toAdd.peek())) {
                    putIn(G, all, out, rest, toAdd.pop());
                } else {
                    inValid = false;
                }
                changed=true;
            }
        }

        if (inValid && outValid) {
            if (Math.random() > 0.5) {
                total = boolDimBranch(G, allSave, outSave, restSave) * 2;
            } else {
                total = boolDimBranch(G, all, out, rest) * 2;
            }
        } else if (outValid) {
            total = boolDimBranch(G, allSave, outSave, restSave);
        }
        else if (inValid)
        {
//			System.out.println("branching with "+v+" in");
            total = boolDimBranch(G, all, out, rest);
//			System.out.println("total = "+total);
        }

        //move vertices back
        out.cloneInPlace(oldOut);
        all.cloneInPlace(oldAll);
        rest.cloneInPlace(oldRest);

        return total;

    }

    private static void putIn(IndexGraph g, VSubSet all,VSubSet out,VSubSet rest,IndexVertex v)
    {
        //System.out.println("Before adding "+v);
        //System.out.println(all+", "+out+", "+rest);
        all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
    }

}
