using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace BoolWidth.Lib
{
    struct Subset //<TWord> where TWord : struct
    {
        private const int WordSize = sizeof(uint);
        private readonly uint[] bits;

        public Subset(int bitCount)
        {
            bits = new uint[(bitCount - 1) / WordSize + 1];
        }

        public bool this[int i]
        {
            get
            {
                int wordIndex = i / WordSize;
                int bitIndex = i % WordSize;
                return (bits[wordIndex] & (uint) (1 << bitIndex)) != 0;
            }
            set
            {
                int wordIndex = i / WordSize;
                int bitIndex = i % WordSize;
                if (value)
                {
                    bits[wordIndex] |= (uint)(1 << bitIndex);
                }
                else
                {
                    bits[wordIndex] &= ~(uint)(1 << bitIndex);
                }
                
            }
        }
    }

    class Subsets<TItem, TColl> : ICollection<Subset> where TColl : ICollection<Subset>, new()
    {
        private readonly ISet<TItem> _groundSet;
        private TColl _subsets;

        private TItem[] GroundSetArray
        {
            get { return _groundSet.ToArray(); }
        }

        public int GroundSetCount
        {
            get { return _groundSet.Count(); }    
        }

        public Subsets(ISet<TItem> groundSet)
        {
            _groundSet = groundSet;
            _subsets = new TColl();
        }
        
        public IEnumerable<TItem> SubsetMembers(Subset subset)
        {
            var groundSetArray = GroundSetArray;
            for (var i = 0; i < GroundSetCount; i++)
            {
                if (subset[i]) yield return groundSetArray[i];
            }
        }

        public IEnumerator<Subset> GetEnumerator()
        {
            return _subsets.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public static void test()
        {
            var a = new Subsets<string, List<Subset>>(new HashSet<string>(new string[] {"a","b", "c"}));
        }

        public void Add(Subset item)
        {
            _subsets.Add(item);
        }

        public void Add(ISet<TItem> toAdd)
        {
            var subset = new Subset();
            var groundSetArray = GroundSetArray;
            for (int i = 0; i < groundSetArray.Count(); i++)
            {
                subset[i] = toAdd.Contains(groundSetArray[i]);
            }
            Add(subset);
        }

        public void Add()
        {
            
        }

        public void Clear()
        {
            _subsets.Clear();
        }

        public bool Contains(Subset item)
        {
            return _subsets.Contains(item);
        }

        public void CopyTo(Subset[] array, int arrayIndex)
        {
            throw new NotImplementedException();
        }

        public bool Remove(Subset item)
        {
            return _subsets.Remove(item);
        }

        public int Count
        {
            get { return _subsets.Count; }
        }

        public bool IsReadOnly
        {
            get { return _subsets.IsReadOnly; }
        }
    }


}
