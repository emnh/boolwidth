package boolwidth.opencl;

import com.nativelibs4java.opencl.*;
import com.nativelibs4java.opencl.util.*;
import com.nativelibs4java.util.*;
import graph.BiGraph;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import org.bridj.Pointer;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.bridj.Pointer.*;

public class OpenCLCutBoolComputer {

    static OpenCLCutBoolComputer cache = null;
    String src = null;
    CLContext context;
    ByteOrder byteOrder;
    CLProgram program;
    CLKernel addFloatsKernel;

    public OpenCLCutBoolComputer() {
        // Read the program sources and compile them :
        try {
            src = IOUtils.readText(OpenCLCutBoolComputer.class.getResource("TutorialKernels.cl"));
        } catch (IOException e) {
            System.out.println("failed to locate OpenCL kernel TutorialKernels.cl");
            System.exit(-1);
        }
        context = JavaCL.createBestContext();
        byteOrder = context.getByteOrder();
        program = context.createProgram(src).build();
        addFloatsKernel = program.createKernel("count_hoods");
    }

    public static void initialize() {
        if (cache == null) {
            cache = new OpenCLCutBoolComputer();
        }
    }

    public static <V, E> long estimateNeighbourHoods(BiGraph<V, E> bigraph, int sampleCount) {
        initialize();
        return cache.estimateNeighbourHoods2(bigraph, sampleCount);
    }

    public <V, E> long estimateNeighbourHoods2(BiGraph<V, E> bigraph, int sampleCount) {
        // prepare data
        ArrayList<PosSubSet<Vertex<V>>> bmat = new ArrayList<>();
        PosSet<Vertex<V>> groundSet = new PosSet<>(bigraph.vertices()); // TODO: highly inefficient, but must be so to deal with cumbersome bigraph ids
        for (Vertex<V> node : bigraph.rightVertices()) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(groundSet, bigraph.incidentVertices(node));
            bmat.add(neighbors);
        }

        // OpenCL stuff
        int rowCount = bigraph.numRightVertices();
        int colCount = bigraph.numVertices(); // TODO: highly inefficient, but must be so to deal with cumbersome bigraph ids
        if (rowCount == 0 || colCount == 0) return 1;
        //int colWordCount = (colCount / Long.SIZE) + 1;
        int colWordCount = bmat.get(0).getWords().length;
        Pointer<Long> mat = allocateLongs(rowCount * colWordCount).order(byteOrder);
        Pointer<Long> randoms = allocateLongs(sampleCount * colWordCount).order(byteOrder);
        Pointer<Integer> randomShuffles = allocateInts(sampleCount * colCount).order(byteOrder);
        Pointer<Long> outPtrPre = allocateLongs(sampleCount).order(byteOrder);
        Pointer<Long> resultsPtrPre = allocateLongs(2).order(byteOrder);
        int i = 0;
        for (Vertex<V> node : bigraph.rightVertices()) {
            long[] words = bmat.get(i).getWords();
            //System.out.printf("words length: %d\n", words.length);
            int j = 0;
            for (long word : words) {
                mat.set(i * colWordCount + j,word);
                j += 1;
            }
            i += 1;
        }
        Random rnd = new java.util.Random();
        for (i = 0; i < sampleCount; i++) {
            for (int j = 0; j < colWordCount; j++) {
                long val = rnd.nextLong(); // TODO: take care of signed issues?
                //System.out.printf("%d\n", i * colWordCount + j);
                randoms.set(i * colWordCount + j, val);
            }
        }

        ArrayList<Integer> base = new ArrayList<>();
        for (int j = 0; j < colCount; j++) {
            base.add(j);
        }
        for (i = 0; i < sampleCount; i++) {
            Collections.shuffle(base);
            for (int j = 0; j < colCount; j++) {
                randomShuffles.set(i * colCount + j, base.get(j));
            }
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Long> bufmat = context.createBuffer(CLMem.Usage.Input, mat);
        CLBuffer<Long> bufrandoms = context.createBuffer(CLMem.Usage.Input, randoms);
        CLBuffer<Integer> bufrandomShuffles = context.createBuffer(CLMem.Usage.Input, randomShuffles);

        // Create an OpenCL output buffer :
        CLBuffer<Long> out = context.createBuffer(CLMem.Usage.Output, outPtrPre);
        CLBuffer<Long> resultsBuffer = context.createBuffer(CLMem.Usage.Output, resultsPtrPre);

        long start = System.currentTimeMillis();
        addFloatsKernel.setArgs(
                bufrandoms, bufrandomShuffles, bufmat, colCount, rowCount, colWordCount, out, resultsBuffer,
                sampleCount,
                LocalSize.ofLongArray(colWordCount), LocalSize.ofLongArray(colWordCount), LocalSize.ofLongArray(colWordCount));

        CLQueue queue = context.createDefaultQueue();
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[]{sampleCount}, new int[]{1});

        ReductionUtils.Reductor<Long> reductor = ReductionUtils.createReductor(context, ReductionUtils.Operation.Add, OpenCLType.Long, 1);
        long estimate = reductor.reduce(queue, out).get();

        queue.finish();
        queue.release();
        bufmat.release();
        bufrandoms.release();
        bufrandomShuffles.release();
        out.release();
        resultsBuffer.release();
        addEvt.release();

        /*addFloatsKernel.release();
        program.release();
        context.release();*/

        //Pointer<Long> outPtr = out.read(queue, addEvt); // blocks until add_floats finished
        //Pointer<Long> resultsPtr = resultsBuffer.read(queue, addEvt); // blocks until add_floats finished
        long end = System.currentTimeMillis();

        // Print the first 10 output values :
        /*long avg = 0;
        for (i = 0; i < sampleCount; i += 1) {
            avg += outPtr.get(i);
            //System.out.println("out[" + i + "] = " + outPtr.get(i));
        }
        double d = (double) resultsPtr.get(0) / resultsPtr.get(1);
        System.out.printf("results: %d %d/%d/%.2f\n", value, resultsPtr.get(0), resultsPtr.get(1), d);
        System.out.printf("time: %d, num: %d, sum: %d, average: %.2f\n", end - start, sampleCount, avg, (double) avg / sampleCount);
        */
        return estimate / sampleCount;
    }
}