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

def main():
    'entry point'

if __name__ == '__main__':
    rowct = int(sys.argv[1])
    colct = rowct
    print rowct, colct
    for i in range(rowct):
        for j in range(colct):
            if i == colct - j - 1:
                print 1,
            else:
                print 0,
        print

    main()

