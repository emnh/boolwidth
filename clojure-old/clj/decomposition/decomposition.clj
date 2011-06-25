(ns 
  clj.decomposition.decomposition
  (:refer-clojure :exclude [parents])
  (:require
    [clj.common.core :as common]
    [clj.util.util :as util]
    [clj.graph.graph :as graph]
    [clj.sets.set :as sets]
    )
  )

(def std-format-doc
  "The standard interchange format for a decomposition is defined using
  structural recursion as follows:
  Base case, leaf node: (<integer index of vertex in graph>)
  Inductive case, internal node: (<left node><right node>)
  Whitespace is insignificant.
  "
  )

(def std-format-example
  '(
    (
     (
      (1)
      (2)
      )
     (
      (3)
      (4)
      )
     )
    )
  )

(defprotocol
  PDecomposition
  (root [x] "Return root node")
  (graph [x] "Return decomposition graph")
  (decomposition? [x] "Return true if x is a boolean-decomposition of its graph")
  (cuts [x] "Return sequence of all cuts in decomposition in unspecified order.")
  (canonical-cuts
    [x]
    "Return lexicographically ordered sequence of canonical cuts (PCut) in
    decomposition. The lexicographic ordering is on the sorted list of vertices
    on the left side of the canonical cut. ")
  (parent [x node] "Return parent of node")
  (children [x node] "Return left and right child of node")
  (left [x node] "Return left child of node")
  (right [x node] "Return right child of node")
  (cut [x node] "Return graph vertices in leaves of this subdecomposition
                resulting from removing the edge to parent, or all vertices of
                this is the root.")
  (leaf-vertex [x node] "Return the graph vertex in a leaf")
  (is-internal? [x node] "Return true if x is an internal node")
  (is-leaf? [x node] "Return true if x is a leaf")
;  (to-std-format [x] "Return decomposition as standard format")
;  (from-std-format [x] "Return decomposition from standard format")
  )

(defprotocol
  PDecompositionBuilder
  (move-left
    [x node vertex]
    "Return new decomposition where vertex has been moved from right side of
    split to left side of split")
  (move-right
    [x node vertex]
    "Return new decomposition where vertex has been moved from left side of
    split to right side of split")
  )

(defn
  is-root?
  "Return true if node is the root"
  [x node]
  (= (root x) node))

;(defprotocol
;  PLeaf
;  (leaf-vertex [x] "Return vertex of this leafnode")
;  )

