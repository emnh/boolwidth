package control

import interfaces.IGraph
import io.DiskGraphS
import graph.{AdjacencyListGraph, Vertex}
import exceptions.FatalHandler
import java.io.FileNotFoundException

/**
 * Created by IntelliJ IDEA.
 * User: emh
 * Date: Apr 6, 2010
 * Time: 1:56:38 AM
 * To change this template use File | Settings | File Templates.
 */

object ControlUtilS {

    val GRAPHLIB = ControlUtil.GRAPHLIB

    def forTestGraphs[V, E](graphAction : IGraph[Vertex[V], V, E] => Boolean) : Unit = {
        var proto: IGraph[Vertex[V], V, E] = 
            new AdjacencyListGraph[Vertex[V], V, E](new Vertex.Factory[V]());
        try {
            DiskGraphS.forGraphs[IGraph[Vertex[V],V,E],Vertex[V],V,E](GRAPHLIB, proto, graphAction)
        }
        catch {
            case e: FileNotFoundException => {
                FatalHandler.handle(e)
            }
        }
        return null
    }
}
