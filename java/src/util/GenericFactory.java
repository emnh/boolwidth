package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import exceptions.FatalHandler;

/**
 * Lookup constructor for a class and use createNew to call it.
 * 
 * Example: Class<T> CarPool() {
 * 
 * GenericFactory<T> f; public CarPool(Class<T> cls) { f =
 * GenericFactory<T>(cls, int.class); }
 * 
 * public T createCar(Class<T> cls, int i) { return f.createNew(i); } }
 * 
 * @author emh
 * @param <T>
 */

public class GenericFactory<T> {
	Constructor<T> c;

	/**
	 * @param productcls
	 *            class to instantiate
	 * @param parameterTypes
	 *            constructor parameter types
	 */
	public GenericFactory(Class<?> productcls, Class<?>... parameterTypes) {

		try {
			this.c = uncheckedCT(productcls.getConstructor(parameterTypes));
		} catch (NoSuchMethodException e) {
			FatalHandler.handle(e);
		}
	}

	public T createNew(Object... initargs) {
		T vv = null;
		try {
			vv = this.c.newInstance(initargs);
		} catch (InstantiationException e) {
			FatalHandler.handle(e);
		} catch (IllegalAccessException e) {
			FatalHandler.handle(e);
		} catch (InvocationTargetException e) {
			FatalHandler.handle(e);
		}
		return vv;
	}

	@SuppressWarnings("unchecked")
	private Constructor<T> uncheckedCT(Constructor<?> c) {
		return (Constructor<T>) c;
	}
}