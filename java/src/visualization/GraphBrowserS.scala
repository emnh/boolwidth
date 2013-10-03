package visualization

import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.EventList
import javax.swing._
import java.awt.{Component, GridBagConstraints, GridBagLayout}
import java.util.{ArrayList, Collection}

import util.TypeDefs.{TDecomposition}
import interfaces.{IDecomposition, IAttributeStorage, IGraph}
import prefuse.controls.FocusControl
import java.awt.event.MouseEvent
import prefuse.data.Node
import prefuse.visual.{NodeItem, VisualItem}
import prefuse.util.ColorLib
import boolwidth.{CutBool, DNode, Decomposition}
import scala.collection.JavaConversions._
import graph.{BiGraph, Vertex}

class GraphCellRenderer extends DefaultListCellRenderer {
    override def getListCellRendererComponent(
            list: JList, // the list
            value: Object, // value to display
            index: Int, // cell index
            isSelected: Boolean, // is the cell selected
            cellHasFocus: Boolean // does the cell have focus
            ): Component = {
        var name : String = value.asInstanceOf[IAttributeStorage].getAttr("name")
        if (name == null) {
            name = index.toString()
            value.asInstanceOf[IAttributeStorage].setAttr("name", name)
        }
        return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
    }
}

class GraphBrowserS[TVertex <: graph.Vertex[V], V, E]
(graphs: Collection[_ <: IGraph[TVertex, V, E]])
        extends JFrame("Graph browser") {
    type TGraph = IGraph[TVertex, V, E]
    val graphview = new ShowGraphS
    val panel: JPanel = new JPanel
    add(panel)
    val gridbag = new GridBagLayout
    panel.setLayout(gridbag)
    val c = new GridBagConstraints

    val graphEventList = new BasicEventList[TGraph]

    graphEventList.addAll(graphs)

    val ls = new ListSelect[TGraph](
        graphEventList, new SelectAction[TGraph] {
        def select(graph: TGraph): Unit = {
            graphview.setGraph(graph, (v : TVertex) => v.toString())
        }
    })
    ls.graphlist.setCellRenderer(new GraphCellRenderer)
    panel.add(ls)
    panel.add(graphview)
    c.fill = GridBagConstraints.VERTICAL
    gridbag.setConstraints(ls, c)
    c.fill = GridBagConstraints.BOTH
    gridbag.setConstraints(graphview, c)
    panel.setSize(800, 600)
    pack
}

class DCBrowserS[TVertex <: DNode[TVertex, V], V, E]
    (decompositions: Collection[_ <: IDecomposition[TVertex, V, E]])
        extends JFrame("Graph browser") {

    val LEFTCOLOR = ColorLib.rgb(0, 0, 255)
    val RIGHTCOLOR = ColorLib.rgb(255, 0, 0)

    // shows the cut defined by the decomposition node
    def selectDCNode(nodeitem : NodeItem) {
        val vertex = nodeitem.get(ShowGraphS.ORIGINAL).asInstanceOf[TVertex]
        showncut = showndecomposition.getCut(vertex)
        // graphview.setGraph()
        //printf("click: %s\n", vertex)
        //nodeitem.setFillColor(ColorLib.blue(255))

        graphview.setNodeColors((n : NodeItem) => {
            val v = n.get(ShowGraphS.ORIGINAL).asInstanceOf[TGraphVertex]
            val bi_v = showncut.getReverse(v)
            //printf("o: %s, bi: %s\n", v, bi_v)
            assert(bi_v != null)            
            if (showncut.isLeft(bi_v)) LEFTCOLOR else RIGHTCOLOR
        })
        dcview.resetNodeColors
        val SELECTED_COLOR = ColorLib.rgb(0, 0, 255)
        nodeitem.set(ShowGraphS.FILLCOLOR, SELECTED_COLOR)
        //nodeitem.set
    }

    // bind the select DC node function to node click
    class NodeClick() extends FocusControl {
        override def itemClicked(item: VisualItem, e: MouseEvent) {
            if (item.isInstanceOf[NodeItem]) {
                val nodeitem = item.asInstanceOf[NodeItem]
                selectDCNode(nodeitem)
            }
        }
    }

    // types
    type TDecomp = IDecomposition[TVertex, V, E]
    type TBiGraph = BiGraph[V, E]
    type TGraphVertex = Vertex[V]

    // current
    var showndecomposition : TDecomp = null
    var showncut : TBiGraph = null

    // decomposition view
    val dcview = new ShowGraphS
    dcview.addControlListener(new NodeClick)

    // cut view
    val graphview = new ShowGraphS

    // decomposition list
    val graphEventList = new BasicEventList[TDecomp]
    graphEventList.addAll(decompositions)

    val ls = new ListSelect[TDecomp](
        // action for selecting a decomposition in the list
        graphEventList, new SelectAction[TDecomp] {
        def select(graph: TDecomp): Unit = {
            showndecomposition = graph
            // view the decomposition
            dcview.setGraph(graph, (v : TVertex) => {
                String.format("%d: bw=%d", new java.lang.Integer(v.id()), v.getAttr("hoods"))
            })
            // generic maximum function
            def getMax[T](l: Iterable[T],
                          firstGreater: (T, T) => Boolean) : T = {
                var max = l.head
                //val it = l.iterator
                for (v : T <- l) {
                //while (it.hasNext) {
                //    val v = it.next
                    if (firstGreater(v, max)) max = v
                }
                return max
            }
            // get the cut
            val max : TVertex = getMax(graph.vertices(),
                (v1 : TVertex, v2: TVertex) => {
                    val f = (v : TVertex) => (v.getAttr("hoods").asInstanceOf[Int])
                    f(v1) > f(v2)
                }
            )
            printf("max: %s\n", max)
            graphview.setGraph(showndecomposition.getGraph())
            selectDCNode(dcview.getVisualItemForNode(max))
        }
    })
    ls.graphlist.setCellRenderer(new GraphCellRenderer)
    ls.select(0)


    // main panel and layout init
    val panel = new JPanel
    add(panel)
    val gridbag = new GridBagLayout
    panel.setLayout(gridbag)
    val c = new GridBagConstraints

    // add list and graph views
    panel.add(ls)
    panel.add(dcview)
    panel.add(graphview)

    // layout
    c.fill = GridBagConstraints.VERTICAL
    gridbag.setConstraints(ls, c)
    c.fill = GridBagConstraints.BOTH
    gridbag.setConstraints(dcview, c)
    gridbag.setConstraints(graphview, c)
    panel.setSize(800, 600)
    pack
}
