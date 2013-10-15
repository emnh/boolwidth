# BoolWidth Java Project

This is a heuristic search project to generate boolean width decompositions of graphs.

# Prepare

Library dependencies are in git submodule.
Make sure you have updated them, as in main project readme.

# Compile

## Using IDE

Open the IntelliJ IDEA project in java folder.

I haven't used the Eclipse project in a while, but you can try.

## Console

Run:

    bash scripts/build-java.sh

Or with leiningen (build tool for clojure that does java as well):

    cd clj
    lein javac

# Run

This will run the heuristic on a graph to create a decomposition:

    bash ./scripts/run.sh data/graphLib_ours/hsugrid/hsu-4x4.dimacs

# Scala

The scala files were an experiment and are not supported at this time but you are welcome to play with them.
