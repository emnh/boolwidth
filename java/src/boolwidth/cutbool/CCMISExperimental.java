package boolwidth.cutbool;

import sadiasrc.graph.*;
import sadiasrc.heuristic.NDOrdering;
import sadiasrc.util.IndexedSet;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;

public class CCMISExperimental {

	//stores neighborhood in bitsets for faster intersection and union
	private static ArrayList<VSubSet> neighbourhoods;
	//groundset for bitset
	private static IndexedSet<IndexVertex> groundSet;

	private static VSubSet leftVertices;
	private static VSubSet rightVertices;
	private static VSubSet minLeftRightVertices;

    private static ArrayList<IndexVertex> ndOrdering;

	private static long zeroCount;

    public static long[] containingCount;

    private static final boolean LOOK_FOR_TWINS = false;
    private static final boolean LOOK_FOR_OUT_TWINS = false;
    private static final boolean SELECT_SEPARATOR = false;
    private static final boolean USE_NDORDERING = false;
    private static final boolean GASPERS_BRANCH = false || SELECT_SEPARATOR;

    // this option leads to incorrect answer, should not be used
    private static final boolean ENUMERATE_OUT_SEPARATOR = false;

    public static class Dissect {
        public static VSubSet findSeparator(BiGraph g, VSubSet all) {
            VSubSet seen = new VSubSet(groundSet);

            IndexVertex firstVertex = null;
            int minDegree = Integer.MAX_VALUE;
            for (IndexVertex v : all) {
                int degree = neighbourhoods.get(v.id()).intersection(all).size();
                if (degree < minDegree) {
                    firstVertex = v;
                    minDegree = degree;
                }
            }

            ArrayList<VSubSet> layers = new ArrayList<>();
            VSubSet firstLayer = new VSubSet(groundSet);
            seen.add(firstVertex);
            firstLayer.add(firstVertex);
            layers.add(firstLayer);

            int layerIndex = 0;
            while (seen.size() < (all.size() - layers.get(layerIndex).size()) / 2
                    && !layers.get(layerIndex).isEmpty()) {
                VSubSet nextLayer = new VSubSet(groundSet);
                for (IndexVertex current : layers.get(layerIndex)) {
                    VSubSet neighbours = neighbourhoods.get(current.id());
                    neighbours = neighbours.intersection(all);
                    neighbours = neighbours.subtract(seen);
                    seen = seen.union(neighbours);
                    nextLayer = nextLayer.union(neighbours);
                }
                layers.add(nextLayer);
                layerIndex++;
            }

            VSubSet returnValue = null;
            if (!layers.get(layerIndex).isEmpty()) {
                returnValue = layers.get(layerIndex);
                // System.out.println("separator size: (" + returnValue.size() + "/" + all.size() + ")");
            } else {
                returnValue = all;
            }

            return returnValue;
        }
    }
	
	public static long BoolDimBranch(BiGraph G)
	{
		neighbourhoods = new ArrayList<VSubSet>(G.numVertices());
		groundSet = new IndexedSet<IndexVertex>(G.vertices());
        containingCount = new long[G.numVertices()];
		for(int i = 0; i < G.numVertices(); i++)
		{
            IndexVertex v = G.getVertex(i);
            containingCount[v.id()] = 0;
			neighbourhoods.add(new VSubSet(groundSet, G.neighbours(v)));
		}

		leftVertices = new VSubSet(groundSet);
		leftVertices.addAll(G.leftVertices());
		rightVertices = new VSubSet(groundSet);
		rightVertices.addAll(G.rightVertices());

		minLeftRightVertices = leftVertices.size() >= rightVertices.size() ? rightVertices : leftVertices;

        if (USE_NDORDERING) {
            ndOrdering = NDOrdering.computeNDOrdering(G);
        }
		
		VSubSet all = new VSubSet(groundSet);
		VSubSet out = new VSubSet(groundSet);
		VSubSet rest = new VSubSet(groundSet);
        VSubSet separator = new VSubSet(groundSet);
		all.addAll((Collection<? extends IndexVertex>) G.vertices());
		rest.addAll((Collection<? extends IndexVertex>) G.vertices());

        zeroCount = 0;

		long count = boolDimBranch(G, all, out, rest, separator, 0);

        // System.out.println("zero count: " + zeroCount);

        return count;
	}

