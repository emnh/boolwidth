#!/bin/bash
logfile=$1
logfile=tex/newlog.txt
outdir=~/thesis/tex/chapters/results/generated
cd ..
mkdir -p $outdir
./tools/merge-log-tex.py $outdir $logfile ./tex/gen/tabledata.tex
