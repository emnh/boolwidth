package control;

import boolwidth.CutBool;
import boolwidth.cutbool.*;
import boolwidth.cutbool.ccmis_trial.CCMISRe;
import boolwidth.cutbool.ccmis_trial.IndexGraph;
import boolwidth.opencl.JOCLOpenCLCutBoolComputer;
import boolwidth.opencl.OpenCLCutBoolComputer;
import graph.AdjacencyListGraph;
import graph.BiGraph;
import graph.Edge;
import graph.Vertex;
import io.DiskGraph;
import sadiasrc.decomposition.CCMIS;
import sadiasrc.decomposition.CCMISApprox;
import sadiasrc.decomposition.CCMISStack;

import java.util.ArrayList;
import java.util.function.LongSupplier;

/**
 * Created by emh on 5/22/14.
 */
public class MISBackTrackTest {

    public static void main(String[] args) {

        ArrayList<String> fileNames  = new ArrayList<String>();

        fileNames.add(ControlUtil.GRAPHLIB + "other/risk.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "prob/pigs-pp.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1sem_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "prob2/BN_100.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1r69_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1ail_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "other/macaque71.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/jean.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "protein/1aba_graph.dimacs");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/david.dgf");
        fileNames.add(ControlUtil.GRAPHLIB + "coloring/queen8_8.dgf");
        //fileNames.add(ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs");
        //fileNames.add(ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs");

        //fileNames.clear();
        //fileNames.add(ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf");
        //fileNames.add(ControlUtil.GRAPHLIB + "protein/1sem_graph.dimacs");

        //JITWarmUp();

        for (String file : fileNames) {
            System.out.println(file);
            AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
            DiskGraph.readGraph(file, graph);
            processFile(file);
            System.out.println("");
        }
    }

