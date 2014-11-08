package boolwidth.opencl;

import boolwidth.CutBool;
import graph.BiGraph;
import graph.Vertex;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;

import static org.jocl.CL.*;

import org.apache.commons.io.FileUtils;
import org.jocl.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.bridj.Pointer.allocateLongs;

public class JOCLOpenCLCutBoolComputer {

    static JOCLOpenCLCutBoolComputer cache = null;
    String src = null;

    cl_context context;
    cl_device_id device;
    cl_program program;
    cl_kernel kernel;
    cl_command_queue commandQueue;

    public JOCLOpenCLCutBoolComputer() {
        // Read the program sources and compile them :

        File file = new File(JOCLOpenCLCutBoolComputer.class.getResource("TutorialKernels.cl").getFile());
        try {
            src = FileUtils.readFileToString(file);
        } catch (IOException e) {
            System.out.println("failed to locate OpenCL kernel TutorialKernels.cl");
            System.exit(-1);
        }

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_GPU; // CL_DEVICE_TYPE_ALL
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        device = devices[deviceIndex];

        //String deviceName = getString(device, CL_DEVICE_NAME);
        //System.out.printf("CL_DEVICE_NAME: %s\n", deviceName);

        // Create a context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create the program from the source code
        program = clCreateProgramWithSource(context,
                1, new String[]{ src }, null, null);

        // Build the program
        long start = System.currentTimeMillis();
        clBuildProgram(program, 0, null, null, null, null);
        long end = System.currentTimeMillis();
        //System.out.printf("compilation time: %d\n", end - start);

        // Create the kernel
        kernel = clCreateKernel(program, "count_hoods", null);

        // Create a command-queue for the selected device
        commandQueue =
                clCreateCommandQueue(context, device, 0, null);

        /*long size[] = new long[1];
        Pointer sizePtr = Pointer.to(size);
        clGetDeviceInfo(device, CL_DEVICE_LOCAL_MEM_SIZE, Sizeof.cl_ulong, sizePtr, null);
        System.out.printf("local mem size: %d\n", size[0]);*/
    }

    public static void initialize() {
        if (cache == null) {
            cache = new JOCLOpenCLCutBoolComputer();
        }
    }

    public static <V, E> long estimateNeighbourHoods(BiGraph<V, E> bigraph, int sampleCount) {
        initialize();
        long before = System.currentTimeMillis();
        long bw = cache.estimateNeighbourHoods2(bigraph, sampleCount);
        long after = System.currentTimeMillis();
        /*System.out.printf("computing cutbool %d/%d: %d: %.2f, time: %d\n",
                bigraph.numLeftVertices(), bigraph.numVertices(),
                bw, CutBool.getLogBW(bw), after - before);*/
        return bw;
    }

