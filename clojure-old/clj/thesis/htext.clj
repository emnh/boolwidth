(ns 
  clj.thesis.htext
  (:use
    [clj.tex.tex :only 
     [section par v f def-tex-def def-tex-defn todo caption]]
    [clojure.pprint :only [pprint]]
    [clj.thesis.examples]
    [clj.thesis.thesis :only [definition functions examples implementation]]
    ; note that get-source doesn't work on definitions in the current file
    [clojure.contrib.repl-utils :only [get-source]]
    )
  (:require 
    [clojure.contrib.string :as cstr]
    [clojure.pprint :as pprint]
    [clj.tex.graph :as texgraph]
    [clj.util.util :as util]
    [clj.tex.tex :as tex]
    [clj.tex.algo :as ta]
    [clj.sets.set :as sets]
    [clj.thesis.thesis :as thesis]
    [clj.graph.graph :as graph]
    [clj.boolwidth.cutbool :as cb]
    [clj.decomposition.decomposition :as dc]
    [clj.decomposition.split :as split]
    [clj.viz.graphviz :as dot]
    ))


(tex/chapter "Heuristic text")

(def todef (todo "define"))

(defn 
  toref
  [text]
  (str (todo "ref") " " text)
  )

;(pprint
;  (macroexpand-1
;    '(def-tex-cmd hyperref)))
;(hyperref :opts ["def-split"] "text")


;(pprint
;  (macroexpand-1
;    '(def-tex-def 
;      split 
;      "split"
;      "A split on a subset of vertices in a graph is a partition of the set of
;      vertices in 2 parts, denoted left and right.")))

(def-tex-def 
  split 
  "split"
  (str "A split on a subset of vertices " (v V) " in a graph is a partition of the set of
  vertices V in 2 parts, left and right, such that left and right are disjoint
  and their union equals V."))

(def-tex-def
  rooted-boolean-decomposition
  "rooted boolean decomposition"
  (str "A rooted boolean decomposition of a graph is a pair (T tree where each
       node has degree 1 or 3, except for the root, which has degree 2, and the
       leaf nodes are in bijection with the vertices of the graph."))

(let
  [v-G (v G)
   ]
  (def-tex-def
    partial-boolean-decomposition
    "partial boolean decomposition"
    (str "A partial boolean decomposition is ...")))
;    (str "A partial boolean decomposition of a graph " v-G " is a "
;         rooted-boolean-decomposition " where some of the leaf nodes may be "
;         uninitialized-dc-nodes ". Instead of the leaf nodes being in bijection
;                                with the set of vertices of the graph, the
;                                disjoint union of the set of vertex subsets
;                                associated with the leaf nodes is equal to the
;                                set of vertices of the graph.
;                                .")))

(def-tex-def
  uninitialized-dc-node
  "uninitialized decomposition node"
  (str "An uninitialized decomposition node is a leaf node which maps to a set
       of vertices that may be larger than one."))

(def-tex-def
  initialized-dc-node
  "initialized decomposition node"
  (str "An initialized decomposition node is either the root of degree 2, an
       internal decomposition node with a parent and a left and right child or a leaf node
       which maps to a vertex in the graph. Every internal initialized
       decomposition node is a " split ". Each initialized decomposition node also
                                       keeps a pointer to the best subtree "
       partial-boolean-decomposition " found for the vertices associated with
       this decomposition node, and this subtree is not necessarily the same as
                                     the one currently being investigated by
                                     the heuristic."))

(def-tex-def
  dc-node
  "decomposition node"
  (str "Either an " uninitialized-dc-node " or an " initialized-dc-node))

;(def-tex-def
;  unrooted-boolean-decomposition
;  "unrooted boolean decomposition"
;  "An unrooted boolean decomposition of a graph " (v G) " is a tree where each node has
;  degree 1 or 3 and the leaf nodes are in bijection with the vertices of the
;  graph. This is the definition used in \cite{boolwidth-of-graphs}.")

(def-tex-def
  main-pass
  "heuristic pass"
  "An iteration of the top level loop of the heuristic.")

(def-tex-def
  split-constraints
  "split size constraints"
  (str "A set of functions that ensure that the number of vertices on the left
       and right side of a split satisfy certain criteria. For the main results
       the split size constraints ensure that the number of vertices on each
       side is at least 1/3 (rounded up to the nearest integer) of the vertices
       in the split."))

(def-tex-def
  random-initializer
  "random initializer algorithm"
  (str (todo "define"))
  )

(def-tex-def
  flag-local-search ; the flag called greedy in the code
  "local-search flag"
  "If this is true, then local search is used, otherwise, when trying to
  improve a split the new candidate is initialized completely random rather
  than as a small difference from the current split as with local search. This
  is true for the main results.  Intuition from test runs says that local
  search is always better than re-randomization.")

(def-tex-def
  flag-init-greedy
  "greedy initialization flag"
  (str "A flag determining whether initialization of an " uninitialized-dc-node
       " is done by greedy-initializer or random-initializer. My
                                                                   intuition
       from test runs indicate that the greedy algorithm is always
       better. The flag is true for the main results."))

(def-tex-def
  config-search-steps
  "split improvement search steps" (str "The number of new split candidates
                                        generated within each " main-pass "
                                        before the algorithm gives up trying to
                                        improve a cut. My intuition from test
                                        runs indicate that 1 is generally the
                                                                          best
                                        value. It is set to 1 for the main results."
  ))

(def-tex-def
  current-bw-upper-bound
  "current upper bound on boolean-width of the graph"
  (str "A variable maintained during the heuristic that is the best current
       upper bound on the boolean-width of the graph. It is updated every time
       a better " rooted-boolean-decomposition " is found."))

(def-tex-def
  subtree-cache
  "the subtree cache"
  (str "A global cache mapping a subset of vertices to an " initialized-dc-node
       ", which is the root of a subtree of a " partial-boolean-decomposition
       ". This cache is maintained because we want to keep the work done on a
       subtree on a set of vertices in case we return to investigating it after
       having thrown away the subtree earlier."))

(def-tex-def
  flag-allow-all-below-current-bound
  "allow-all-below-current-bound flag"
  "If this is true, then all new candidate splits that are below the current
  bound are kept, if not, only improvements over the current split are kept.
  The flag is false for main results.")

(def-tex-def
  flag-candidates
  "use-list-of-subtree-candidates flag"
  (str "If this is true, then for each " initialized-dc-node "maintain a list
                                                             of subtrees of "
       partial-boolean-decompositions " as potential candidates for the best
                                      subtree, rather than just the one that is
                                      maintained if this flag is false. Each
                                      time this              "
       initialized-dc-node " is processed, the next candidate subtree is
                           selected in a round robin fashion, but only if the
                           top split in the subtree is below the "
       current-bw-upper-bound ", since we concentrate on hill-climbing down
                              below the bound with one candidate before
                              widening the search. The behavior introduced by
                              this parameter was introduced because it is hard
                              for the algorithm to improve a split (i.e. most
                              randomly generated local search candidates are
                              not better), and widening the search by having
                              more candidates to local search from was thought
                              to perhaps improve the situation . In practice it
                              turned out to worsen the performance, probably
                              because the search effort is diluted over many
                              subtrees rather than concentrated on one. This
                              parameter
       is false in the main results."))

(def-tex-def
  flag-all-candidates
  "process-all-active-candidates flag"
  (str "This flag only applies if " flag-candidates " is true. If this flag is
                                                    true, then each time an "
       initialized-dc-node " is processed, it tries to improve all subtrees in
                           the list of candidates, rather than just one at a
                           time in round robin fashion."))

(def-tex-def
  flag-greedy-exit-early
  "greedy-initializer-exits-early flag"
  (str
    "If this is true, then the greedy split initializer exits as soon as it has a viable candidate."
    (todo "elaborate")))

(def-tex-def
  greedy-initializer
  "greedy initializer algorithm"
  (str "If this is the root split or                             "
       flag-candidates " is true, start by putting all the graph vertices on the left
                       side of the split, if not, put randomly half on the left side and
                       half on the right side. This is done to minimize the
                       smallest side of the split. " (todo "explain candidates
                                                           part")
       "Let the current split be S, let the best split found be B."
       "In each iteration, for each vertex " (v v) "on the left side of S, compute
                                                   all the splits generated by
                                                   moving " (v v) " to the
                                                                  right side
                                                                  and choose
                                                                  the one with
                                                                  lowest
                                                                  boolean-width
                                                                  as new S.
       Continue this process until all nodes have been moved to the right side,
                                                                  except when "
       flag-greedy-exit-early " is true. After each iteration, if S satisfies "
       split-constraints " and is better than B, then set B to S. Also, if "
       flag-candidates " and S satisfies " split-constraints " and it is below"
       current-bw-upper-bound " then add S to list of active candidates for
                              this " dc-node ". At the end return B."))


(def-tex-def
  cut-comparator
  "cut comparator function"
  "A set of functions for comparing 2 cuts or 2 splits and determines which one
  has lower boolean-width. This may be replaced by an approximation function.
  Computed boolean-widths and lower bounds are cached.")

(def-tex-def
  graph
  "graph"
  "A graph is a pair $()$. A graph defined in this way is often called simple and
  undirected.")


;(let
;  [v-graph (v G)]
;  (def-tex-fun
;    vertices
;    "Vertices of a "
;    )
;  )

(def-tex-defn
  vertices
  [v-graph (v G)]
  (tex/ensuremath (str "V(" v-graph ")"))
  (str "The vertices of the " graph v-graph "."))

(def-tex-defn
  dc-root
  [dc (v D)]
  (str "root of " dc)
  (str "The root of the " (toref "decomposition tree") dc "."))

(def-tex-defn
  try-to-improve-subtree
  [root (v R)]
  (str "try to improve the subtree rooted at " root)
  todef)

(tex/section
  "Heuristic main loop"

  (let
    [
     v-graph (v G)
     v-bound (v B)
     v-dc (v WPT)
     ]
    (ta/algorithm
      (caption "Local search heuristic")
      (ta/Input (str "A graph " v-graph " and an upper bound on the boolean-width of
                                the graph " v-bound)) 
      (ta/Return (str "A boolean-decomposition of boolean-width lower than " v-bound
                 ", if found."))
      (ta/Begin
        (str
          (ta/s "Initialize " v-dc " to a single leaf node, the root, containing " 
            (vertices v-graph) ".")
          (ta/While 
            "predefined time limit or number of iterations not exceeded"
            (try-to-improve-subtree (dc-root v-dc)))))
      )
    )
  )

(tex/section
  "Improve subtree algorithm"
  try-to-improve-subtree-def
  )


; ============================== OBSOLETE (MINE FOR GEMS) ===========================

(tex/chapter
  "Obsolete text"
  )

(tex/section
  "Definitions"
  (par (todo "Merge new definition content with background chapter."))
  (par "A definition of a mathematical object will usually be written as a
       structural requirement, for example a decomposition is a tree satisfying
       certain conditions, together with a set of functions satisfying the
       requirements of their description, for example a decomposition has,
       among others, a function that returns the set of cuts on the
       decomposition. The reason for this is to reduce the amount of redundancy
       between mathematical definitions and programming interfaces, which are
       just two different styles of representing axioms. The structural
       requirement represents parts of the definitions that are not described
       by the other functions, and is equivalent to a predicate function that
       decides whether an object satisfies the structural part of a definition.
       The structural requirements are implemented as a predicate function
       only when they are used in algorithms or for error checking, while the
       other functions are always implemented and serve as the protocol /
       interface for records / classes. An example of a structural predicate is
       vertex-neighborhood?([G N]) which tests whether N is a neighborhood of a
       vertex in the graph neighborhood? is a binary predicate of a
       neighborhood and a graph. " (todo "Simplify and make this more uniform
                                         by always specifying the structural
                                         requirement as a predicate
                                                        function"))
  (par "The functions can be considered axioms related to a mathematical object
       definition: for example, x is a graph iff it satisfies the structural
       requirements and has a set of functions that when called on the graph
       (and potentially other arguments) return values satisfying the
       requirements documented in the protocol. The axiom set is not minimal,
       i.e. some of the functions may be defined in terms of other functions.
       " (todo "They probably should be as small as possible for definitions to
               be more easily digested, but should this be prioritized? Rectify
               this later by separating the set of functions that can
               obviously/easily be defined in terms of a more basic set."))
  main-pass-def
  split-def
  rooted-boolean-decomposition-def
  partial-boolean-decomposition-def
  uninitialized-dc-node-def
  initialized-dc-node-def
  dc-node-def
  split-constraints-def
  current-bw-upper-bound-def
  flag-candidates-def
  flag-all-candidates-def
  )

(let
  [v-graph (v G)
   v-decomp (v P)
   v-split (v S)
   ]
  (tex/section
    "The main loop"
    (par "The heuristic runs for a pre-defined length of time (this could
         easily be changed to user-interruptible so it is a true anytime
         algorithm by replacing the check against time with a check for
         interrupt) and then returns the best decomposition found. The input to
         the heuristic is a graph " v-graph " and optionally a "
         partial-boolean-decomposition " " v-decomp ". If no " partial-boolean-decomposition "
         is given, it is initialized with the root being an "
         uninitialized-dc-node "associated with the set of all the vertices of
                               the graph.  Each " main-pass "iterates over all
                                                            the " dc-nodes " in
                                                                           the
         " partial-boolean-decomposition ", including children of "
         initialized-dc-nodes " created by this " main-pass " except for
         subtrees omitted as described under processing conditions. A newly
                                                            created " dc-node "
         always starts out as an " uninitialized-dc-node ". After each "
         main-pass " the " current-bw-upper-bound " is updated if a better "
         rooted-boolean-decomposition " was found.")))

(tex/section
  "Processing conditions (controlling branching, checking bounds)" 

  (par "The conditions for initializing an " initialized-dc-node " is as following:"
       (todo "describe conditions"))

  (par "The conditions for trying to improve an " initialized-dc-node " is as following:"
       (todo "describe conditions"))
  )

(tex/section
  "Processing a decomposition node"
  subtree-cache-def
  config-search-steps-def
  (par "If the " dc-node " is an " uninitialized-dc-node ", it is initialized.
                                                         Then the algorithm
       spends " config-search-steps " trying to improve it using the algorithm
       described below. Each attempt at improving consists of generating a new
       candidate and deciding whether to keep it.")
  )

(tex/subsection
  "Initializing an uninitialized decomposition node"
  (par "The " uninitialized-dc-node " node is replaced by an "
       initialized-dc-node " according to the setting of " flag-init-greedy ". Then the left and right vertex subsets are looked up in the " subtree-cache " and initialized if found.")
  flag-init-greedy-def
  flag-greedy-exit-early-def
  greedy-initializer-def
  random-initializer-def
  )

(tex/subsection
  "Generating new split candidates"
  flag-local-search-def
  (par "The input to the split generator algorithm is N, an "
       initialized-dc-node ", C, a " cut-comparator " and D, the current depth
                                                    (distance from N to the root.")

  (par "If " flag-local-search " is true, then the following local search process is used.")
  (par "First the number of nodes to move from the left, n-left, is selected randomly
       and uniformly from the range 0 to the maximum number of nodes that can
       be moved without violating " split-constraints ", assuming no nodes are
                                                      moved from the right to
                                                      the left. The number of
                                                      nodes to move from the
                                                      right, n-right, is
                                                      selected similarly.")
  (par "Then a random subset of the specified sizes, n-left and n-right, are
       selected, respectively, from the left and right side of the split N, and
       then these nodes are moved to create a new split.")

  (par "If " flag-local-search " is false, then the following local search process is used.")
  (par "A random subset of the vertices in N are put on the left side and the other half on the right side, subject to the constraints of " split-constraints)
  )

(tex/subsection
  "Deciding whether to keep a new split candidate"
  cut-comparator-def
  flag-allow-all-below-current-bound-def
  (par "The input is new and old splits, let them be called new-split and
       old-split, and C, a " cut-comparator ". It returns true if we want to
                                            keep the new split candidate
                                            instead of the old.")
  (par "If " flag-local-search " is false, return true if the new split is lower than " current-bw-upper-bound)
  (par "If " flag-local-search " is true and "
       flag-allow-all-below-current-bound " is false then return true if the new split
                                          has lower boolean-width than the old
       split.")
  (par "If " flag-local-search " is true and "
       flag-allow-all-below-current-bound " is true, then... " (todo "decipher
                                                                     all the if
                                                                     clauses
                                                                     and
                                                                     describe.
                                                                     not really
                                                                     important
                                                                     though,
                                                                     since this
                                                                     flag is
                                                                     off for
                                                                     main
                                                                     results
                                                                     "))
  (todo "explain reasoning, impact of flags")
  )