    protected static void JITWarmUp() {
        //String fileName = ControlUtil.GRAPHLIB_OURS + "hsugrid/hsu-4x4.dimacs"; //ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        String fileName = ControlUtil.GRAPHLIB + "coloring/queen5_5.dgf";
        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);
        ArrayList<Vertex<Integer>> lefts = new ArrayList<Vertex<Integer>>();
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);

        // default JIT compile threshold is 10000 on server, 1500 on client
        for (int i = 0; i < 10000; i++) {
            CutBool.countNeighborhoods(bigraph);
            CCMIS.BoolDimBranch(convertSadiaBiGraph(bigraph));
            CCMISRe.BoolDimBranch(new IndexGraph(bigraph));
        }
    }

    public static sadiasrc.graph.BiGraph convertSadiaBiGraph(BiGraph<Integer, String> bigraph) {

        sadiasrc.graph.BiGraph sadiaBiGraph = new sadiasrc.graph.BiGraph(bigraph.numLeftVertices(), bigraph.numRightVertices());

        for (Edge<Vertex<Integer>, Integer, String> e : bigraph.edges()) {
            int id1 = e.endVertices().get(0).id();
            int id2 =  e.endVertices().get(1).id();
            //System.out.printf("inserting edge %d-%d\n", id1, id2);
            sadiaBiGraph.insertEdge(id1, id2);
        }

        return sadiaBiGraph;
    }

    /*public static IndexGraph convertIndexGraph(BiGraph<Integer, String> bigraph) {

        IndexGraph sadiaBiGraph = new IndexGraph(bigraph.numVertices());

        for (Edge<Vertex<Integer>, Integer, String> e : bigraph.edges()) {
            int id1 = e.endVertices().get(0).id();
            int id2 =  e.endVertices().get(1).id();
            sadiaBiGraph.insertEdge(id1, id2);
        }

        return sadiaBiGraph;
    }*/

    static class BenchmarkResult {
        public long duration;
        public long count;
        public long returnValue;

        public long eachDuration() {
            return duration / count;
        }
    }

    public static BenchmarkResult doBenchMark(LongSupplier fun, boolean once) {
        long startTime;
        long endTime;
        long duration = 0;
        int count = 1;

        startTime = System.nanoTime();
        long ret = fun.getAsLong();
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        while (duration < 1000 && !once) {
            ret = fun.getAsLong();
            count++;
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1000000;
        }
        BenchmarkResult result = new BenchmarkResult();
        result.duration = duration;
        result.count = count;
        result.returnValue = ret;
        return result;
    }

    public static void processFile(String fileName) {
        AdjacencyListGraph<Vertex<Integer>, Integer, String> graph = new AdjacencyListGraph.D<Integer, String>();
        DiskGraph.readGraph(fileName, graph);
        ArrayList<Vertex<Integer>> lefts = new ArrayList<Vertex<Integer>>();
        /*int[] queen88a = new int[] {0,32,1,4,36,37,6,38,7,8,9,43,12,14,46,47,48,17,50,19,51,20,21,25,26,58,27,61,31};
        for (int i : queen88a) {
            lefts.add(graph.getVertex(i));
        }*/
        for (int i = 0; i < graph.numVertices() / 2; i++) {
            lefts.add(graph.getVertex(i));
        }
        BiGraph<Integer, String> bigraph = new BiGraph<>(lefts, graph);

        BiGraph<Integer, String> bigraphdup = new BiGraph<>(graph);

        final int sampleCount = 1000;
        long bw = 0;
        long est;
        boolean test = true;

        BenchmarkResult ret;

        ret = doBenchMark(() -> CutBool.countNeighborhoods(bigraph), test);
        System.out.printf("UNN (bigraph) (%dms): log2(%d)=%.2f\n", ret.eachDuration(), ret.returnValue, CutBool.getLogBW(ret.returnValue));
        /*
        ret = doBenchMark(() -> MISBackTrackPersistent.countNeighborhoods(graph), test);
        System.out.printf("MIS backtrack persistent (%dms): %d\n", ret.eachDuration(), ret.returnValue);
        */

        ret = doBenchMark(() -> CCMIS.BoolDimBranch(convertSadiaBiGraph(bigraph)), test);
        System.out.printf("Sadia CCMIS backtrack (%dms): log2(%d)=%.2f\n", ret.eachDuration(), ret.returnValue, CutBool.getLogBW(ret.returnValue));

        /*
        ret = doBenchMark(() -> CCMISStack.BoolDimBranch(convertSadiaBiGraph(bigraph)), test);
        System.out.printf("Explicit Stack CCMIS (%dms): %d\n", ret.eachDuration(), ret.returnValue);

        ret = doBenchMark(() -> CCMISRe.BoolDimBranch(new IndexGraph(bigraph)), test);
        System.out.printf("Eivind CCMIS backtrack (%dms): %d\n", ret.eachDuration(), ret.returnValue);
*/

        ret = doBenchMark(() -> CBBacktrackEstimateBinary.estimateNeighborhoods(bigraph, sampleCount), test);
        System.out.printf("Approx CB backtrack (%dms): log2(%d)=%.2f\n", ret.eachDuration(), ret.returnValue, CutBool.getLogBW(ret.returnValue));

        ret = doBenchMark(() -> CBBackTrackEstimateBinaryFast.estimateNeighborhoods(bigraph, sampleCount), test);
        System.out.printf("Fast Approx CB backtrack (%dms): log2(%d)=%.2f\n", ret.eachDuration(), ret.returnValue, CutBool.getLogBW(ret.returnValue));

        JOCLOpenCLCutBoolComputer.initialize();
        ret = doBenchMark(() -> JOCLOpenCLCutBoolComputer.estimateNeighbourHoods(bigraph, sampleCount), test);
        System.out.printf("OpenCL Approx CB (%dms): log2(%d)=%.2f\n", ret.eachDuration(), ret.returnValue, CutBool.getLogBW(ret.returnValue));

        /*
        ret = doBenchMark(() -> CCMISApprox.BoolDimBranch(convertSadiaBiGraph(bigraph), sampleCount), test);
        System.out.printf("Approx CCMIS backtrack (%dms): %d\n", ret.eachDuration(), ret.returnValue);

        est = MISBackTrack.countNeighborhoods(bigraph);
        System.out.printf("MIS backtrack (%dms): %d\n", duration, est);

        est = MISBackTrackPersistentApproximation.countNeighborhoods(bigraph, sampleCount);
        System.out.printf("MIS approximation (%dms): %d\n", duration, est);

        est = CBBacktrackBinary.countNeighborhoods(bigraph);
        System.out.printf("CB bactrack (%dms): %d\n", duration, est);

        est = CBBacktrackEstimateBinary.estimateNeighborhoods(bigraph, sampleCount);
        System.out.printf("CB bactrack bin approximation (%dms): %d\n", duration, est);
        */

    }
}
