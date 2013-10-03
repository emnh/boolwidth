(ns 
  clj.decomposition.impl
  (:refer-clojure :exclude [parents])
  (:require
    [clj.common.core :as common]
    [clj.util.util :as util]
    [clj.graph.graph :as graph]
    [clj.decomposition.decomposition :as dc]
    )
  )

(defrecord
  InternalNode
  [left right]
  )

(defrecord
  LeafBag
  [vertices]
  Object
  (toString
    [x]
    (str "Leaf: " vertices))
  )

(defrecord
  LeafNode
  [vertex]
  Object
  (toString
    [x]
    (str "Leaf: " vertex))
  )

(defn
  is-node?
  [node]
  (or (instance? InternalNode node) (instance? LeafNode node))
  )

(defn
  new-internal-node
  [left right]
  (do
    (assert (is-node? left))
    (assert (is-node? right))
    (InternalNode. left right)
    )
  )

(defn new-leaf-node
  [vertex]
  (LeafNode. vertex))

(defn dc-dfs
  [dc node]
  (lazy-seq
    (cons
      node
      (if
        (dc/is-internal? dc node)
        (lazy-cat
          (dc-dfs dc (dc/left dc node))
          (dc-dfs dc (dc/right dc node)))))))

(defn
  check-node
  [dc node]
  (let
    [
     is-leaf (dc/is-leaf? dc node)
     is-internal (dc/is-internal? dc node)
     ok (not= is-leaf is-internal) ; implements one, but not both protocols
     ; check children of internal node
     ok (if 
          (and ok is-internal)
          (and
            (check-node dc (dc/left dc node))
            (check-node dc (dc/right dc node)))
          ok)
     ]
    ok)
  )

(defn
  check-sub-decomposition?
  "Return true if dc is a sub-decomposition of graph vertices"
  [dc vertices]
  (and
    (satisfies? dc/PDecomposition dc)
    (satisfies? graph/PGraph (dc/graph dc))
    (= (set (graph/vertices dc)) (set vertices))
    )
  )

(defn
  check-decomposition?
  "Return true if dc is a boolean-decomposition of its graph"
  [dc]
  (and
    (check-node dc (dc/root dc))
    (check-sub-decomposition? dc (graph/vertices dc))
    )
  )

(defrecord
  Decomposition
  [_graph _root _parents]

  dc/PDecomposition
  (graph [x] _graph)
  (root [x] _root)
  (decomposition? [x] (check-decomposition? x))
  (children
    [x node]
    (if
      (dc/is-internal? x node)
      (seq [(dc/left x node) (dc/right x node)])))
  (parent
    [x node] 
    (assert (is-node? node))
    (get _parents node))
  (left 
    [x node] 
    (assert (dc/is-internal? x node))
    (.left node))
  (right
    [x node] 
    (assert (dc/is-internal? x node))
    (.right node))
  (leaf-vertex
    [x node]
    (assert (dc/is-leaf? x node))
    (.vertex node))
  (is-internal? 
    [x node] 
    (assert (is-node? node))
    (instance? InternalNode node))
  (is-leaf?
    [x node]
    (instance? LeafNode node))

  graph/PGraph
  (neighbors
    [x node]
    (if
      (dc/is-root? x node)
      (dc/children x node)
      (cons (dc/parent x node) (dc/children x node))))
  (vertices [x] (dc-dfs x _root))

  )

(defn
  new-decomposition 
  "Creates a new boolean decomposition, satisfying PDecomposition. The root
  must be a node created with new-decomposition-node."
  [graph root]
  (graph/graph? graph)
  (assert (is-node? root))
  (let
    ; TODO: fix hackishness
    [dc (Decomposition. graph root nil) ; no parents
     ; assumes dc-dfs implementation doesn't use parents
     internal-nodes
     (filter #(dc/is-internal? dc %) (graph/vertices dc))
     parents_ 
     (reduce
       (fn
         [map_ node]
         (into map_ (hash-map (dc/left dc node) node (dc/right dc node) node)))
       {}
       internal-nodes)
     dc (Decomposition. graph root parents_) ; with parents
     ]
    dc
    )
  )

(defn new-decomposition-node
  "[vertex] Return leaf node containing vertex.
   [left right] Return internal node with left and right nodes.
  "
  ([vertex] (new-leaf-node vertex))
  ([left right] (new-internal-node left right))
  )

(defn to-std-format
  [dc]

  )
