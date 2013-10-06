#!/bin/bash

# libraries are in dropbox to avoid lots of binaries in git repository

# Download libraries
location=https://dl.dropboxusercontent.com/u/101905846/boolwidth-lib/
( cat | xargs -i wget -c $location/{} ) << EOF
glazedlists-1.8.0_java15.jar
google-collect-1.0.jar
prefuse.jar
scala-compiler.jar
scala-library.jar
staticproxy.jar
trove-3.0.0a3.jar
xmlpull-1.1.3.1.jar
xpp3_min-1.1.4c.jar
xstream-1.3.1.jar
EOF
