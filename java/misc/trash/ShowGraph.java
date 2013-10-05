/**
 * Copyright (c) 2004-2006 Regents of the University of California.
 * See "license-prefuse.txt" for licensing terms.
 */
package visualization;

import graph.Edge;
import graph.Vertex;
import interfaces.IGraph;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.GridLayout;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.tuple.TupleSet;
import prefuse.render.*;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualItem;


class GraphGridLayout extends GridLayout {

	private Rectangle2D b;
	private boolean hasrun = false;

	public GraphGridLayout(String group, int nrows, int ncols) {
		super(group, nrows, ncols);
	}

    // TODO: don't need all this. can call setBoundary and super
	/**
	 * copied from GridLayout, changed boundary
	 */
	@Override
	public void run(double frac) {

		if (this.b == null) {
			this.b = getLayoutBounds();
		}

		if (!this.hasrun) {
			this.hasrun = true;
		} else {
			return;
		}

		double bx = this.b.getMinX(), by = this.b.getMinY();
		double w = this.b.getWidth(), h = this.b.getHeight();

		// so that peripheral nodes are not half hidden
		final int SHRINKFACTOR = 10;
		bx += (w / SHRINKFACTOR);
		by += (h / SHRINKFACTOR);
		w -= (w / SHRINKFACTOR * 2);
		h -= (h / SHRINKFACTOR * 2);

		TupleSet ts = this.m_vis.getGroup(this.m_group);
		int m = this.rows, n = this.cols;
		if (this.analyze) {
			int[] d = analyzeGraphGrid(ts);
			m = d[0];
			n = d[1];
		}

		Iterator<?> iter = ts.tuples();
		// layout grid contents
		for (int i = 0; iter.hasNext() && i < m * n; ++i) {
			VisualItem item = (VisualItem) iter.next();
			item.setVisible(true);
			double x = bx + w * ((i % n) / (double) (n - 1));
			double y = by + h * ((i / n) / (double) (m - 1));
			System.out.printf("%.2f, %.2f\n", x, y);
			setX(item, null, x);
			setY(item, null, y);
		}
		// set left-overs invisible
		while (iter.hasNext()) {
			VisualItem item = (VisualItem) iter.next();
			item.setVisible(false);
		}
	}

}

@SuppressWarnings("serial")
public class ShowGraph extends Display {

	private static final String GRAPH = "graph";
	private static final String NODES = "graph.nodes";
	private static final String EDGES = "graph.edges";

    private static final String LABEL = "label";

	private final ActionList colors;
	private final ActionList layout;
	private final ActionList spacelayout;
	private boolean started = false;
	private final Graph pfs_graph;

	public ShowGraph() {
		// initialize display and data
		super(new Visualization());

		// Rectangle r = getBounds();
		// TODO: default node width
		// r.x += 10;
		// setBounds(r);

		// set up the renderers
		// draw the nodes as basic shapes
		//Renderer nodeR = new ShapeRenderer(20);
        LabelRenderer nodeR = new LabelRenderer(LABEL);
        nodeR.setRoundedCorner(10, 10);

		// draw aggregates as polygons with curved edges
		Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
		((PolygonRenderer) polyR).setCurveSlack(0.15f);

		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(nodeR);
		drf.add("ingroup('aggregates')", polyR);
		this.m_vis.setRendererFactory(drf);

		// set up the visual operators
		// first set up all the color actions
		ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
		nStroke.setDefaultColor(ColorLib.gray(100));
		nStroke.add("_hover", ColorLib.gray(50));

		ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
		nFill.setDefaultColor(ColorLib.gray(255));
		nFill.add("_hover", ColorLib.gray(200));

        ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
		nFill.setDefaultColor(ColorLib.gray(255));

		ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
		nEdges.setDefaultColor(ColorLib.gray(100));

		// bundle the color actions
		this.colors = new ActionList();
		this.colors.add(nStroke);
		this.colors.add(nFill);
        this.colors.add(nText);
		this.colors.add(nEdges);

		// now create the main layout routine
		this.layout = new ActionList(Activity.INFINITY);
		// ActionList layout = new ActionList();
		this.layout.add(this.colors);

		this.spacelayout = new ActionList();
		setDefaultLayout();
		this.layout.add(this.spacelayout);

		this.layout.add(new RepaintAction());

		this.m_vis.putAction("layout", this.layout);

		// set up the display
		setSize(500, 500);
		pan(250, 250);
		setHighQuality(true);
        addControlListener(new DragControl());
		addControlListener(new ZoomControl());
		addControlListener(new PanControl());

        Table nodetable = new Table();
        nodetable.addColumn(LABEL, String.class);
		this.pfs_graph = new Graph(nodetable, false);
		this.m_vis.addGraph(GRAPH, this.pfs_graph);

		setGraphProperties();

		start();
	}

	public void setDefaultLayout() {
		setLayout(new ForceDirectedLayout(GRAPH, true));
	}

	public void setGraph(IGraph<?, ?, ?> graph) {
		this.layout.setEnabled(false);
		// this.layout.cancel();
		this.pfs_graph.clear();
        HashMap<Vertex<?>, Node> nodemap = new HashMap<Vertex<?>, Node>();
		for (Vertex<?> v : graph.vertices()) {
			Node n = this.pfs_graph.addNode();
            nodemap.put(v, n);
            // TODO: take attribute mapper as argument
            n.setString(LABEL, v.toString());
		}
		for (Edge<?, ?, ?> e : graph.edges()) {
			Node n1 = nodemap.get(e.left());
			Node n2 = nodemap.get(e.right());
			this.pfs_graph.addEdge(n1, n2);
		}
		setGraphProperties();
		// setDefaultLayout();
		this.layout.setEnabled(true);
	}

	private void setGraphProperties() {

		this.m_vis.setInteractive(EDGES, null, false);
		this.m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
	}

	public void setGridLayout(int n, int m) {
		setLayout(new GraphGridLayout(NODES, n, m));
	}

	public void setLayout(Layout newspacelayout) {
		try {
			this.spacelayout.remove(0);
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		this.spacelayout.add(newspacelayout);
	}

	public void start() {
		if (!this.started) {
			this.m_vis.run("layout");
			this.started = true;
		}
	}

	public static <TVertex extends Vertex<V>, V, E> JFrame demo(
			IGraph<TVertex, V, E> graph) {
		ShowGraph ad = new ShowGraph();
		ad.setGraph(graph);
		ad.start();
		JFrame frame = new JFrame("p r e f u s e  |  a g g r e g a t e d");
		frame.getContentPane().add(ad);
		frame.pack();
		return frame;
	}
}