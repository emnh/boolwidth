package visualization

import javax.swing.JFrame
import control.ControlUtil
import java.util.ArrayList
import java.util.Collection
import boolwidth.{DNode, Decomposition, ExactBoolWidth}
import graph._
import io.ConstructGraph
import interfaces.{IDecomposition, IGraph}

object UITest {
    type V = Int
    type E = Int

//    def collectionUpCast[Tto](from : Collection[_ <: Tto]) : Collection[Tto] = {
//        return new ArrayList[Tto](from)
//    }
    def collectionUpCast[Tto](from : ArrayList[_ <: Tto]) : Collection[Tto] = {
        return new ArrayList[Tto](from)
    }

    def decompositionsTest() {
        val inFileName = "graphLib_ours/hsugrid/hsu-4x4.dimacs"
        val graph : IGraph[Vertex[V], V, E] = ControlUtil.getTestGraph(inFileName)

        println(graph.toString)

        val upper_bound = 6
        val ebw = new ExactBoolWidth[Vertex[V], V, E](true)
        val comp = new ebw.Computer(graph, upper_bound)
        val bw = comp.result
        val testgraph: IGraph[DNode.D[V], _, E] = ebw.getDecomposition
        val decomps = comp.getDecompositionsOfWidth(bw.asInstanceOf[Int])
        //val decomps2 : Collection[IGraph[DNode.D[V], PosSubSet[Vertex[V]], E]] = collectionUpCast(decomps);
        val decomps2 : Collection[IDecomposition[DNode.D[V], V, E]] = collectionUpCast(decomps);

        val frame = new DCBrowserS(decomps);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
    }

    def graphBrowserTest() {
        val frame: JFrame = new GraphBrowserS(GraphIterator.getSmallCubes(22, 4))
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
    }

    def showGraphTest() {
        val inFileName = "graphLib_ours/hsugrid/hsu-4x4.dimacs"
        val graph = ConstructGraph.construct(inFileName)
        val frame = ShowGraphS.demo(graph)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
    }

    def main(args: Array[String]) {
        decompositionsTest
    }
}
