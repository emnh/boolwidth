package util

import interfaces.IGraph
import boolwidth.{DNode, Decomposition}
import graph.Vertex

/**
 * Created by IntelliJ IDEA.
 * User: emh
 * Date: Mar 27, 2010
 * Time: 12:34:49 PM
 * To change this template use File | Settings | File Templates.
 */

object TypeDefs {
    // most general graph type
    // should be used as method parameter for all methods/classes that:
    //  - want to read a graph knowing only about the general vertex type
    //  - don't need to add vertices to the graph
    //  - don't need to return a graph, vertices or edges
    type TGraph = IGraph[_, _, _]
    type TVertex = Vertex[_]

    // most general decomposition type
    type TDecomposition = Decomposition[_, _, _]
    type TDNode = DNode[_, _]

    // after scala 2.8 this can be replaced with import scala.collection.JavaConversions._
    //implicit def javaIteratorToScalaIterator[A](it : java.util.Iterator[A]) = 
    //    new IteratorWrapper(it)
}
