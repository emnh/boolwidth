using System;
using System.Collections.Generic;
using System.Linq;
using BoolWidth.Graph;
using BoolWidth.Lib;

namespace BoolWidth.Io
{
    /**
     * Graph representation is not important in this graph.
     * It's just an intermediate between file format and efficient graph implementation, 
     * which should construct from an IGraphReader.
     * 
     * Builds an undirected graph, where Neighbours(left, right) = Neighbours(right, left).
     */
    public class GraphBuilder : GraphFileReader, IGraphReader<GraphBuilder.Node>
    {
        public class Node
        {
            public readonly HashSet<Node> Neighbours = new HashSet<Node>();
            public string Label;
        }

        private readonly Dictionary<string, Node> _labeledNodes = new Dictionary<string, Node>();

        public bool Directed;

        public GraphBuilder(string fileName, bool directed = false) : base(fileName)
        {
            Directed = directed;
        }

        public override int NodeCount { get; set; }
        public override int EdgeCount { get; set; }

        public IEnumerable<Node> Nodes
        {
            get { return _labeledNodes.Values; }
        }

        public IEnumerable<Tuple<Node, Node>> Neighbours()
        {
            return GraphLib.Neighbors(this, Directed);
        }

        public IEnumerable<Node> Neighbours(Node node)
        {
            return node.Neighbours;
        }

        public bool Neighbours(Node left, Node right)
        {
            return GraphLib.Neighbors(this, left, right);
        }

        public override void AddNode(string label)
        {
            Node left = _labeledNodes.GetValueOrAddDefault(label, () => new Node() { Label = label });
            // might be inefficient, but it's not important
            NodeCount = Nodes.Count();
        }

        public override void AddEdge(string leftLabel, string rightLabel)
        {
            Node left = _labeledNodes.GetValueOrAddDefault(leftLabel, () => new Node() {Label = leftLabel});
            Node right = _labeledNodes.GetValueOrAddDefault(rightLabel, () => new Node() { Label = rightLabel});
            left.Neighbours.Add(right);
            right.Neighbours.Add(left);
            // might be inefficient, but it's not important
            NodeCount = Nodes.Count();
        }
    }
}