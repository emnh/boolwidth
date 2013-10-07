(ns
  clj.graph.graph
  (:use
    [clojure.test :only [deftest is]]
;    [clojure.contrib.def]
;    [clojure.pprint]
;    [clojure.contrib.trace]
;    [clojure.contrib.str-utils]
;    [clojure.walk]
;    [clojure.template]
    )
  (:require
    [clj.util.util :as util]
    [clj.graph.hood :as hood]
    [clj.graph.hoods :as hoods]
    [clj.common.core :as common]
    [clj.sets.set :as sets]
    [clj.sets.bitset :as bitset]
    [clojure.set :as cset]
    )
  (:import
    clojure.lang.Seqable
    interfaces.IGraph
    graph.Vertex
    graph.BiGraph
    io.DiskGraph
    clojure.lang.IPersistentVector
    clj.graph.hood.NeighborhoodSet
    )
  )

(util/enable-reflection-warnings)

; Conventions
; - protocols, types have capital first letter: Graph, MatrixGraph
; - protocols start with P
; - use the constructor functions, e.g. new-matrix-graph, not MatrixGraph. constructor directly

; ----- PROTOCOLS -----

(defprotocol
  PGraph
  (vertices [x] "Get vertex sequence")
  (neighbors [x vertex] "Get sequence of neighbors of vertex")
  )

(defn
  graph?
  [graph]
  (satisfies? PGraph graph)
  )

