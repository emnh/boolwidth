# Getting started

    git clone https://github.com/emnh/boolwidth

The data files and boolwidth-explorer are in submodules:

    git submodule update --init

# Submodules

- [explorer](https://github.com/emnh/boolwidth-explorer): boolwidth-explorer repository as git submodule
- [java/data](https://github.com/emnh/boolwidth-data): boolwidth-data repository as git submodule

# Contents

Main:
- java/src: original java source. This is the project which results I used in my master thesis.
  - Licensed under GNU GPLv2.
  - Code collaborators:
    - Martin Vatshelle (Ph.D. student coordinating the project and writing code)
    - Kari Ringdal (did coding for us as part of University course work)
    - Oliver Tynes (did coding for us as part of University course work)
    - Eivind Magnus Hvidevold (me. did most of the project coding for my Master thesis)

- java/clj: Clojure addons and rewrites os java/src. A bit unorganized still.
  - Needs cleanup and build/run instructions to be more useful.

- cbool: reimplementation of basic cutbool algorithm in C. I think I remember it works, but is not very general (using ints) or usable as library.

- CSharp/BoolWidth: Partial rewrite in C#. Nothing works .

- FSharpTest/CutBoolExperiments: Testing a new smart algorithm for fast cutbool computation in F#. Doesn't work yet. Don't think it even compiles ATM. I think the idea is good though.

# Resources

Related projects:

http://www.staff.science.uu.nl/~bodla101/treewidthlib/

http://www.treewidth.com/
