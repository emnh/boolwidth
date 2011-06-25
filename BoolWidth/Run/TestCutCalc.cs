using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using BoolWidth.Core;

namespace BoolWidth.Run
{
    class TestCutCalc
    {
        public static void Run(string[] args)
        {
            int n = 100;
            for (int p = 0; p < n; p++)
            {
                CutCalc.Calc(n, p);
            }
        }
    }
}
