using System;
using System.Diagnostics;
using System.IO;

namespace BoolWidth.Io
{
    public abstract class GraphFileReader
    {
        public GraphFileReader (String fileName)
        {
            ParseFile(File.ReadAllLines(fileName));
        }

        public void ParseFile(String[] lines)
        {
            bool headerFound = false;
            foreach (var line in lines)
            {
                var tokens = line.Split(' ');
                var type = tokens[0];
                switch (type)
                {
                    case "c": // comment
                        break;
                    case "p":
                        var nodeCount = Convert.ToInt32(tokens[2]);
                        var edgeCount = Convert.ToInt32(tokens[3]);
                        NodeCount = nodeCount;
                        EdgeCount = edgeCount;
                        headerFound = true;
                        break;
                    case "e": // edge
                        Debug.Assert(headerFound, "edge before header in graph file: " + fileName);
                        AddEdge(tokens[1], tokens[2]);
                        break;
                    case "n": // node
                        AddNode(tokens[1]);
                        break;
                }
            }
        }

        // can't be trusted as node count in file is often wrong
        public abstract int NodeCount { get; set; }
        // can't be trusted as edge count in file is often wrong
        public abstract int EdgeCount { get; set; }
        public abstract void AddNode(string label);
        public abstract void AddEdge(string leftLabel, string rightLabel);
    }
}