(ns 
  clj.decomposition.split

  (:require
    [clj.common.core :as common]
    [clj.util.util :as util]
    [clj.graph.hood :as hood]
    [clj.graph.graph :as graph]
    [clj.sets.set :as sets]
    )

  )

(defprotocol
  PSplit
  (split-vertices [x] "Return vertices split by this split")
  (left [x] "Return left vertex set")
  (right [x] "Return right vertex set")
  (left-cut [x] "Return left cut")
  (right-cut [x] "Return right cut"))

(defrecord
  Split [_graph _left _right]
  PSplit
  (split-vertices [x] (sets/union _left _right))
  (left [x] _left)
  (right [x] _right)
  (left-cut [x] (graph/new-cut _graph _left))
  (right-cut [x] (graph/new-cut _graph _right)))

(defn
  new-split
  [graph left right]
  (let
    ; convert to sets if necessary
    [universe (graph/vertices graph)
     left (if
            (sets/set? left)
            left
            (hood/new-subset universe left))
     right (if
            (sets/set? right)
            right
            (hood/new-subset universe right))]
    (Split. graph left right)))