	/**
	 * @param G The bipartite graph of which we want to compute the boolean dimension
	 * @param all = (out union rest)
	 * @param out =X the set of vertices chosen not to be used and not dominated by in (not in the IDS not dominated)
	 * @param rest =P the set of vertices not yet considered
	 * @return
	 * @throws InvalidAlgorithmParameterException
	 */

	public static long boolDimBranch(BiGraph G, VSubSet all, VSubSet out, VSubSet rest, VSubSet separator, int depth) {
        // check termination conditions
		if(rest.isEmpty()) {
			if(out.isEmpty()) {
                return 1;
            } else {
                // zeroCount++;
                return 0;
            }
		}

		// check to see if the graph is disconnected
        boolean isConnected = BasicGraphAlgorithms.isConnected(G, all, neighbourhoods);

		// if not connected then call for components and multiply
		if(!isConnected) {
			long total = 1;
			for(VSubSet vs : BasicGraphAlgorithms.connectedComponentsVSubSets(G, all)) {
				VSubSet newAll = vs;
				VSubSet newOut = newAll.intersection(out);
				VSubSet newRest = newAll.intersection(rest);
                VSubSet newSeparator = new VSubSet(groundSet);

				long next = boolDimBranch(G, newAll, newOut, newRest, newSeparator, depth);
                if (next == 0) return 0;
                total = Math.multiplyExact(total, next);
			}

			return total;
		}

        // check to see if out is a separator
        if (ENUMERATE_OUT_SEPARATOR && out.size() > 0 && out.size() < 5) {
            boolean outIsSeparator = !BasicGraphAlgorithms.isConnected(G, rest, neighbourhoods);
            if (outIsSeparator) {
                ArrayList<VSubSet> connectedComponents =
                        new ArrayList<>(BasicGraphAlgorithms.connectedComponentsVSubSets(G, rest));

//                if (connectedComponents.size() > 2) {
//                    ArrayList<VSubSet> connectedComponents2 = new ArrayList<>();
//                    connectedComponents2.add(connectedComponents.get(0));
//                    VSubSet restComponents = new VSubSet(groundSet);
//                    for (int i = 1; i < connectedComponents.size(); i++) {
//                        restComponents = restComponents.union(connectedComponents.get(i));
//                    }
//                    connectedComponents2.add(restComponents);
//
//                    System.out.println("out at depth " + depth +
//                            " separates " + connectedComponents.size() +
//                            " components and out size is " + out.size());
//                }

                ArrayList<IndexVertex> outAL = new ArrayList<>(out);

                long allTotal = 0;
                int outSize = out.size();

                Stack<ArrayList<Integer>> componentPositions = new Stack<>();
                ArrayList<Integer> currentComponentPositions = new ArrayList<>();
                componentPositions.push(currentComponentPositions);
                while (!componentPositions.isEmpty()) {
                    currentComponentPositions = componentPositions.pop();
                    for (int componentIndex = 0; componentIndex < connectedComponents.size(); componentIndex++) {
                        ArrayList<Integer> currentComponentPositions2 = new ArrayList<>(currentComponentPositions);
                        int currentOutIndex = currentComponentPositions2.size();
                        IndexVertex currentOut = outAL.get(currentOutIndex);
                        VSubSet currentOutNeighbourHood = neighbourhoods.get(currentOut.id());
                        currentComponentPositions2.add(componentIndex);
                        if (connectedComponents.get(componentIndex).intersection(currentOutNeighbourHood).size() == 0) {
                            continue;
                        }
                        if (currentComponentPositions2.size() < outSize) {
                            componentPositions.push(currentComponentPositions2);
                        } else {
                            // System.out.println("pos: " + currentComponentPositions2);

                            long total = 1;
                            int currentComponent = 0;
                            for(VSubSet vs : connectedComponents)
                            {
                                VSubSet newAll = vs.clone();
                                VSubSet newOut = new VSubSet(groundSet); //newAll.intersection(out);
                                VSubSet newRest = newAll.intersection(rest);
                                VSubSet newSeparator = new VSubSet(groundSet);

                                int outIndex = 0;
                                for (int position : currentComponentPositions2) {
                                    if (position == currentComponent) {
                                        IndexVertex vOut = outAL.get(outIndex);
                                        newAll.add(vOut);
                                        newOut.add(vOut);
                                    }
                                    outIndex++;
                                }

                                long next = boolDimBranch(G, newAll, newOut, newRest, newSeparator, depth);
                                if (next == 0) {
                                    // System.out.println("total 0");
                                    total = 0;
                                    break;
                                }
                                total = Math.multiplyExact(total, next);
                                currentComponent++;
                            }
                            allTotal += total;
                        }
                    }
                }

                return allTotal;
            }
        }

        // look for twins and branch after removing one of them
        // generally slower
        if (LOOK_FOR_TWINS) {
            TreeMap<VSubSet, IndexVertex> closedNeighbourHoods = new TreeMap<>();
            for (IndexVertex r : rest) {
                VSubSet rClosedNeighbourHood = neighbourhoods.get(r.id());
                rClosedNeighbourHood = rClosedNeighbourHood.intersection(all);
                rClosedNeighbourHood.add(r);
                if (closedNeighbourHoods.containsKey(rClosedNeighbourHood)) {
                    IndexVertex s = closedNeighbourHoods.get(rClosedNeighbourHood);
                    // System.out.println("twin: (" + r.id() + "," + s.id() + ")");

                    VSubSet rAll = all.clone();
                    VSubSet rRest = rest.clone();
                    VSubSet rOut = out.clone();

                    rAll.remove(s);
                    rRest.remove(s);

                    long containingR = containingCount[r.id()];
                    //System.out.println("containingR: " + containingR);
                    long total = boolDimBranch(G, rAll, rOut, rRest, separator, depth);
                    containingR = containingCount[r.id()] - containingR;

                    return total + containingR;
                }
                closedNeighbourHoods.put(rClosedNeighbourHood, r);
            }
        }

        // look for out twins and branch after removing one of them
        // generally slower
        if (LOOK_FOR_OUT_TWINS) {
            TreeMap<VSubSet, IndexVertex> closedNeighbourHoods = new TreeMap<>();
            for (IndexVertex r : out) {
                VSubSet rNeighbourHood = neighbourhoods.get(r.id());
                rNeighbourHood = rNeighbourHood.intersection(all);
                if (closedNeighbourHoods.containsKey(rNeighbourHood)) {
                    IndexVertex s = closedNeighbourHoods.get(rNeighbourHood);
                    // System.out.println("out twin: (" + r.id() + "," + s.id() + ")");

                    VSubSet rAll = all.clone();
                    VSubSet rRest = rest.clone();
                    VSubSet rOut = out.clone();

                    rAll.remove(s);
                    rOut.remove(s);

                    long total = boolDimBranch(G, rAll, rOut, rRest, separator, depth);

                    return total;
                }
                closedNeighbourHoods.put(rNeighbourHood, r);
            }
        }

		// find a vertex to branch on
        boolean force_gaspers_if_out = false;
        IndexVertex v = null;
        if (USE_NDORDERING) {
            // can use all instead of rest, but need to use GASPERS_BRANCH then. it is slower.
            VSubSet selection = rest;
            for (IndexVertex o : ndOrdering) {
                if (selection.contains(o)) {
                    v = o;
                    break;
                }
            }
        } else if (SELECT_SEPARATOR) {
            VSubSet remainingSeparator = separator.intersection(all);
            if (remainingSeparator.isEmpty()) {
                if (all.size() > 10) {
                    separator = Dissect.findSeparator(G, all);
                    remainingSeparator = separator.intersection(all);
                } else {
                    separator = all;
                    remainingSeparator = all;
                }
            }
            // select maximum degree
            int maxDeg = -1;
            VSubSet selection = remainingSeparator;
            for (IndexVertex w : selection) {
                int t = neighbourhoods.get(w.id()).intersection(rest).size();
                if (t > maxDeg) {
                    v = w;
                    maxDeg = t;
                }
            }
            if (v == null) {
                selection = rest;
                for (IndexVertex w : selection) {
                    int t = neighbourhoods.get(w.id()).intersection(rest).size();
                    if (t > maxDeg) {
                        v = w;
                        maxDeg = t;
                    }
                }
            }
        } else if (GASPERS_BRANCH) {
            // if there is an out vertex of degree 2 select it
            for (IndexVertex w : out) {
                if (neighbourhoods.get(w.id()).intersection(all).size() == 2) {
                    v = w;
                    break;
                }
            }

            if (v == null) {
                // select minimum degree
                int minDeg = Integer.MAX_VALUE;
                VSubSet selection = all.intersection(minLeftRightVertices);
                for (IndexVertex w : selection) {
                    int t = neighbourhoods.get(w.id()).intersection(all).size();
                    if (t < minDeg) {
                        v = w;
                        minDeg = t;
                    }
                }
                if (v == null) {
                    selection = rest;
                    for (IndexVertex w : selection) {
                        int t = neighbourhoods.get(w.id()).intersection(all).size();
                        if (t < minDeg) {
                            v = w;
                            minDeg = t;
                        }
                    }
                }
            }
        } else {
            VSubSet selection = rest.intersection(minLeftRightVertices);
            VSubSet degreeSelection = rest;

            /*
            boolean outIsSeparator = !BasicGraphAlgorithms.isConnected(G, rest, neighbourhoods);
            if (outIsSeparator) {
                separator = out;
            }
            VSubSet remainingSeparator = separator.intersection(all);
            if (!remainingSeparator.isEmpty()) {
                selection = remainingSeparator;
                degreeSelection = remainingSeparator;
                force_gaspers_if_out = true;
            }*/

            // select maximum degree
            int maxDeg = -1;
            for (IndexVertex w : selection) {
                int t = neighbourhoods.get(w.id()).intersection(degreeSelection).size();
                if (t > maxDeg) {
                    v = w;
                    maxDeg = t;
                }
            }
            if (v == null) {
                selection = rest;
                for (IndexVertex w : selection) {
                    int t = neighbourhoods.get(w.id()).intersection(degreeSelection).size();
                    if (t > maxDeg) {
                        v = w;
                        maxDeg = t;
                    }
                }
            }
        }

        long total = 0;

        if (rest.contains(v)) {
            // try v in and branch
            VSubSet vInAll = all.clone();
            VSubSet vInRest = rest.clone();
            VSubSet vInOut = out.clone();
            ArrayList<IndexVertex> inNewlyAdded = new ArrayList<>();
            inNewlyAdded.add(v);

            // moving v to in
            putIn(G, vInAll, vInOut, vInRest, v);
            if (isValid(G, vInAll, vInOut, vInRest, inNewlyAdded)) {
                long vCount = boolDimBranch(G, vInAll, vInOut, vInRest, separator, depth + 1);
                for (IndexVertex a : inNewlyAdded) {
                    containingCount[a.id()] += vCount;
                }
                total += vCount;
            }
        }

        if (GASPERS_BRANCH || force_gaspers_if_out) {
            // try all neighbours of v in
            VSubSet vRestNeighbours = neighbourhoods.get(v.id()).intersection(rest);

            ArrayList<IndexVertex> vNList = new ArrayList<>(vRestNeighbours);
            VSubSet before = new VSubSet(groundSet);

            for (int i = 0; i < vNList.size(); i++) {
                IndexVertex w = vNList.get(i);
                VSubSet wNeighbours = neighbourhoods.get(w.id());

                // try w in and branch
                VSubSet vInAll = all.clone();
                VSubSet vInRest = rest.clone();
                VSubSet vInOut = out.clone();
                ArrayList<IndexVertex> inNewlyAdded = new ArrayList<>();

                VSubSet newOut = before.subtract(wNeighbours);
                vInRest = vInRest.subtract(newOut);
                vInOut = vInOut.union(newOut);

                //vInAll.remove(v);
                //vInRest.remove(v);
                //vInOut.remove(v);
                inNewlyAdded.add(w);

                // moving w to in
                putIn(G, vInAll, vInOut, vInRest, w);
                if (isValid(G, vInAll, vInOut, vInRest, inNewlyAdded)) {
                    long vCount = boolDimBranch(G, vInAll, vInOut, vInRest, separator, depth + 1);
                    for (IndexVertex a : inNewlyAdded) {
                        containingCount[a.id()] += vCount;
                    }
                    total += vCount;
                }
                before.add(w);
            }

        } else {
            // try v out and branch
            VSubSet vOutAll = all.clone();
            VSubSet vOutRest = rest.clone();
            VSubSet vOutOut = out.clone();
            ArrayList<IndexVertex> outNewlyAdded = new ArrayList<>();

            // moving v to out
            vOutRest.remove(v);
            vOutOut.add(v);
            if (isValid(G, vOutAll, vOutOut, vOutRest, outNewlyAdded)) {
                total += boolDimBranch(G, vOutAll, vOutOut, vOutRest, separator, depth + 1);
                for (IndexVertex a : outNewlyAdded) {
                    containingCount[a.id()] += total;
                }
            }
        }

		return total;
	}

