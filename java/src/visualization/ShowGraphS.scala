package visualization

import java.util.HashMap
import javax.swing.JFrame
import interfaces.IGraph
import prefuse.action.{RepaintAction, ActionList}
import prefuse.action.layout.graph.ForceDirectedLayout
import prefuse.data.{Graph, Table, Node}
import prefuse.action.layout.{Layout, GridLayout}
import prefuse.activity.Activity
import prefuse.util.ColorLib
import prefuse.action.assignment.ColorAction
import prefuse.render.{DefaultRendererFactory, PolygonRenderer, Renderer, LabelRenderer}
import prefuse.{Visualization, Constants, Display}
import prefuse.controls.{FocusControl, PanControl, ZoomControl, DragControl}
import prefuse.visual.{NodeItem, VisualItem}
import graph.{BiGraph, Edge, Vertex}
//import util.TypeDefs.{TGraph, TVertex}
import scala.collection.JavaConversions._
/**
 * Created by IntelliJ IDEA.
 * User: emh
 * Date: Mar 28, 2010
 * Time: 5:30:38 PM
 * To change this template use File | Settings | File Templates.
 */


object ShowGraphS {
    private val GRAPH = "graph"
    private val EDGES = "graph.edges"
    private val NODES = "graph.nodes"
    private val LABEL = "label"
    // node from boolwidthlib
    val ORIGINAL = "originalnode"
    val FILLCOLOR = "fcolor"

    def demo[TVertex <: Vertex[V], V, E](graph: IGraph[TVertex, V, E]): JFrame = {
        var ad: ShowGraphS = new ShowGraphS
        ad.setGraph(graph, (v : TVertex) => v.toString())
        ad.start
        var frame: JFrame = new JFrame("p r e f u s e  |  a g g r e g a t e d")
        frame.getContentPane.add(ad)
        frame.pack
        return frame
    }
}

class ShowGraphS extends Display(new Visualization()) {

    private var started = false

    // Rectangle r = getBounds();
    // TODO: default node width
    // r.x += 10;
    // setBounds(r);

    // set up the renderers
    // draw the nodes as basic shapes
    //Renderer nodeR = new ShapeRenderer(20);

    val nodeR = new LabelRenderer(ShowGraphS.LABEL)
    nodeR.setRoundedCorner(10, 10)

    // draw aggregates as polygons with curved edges
    //var polyR: Renderer = new PolygonRenderer(Constants.POLY_TYPE_CURVE)
    //(polyR.asInstanceOf[PolygonRenderer]).setCurveSlack(0.15f)

    var drf: DefaultRendererFactory = new DefaultRendererFactory
    drf.setDefaultRenderer(nodeR)
    //drf.add("ingroup('aggregates')", polyR)
    this.m_vis.setRendererFactory(drf)

    // set up the visual operators
    // first set up all the color actions
    var nStroke: ColorAction = new ColorAction(ShowGraphS.NODES, VisualItem.STROKECOLOR)
    nStroke.setDefaultColor(ColorLib.gray(100))
    nStroke.add(VisualItem.HOVER, ColorLib.gray(50))

    val nFill = new ColorAction(ShowGraphS.NODES, VisualItem.FILLCOLOR) {
        override def getColor(item: VisualItem) : Int = {
            if (item.isInstanceOf[NodeItem]) {
                val ni = item.asInstanceOf[NodeItem]
                val color = ni.get(ShowGraphS.FILLCOLOR)
                if (color.isInstanceOf[Int]) {
                    //printf("color %s: %d\n", ni, color.asInstanceOf[Int])
                    return color.asInstanceOf[Int]
                } else {
                    return super.getColor(item)
                }
            } else {
                return super.getColor(item)
            }
        }
    }
    nFill.setDefaultColor(ColorLib.gray(255))
    nFill.add(VisualItem.HOVER, ColorLib.gray(200))

    val nText = new ColorAction(ShowGraphS.NODES, VisualItem.TEXTCOLOR)
    nText.setDefaultColor(ColorLib.gray(0))

    val nEdges = new ColorAction(ShowGraphS.EDGES, VisualItem.STROKECOLOR)
    nEdges.setDefaultColor(ColorLib.gray(100))

