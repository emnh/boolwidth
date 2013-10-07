package control;

import graph.Vertex;
import interfaces.IGraph;
import io.DiskGraph;
import io.FileLock;
import io.GraphViz;
import io.Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import boolwidth.BooleanDecomposition;
import boolwidth.CutBool;
import boolwidth.heuristics.LSDecomposition;
import boolwidth.heuristics.LocalSearchR;
import exceptions.FatalHandler;
import exceptions.InvalidGraphFileFormatException;

public class HeuristicTest<V, E> {

    public long bw;
    public long time;
    public LSDecomposition<V, E> decomposition;

    public static class HeuristicJob<V, E> implements Runnable {

        private final File file;
        private String name;

        public HeuristicJob(File file) {
            setName(file.toString());
            this.file = file;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(this.name);

            FileLock fl = null;

            try {
                // check if another process is working on this graph
                try {
                    fl = FileLock.tryLockFile(this.file.toString());
                    if (fl == null) {
                        System.out.printf(
                                "someone else is busy with \"%s\", skipping\n",
                                this.file);
                        return;
                    }
                } catch (FileNotFoundException e) {
                    FatalHandler.handle(e);
                }

                // read graph file
                IGraph<Vertex<V>, V, E> graph = null;
                try {
                    graph = ControlUtil.getTestGraph(this.file.toString());
                } catch (InvalidGraphFileFormatException e) {
                    System.out.printf(
                            "warning: invalid graph file format: \"%s\": \"%s\"\n",
                            this.file, e);
                    return;
                }

                System.out.printf("%s, %d nodes, %d edges\n", DiskGraph
                        .getFileName(graph), graph.numVertices(), graph.numEdges());

                // look for decomposition
                if (graph.numVertices() > 0 && graph.numVertices() < 64) {
                    HeuristicTest<V, E> ht = new HeuristicTest<V, E>();
                    ht.doHeuristic(graph);
                }
            } finally {
                if (fl != null) {
                    fl.release();
                }
            }
        }

        public void setName(String name) {
            this.name = name;
        }


    }

    public static<V, E> void doAll() {

        // set to number of cpu cores
        final int JOBS = Runtime.getRuntime().availableProcessors();

        System.err.printf("running %d jobs at a time\n", JOBS);

        ExecutorService jobManager = Executors.newFixedThreadPool(JOBS);

        String path = ControlUtil.GRAPHLIB;

        int i = 0;
        for (File file : DiskGraph.iterateOver(path, true)) {
            jobManager.execute(new HeuristicJob<V, E>(file));
            i++;
        }

        jobManager.shutdown();
        boolean done = false;
        while (!done) {
            System.err.println("waiting for jobs to finish");
            try {
                done = jobManager.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.err.println("done");
    }

    public static<V, E> void doAllLog() {
        String path = ControlUtil.GRAPHLIB;

        int i = 0;
        for (File file : DiskGraph.iterateOver(path, true)) {
            HeuristicTest.doLog(file.toString());
            i++;
            //System.out.println(i);
        }
    }


    public static void doLog(String fileName) {
        File outputFile = new File(ControlUtil.getOutputDir(fileName),
        "decomp.xml");
        Storage st = null;
        if (outputFile.exists()) {
            st = Storage.fromFile(outputFile.toString());
        } else {
            //System.out.printf("no decomposition for: %s\n", fileName);
            return;
        }
        IGraph<?,?,?> graph = ControlUtil.getTestGraph(fileName);

        System.out.printf("boolean-width(decomposition(%s(v=%d,e=%d))): bw=%d, ", fileName,
                //st.getGraph().numVertices(), st.getGraph().numEdges(),
                graph.numVertices(), graph.numEdges(),
                BooleanDecomposition.getBoolWidth(st.getBestDecomposition()));

        System.out.println("time: " + 0);
    }

    public static <V1, E1> void main(String[] args) throws Exception {

        if (args.length > 0 && args[0].equals("log")) {

            doAllLog();

        } else if (args.length > 0 && args[0].equals("all")) {

            doAll();

        } else {

            //String fileName = ControlUtil.GRAPHLIB + "prob/pigs-pp.dgf";
            String fileName = ControlUtil.GRAPHLIB + "coloring/queen7_7.dgf";
            if (args.length > 0) {
                fileName = args[0];
            }

            FileLock fl = null;
            try {
                /*
                fl = FileLock.tryLockFile(fileName);
                if (fl == null) {
                    System.out.printf(
                            "someone else is busy with \"%s\", skipping\n",
                            fileName);
                    return;
                } */

                // int[] K = { 2, 3, 4 };
                IGraph<Vertex<Integer>, Integer, String> graph;
                graph = ControlUtil.getTestGraph(fileName);

                // String fileName = "4x4-grid";
                // graph = ConstructGraph.gridGraph(4, 4);

                // graph = ConstructGraph.gridGraph(5, 5);
                // int[] K = { 2, 3, 4 };
                // String Ks = Arrays.toString(K);
                // graph = ConstructGraph.cubeGraph(true, K);

                // System.out.println("Graph: " + graph);

                // HeuristicTest<V1, E1> ht = new HeuristicTest<V1, E1>();
                HeuristicTest<Integer, String> ht = new HeuristicTest<Integer, String>();

                ht.doHeuristic(graph);

                if (ht.decomposition != null) {
                    GraphViz.saveGraphDecomposition(fileName, graph, ht.bw,
                            ht.decomposition, ht.time);
                }
            //} catch (FileNotFoundException e) {
            //    FatalHandler.handle(e);
            } finally {
                if (fl != null) fl.release();
            }
        }
        // get rid of potentially hung threads (that is, incomplete iterations)
        System.exit(0);
    }

    public <TVertex extends Vertex<V>> void doHeuristic(
            IGraph<Vertex<V>, V, E> graph) {
        long start = System.currentTimeMillis();

        String fileName = DiskGraph.getFileName(graph);
        assert (fileName != null);

        File outputFile = new File(ControlUtil.getOutputDir(fileName),
        "decomp.xml");
        Storage st = null;
        if (outputFile.exists()) {
            st = Storage.fromFile(outputFile.toString());
            // if (st.getBestDecomposition() != null) {
            // System.out.printf("boolean-width(decomposition(%s)): %d, ",
            // fileName,
            // BooleanDecomposition.getBoolWidth(st.getBestDecomposition()));
            // }
        } else {
            st = new Storage(graph, this.decomposition);
        }

        LocalSearchR<V, E> lsr = new LocalSearchR<V, E>();

        LocalSearchR<V, E>.Result result = lsr.localSearch(graph, st
                .getBestDecomposition());

        if (result.success) {
            this.decomposition = result.decomposition;
            this.bw = CutBool.booleanWidth(this.decomposition);
            st.updateBestDecomposition(this.decomposition);
        } else {
            System.out.println("failed to meet bound");
            return;
        }

        // System.out.println("Decomposition: " +
        // this.decomposition.toString());
        System.out.println("Decomposition: "
                + st.getBestDecomposition().toString());

        st.save(outputFile.toString());

        System.out.printf("boolean-width(decomposition(%s(v=%d,e=%d))): bw=%d, ", fileName,
                graph.numVertices(), graph.numEdges(),
                BooleanDecomposition.getBoolWidth(st.getBestDecomposition()));
        long end = System.currentTimeMillis();
        this.time = end - start;
        System.out.println("time: " + this.time);
    }
}
