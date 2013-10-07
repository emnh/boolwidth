using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BoolWidth.Graph
{
    public class GraphLib
    {
        class UnorderedTupleComparer<T> : IEqualityComparer<Tuple<T, T>> //where T : IEquatable<T>
        {
            public bool Equals(Tuple<T, T> x, Tuple<T, T> y)
            {
                if (x.Item1.Equals(y.Item1) && x.Item2.Equals(y.Item2))
                {
                    return true;
                }
                if (x.Item1.Equals(y.Item2) && x.Item2.Equals(y.Item1))
                {
                    return true;
                }
                return false;
            }

            public int GetHashCode(Tuple<T, T> obj)
            {
                return obj.Item1.GetHashCode() ^ obj.Item2.GetHashCode();
            }
        }
        
        public static IEnumerable<Tuple<TNode, TNode>> DirectedNeighbors<TNode>(IGraphReader<TNode> graphReader)
        {
            return from node in graphReader.Nodes 
                   from neighbor in graphReader.Neighbours(node)
                   select new Tuple<TNode, TNode>(node, neighbor);
        }

        public static IEnumerable<Tuple<TNode, TNode>> UndirectedNeighbors<TNode>(IGraphReader<TNode> graphReader)
        {
            return (from node in graphReader.Nodes
                   from neighbor in graphReader.Neighbours(node)
                   select Tuple.Create(node, neighbor)).Distinct(new UnorderedTupleComparer<TNode>());
        }

        public static IEnumerable<Tuple<TNode, TNode>> Neighbors<TNode>(IGraphReader<TNode> graphReader, bool directed = false)
        {
            return directed ? DirectedNeighbors(graphReader) : UndirectedNeighbors(graphReader);
        }

        public static bool Neighbors<TNode>(IGraphReader<TNode> graphReader, TNode left, TNode right)
        {
            return graphReader.Neighbours(left).Contains(right);
        }
    }
}
