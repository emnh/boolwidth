package boolwidth;

import interfaces.IDecomposition;

public class BooleanDecomposition {

	public final static String BOOLWIDTH_FIELD = "boolwidth";


	/**
	 * computation time to find decomposition of this boolWidth
	 * TODO: annotation for generating getter and setter
	 */
	public final static String BOOLWIDTH_TIME_FIELD = "boolwidthTime";

	public static long getBoolWidth(IDecomposition<?, ?, ?> dc) {
		return (Long) dc.getAttr(BOOLWIDTH_FIELD);
	}

	public static int getBoolWidthTime(IDecomposition<?, ?, ?> dc) {
		return (Integer) dc.getAttr(BOOLWIDTH_TIME_FIELD);
	}

	public static void setBoolWidth(IDecomposition<?, ?, ?> dc, long bw) {
		dc.setAttr(BOOLWIDTH_FIELD, bw);
	}

	public static void setBoolWidthTime(IDecomposition<?, ?, ?> dc, long bw) {
		dc.setAttr(BOOLWIDTH_TIME_FIELD, bw);
	}
}
