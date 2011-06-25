using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using BoolWidth.Graph;
using BoolWidth.Io;

namespace BoolWidth.Run
{
    class MyNode : Graph<MyNode>.Node
    {
        public string Label;

        public override string ToString()
        {
            return Label;
        }
    }

    class TestGraphReader
    {
        public static void Run(string[] args)
        {
            var fileName = "D:\\sync\\graphLib\\freq\\celar01.dgf";

            var gb = new GraphBuilder(fileName);
            
            var graph = Graph<MyNode>.CreateGraph(gb, (n) => new MyNode() { Label = n.Label });

            foreach (var pair in graph.Neighbours())
            {
                Console.WriteLine("Edge: {0} {1}", pair.Item1, pair.Item2);
            }
            Console.WriteLine("Nodes: {0}, Edges: {1}", graph.NodeCount, graph.EdgeCount);

            Console.ReadKey();
        }
        
    }
}
