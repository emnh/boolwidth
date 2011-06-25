(ns clj.viz.graphviz
  (:use 
    [clojure.contrib.duck-streams]
    [clj.graph.graph]
    [clj.boolwidth.cutbool]
    [clj.sets.bitset]
    )
  (:import
    io.DiskGraph
    control.ControlUtil
    )
  )


(defn qstr
  [s]
  (str \" s \")
  )

(defn to-dot-attr
  [[key value]]
  (println (name key) "=" (qstr value))
  )

(defn to-dot-attrs
  [attrs]
  (if attrs
    (do
      (println \[)
      (dorun (map to-dot-attr attrs))
      (print \])
      )
    )
  )

(defn to-dot-node
  [[id attrs]]
  (print id "")
  (to-dot-attrs attrs)
  (println \;)
  )

(defn to-dot-nodes
  [g]
  (dorun (map to-dot-node (:nodes g)))
  )

(defn to-dot-edge
  [g [[node1 node2] attrs]]
  (let [arrow (if (= (:type g) :digraph) "->" "--")]
    (print node1 arrow node2)
    (to-dot-attrs attrs)
    (println \;)
    )
  )

(defn to-dot-edges
  [g]
  (dorun (map (partial to-dot-edge g) (:edges g)))
  )

(defn to-dot-graph-attrs
  [g]
  (if
    (:graph g)
    (do
      (print "graph ")
      (to-dot-attrs (:graph g))
      (println \;)
      )
    )
  )

(defn to-dot
  (
   [g]
   (do
     (print (name (:type g)))
     (if (:name g) (print "" (qstr (:name g)) ""))
     (println \{)
     (binding [print #(print "oh hai " %)]
       (print "helo")
       (to-dot-graph-attrs g)
       (to-dot-nodes g)
       (to-dot-edges g)
       )
     (println \})
     )
   )
  (
   [filename g]
   (with-out-writer filename (to-dot g))
   )
  )

(defn atest
  []
  (let 
    [testgraph 
     { 
      :type :digraph 
      :name "g"
      :graph { :splines true }
      :node { :shape "record" :pin true }

      :nodes
      {
       "node0" { :label "a" :pos "0,1" }
       "node1" { :label "b" :pos "0,1" }
       "node2" { :label "c" :pos "0,1" }
       }

      :edges
      {
       ["node0" "node1"] nil
       ["node1" "node2"] nil
       ["node2" "node0"] nil
       }
      }
     ]
;    (to-dot "test.dot" testgraph)
    (to-dot testgraph)
    )
  )

(defn
  ctest
  []
  (let
    [
     graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib_ours/hsugrid/hsu-4x4.dimacs")
     mgraph (to-matrix-graph graph)
     v (.vertices mgraph)
     subsets (take 
               20
               (random-combinations v (int (/ (count v) 2)))
;              (random-combinations v (int (/ (count v) 5)))
;              (random-subsets v)
               )
     cuts (doall (map #(to-cut mgraph %) subsets))
     bigraphs (doall (map #(to-bigraph graph %) subsets))
     cut (first cuts)
     bigraph (first bigraphs)
     ]
    
    )
  )

(atest)
