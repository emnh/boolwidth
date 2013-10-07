package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Used to create .lock-files. Need it for networked file system that don't
 * support OS locks. TODO: Use OS locking in addition.
 * 
 * @author emh
 * 
 */
public class FileLock {

	protected final File file;
	protected OnExitRelease onexit = new OnExitRelease();

	class OnExitRelease extends Thread {

		@Override
		public void run() {
			release(true);
		}
	}

	protected static synchronized File getLockFile(File file) {
		return new File(file.toString() + ".lock");
	}

	protected static synchronized boolean isLocked(File file) {
		return FileLock.getLockFile(file).exists();
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		FileLock lock = FileLock.tryLockFile("test.txt");
		try {
			if (lock != null) {
				System.out.println("got lock!");
				Thread.sleep(5000);
			} else {
				System.out.println("is locked!");
			}
		} finally {
			if (lock != null) {
				lock.release();
			}
		}
	}

	public synchronized static FileLock tryLockFile(String fileName)
	throws FileNotFoundException {
		File file_ = new File(fileName);
		if (isLocked(file_)) {
			return null;
		} else {
			return new FileLock(file_);
		}
	}

	protected FileLock(File file) throws FileNotFoundException {
		this.file = file;
		createLockFile();
	}

	protected void createLockFile() throws FileNotFoundException {
		new FileOutputStream(getLockFile());
		Runtime.getRuntime().addShutdownHook(this.onexit);
	}

	protected synchronized File getLockFile() {
		return getLockFile(this.file);
	}

	public synchronized void release() {
		release(false);
	}

	public synchronized void release(boolean fromShutdown) {
		//System.out.println("releasing lock");
		getLockFile().delete();
		if (!fromShutdown) {
			Runtime.getRuntime().removeShutdownHook(this.onexit);
		}
	}
}
