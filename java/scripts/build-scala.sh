#!/bin/zsh
#CP=$PWD/lib/prefuse.jar:$PWD/lib/glazed*.jar
CP=$(grep -o "[^\"]*\.jar" .classpath | tr '\n' :)
scalac=fsc
javac=javac
classes=sbuild/classes
gensrc=src_generated
mkdir -p $classes $gensrc
$scalac -cp $CP \
    -sourcepath src \
    -d $classes \
    src/**/*.scala \
    src/**/*.java
#$javac -Xjcov -cp $CP:$classes:bin \
#    -processor annotations.process.BWAnnotationProcessor \
#    -sourcepath src \
#    src/**/*.java

$javac -Xjcov -cp $CP:$classes \
    -sourcepath src \
    -s $gensrc \
    -d bin \
    src/**/*.java
