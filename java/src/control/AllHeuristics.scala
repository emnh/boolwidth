
package control

import interfaces.IGraph
import io.DiskGraph
import graph.Vertex
import scala.collection.JavaConversions._

object AllHeuristic {

    def main(args : Array[String]) {
        type TGraph = IGraph[Vertex[Int], Int, Int]
        var i = 0
        ControlUtilS.forTestGraphs[Int, Int](
        (graph : TGraph) => {
            i += 1
            printf("%d: %s, %d nodes, %d edges\n", i,
                DiskGraph.getFileName(graph),
                graph.numVertices, graph.numEdges)
            if (graph.numVertices() < 64) {
                //println(graph)
                val ht = new HeuristicTest[Int, Int]
                ht.doHeuristic(graph)
            }

            true
        })
    }
}
