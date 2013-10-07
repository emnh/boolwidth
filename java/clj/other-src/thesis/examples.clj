(ns
  clj.thesis.examples
  (:use 
    [clojure.pprint]
    )
  (:require 
    [clj.sets.set :as sets]
    [clj.graph.hood :as hood]
    [clj.graph.graph :as graph]
    [clj.decomposition.decomposition :as dc]
    [clj.boolwidth.cutbool :as cb]
    [clj.viz.graphviz :as dot]
    [clj.common.core :as common]
    [clj.boolwidth.heuristics :as heuristics]
    [clj.tex.tex :as tex]
    [clj.tex.graph :as texgraph]
    )
  (:import
    control.ControlUtil
    )
  )

; ========================== SET ==========================
(def example-set-universe (set (range 10)))

(defn new-set
  [set_]
  (hood/new-subset example-set-universe set_)
  )

(def empty-set (new-set #{}))
(def example-set (new-set #{1 2 7}))
(def example-set-2 (new-set #{3 6 7 9}))

; ========================== GRAPH ==========================

(def example-graph-filename "/home/emh/bw/graphLib_ours/hsugrid/hsu-3x3.dimacs")
(def example-graph (ControlUtil/getTestGraph example-graph-filename))
(def example-mgraph 
  (assoc
    (common/named
      (graph/new-matrix-graph example-graph)
      "hsu-3x3"
      )
    :layout
    "neato"
    )
  )
; get vertex with id 4
(def
  example-mgraph-vertex
  (first
    (filter
      #(= (common/index % example-mgraph) 4) (graph/vertices example-mgraph)))
  )

; ========================== CUT ==========================

(let
  [v (graph/vertices example-mgraph)
   left (map #(nth v (dec %)) [2 3 6 9])
   ]
;  (println left)
  (def example-cut (graph/new-cut example-mgraph left))
;  (println "vertices" v)
;  (println "left" (graph/left-vertices example-cut))
;  (println "left-hoods" (graph/left-hoods example-cut))
;  (println "right" (graph/right-vertices example-cut))
;  (println (map #(vector % (neighbors example-cut %)) v))
  )


; ========================== DECOMPOSITION ==========================

(def example-decomposition (heuristics/even-heuristic example-mgraph))
(def example-decomposition-greedy (heuristics/greedy-heuristic example-mgraph))

;(pprint example-decomposition-greedy)

;(pprint (dc/root decomposition))
;(pprint 
;  (graph/vertices decomposition)
;  )

(let
  [
   universe (graph/vertices example-mgraph)
   left-seq (map #(nth universe %) [0 4 6 7])
   left (hood/new-subset universe (set left-seq))
   right (sets/complement left) ;(hood/new-subset universe universe)
   ]
;  (pprint (heuristics/find-best-left example-mgraph left right))
  )

;(dot/to-dot decomposition)

;(cutbool/all-neighborhoods example-cut)
;(println
;  (tex/to-tex
;    (texgraph/new-tex-node-subsets
;      example-mgraph 
;      (cb/counting-seq-of-neighborhoods example-cut))))
;(pprint 
;  (seq (graph/left-hoods example-cut)))
;(pprint 
;  (sets/subset-universe (graph/left-hoods example-cut)))

;(pprint
;  (cb/counting-seq-of-neighborhoods example-cut))


(cb/count-neighborhoods example-cut)

;(pprint (count (edgepairs example-mgraph)))
;(println (dot/to-dot example-mgraph))
;
;(println (to-tex example-cut :adjlist false))

