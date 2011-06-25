(ns 
  clj.boolwidth.heuristics
  (:use
    [clojure.pprint]
    )
  (:require
    [clj.sets.set :as sets]
    [clj.graph.graph :as graph]
    [clj.boolwidth.cutbool :as cb]
    [clj.decomposition.decomposition :as dc]
    [clj.decomposition.impl :as dci]
    [clj.decomposition.split :as split]
    )
  )

(defprotocol
  SubDecompositionHeuristic
  (cancel [heuristic] "Cancel a running heuristic. A heuristic may run forever
                      if there is no better subdecomposition, so this method is
                      essential.")
  (improve-sub-decomposition
    [heuristic graph vertexsubset decomposition]
    "Return a subdecomposition of lower width or nil of none exists. May not
    terminate.")
  )

(defprotocol
  CutHeuristic
  (improve-cut
    [heuristic cut] 
    "Return a cut of lower width or nil of none exists. May not terminate."
    )
  )

(defrecord
  ConfigurableHeuristic
  [heuristic-function]
  )

(defn cut-local-heuristic
  "cut-heuristic-f is a function that takes...
  "
  ([cut-heuristic-f graph] 
   (let
     [root 
      (cut-local-heuristic cut-heuristic-f graph (graph/vertices graph))
      ]
     (dci/new-decomposition graph root)))
  ([cut-heuristic-f graph vertices]
   (if
     (<= (count vertices) 1)
     (dci/new-leaf-node (first vertices))
     (let
       [
        [left-subset right-subset] (cut-heuristic-f graph vertices)
        left-node (cut-local-heuristic cut-heuristic-f graph left-subset)
        right-node (cut-local-heuristic cut-heuristic-f graph right-subset)
        ]
       (dci/new-internal-node left-node right-node)
       )))
  )

(defn even-heuristic
  [graph]
  (let
    [cut-heuristic-f 
     (fn
       [graph vertices]
       (let
         [ct (count vertices)
          left (take (/ ct 2) vertices)
          right (drop (/ ct 2) vertices)]
         [left right]))
     decomp (cut-local-heuristic cut-heuristic-f graph)]
    (assert (dc/decomposition? decomp))
    decomp))

(defn find-best-left
  "Return the best left side that can be found by moving one vertex from the
  right to the left. left and right must be sets."
  [graph split]
  (let
    [split-candidates
     (map
      (fn [v]
        (split/new-split
          graph
          (sets/conj (split/left split) v)
          (sets/disj (split/right split) v)
          ))
      (split/right split))
     best (first (cb/least-splits split-candidates))
     ]
    best))

(defn greedy-heuristic
  "todo: function description"
  [graph]
  (let
    [
     cut-heuristic-f 
     (fn
       [graph vertices]
       (let
         [ct (count vertices)
          first-split (split/new-split graph #{} vertices)
          best-half-split 
          (nth 
            (iterate (fn [split] (find-best-left graph split)) first-split)
            (/ ct 2))
          left (seq (split/left best-half-split))
          right (seq (split/right best-half-split))
          ]
         [left right]))
     decomp (cut-local-heuristic cut-heuristic-f graph)]
    (assert (dc/decomposition? decomp))
    decomp))

;(defn greedy-heuristic
;  [graph]
;  ()
;  )
