package io;

import exceptions.FatalHandler;
import graph.AdjacencyListGraph;
import graph.CleanBinaryTree;
import graph.Vertex;
import interfaces.IDecomposition;
import interfaces.IGraph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import util.Util;
import boolwidth.BooleanDecomposition;

import com.thoughtworks.xstream.XStream;

public class Storage {

	private IGraph<?, ?, ?> graph;

	private IDecomposition<?, ?, ?> bestDecomposition;

	private ArrayList<IDecomposition<?, ?, ?>> decompositions;

	protected transient XStream xstream = new XStream();

	public static Storage fromFile(String inputfile) {
		Storage s = new Storage();
		s.load(inputfile);
		return s;
	}

	private Storage() {
		this.xstream.useAttributeFor(int.class);
		Class<?>[] attributeClasses = {
				AdjacencyListGraph.class,
				Vertex.class,
				CleanBinaryTree.class
		};
		final String ATTRIBUTES_FIELD = "attributes";
		for (Class<?> cls : attributeClasses) {
			this.xstream.registerLocalConverter(cls,
					ATTRIBUTES_FIELD, new AttributesConverter(this.xstream.getMapper()));
		}
		this.xstream.alias("boolwidth.Storage", Storage.class);
	}

	public Storage(IGraph<?, ?, ?> graph,
			IDecomposition<?, ?, ?> bestDecomposition) {
		this();
		this.graph = graph;
		this.bestDecomposition = bestDecomposition;
	}

	public IDecomposition<?, ?, ?> getBestDecomposition() {
		return this.bestDecomposition;
	}

	public ArrayList<IDecomposition<?, ?, ?>> getDecompositions() {
		return this.decompositions;
	}

	public IGraph<?, ?, ?> getGraph() {
		return this.graph;
	}

	public void load(String inputfile) {
		try {
			FileInputStream fs = new FileInputStream(inputfile);
			this.xstream.fromXML(fs, this);
			fs.close();
			// if (o instanceof Storage) {
			// ret = (Storage) o;
			// } else {
			// throw new InputMismatchException(
			// "tried to read XML file not containing Storage instance");
			// }
		} catch (FileNotFoundException e) {
			FatalHandler.handle("file not found", e);
		} catch (IOException e) {
			FatalHandler.handle("io error", e);
		}
	}

	public void save(String outputfile) {
		Util.stringToFile(outputfile, this.xstream.toXML(this));
	}

	public void setBestDecomposition(IDecomposition<?, ?, ?> bestDecomposition) {
		// bestDecomposition.
		this.bestDecomposition = bestDecomposition;
	}

	public void setDecompositions(
			ArrayList<IDecomposition<?, ?, ?>> decompositions) {
		this.decompositions = decompositions;
	}

	public void setGraph(IGraph<?, ?, ?> graph) {
		this.graph = graph;
	}

	/**
	 * Replace bestDecomposition if bw is lower
	 * 
	 * @param bw
	 * @param candidate
	 * @return
	 */
	public boolean updateBestDecomposition(IDecomposition<?, ?, ?> candidate) {
		boolean updated = false;
		long newBw = BooleanDecomposition.getBoolWidth(candidate);
		if (this.bestDecomposition == null ||
				newBw < BooleanDecomposition.getBoolWidth(this.bestDecomposition)) {
			this.setBestDecomposition(candidate);
			updated = true;
		}
		return updated;
	}

}
