using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using BoolWidth.Io;
using BoolWidth.Lib;

namespace BoolWidth.Graph
{

    public class Graph<TNode> : IGraphReader<TNode> where TNode : Graph<TNode>.Node, new()
    {
        /**
         * Node members should be considered accessible only to containing Graph class.
         * TODO: See http://stackoverflow.com/questions/1664793/how-to-restrict-access-to-nested-class-member-to-enclosing-class for implementation.
         */
        public class Node
        {
            public int Index;
            public TNode[] Neighbours;
        }

        public bool Directed { get; private set;  }

        protected TNode[] _nodes = new TNode[0];
        

        /**
         * Use Create factory method instead.
         */
        protected Graph()
        {
            
        }


        /**
         * Factory method
         */
        public static TGraph Create<TGraph, TInNode>(
            IGraphReader<TInNode> graphReader,
            NodeConverter<TInNode, TNode> nodeConverter = null,
            bool directed = false
            ) where TGraph : Graph<TNode>, new()
        {
            var graph = new TGraph();
            graph.Initialize(graphReader, nodeConverter, directed);
            return graph;
        }

        /**
         * Initialize graph from graph reader
         */
        protected virtual void Initialize<TInNode>(
            IGraphReader<TInNode> graphReader,
            NodeConverter<TInNode, TNode> nodeConverter = null,
            bool directed = false
            )
        {
            if (nodeConverter == null)
            {
                nodeConverter = (node) => new TNode();
            }
            Directed = directed;
            if (graphReader == null) throw new ArgumentNullException("graphReader");
            if (nodeConverter == null) throw new ArgumentNullException("nodeConverter");
            _nodes = new TNode[graphReader.NodeCount];
            int i = 0;
            var nodeConversion = new Dictionary<TInNode, TNode>(graphReader.Nodes.Count());
            foreach (var inNode in graphReader.Nodes)
            {
                var node = nodeConverter(inNode);
                node.Index = i;
                node.Neighbours = new TNode[0];
                nodeConversion.Add(inNode, node);
                _nodes[i] = node;
                i++;
            }
            foreach (var inNode in graphReader.Nodes)
            {
                var node = nodeConversion[inNode];
                node.Neighbours = (from n in graphReader.Neighbours(inNode) select nodeConversion[n]).ToArray();
            }
        }

        public int NodeCount
        {
            get { return _nodes.Length; }
        }

        public int EdgeCount
        {
            get { return Neighbours().Count(); }
        }

        public IEnumerable<TNode> Nodes
        {
            get
            {
                return _nodes;
            }
        }

        public IEnumerable<Tuple<TNode, TNode>> Neighbours()
        {
            return GraphLib.Neighbors(this, Directed);
        }

        public IEnumerable<TNode> Neighbours(TNode node)
        {
            return node.Neighbours;
        }

        public bool Neighbours(TNode left, TNode right)
        {
            return GraphLib.Neighbors(this, left, right);
        }
    }

    class HybridGraph<TNode> : Graph<TNode> where TNode : Graph<TNode>.Node, new()
    {
        private Subsets<TNode,List<Subset>> _neighbours;

        protected override void Initialize<TInNode>(IGraphReader<TInNode> graphReader, NodeConverter<TInNode, TNode> nodeConverter = null, bool directed = false)
        {
            base.Initialize<TInNode>(graphReader, nodeConverter, directed);
            foreach (var node in _nodes)
            {
                _neighbours.Add(node.Neighbours);
            }
        }
    }
}