    // bundle the color actions
    val colors = new ActionList
    colors.add(nStroke)
    colors.add(nFill)
    colors.add(nText)
    colors.add(nEdges)

    // now create the main layout routine
    val layoutlist = new ActionList(Activity.INFINITY)
    this.layoutlist.add(this.colors)

    val spacelayout = new ActionList
    setDefaultLayout
    this.layoutlist.add(this.spacelayout)

    this.layoutlist.add(new RepaintAction)

    this.m_vis.putAction("layout", this.layoutlist)

    setSize(500, 500)
    pan(250, 250)
    setHighQuality(true)

    addControlListener(new DragControl)
    addControlListener(new ZoomControl)
    addControlListener(new PanControl)

    var nodetable: Table = new Table
    nodetable.addColumn(ShowGraphS.LABEL, classOf[String])
    nodetable.addColumn(ShowGraphS.ORIGINAL, classOf[Vertex[_]])
    nodetable.addColumn(ShowGraphS.FILLCOLOR, classOf[java.lang.Integer])
    val pfs_graph = new Graph(nodetable, false)
    this.m_vis.addGraph(ShowGraphS.GRAPH, this.pfs_graph)

    setGraphProperties
    start

    private def setGraphProperties: Unit = {
        this.m_vis.setInteractive(ShowGraphS.EDGES, null, false)
        this.m_vis.setValue(ShowGraphS.NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE)
    }

    // TODO: added this since default param not working. should work in 2.8 final
    def setGraph[TVertex <: Vertex[V], V, E](
            graph: IGraph[TVertex, V, E]
            ): Unit = {
            setGraph(graph, (v : TVertex) => v.toString())
    }

    def setGraph[TVertex <: Vertex[V], V, E](
            graph: IGraph[TVertex, V, E],
            nodeprinter : TVertex => String = ((v : TVertex) => v.toString())
            ): Unit = {
        this.layoutlist.setEnabled(false)
        this.pfs_graph.clear
        val nodemap = new HashMap[TVertex, Node]
        for (v <- graph.vertices) {
            //println(v.id())
            var n: Node = this.pfs_graph.addNode
            nodemap.put(v, n)
            n.setString(ShowGraphS.LABEL, nodeprinter(v))
            n.set(ShowGraphS.ORIGINAL, v)
        }
        for (e : Edge[TVertex, V, E] <- graph.edges()) {
            var n1: Node = nodemap.get(e.left)
            var n2: Node = nodemap.get(e.right)
            this.pfs_graph.addEdge(n1, n2)
        }
        setGraphProperties
        this.layoutlist.setEnabled(true)
    }

    def getVisualItemForNode(o : AnyRef) : NodeItem = {
        for (v <- m_vis.items(ShowGraphS.NODES)) {
            if (v.isInstanceOf[NodeItem]) {
                val vi = v.asInstanceOf[NodeItem]
                if (vi.get(ShowGraphS.ORIGINAL) == o) {
                    return vi
                }
            }
        }
        return null
    }

    def setNodeColors(colorMapper: NodeItem => Int) {
        for (v <- m_vis.items(ShowGraphS.NODES)) {
            if (v.isInstanceOf[NodeItem]) {
                val vi = v.asInstanceOf[NodeItem]
                vi.set(ShowGraphS.FILLCOLOR, colorMapper(vi))
            }
        }
    }

    def resetNodeColors() {
        for (v <- m_vis.items(ShowGraphS.NODES)) {
            if (v.isInstanceOf[NodeItem]) {
                val vi = v.asInstanceOf[NodeItem]
                vi.set(ShowGraphS.FILLCOLOR, null)
            }
        }
    }

    def start: Unit = {
        if (!this.started) {
            this.m_vis.run("layout")
            this.started = true
        }
    }


    def setLayout(newspacelayout: Layout): Unit = {
        try {
            this.spacelayout.remove(0)
        }
        catch {
            case e: ArrayIndexOutOfBoundsException => {
            }
        }
        this.spacelayout.add(newspacelayout)
    }

    def setDefaultLayout: Unit = {
        setLayout(new ForceDirectedLayout(ShowGraphS.GRAPH, true))
    }

    //        def setGridLayout(n: Int, m: Int): Unit = {
    //            setLayout(new GraphGridLayoutS(NODES, n, m))
    //        }


}
