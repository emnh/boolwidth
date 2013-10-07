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

def usage():
    'print usage'
    print 'usage: %s [options]' % sys.argv[0]

def main():
    'entry point'
    if len(sys.argv) < 1:
        usage()
        sys.exit(1)
    fname = sys.argv[1] #'log.txt'
    lines = file(fname).readlines()
    lines = [x for x in lines if 'boolean-width' in x]

    twfname = 'graphLib/treewidthlib_known_bounds/known_bounds.txt'
    twlines = file(twfname).readlines()
    twmap = {}
    for line in twlines:
        line = line.strip()
        twfname, measure, ubound, lbound = line.split(' ')
        if measure == 'treewidth':
            twmap[twfname] = (int(lbound), int(ubound))

    avg = 0
    ct = 0
    maxbw = 0

    def logbw(bw):
        logbw_ = math.log(bw) / math.log(2)
        return logbw_

    def ceilbw(bw):
        logbw_ = math.ceil(logbw(bw)*10) / 10
        return logbw_

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
        maxbw = max(ceilbw(bw), maxbw)
        time = int(time)
        twlbound = '-'
        twubound = '-'
        if gfbase in twmap:
            twlbound, twubound = twmap[gfbase]
            if not 'pp' in gfbase:
                avg += twlbound / logbw(bw)
                ct += 1
        bws = 'boolw: log(%d) = %.1f' % (bw, ceilbw(bw))
        bwtimes = '%ds' % ((time / 1000) + 1)
        print 'graph: %s, v: %2d, e: %4d %s, tw lbound: %s, tw ubound: %s, bw time: < %s' \
                % (gfbase.ljust(15),
                        vertexct,
                        edgect,
                        bws.ljust(25),
                        str(twlbound).rjust(5),
                        str(twubound).rjust(5),
                        bwtimes.rjust(5))

    if ct > 0:
        print 'comparable: %d, avg(tw lbound / boolw): %.2f' % (ct, avg / ct)
        print 'maxbw: ', maxbw

if __name__ == '__main__':
    main()

