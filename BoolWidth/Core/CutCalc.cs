using System;
using System.Collections.Generic;
using System.Linq;

namespace BoolWidth.Core
{
    class CutCalc
    {
        static int MaxCut(int n, int splitSize, int l)
        {
            int r = splitSize - l;
            int lMinCut = Math.Min(l, n - l);
            int rMinCut = Math.Min(r, n - r);
            int maxCut = Math.Max(lMinCut, rMinCut);
            return maxCut;
        }

        public static void Calc(int n, int splitSize)
        {
            int minMaxCut = n;
            int minL = -1;
            List<int> ls = new List<int>();
            for (int l = 0; l < splitSize; l++)
            {
                int maxCut = MaxCut(n, splitSize, l);
                if (minMaxCut >= maxCut)
                {
                    minMaxCut = maxCut;
                    minL = l;
                }
            }
            for (int l = 0; l < splitSize; l++)
            {
                if (minMaxCut == MaxCut(n, splitSize, l))
                {
                    ls.Add(l);
                }
            }
            var lss = (from x in ls select x.ToString());

            Console.WriteLine(
                String.Format("n: {0}, splitSize: {1}, ls: {2}, minMaxCut: {3}, ratio: {4}",
                n, splitSize, String.Join(",", lss), minMaxCut, minL / (double)splitSize));
        }
    }
}