(defprotocol
  PGraphHoods
  (neighborhood [graph vertex] "Return neighborhood n of vertex, where n implements PSet, PSubset")
  (neighborhoods
    [graph vertices]
    "Return a set of neighborhoods N, where n is a member of N if and only if n
    is a neighborhood of a vertex v in vertices. N implements PSubsets")
  )

(defprotocol
  PCut
  (cut-graph [x] "Return the graph for which this is a cut")
  (left-vertices [x] "Return subset S of nodes to the left, where S implements PSet, PSubset")
  (right-vertices [x] "Return subset S of nodes to the right, where S implements PSet, PSubset")
  (left-hoods
    [x]
    "Return set of neighborhoods N of the nodes to the left, where N implements
    PSubsets, PSet")
  (right-hoods
    [x]
    "Return set of neighborhoods N of the nodes to the right, where N
    implements PSubsets, PSet")
  )

; TODO: use default mixin instead
(extend-protocol
  PGraph
  clj.graph.graph.PCut
  (vertices [x] (vertices (cut-graph x)))
  (neighbors [x vertex]
             (let
               [isleft (sets/contains? (left-vertices x) vertex)
                ]
               (sets/difference
                 (neighbors (cut-graph x) vertex)
                 (if isleft (left-vertices x) (right-vertices x))
                 )
               )
             )
  )

; ----- GENERAL GRAPH FUNCTIONS -----

(defn graph-new-neighborhood
  [graph vertex]
  (hood/new-neighborhood (vertices graph) (neighbors graph vertex))
  )

(defn graph-new-neighborhoods
  [graph vertices_]
  (let
    [universe (apply vector (vertices graph))
     hoods (set (map #(neighborhood graph %) vertices_))
     ]
    (hoods/new-neighborhoods universe hoods)
    )
  )

(defn edges-as-pairs
  "Return a sequence of all distinct edges (pair-set of nodes) in undirected
  graph.  If there is an edge from a to b then only [a b] or [b a] is listed,
  not both."
  [graph]
  (let
    [pair-with-neighbors
     (fn [node]
       (map
         (fn [neighbor] (set [node neighbor]))
         (neighbors graph node)
         )
       )
     ]
     (->>
       (vertices graph)
       (map pair-with-neighbors)
       (reduce into)
       (distinct)
       )
    )
  )

; ----- DEFAULT PROTOCOL IMPLEMENTATIONS -----


; ----- IMPLEMENTATIONS FOR BACKWARDS COMPATIBILITY -----

(extend-protocol
  PGraph
  IGraph
  (vertices [graph] (.vertices graph))
  (neighbors [graph vertex] (map
          #(.opposite graph vertex %1)
          (.incidentEdges graph vertex)
          ))
  )

(deftest
  graph-igraph-test
  )

(extend-protocol
  common/PIndexed
  Vertex
  (index
    [x collection]
    (let
      [idx (.id x)]
      (assert (number? idx))
      idx
      )
    )
  )

(deftest
  indexed-vertex
  (is (= (common/index (Vertex. "data" 2) nil) 2))
  )

; ----- IMPLEMENTATIONS -----
;
; ----- ADJACENCY MATRIX GRAPH -----

;(print (env "tabular" ["helo" "sdf"] :extra "lll"))
;(print (tabular ["helo" "sdf"] :extra "|lll"))

(defrecord
  MatrixGraph
  [
   ; vector of vertices (Indexed)
   _vertices
   ; vector of subsets containing neighbors of each vertex
   edge-matrix
   ]

  PGraph
  (vertices [x] _vertices)
  ; TODO: call seq on return value and fix bugs in callers who should call neighborhood
  (neighbors [x vertex] (nth edge-matrix (common/index vertex x)))

  ; TODO: implement more efficient GraphHoods for MatrixGraph, instead of using default
  )

; TODO: make default implementation mixin instead
(extend-protocol
  PGraphHoods
  Object ; Graph
  (neighborhood
    [x vertex]
    (graph-new-neighborhood x vertex)
    )
  (neighborhoods
    [x vertices]
    (graph-new-neighborhoods x vertices)
    )
  )

(defn
  #^MatrixGraph new-matrix-graph
  [graph]
  (let
    [vertices (apply vector (vertices graph))
     ct (count vertices)
     ordered-vertices
     (reduce #(assoc %1 (common/index %2 graph) %2) (apply vector (repeat ct nil)) vertices)
     edge-matrix (apply vector (map #(neighborhood graph %) ordered-vertices))
     ]
    ; check correct order of vertex neighborhoods
    (assert (every?
      (fn
        [vertex]
        (do
;          (println (nth edge-matrix (index vertex graph)))
;          (println (new-neighborhood graph vertex))
          (=
            (nth edge-matrix (common/index vertex graph))
            (neighborhood graph vertex)
            )
          )
        )
      vertices
      ))
    (MatrixGraph. vertices edge-matrix)
    )
  )

;(type MatrixGraph)
;(type clj.graph.graph.MatrixGraph)

(deftest
  new-matrix-graph-test
  (is
    (=
      (
       )
      )
    )
  )

; ----- ADJACENCY MATRIX CUT -----


; for use in MatrixCut to eliminate redundant code in left/right functions
; TODO: define macro that can define functions with access to private fields of records
(defn
  left-right-hoods
  [matrix-graph nodes limited-to]
  (let
    [
     hoods (neighborhoods matrix-graph nodes)
     universe (sets/subset-universe hoods)
     limited-hoods (map #(sets/intersection % limited-to) (sets/subsets hoods))
     hoods (hoods/new-neighborhoods universe limited-hoods)
     ]
    hoods))

(defrecord
  MatrixCut
  [
   ; MatrixGraph
   matrix-graph
   ; bitset
   _left-vertices
   ; bitset
   _right-vertices
   ]

  PCut
  (cut-graph [x] matrix-graph)
  (left-vertices [x] _left-vertices)
  (right-vertices [x] _right-vertices)
  (left-hoods [x] (left-right-hoods matrix-graph _left-vertices _right-vertices))
  (right-hoods [x] (left-right-hoods matrix-graph _right-vertices _left-vertices)))

(defn new-cut
  "takes mgraph, a MatrixGraph, and set of left vertices, left
   Returns a MatrixCut.
  "
  ([mgraph left]
   (let
     [universe (vertices mgraph)
      left-subset (hood/new-subset universe left)
      right-subset (sets/complement left-subset)]
     ; TODO: use neighborhood predicate, and support generic set operations
     (do
       (assert (hood/neighborhoodset? left-subset))
       (assert (hood/neighborhoodset? right-subset))
       (MatrixCut. mgraph left-subset right-subset)))))

; ----- GRAPHHOODS FUNCTIONS -----

; check equality function for PSet and regular clojure set

; TODO: test, debug
(defn vertex-neighborhood?
  [graph x]
  "Return true if x is a neighborhood of a single vertex v in the graph, that
  is, x is a subset S of the vertices of the graph where s is in S if and only
  if v is adjacent to s."
  (and
    (satisfies? PGraph graph)
    (satisfies? sets/PSubset x)
    (seq
      (map #(= (set (seq x)) (neighbors graph %)) (vertices graph)))))

; TODO: test, debug
(defn neighborhood?
  [graph x]
  "Return true if x is a neighborhood of a subset of vertices V in the graph,
  that is, x is a subset S of the vertices of the graph where s is in S if and
  only if there is a v in V adjacent to s."
  (and
    (satisfies? PGraph graph)
    (satisfies? sets/PSubset x)
    (let
      [all-hoods (map set (sets/subsets (neighborhoods graph (vertices graph))))]
      (some #(= % (set x)) all-hoods))))


; ----- TODO FIX FUNCTIONS -----

; TODO: depends on Vertex
; TODO: this better coincide with toString of Vertex for now
(defn
  node-label
  "Return node label intended for human consumption. Currently 0-indexed index
  as string."
  [graph node]
;  (str (inc (common/index node graph))))
  (str (common/index node graph)))
