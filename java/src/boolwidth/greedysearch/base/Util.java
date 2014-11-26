package boolwidth.greedysearch.base;

import com.github.krukow.clj_lang.PersistentVector;
import com.github.krukow.clj_lang.PersistentHashSet;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by emh on 11/2/2014.
 */
public class Util {

    public static <T> T timedExecution(Supplier<T> fun, int timeOut) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Future<T> future = executor.submit(() -> fun.get());

        executor.shutdown();            //        <-- reject all further submissions

        try {
            return future.get(timeOut, TimeUnit.MILLISECONDS);  //     <-- wait 8 seconds to finish
        } catch (InterruptedException e) {    //     <-- possible error cases
            System.out.println("job was interrupted");
        } catch (ExecutionException e) {
            System.out.println("caught exception: " + e.getCause());
        } catch (TimeoutException e) {
            future.cancel(true);              //     <-- interrupt the job
            System.out.println("timeout");
        }

        // wait all unfinished tasks for 1 millisecond
        //if(!executor.awaitTermination(1, TimeUnit.MILLISECONDS)){
            // force them to quit by interrupting
        executor.shutdownNow();
        //}
        return null;
    }

    public static <T> T getSingle(PersistentHashSet<T> set) {
        if (set.size() > 1) {
            throw new IndexOutOfBoundsException("set was not size 1");
        }
        for (T r : set) return r;
        throw new IndexOutOfBoundsException("set was empty");
    }

    public static <T> T getFirst(PersistentHashSet<T> set) {
        for (T r : set) return r;
        throw new IndexOutOfBoundsException("set was empty");
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    // TODO: ask library author krukow to do this instead of working around it
    public static <T> PersistentHashSet<T> createPersistentHashSet(List<T>... elements) {
        return PersistentHashSet.create();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    // TODO: ask library author krukow to do this instead of working around it
    public static <T> PersistentVector<T> createPersistentVector(List<T>... elements) {
        return PersistentVector.create();
    }
}