    public <V, E> long estimateNeighbourHoods2(BiGraph<V, E> bigraph, int sampleCount) {

        long beforeData = System.currentTimeMillis();

        // prepare data
        ArrayList<PosSubSet<Vertex<V>>> bmat = new ArrayList<>();
        PosSet<Vertex<V>> groundSet = new PosSet<>(bigraph.vertices()); // TODO: highly inefficient, but must be so to deal with cumbersome bigraph ids
        Collection<Vertex<V>> vertices = new ArrayList<Vertex<V>>();

        // choose smallest side for iteration
        if (bigraph.numRightVertices() < bigraph.numLeftVertices()) {
            bigraph.rightVertices().forEach((v) -> vertices.add(v));
        } else {
            bigraph.leftVertices().forEach((v) -> vertices.add(v));
        }

        for (Vertex<V> node : vertices) {
            PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(groundSet, bigraph.incidentVertices(node));
            bmat.add(neighbors);
        }

        // OpenCL stuff
        int rowCount = vertices.size();
        int colCount = bigraph.numVertices(); // TODO: highly inefficient, but must be so to deal with cumbersome bigraph ids
        if (rowCount == 0 || colCount == 0) return 1;
        //int colWordCount = (colCount / Long.SIZE) + 1;
        int colWordCount = bmat.get(0).getWords().length;

        long[] mat = new long[rowCount * colWordCount];
        long[] randoms = new long[sampleCount * colWordCount];
        long[] outPtrPre = new long[sampleCount];
        long[] resultsPtrPre = new long[2];
        //Pointer<Integer> randomShuffles = allocateInts(sampleCount * colCount).order(byteOrder);

        int i = 0;
        for (Vertex<V> node : vertices) {
            long[] words = bmat.get(i).getWords();
            //System.out.printf("words length: %d\n", words.length);
            int j = 0;
            for (long word : words) {
                mat[i * colWordCount + j] = word;
                j += 1;
            }
            i += 1;
        }
        Random rnd = new Random();
        for (i = 0; i < sampleCount; i++) {
            for (int j = 0; j < colWordCount; j++) {
                long val = rnd.nextLong(); // TODO: take care of signed issues?
                //System.out.printf("%d\n", i * colWordCount + j);
                randoms[i * colWordCount + j] = val;
            }
        }

        /*ArrayList<Integer> base = new ArrayList<>();
        for (int j = 0; j < colCount; j++) {
            base.add(j);
        }
        for (i = 0; i < sampleCount; i++) {
            Collections.shuffle(base);
            for (int j = 0; j < colCount; j++) {
                randomShuffles.set(i * colCount + j, base.get(j));
            }
        }*/

        Pointer ptrMat = Pointer.to(mat);
        Pointer ptrRandoms = Pointer.to(randoms);
        Pointer ptrOut = Pointer.to(outPtrPre);
        Pointer ptrResults = Pointer.to(resultsPtrPre);

        cl_mem memObjects[] = new cl_mem[4];
        memObjects[0] = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_long * randoms.length, ptrRandoms, null);
        memObjects[1] = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_long * mat.length, ptrMat, null);
        memObjects[2] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_long * outPtrPre.length, null, null);
        memObjects[3] = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_long * resultsPtrPre.length, null, null);

        long start = System.currentTimeMillis();

        // Set the arguments for the kernel
        int a = 0;
        clSetKernelArg(kernel, a++,
                Sizeof.cl_mem, Pointer.to(memObjects[0]));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_mem, Pointer.to(memObjects[1]));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_int, Pointer.to(new int[] { colCount }));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_int, Pointer.to(new int[] { rowCount }));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_int, Pointer.to(new int[] { colWordCount }));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_mem, Pointer.to(memObjects[2]));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_mem, Pointer.to(memObjects[3]));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_int, Pointer.to(new int[] { sampleCount }));
        clSetKernelArg(kernel, a++,
                Sizeof.cl_long * rowCount * colWordCount, null);
        clSetKernelArg(kernel, a++,
                Sizeof.cl_long * rowCount * colWordCount, null);
        clSetKernelArg(kernel, a++,
                Sizeof.cl_long * colWordCount, null);
        clSetKernelArg(kernel, a++,
                Sizeof.cl_long * colWordCount, null);
        clSetKernelArg(kernel, a++,
                Sizeof.cl_long * colWordCount, null);

        // Set the work-item dimensions
        long global_work_size[] = new long[]{sampleCount};
        long local_work_size[] = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size, 0, null, null);

        // Read the output data
        clEnqueueReadBuffer(commandQueue, memObjects[2], CL_TRUE, 0,
                sampleCount * Sizeof.cl_long, ptrOut, 0, null, null);
        /*clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
                2 * Sizeof.cl_long, ptrResults, 0, null, null);*/

        // Print the first 10 output values :
        long estimate = 0;
        for (i = 0; i < sampleCount; i += 1) {
            estimate += outPtrPre[i];
            //System.out.println("out[" + i + "] = " + outPtrPre[i]);
        }

        // Release kernel, program, and memory objects
        clReleaseMemObject(memObjects[0]);
        clReleaseMemObject(memObjects[1]);
        clReleaseMemObject(memObjects[2]);
        clReleaseMemObject(memObjects[3]);

        //clReleaseKernel(kernel);
        //clReleaseProgram(program);
        //clReleaseCommandQueue(commandQueue);
        //clReleaseContext(context);

        long end = System.currentTimeMillis();

        //System.out.printf("OpenCL kernel time: %d, including data init time: %d\n", end - start, end - beforeData);
        /*
        double d = (double) resultsPtr.get(0) / resultsPtr.get(1);
        System.out.printf("results: %d %d/%d/%.2f\n", value, resultsPtr.get(0), resultsPtr.get(1), d);
        System.out.printf("time: %d, num: %d, sum: %d, average: %.2f\n", end - start, sampleCount, avg, (double) avg / sampleCount);
        */
        return estimate / sampleCount;
    }

    private static String getString(cl_device_id device, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        clGetDeviceInfo(device, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }

    private void shutdown()
    {
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }

}