#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ft=python ts=4 sw=4 sts=4 et fenc=utf-8
# Original author: "Eivind Magnus Hvidevold" <hvidevold@gmail.com>
# License: GNU GPLv3 at http://www.gnu.org/licenses/gpl.html

'''
'''

import os
import sys
import re
import math
import inspect
from xml.etree import ElementTree
import Gnuplot

class ScatterPlot(object):

    def __init__(self, filename):
        self.filename = filename
        self.docfilename = filename.replace('.tex', '-doc.tex')

        # document wrapper for compile
        fd = file(self.docfilename, 'w')
        fd.write(
'''\\documentclass{article}
\\usepackage{amsmath}
\\usepackage{gnuplot-lua-tikz}

\\input{commands.tex}

\\begin{document}
\\input{%s}
\\end{document}
''' % os.path.basename(filename))
        fd.close()

        self.data = []
        self.g = Gnuplot.Gnuplot()
        self.g('set term lua tikz')
        self.g('set out "%s"' % filename)

    def call(self, *args):
        self.g(*args)
        return self

    def __getattr__(self, attr):
        if hasattr(self.g, attr):
            f = getattr(self.g, attr)
            if inspect.ismethod(f):
                def wrap(*args):
                    f(*args)
                    return self
                return wrap
            else:
                raise AttributeError()
        else:
            raise AttributeError()

def escapeLatex(x):
    # copy pasted mapping from web page
    cmap = r'''
    <maps>
    <map from="&lt;" to="\textless{}"/>
    <map from="&gt;" to="\textgreater{}"/>
    <map from="~" to="\textasciitilde{}"/>
    <map from="^" to="\textasciicircum{}"/>
    <map from="&amp;" to="\&amp;"/>
    <map from="#" to="\#"/>
    <map from="_" to="\_"/>
    <map from="$" to="\$"/>
    <map from="%" to="\%"/>
    <map from="|" to="\docbooktolatexpipe{}"/>
    <map from="{" to="\{"/>
    <map from="}" to="\}"/>
    <map from="\textbackslash  " to="\textbackslash \ "/>
    <map from="\" to="\textbackslash "/>
    </maps>
    '''
    maps = ElementTree.fromstring(cmap)
    maps = maps.getchildren()
    cmap = {}
    for xmap in maps:
        mapfrom = xmap.get('from')
        mapto = xmap.get('to')
        cmap[mapfrom] = mapto
    newx = ''
    for c in x:
        if c in cmap:
            newx += cmap[c]
        else:
            newx += c
    return newx

def usage():
    'print usage'
    print 'usage: %s [options]' % sys.argv[0]

