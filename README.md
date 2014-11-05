# Getting started

    git clone https://github.com/emnh/boolwidth

The data files and boolwidth-explorer are in submodules:

    git submodule update --init

# Git submodules

A git submodule is a subfolder stored in a different repository.

- [explorer](https://github.com/emnh/boolwidth-explorer)
  - Separate repository for easy push to heroku.
- [java/data](https://github.com/emnh/boolwidth-data)
  - Separate for convenience because of data size.

# Contents

Main:
- java/src: original java source. This is the project which results I used in my master thesis.
  - Licensed under GNU GPLv2.
  - Code collaborators:
    - Martin Vatshelle (Ph.D. student coordinating the project and writing code)
    - Kari Ringdal (did coding for us as part of University course work)
    - Oliver Tynes (did coding for us as part of University course work)
    - Sadia Sharmin (Ph.D. student writing lots of new code after I finished my Master thesis)
      - java/src/sadiasrc is her evolution of the main tree.
    - Eivind Magnus Hvidevold (me. did most of the project coding for my Master thesis)

- java/clj: Clojure addons and rewrites os java/src.

- cbool: reimplementation of basic cutbool algorithm in C. I think I remember it works, but is not very general (using ints) or usable as library.

- fsharp/CutBoolExperiments: Testing a new smart algorithm for fast cutbool computation in F#.

- csharp/BoolWidth: Partial rewrite in C#. Nothing works.

# Resources

## Related projects:

http://www.staff.science.uu.nl/~bodla101/treewidthlib/

http://www.treewidth.com/

## YourKit Profiler

YourKit is kindly supporting this open source project with its full-featured Java Profiler. YourKit, LLC is the creator of innovative and intelligent tools for profiling Java and .NET applications. Take a look at YourKit's leading software products:
- [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp")
- [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp")
