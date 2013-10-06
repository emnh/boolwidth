#!/bin/bash
CP=$(grep -o "[^\"]*\.jar" .classpath | tr '\n' :)
scalac=fsc
javac=javac
gensrc=src_generated
mkdir -p bin $gensrc

$javac -Xjcov -cp $CP \
    -sourcepath src \
    -s $gensrc \
    -d bin \
    src/**/*.java