def main():
    'entry point'
    if len(sys.argv) < 3:
        usage()
        sys.exit(1)
    outdir = sys.argv[1]
    fname = sys.argv[2] #'log.txt'
    lines = file(fname).readlines()
    lines = [x for x in lines if 'boolean-width' in x]

    class TwRecord(object):
        pass

    twfname = 'graphLib/treewidthlib_known_bounds/known_bounds.txt'
    twlines = file(twfname).readlines()
    twmap = {}
    for line in twlines:
        line = line.strip()
        tw = TwRecord()
        tw.url, tw.twfname, tw.measure, tw.ubound, tw.lbound = line.split(' ')
        if tw.measure == 'treewidth':
            tw.lbound = int(tw.lbound)
            tw.ubound = int(tw.ubound)
            twmap[tw.twfname] = tw

    outputfname = outdir + '/tabledata.tex' #file(sys.argv[2], 'w')
    outputfile = file(outputfname, 'w')
    def out(x):
        outputfile.write(str(x))
    def outl(x):
        out(x + '\n')

    scatter = []

    first = True
    class Record(object):

        @property
        def ratio(self):
            if self.twlbound == '-':
                ratio = 0
            else:
                ratio = self.twlbound / float(self.logbw)
            return ratio

        @property
        def uratio(self):
            if self.twlbound == '-':
                ratio = 0
            else:
                ratio = self.twubound / float(self.logbw)
            return ratio


        # used for sorting
        @property
        def cratio(self):
            if self.ratio == 0:
                return 0
            else:
                return math.ceil(self.ratio * 10) / 10

        @property
        def pratio(self):
            if self.ratio == 0:
                return '-'
            else:
                return self.cratio

        @property
        def logbw(self):
            logbw = math.log(self.bw, 2)
            return logbw

        @property
        def ceilbw(self):
            logbw = math.ceil(self.logbw * 10) / 10
            return logbw

        @property
        def url(self):
            if self.gfbase in twmap:
                return twmap[self.gfbase].url
            else:
                return None

        @property
        def latexname(self):
            url = self.url
            name = escapeLatex(r.gfbase)
            if url:
                name = r'\href{%s}{%s}' % (url, name)
            return name


    def getRecords():
        records = []
        for line in lines:
            match = re.search('decomposition\(([^\)]*)\(v=(\d+),e=(\d+)\)\)\): bw=(\d+), time: (\d+)', line)
            gfname, vertexct, edgect, bw, time = match.groups()

            gfbase = os.path.basename(gfname)
            gfbase = gfbase.replace('.dgf', '')
            gfbase = gfbase.replace('.dimacs', '')
            gfbase = gfbase.replace('_graph', '')

            vertexct = int(vertexct)
            edgect = int(edgect)
            bw = int(bw)
            if bw == 0:
                continue
            time = int(time)
            twlbound = '-'
            twubound = '-'
            if gfbase in twmap:
                twr = twmap[gfbase]
                twlbound, twubound = twr.lbound, twr.ubound

            r = Record()
            r.gfbase = gfbase
            r.vertexct = vertexct
            r.edgect = edgect
            r.bw = bw
            r.twlbound = twlbound
            r.twubound = twubound
            records.append(r)
        return records
    records = getRecords()

    def getSortKey(r):
        ret = (-r.cratio, r.bw)
        return ret

    records.sort(key=getSortKey)

    avg = 0
    ct = 0
    maxbw = 0

    for r in records:
        if r.gfbase in twmap:
            if not 'pp' in r.gfbase:
                rat = r.ratio
                if rat > 0:
                    avg += rat
                    ct += 1
        maxbw = max(r.ceilbw, maxbw)

    for r in records:
        row = [
                ('Graph name', r.latexname),
                ('Vertices', r.vertexct),
                ('Edges', r.edgect),
                (r'$2^{\textrm{BWUB}}$', r.bw),
                (r'Ratio', r.pratio),
                (r'BWUB', r.ceilbw),
                (r'TWLB', r.twlbound),
                (r'TWUB', r.twubound),
                ('Time', 'TODO')
                ]
        def outrow(row):
            outl(' & '.join(row) + r' \\')

        # print headers
        if first:
            header = [x[0] for x in row]
            for i, v in enumerate(header):
                if i == 0:
                    header[i] = v
                else:
                    fmt = r'\multicolumn{1}{c}{%s}'
                    header[i] = fmt % v
            outrow(header)
            first = False
            outl(r'\midrule \\')
            outl(r'\endhead')

        # print row
        data = [str(x[1]) for x in row]
        outrow(data)

    outputfile.close()

    def esc(x):
        xn = ''
        for c in x:
            if c == '\\':
                xn += c + c
            else:
                xn += c
        return xn

    for filename, xlabel, xdataf in [
            (outdir + '/scatter-v-bw.tex', 'Vertices', lambda r: r.vertexct),
            (outdir + '/scatter-e-bw.tex', 'Edges', lambda r: r.edgect),
            (outdir + '/scatter-epct-bw.tex',
             esc(r'$\frac{\textrm{Edges}}{\binom{\textrm{Vertices}}{2}}$'),
             lambda r: r.edgect / (r.vertexct * (r.vertexct - 1) / 2.0)
            )
            ]:
        ScatterPlot(filename
                ).xlabel(xlabel).ylabel('Width'
                ).call('set key outside center top'
                ).plot(
                    Gnuplot.Data(
                        [[xdataf(r), r.logbw] for r in records],
                        title=esc(r'\Boolw upper bound')),
                    Gnuplot.Data(
                        [[xdataf(r), r.twlbound] for r in records if r.twlbound != '-'],
                        title=esc(r'\Tw lower bound')),
                    Gnuplot.Data(
                        [[xdataf(r), r.twubound] for r in records if r.twlbound != '-'],
                        title=esc(r'\Tw upper bound'))
                )

    ScatterPlot(outdir + '/scatter-v-e-bw.tex'
            ).call('set key outside center top'
            ).xlabel('Vertices').ylabel('Edges').zlabel('\Boolw upper-bound'
            ).splot(
                [[r.vertexct, r.edgect, r.logbw] for r in records]
            )

    if ct > 0:
        print 'comparable: %d, avg(tw lbound / boolw): %.2f' % (ct, avg / ct)
        print 'maxbw: ', maxbw

if __name__ == '__main__':
    main()

