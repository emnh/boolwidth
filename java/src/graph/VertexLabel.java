package graph;

import interfaces.IAttributeStorage;

public class VertexLabel {

	public final static String LABEL_FIELD = "label";
	public final static String ORDER_FIELD = "order";

	public static String getLabel(IAttributeStorage vertex) {
		return vertex.getAttr(LABEL_FIELD);
	}

	public static void setLabel(IAttributeStorage vertex, String label) {
		vertex.setAttr(LABEL_FIELD, label);
	}

	public static String getOrder(IAttributeStorage vertex) {
		return vertex.getAttr(ORDER_FIELD);
	}

	public static void setOrder(IAttributeStorage vertex, String label) {
		vertex.setAttr(ORDER_FIELD, label);
	}
}
