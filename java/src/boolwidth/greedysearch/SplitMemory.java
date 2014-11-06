package boolwidth.greedysearch;

import boolwidth.greedysearch.ds.TreeElement;
import boolwidth.greedysearch.ds.TreeElementComparator;
import com.github.krukow.clj_lang.PersistentHashSet;
import graph.Vertex;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by emh on 11/3/2014.
 */
public class SplitMemory extends Split {

    private double CHEAT_RATIO = 1.0;
    private double lastWorstRatio = 2.0;

    protected ArrayList<TreeElement> oldMoves = new ArrayList<>();
    protected HashSet<Vertex<Integer>> oldMovesVertices = new HashSet<>();
    HashSet<Vertex<Integer>> invalids = new HashSet<>();

    public SplitMemory(SplitMemory old) {
        copy(old);
    }

    public SplitMemory(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        super(depth, decomposition, rights);
    }

    @Override
    public SplitMemory decomposeAdvance(MeasureCut measureCut) {
        SplitMemory result = new SplitMemory(this);
        if (done()) {
            return this;
        } else {
            long oldcb = measureCut.applyAsLong(lefts, null);
            long minmove = Long.MAX_VALUE;
            Vertex<Integer> tomove = null;

            ArrayList<TreeElement> moves = new ArrayList<>();
            HashSet<Vertex<Integer>> movesVertices = new HashSet<Vertex<Integer>>();

            boolean foundit = false;

            int i = 0;
            JSONObject json = new JSONObject();
            json.put("oldmoves", oldMoves);
            System.out.printf("size: %d, %s\n", oldMoves.size(), json.toString());

            //HashSet<Vertex<Integer>> oldMovesVertices = new HashSet<>();

            JSONObject json2 = new JSONObject();
            json2.put("incident", invalids);
            System.out.printf("size: %d, %s\n", invalids.size(), json2.toString());

            for (TreeElement te : oldMoves) {
                Vertex<Integer> v = te.vertex;
                //System.out.printf("oldmoves: %d, %d\n", te.cutbool, v.id());
                if (!rights.contains(v)) {
                    // must have been moved last time
                    continue;
                }
                if (!foundit) {
                    i += 1;
                    PersistentHashSet<Vertex<Integer>> newlefts = lefts.cons(v);
                    long cb = measureCut.applyAsLong(newlefts, v);
                    addOld(moves, movesVertices, v, cb);
                    //addOld(moves, movesVertices, v, te.cutbool*2);
                    //decomposition.getCutBool(newlefts, true);
                    if (te.cutbool < minmove) {
                        minmove = cb;
                        tomove = v;
                        if (cb <= oldcb) {
                            // exit early if we didn't increase
                            System.out.printf("lower-old-cheated: %d/%d\n", i, oldMoves.size());
                            foundit = true;
                        }
                        /*if (!invalids.contains(v)) {
                            System.out.printf("order-old-cheated: %d/%d\n", i, oldMoves.size());
                            foundit = true;
                        }*/
                    }
                } else {
                    addOld(moves, movesVertices, v, te.cutbool*2);
                }
            }

            i = 0;
            if (!foundit) {
                for (Vertex<Integer> v : rights) {
                    if (movesVertices.contains(v)) {
                        // we processed those before
                        continue;
                    }
                    i += 1;
                    PersistentHashSet<Vertex<Integer>> newlefts = lefts.cons(v);
                    long cb = measureCut.applyAsLong(newlefts, v);
                    addOld(moves, movesVertices, v, cb);
                    //decomposition.getCutBool(newlefts, true);
                    if (cb < minmove) {
                        minmove = cb;
                        tomove = v;
                        if (cb <= oldcb) {
                            // exit early if we didn't increase
                            System.out.printf("lower-cheated: %d/%d\n", i, rights.size());
                            break;
                        }
                    }
                }
            }
            moves.sort(new TreeElementComparator());
            result.lefts = result.lefts.cons(tomove);
            result.rights = result.rights.disjoin(tomove);
            result.cutbool = minmove;
            result.lastWorstRatio = (double) minmove / measureCut.applyAsLong(lefts, null);
            System.out.printf("last worst ratio: %.2f\n", result.lastWorstRatio);
            result.reference = tomove;
            result.oldMoves = moves;
            result.oldMovesVertices = oldMovesVertices;
            result.invalids = invalids;
            result.invalids.addAll(this.getDecomposition().getGraph().incidentVertices(tomove));
            result.logStatement();
        }
        return result;
    }

    private void addOld(ArrayList<TreeElement> moves, HashSet<Vertex<Integer>> movesVertices, Vertex<Integer> v, long cb) {
        moves.add(new TreeElement(cb, v));
        movesVertices.add(v);
        invalids.remove(v);
    }
}
