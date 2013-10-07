package io;

import exceptions.FatalHandler;
import exceptions.InvalidGraphFileFormatException;
import graph.Vertex;
import interfaces.IGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import util.Generator;

/** Get graphs from disk **/
public class DiskGraph {

	private static final List<String> GRAPH_EXT = Arrays
	.asList("dgf", "dimacs");

	private final static String SOURCE_FILENAME_FIELD = "sourceFileName";

	public static class FileIterable implements Iterable<File> {

		protected final String path;
		//protected IGraph<TVertex, V, E> graphPrototype;

		public FileIterable(String path) throws FileNotFoundException {
			if (!new File(path).isDirectory()) {
				throw new FileNotFoundException(String.format(
						"in cwd \"%s\", not a directory: \"%s\"", new File(".")
						.getAbsolutePath(), path));
			}
			this.path = path;
		}

		@Override
		public Iterator<File> iterator() {
			return new FileIterator(this);
		}

	}

	public static class FileIterator extends Generator<File> {

		FileIterable fi;

		public FileIterator(FileIterable fi) {
			this.fi = fi;
		}

		@Override
		public void apply() {
			Stack<File> dirs = new Stack<File>();
			File root = new File(this.fi.path);
			dirs.push(root);

			while (!dirs.empty()) {
				File curdir = dirs.pop();
				if (curdir == null) {
					System.out.println("wtf");
				}
				for (File f : curdir.listFiles()) {
					if (f.isDirectory()) {
						dirs.push(f);
					} else {
						String ext = f.toString().replaceFirst("^.*\\.", "");
						if (GRAPH_EXT.contains(ext)) {
							// TODO: filter as parameter
							if (f.length() > 1024 * 1024) {
								System.out.printf(
										"warning: skipping big graph: \"%s\"\n", f
										.toString());
							} else {
								yield(f);
							}
						}
					}
				}
			}
		}
	}

	public static String getFileName(IGraph<?, ?, ?> graph) {
		return graph.getAttr(SOURCE_FILENAME_FIELD);
	}

	@SuppressWarnings("unchecked")
	public static
	<TVertex extends Vertex<V>, V, E, TGraph extends IGraph<TVertex, V, E>>
	Collection<TGraph> getGraphs(
			String path, TGraph graphPrototype) throws FileNotFoundException {
		ArrayList<TGraph> diskGraphs = new ArrayList<TGraph>();
		Stack<File> dirs = new Stack<File>();
		File root = new File(path);
		dirs.push(root);

		if (!root.isDirectory()) {
			throw new FileNotFoundException(String.format(
					"in cwd \"%s\", not a directory: \"%s\"", new File(".")
					.getAbsolutePath(), path));
		}

		while (!dirs.empty()) {
			File curdir = dirs.pop();
			if (curdir == null) {
				System.out.println("wtf");
			}
			for (File f : curdir.listFiles()) {

				if (f.isDirectory()) {
					dirs.push(f);
				} else {
					String ext = f.toString().replaceFirst("^.*\\.", "");
					if (GRAPH_EXT.contains(ext)) {
						// TODO: filter as parameter
						if (f.length() > 1024 * 1024) {
							System.out.printf(
									"warning: skipping big graph: \"%s\"\n", f
									.toString());
						} else {
							TGraph dg = (TGraph) graphPrototype.copy();

							try {
								readGraph(f.toString(), dg);
							} catch (InvalidGraphFileFormatException e) {
								System.out
								.printf(
										"warning: invalid graph file format: \"%s\": \"%s\"\n",
										f.toString(),
										e.toString()
								);
								continue;
							}
							diskGraphs.add(dg);
						}
					}
				}
			}
		}

		return diskGraphs;
	}

	public static FileIterable iterateOver(String path) throws FileNotFoundException {
		return new FileIterable(path);
	}

	public static FileIterable iterateOver(String path, boolean handleException) {
		try {
			return new FileIterable(path);
		} catch (FileNotFoundException e) {
			FatalHandler.handle(
					String.format("no such dir: %s", path), e);
		}
		assert false;
		return null;
	}

	public static <TVertex extends Vertex<V>, V, E> IGraph<TVertex, V, E> readGraph(
			String fileName, IGraph<TVertex, V, E> graph) {
		//System.out.printf("reading: %s\n", fileName);
		try {
			GraphFileReader r = new GraphFileReader(fileName);
			GraphBuilder<TVertex, V, E> gb = new GraphBuilder<TVertex, V, E>(r);
			gb.buildNullGraph(graph);
			setFileName(graph, fileName);
			return graph;
		} catch (FileNotFoundException e) {
			FatalHandler.handle(e);
		}
		return null;
	}

	public static void setFileName(IGraph<?, ?, ?> graph, String filename) {
		graph.setAttr(SOURCE_FILENAME_FIELD, filename);
	}

}
