package graph;

import interfaces.IGraph;

import java.util.Comparator;

public class NamedGraphComparator implements Comparator<IGraph<?, ?, ?>> {

	public static final String DEFAULT_NAME = "";

	@Override
	public int compare(IGraph<?, ?, ?> o1, IGraph<?, ?, ?> o2) {
		int cmp = new Integer(o1.numVertices()).compareTo(o2.numVertices());
		if (cmp == 0) {
			String name1 = o1.getAttr("name");
			if (name1 == null) {
				name1 = DEFAULT_NAME;
			}
			String name2 = o2.getAttr("name");
			if (name2 == null) {
				name2 = DEFAULT_NAME;
			}
			cmp = name1.compareTo(name2);
		}
		return cmp;
	}
}
