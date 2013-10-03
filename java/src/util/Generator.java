package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

public abstract class Generator<ReturnType> extends Thread implements Iterator<ReturnType> {
	private ReturnType next;

	Semaphore callerLock = new Semaphore(0);
	Semaphore generatorLock = new Semaphore(0);

	public Generator() {
		start();
	}

	/**
	 * Override this and call yield(return) for each value.
	 */
	protected abstract void apply();

	@Override
	public boolean hasNext() {
		if (this.next == null) {
			try {
				this.next = next();
			} catch (NoSuchElementException e) {
				return false;
			}
		}
		return this.next != null;
	}

	@Override
	public ReturnType next() {
		// return cached
		if (this.next != null) {
			ReturnType ret_ = this.next;
			this.next = null;
			return ret_;
		}

		if (!this.isAlive()) {
			throw new NoSuchElementException();
		}
		// wake up generator
		this.generatorLock.release();
		// wait for generator
		this.callerLock.acquireUninterruptibly();

		ReturnType ret_ = this.next;
		this.next = null;
		return ret_;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void run() {
		// wait for caller
		this.generatorLock.acquireUninterruptibly();
		apply();
		// return to caller
		this.callerLock.release();
	}

	final protected void yield(ReturnType ret) {
		//System.out.printf("yield: %s\n", ret);
		this.next = ret;
		// wake up caller
		this.callerLock.release();
		// wait for caller
		this.generatorLock.acquireUninterruptibly();
	}
}