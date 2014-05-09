#!/bin/bash

find out -iname \*.cut |
while read fname; do
  #fname=$(echo $fname | sed s@^../../@@)
  # TODO: what about multiple
  outfname="out/"$fname".result"
  matfname="out/"$fname".cut"
  #graphdata/graphLib_ours/hsugrid/hsu-4x4.dimacs
  #mkdir -p $(dirname $outfname)
  echo "Processing $fname"
  ./bt_unions < $fname
  echo
done
