using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BoolWidth.Graph
{

    public delegate TOutNode NodeConverter<in TInNode, out TOutNode>(TInNode node);

    /**
     * For read-only access to graph.
     */
    public interface IGraphReader<TNode>
    {
        int NodeCount { get; }
        int EdgeCount { get; }
        IEnumerable<TNode> Nodes { get; }
        // Return all directed pairs of neighbours
        IEnumerable<Tuple<TNode,TNode>> Neighbours();
        // Return neighbours of node
        IEnumerable<TNode> Neighbours(TNode node);
        // Return true if right is a neighbor of left
        bool Neighbours(TNode left, TNode right);
    }

    /**
     * For read-only access to graph also containing edge data.
     */
    public interface IGraphEdgeReader<TNode, TEdge> : IGraphReader<TNode>
    {
        IEnumerable<TEdge> Edges { get; }
        TNode Left(TEdge edge);
        TNode Right(TEdge edge);
    }

    /**
     * For building graphs.
     */
    public interface IGraphWriter<in TNode, in TEdge>
    {
        void AddNode(TNode node);
        void AddEdge(TNode left, TNode right, TEdge edge);
    }
}
