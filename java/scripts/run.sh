#!/bin/bash
CP=$(grep -o "[^\"]*\.jar" .classpath | tr '\n' :)
java=java
classes=sbuild/classes
#agent=-javaagent:tools/shiftone-jrat.jar
#$java -cp $CP:$classes:bin control.AllHeuristic "$@"
$java $agent -cp $CP:$classes:bin control.HeuristicTest "$@"
#$java $agent -cp $CP:$classes:bin control.CutBoolHeuristicTest "$@"
#$java $agent -cp $CP:$classes:bin bw.io.FileLock "$@"
