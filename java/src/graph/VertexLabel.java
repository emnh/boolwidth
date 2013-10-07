package graph;

import interfaces.IAttributeStorage;

public class VertexLabel {

	public final static String LABEL_FIELD = "label";

	public static String getLabel(IAttributeStorage vertex) {
		return vertex.getAttr(LABEL_FIELD);
	}

	public static void setLabel(IAttributeStorage vertex, String label) {
		vertex.setAttr(LABEL_FIELD, label);
	}
}
