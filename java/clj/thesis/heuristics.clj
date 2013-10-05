(ns 
  clj.thesis.heuristics
  (:use
    [clj.thesis.examples]
    [clojure.pprint :only [pprint]]
    [clj.thesis.thesis :only [definition functions examples implementation]]
    [clojure.contrib.repl-utils :only [get-source]]
    )
  (:require 
    [clj.boolwidth.heuristics :as heuristics]
    [clj.tex.graph :as texgraph]
    [clj.util.util :as util]
    [clj.tex.tex :as tex]
    [clj.sets.set :as sets]
    [clj.thesis.thesis :as thesis]
    [clj.graph.graph :as graph]
    [clj.viz.graphviz :as dot]
    ; note that get-source doesn't work on definitions in the current file
    [clojure.contrib.repl-utils :as repl-utils]
    )
  )

(util/enable-reflection-warnings)


(tex/chapter "Heuristics")

(tex/section
  "Cut-local initial heuristics"

  (tex/par
    "\\todo{write what Im going to do}"
    "I'm going to introduce a simple class of heuristics that are good for
    creating an initial decomposition that can then be improved by other
    heuristics. I will walk through how the algorithms work using examples."
    )

  (tex/par
    "These are cut-local heuristics, which means that they consider
    improvements only locally for the cut they are working on and not on the
    whole decomposition at once. They are initial heuristics, which mean that
    they create an initial decomposition on a graph, as opposed to heuristics
    that start with a decomposition and try to improve it.")
  )

(tex/subsection
  "General algorithm"

  (tex/par
    "This is a higher-order function that takes a function selecting nodes to
    go on the left and right side of each split and uses this function
    recursively locally for each cut. We'll call this function that's passed in
    a divider heuristic."
    "\\todo{explain how it works in verbose handwaving terms. give examples of
    subfunctions}")
  (tex/par
    (tex/cljcode
      (get-source 'heuristics/cut-local-heuristic))))

(tex/subsection
  "Example divider heuristic : even"
  (tex/par "This is just a simple example to show how using the general
           algorithm works. All it does is throw even indexed nodes on one side
           of the split and odd index nodes on the other.")
  (tex/cljcode
    (get-source 'heuristics/even-heuristic)))
  
(tex/subsection
  "Simple divider heuristic : greedy"
  (tex/par
    "\\todo{write it}"
    )
  (tex/cljcode
    (get-source 'heuristics/find-best-left)
    (get-source 'heuristics/greedy-heuristic)))

(tex/subsection
  "Simple divider heuristic : random"
  (tex/par
    "If a graph has many good decompositions, such that the probability of
    picking one at random is high, such as in random graphs, running this
    heuristic repeatedly and keeping the best decomposition found will yield
    esults quickly. The greedy one is generally faster and should be preferred
    for initial decomposition, and though this one can provide improvements, it
    is mostly obsoleted by more other randomized improvement heuristics on top
    of greedy initial heuristic.") 
;  (tex/cljcode
;    (get-source 'heuristics/greedy-heuristic))
  )

(examples
  (binding
    [dot/*tikz-scale* 0.3]
    (thesis/var-ex
      example-mgraph
      example-decomposition
      example-decomposition-greedy
      )))
