# BoolWidth Java Project

This is a heuristic search project to generate boolean width decompositions of graphs.

# Prepare

Library dependencies are in dropbox.

Download libraries with provided script:

    cd lib
    bash download.sh

Or download and extract manually to lib folder:

    https://dl.dropboxusercontent.com/u/101905846/boolwidth-lib/lib.tar.gz

# Compile

## Using IDE

Open the IntelliJ IDEA project in java folder.

I haven't used the Eclipse project in a while, but you can try.

## Console

Run:

    bash scripts/build-java.sh

# Run

This will run the heuristic on a graph to create a decomposition:

    bash ./scripts/run.sh data/graphLib_ours/hsugrid/hsu-4x4.dimacs
