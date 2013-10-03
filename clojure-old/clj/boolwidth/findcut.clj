(ns clj.boolwidth.findcut

  (:use
    [clojure.pprint]
    [clj.graph.graph]
    [clj.boolwidth.cutbool]
    [clj.sets.bitset]
    [clojure.contrib.trace]
    )
  (:require
    [clj.util.util :as util]
    )
  (:import
    graph.BiGraph
    java.io.File
    io.DiskGraph
    control.ControlUtil
    )
  )

(util/enable-reflection-warnings)
(util/enable-reflection-warnings)

; Compatible with clojure.contrib.graph
;(defrecord directed-graph
;  [nodes       ; The nodes of the graph, a collection
;  neighbors])  ; A function that, given a node returns a collection
               ; neighbor nodes.


(defn
  atest
  []
  (let
    [
     graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib/protein/1brf_graph.dimacs")
;     graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib_ours/hsugrid/hsu-4x4.dimacs")
     mgraph (new-matrix-graph graph)
     v (.vertices mgraph)
     subsets (take 
               20
               (random-combinations v (int (/ (count v) 2)))
;              (random-combinations v (int (/ (count v) 5)))
;              (random-subsets v)
               )
     cuts (doall (map #(new-cut mgraph %) subsets))
     bigraphs (doall (map #(new-bigraph graph %) subsets))
     cut (first cuts)
     bigraph (first bigraphs)
     ]
;    (pprint mgraph)
;    (println ["COUNT" (/ (count v) 2)])
;    (pprint (random-combination v (int (/ (count v) 2))))
;    (pprint
;      (count-neighborhoods
;        (new-cut mgraph (random-subset v))))
    
;    (pprint [:bigraph bigraph 
;             :left (.leftVertices bigraph) 
;             :hoods (get-hoods bigraph)])

;    (pprint [:cut cut
;             :hoods (get-hoods cut)
;             ])
    
    (time 
      (let [
            avgcutbool
            (apply avg (map #(double (boolwidth.CutBool/countNeighborhoods %)) bigraphs))]
        (trace "JAVA AVG" avgcutbool)
        )
      )

    (time 
      (trace "JAVA BIGRAPH" (apply min (map #(boolwidth.CutBool/countNeighborhoods %) bigraphs)))
      )

    (time 
      (trace "CLJ BIGRAPH" (apply min (map #(count (neighborhoods %)) bigraphs)))
      )

    (time 
      (trace "CLJ CUT" (apply min (map #(count (neighborhoods %)) cuts)))
      )

    (time
      (trace "JAVA LAZY"
      (let
        [neigborhood-seqs
         (map #(boolwidth.CutBool/neighborhoodIter %) bigraphs)
         ]
        (count (apply map (fn [& args] args) neigborhood-seqs))
        )
      )
      )

    (time
      (trace "CLJ LAZY"
      (let
        [neigborhood-seqs
         (map neighborhoods-lazy cuts)
         ]
        (count (apply map (fn [& args] args) neigborhood-seqs))
        )
      )
      )
    )
  )

(atest)
