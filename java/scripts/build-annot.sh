#!/bin/zsh
#CP=$PWD/lib/prefuse.jar:$PWD/lib/glazed*.jar
CP=$(grep -o "[^\"]*\.jar" .classpath | tr '\n' :):~/devel/master/staticproxy/bin
echo CLASSPATH: $CP
fsc=true
javac=javac
classes=sbuild/classes
gensrc=src_generated
mkdir -p $classes $gensrc
$fsc -cp $CP \
    -sourcepath src \
    -d $classes \
    src/**/*.scala \
    src/**/*.java
$javac -Xjcov -cp $CP:$classes:bin \
    -processor annotations.process.BWAnnotationProcessor \
    -s $gensrc \
    -d bin \
    -sourcepath src \
    src/**/*.java
