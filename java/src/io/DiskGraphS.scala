/**
 * Created by IntelliJ IDEA.
 * User: emh
 * Date: Apr 5, 2010
 * Time: 9:55:11 PM
 * To change this template use File | Settings | File Templates.
 */

package io

import exceptions.FatalHandler
import exceptions.InvalidGraphFileFormatException
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.List
import java.util.Stack
import interfaces.{IAttributeStorage, IGraph}
import graph.Vertex

/**Get graphs from disk **/
object DiskGraphS {
    private final val GRAPH_EXT: List[String] = Arrays.asList("dgf", "dimacs")

    def readGraph[TVertex <: Vertex[V], V, E]
    (fileName: String, graph: IGraph[TVertex, V, E]): IGraph[TVertex, V, E] = {
        System.out.printf("reading: %s\n", fileName)
        try {
            var r: GraphFileReader = new GraphFileReader(fileName)
            var gb: GraphBuilder[TVertex, V, E] = new GraphBuilder[TVertex, V, E](r)
            gb.buildNullGraph(graph)
            setFileName(graph, fileName)
            return graph
        }
        catch {
            case e: FileNotFoundException => {
                FatalHandler.handle(e)
            }
        }
        return null
    }

    def getFileName(graph: IAttributeStorage): String = {
        return graph.getAttr(SOURCE_FILENAME_FIELD)
    }

    private final val SOURCE_FILENAME_FIELD: String = "sourceFileName"

    def setFileName(graph: IAttributeStorage, filename: String): Unit = {
        graph.setAttr(SOURCE_FILENAME_FIELD, filename)
    }

    def forGraphs[
    TGraph <: IGraph[TVertex, V, E],
    TVertex <: Vertex[V], V, E]
    (
     path: String,
     graphPrototype: TGraph,
     graphAction : TGraph => Boolean
    ) {
        var dirs: Stack[File] = new Stack[File]
        var root: File = new File(path)
        dirs.push(root)

        if (!root.isDirectory) {
            throw new FileNotFoundException(
                String.format("in cwd \"%s\", not a directory: \"%s\"",
                    new File(".").getAbsolutePath, path))
        }

        def checkGraphFile(f : File) : Option[TGraph] = {
            val ext = f.toString.replaceFirst("^.*\\.", "")
            val graph = graphPrototype.copy.asInstanceOf[TGraph]
            if (!GRAPH_EXT.contains(ext)) {
                return None
            } else if (f.length > 1024 * 1024) {
                System.out.printf("warning: skipping big graph: \"%s\"\n", f.toString)
                return None
            } else {
                try {
                    readGraph(f.toString, graph)
                } catch {
                    case e: InvalidGraphFileFormatException => {
                        System.out.printf(
                            "warning: invalid graph file format: \"%s\": \"%s\"\n",
                            f.toString, e.toString)
                        return None
                    }
                }
            }
            return Some(graph)
        }

        while (!dirs.empty) {
            var curdir: File = dirs.pop
            if (curdir == null) {
                System.out.println("wtf")
            }
            for (f <- curdir.listFiles) {
                if (f.isDirectory) {
                    dirs.push(f)
                } else {
                    checkGraphFile(f) match {
                        case None => {}
                        case Some(graph) => {
                            if (!graphAction(graph)) {
                                println("returning")
                                return
                            }
                        }
                    }
                }
            }
        }
        println("normal exit")
    }
}
