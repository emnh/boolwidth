package boolwidth.greedysearch;

import com.github.krukow.clj_lang.PersistentVector;
import com.github.krukow.clj_lang.PersistentHashSet;

import java.util.Collection;
import java.util.List;

/**
 * Created by emh on 11/2/2014.
 */
public class Util {

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