    /* look for vertices that can not be dominated and vertices that have to be in */
    private static boolean isValid(BiGraph G, VSubSet all, VSubSet out, VSubSet rest, ArrayList<IndexVertex> newlyAdded) {
        boolean valid = true;
        boolean changed = true;
        Stack<IndexVertex> toAdd = new Stack<>();

        // check if there will be a vertex that cannot be dominated if v is OUT then outValid = false otherwise branch
        while(changed)
        {
            changed = false;

            for(IndexVertex w : all) {
                VSubSet wRestNeighbourHood = rest.intersection(neighbourhoods.get(w.id()));
                if(wRestNeighbourHood.isEmpty()) {
                    // if N(w) in rest is empty no vertex can dominate w, so it must be in
                    if(out.contains(w)){
                        // H2 of Gaspers
                        valid = false;
                        break;
                    } else {
                        // R2 of Gaspers
                        if(!toAdd.contains(w)) {
                            toAdd.push(w);
                        }
                    }
                } else {
                    // if only one neighbour can dominate w that neighbour must be in
                    // R1 of Gaspers
                    if(out.contains(w) && (wRestNeighbourHood.size() == 1)) {
                        IndexVertex u = wRestNeighbourHood.first();
                        if(u != null) {
                            if(!toAdd.contains(u)) {
                                toAdd.push(u);
                            }
                        }
                    }
                }
            }
            while(valid && !toAdd.isEmpty())
            {
                IndexVertex x = toAdd.pop();
                if(rest.contains(x)) {
                    putIn(G, all, out, rest, x);
                    newlyAdded.add(x);
                } else {
                    valid = false;
                }
                changed=true;
            }
        }
        return valid;
    }

    private static void putIn(IndexGraph g, VSubSet all, VSubSet out, VSubSet rest, IndexVertex v)
	{
		all.remove(v);
        out.remove(v);
        rest.remove(v);

        VSubSet hood = neighbourhoods.get(v.id());
        all.subtractInPlace(hood);
        rest.subtractInPlace(hood);
        out.subtractInPlace(hood);
	}

}
