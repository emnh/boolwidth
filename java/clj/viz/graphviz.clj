(ns clj.viz.graphviz
  (:use 
;    [clojure.contrib.duck-streams]
;    [clj.boolwidth.cutbool]
    [clojure.pprint]
    )
  (:require
    [clj.util.util :as util]
    [clj.sets.bitset :as bitset]
    [clojure.contrib.def :as cdef]
    [clojure.contrib.string :as cstr]
    [clj.graph.graph :as graph]
    [clj.decomposition.decomposition :as dc]
    [clojure.contrib.shell-out :as shell]
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
  (let [arrow "--"]
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

(defn
  graph-to-dot 
  "Convert graph to dot format"
  ([graph] (graph-to-dot graph {}))
  (
   [graph options]
   (let
     [nodename (fn [node] (qstr (str node)))
      nodelabel (fn [node] (graph/node-label graph node))
      nodestr (fn 
                [node]
                (util/sprintf 
                  "%s [label=\"%s\"];\n"
                  (nodename node)
                  (nodelabel node)
                  )
                )
      edgestr (fn
                [node]
                (util/sprintf 
                  "%s -- %s;\n" 
                  (nodename (first node))
                  (nodename (second node))
                  )
                )
      ]
     (apply str
       (flatten
         ["graph {\n"
          "node [];\n"
          (map nodestr (graph/vertices graph))
          "edge [];\n"
          (map edgestr (graph/edges-as-pairs graph))
          "}\n"
          ]
         )))))

(defn
  decomposition-to-dot 
  "Convert decomposition to dot format"
  ([decomposition] (decomposition-to-dot decomposition {}))
  (
   [decomposition options]
   (let
     [nodename 
      (apply merge
        (map-indexed
          (fn [i v]
            (hash-map v (str "n" i)))
          (graph/vertices decomposition)))
;      (fn [node] (qstr (str node)))
      graph (dc/graph decomposition)
      nodelabel
      (fn
        [node]
        (if
          (dc/is-internal? decomposition node)
          ""
          (graph/node-label graph (dc/leaf-vertex decomposition node))
          )
        )
      nodestr (fn 
                [node]
                (util/sprintf 
                  "%s [label=\"%s\"];\n"
                  (nodename node)
                  (nodelabel node)
                  )
                )
      edgestr (fn
                [node]
                (util/sprintf 
                  "%s -- %s;\n" 
                  (nodename (first node))
                  (nodename (second node))
                  )
                )
      ]
     (apply str
       (flatten
         ["graph {\n"
          "node [];\n"
          (map nodestr (graph/vertices decomposition))
          "edge [];\n"
          (map edgestr (graph/edges-as-pairs decomposition))
          "}\n"
          ]
         )))))

(defn
  to-dot
  [x] 
  (cond
    (satisfies? dc/PDecomposition x) (decomposition-to-dot x)
    (satisfies? graph/PGraph x) (graph-to-dot x)
    true (throw
           (UnsupportedOperationException.
             (str "to-dot not supported for " (type x))))
    )
  )


(def dotprog "dot")
(def *tikz-scale* 0.5)

(cdef/defnk dot-to-tex
  [dotfname :prog dotprog :scale *tikz-scale*]
  (let
    [
     texfname (util/replace-ext dotfname "tex")
     texlogfname (util/replace-ext dotfname "tex.log")
     {:keys [exit out err]}
     (shell/sh
       "dot2tex"
       "--prog" prog
       "--graphstyle" (str "scale=" scale)
       "--figonly"
       "--usepdflatex"
       "--autosize"
       "-o" texfname
       dotfname
       :return-map true)
     success (not (cstr/substring? "ERROR" err))
     ]
    (spit texlogfname (str "STDOUT\n" out "STDERR\n" err))
    (if
      (not success)
      (do
        (println "Error dot2tex-ing: " dotfname)
        (println "For details, see " texlogfname)
        (throw (RuntimeException. "dot-to-tex compile error"))))
    texfname
    )
  )

(cdef/defnk tex-to-pdf
  [texfname]
  (let
    [
     pdffname (util/replace-ext texfname "pdf")
     pdfdir (.getParent (java.io.File.  pdffname))
     pdflogfname (util/replace-ext pdffname "pdf.log")
     {:keys [exit out err]}
     (shell/sh
       "pdflatex"
       texfname
       :dir pdfdir
       :return-map true)
     success (= 0 exit)
     ]
    (spit pdflogfname (str "STDOUT\n" out "STDERR\n" err))
    (if
      (not success)
      (do
        (println "Error pdflatexing: " pdffname)
        (println "For details, see " pdflogfname)
        (throw (RuntimeException. "tex-to-pdf compile error"))))
    pdffname
    )
  )

(defn
  dot-to-pdf
  [dotfname & options]
  (let
    [
     texfname (apply dot-to-tex dotfname options)
     pdfname (apply tex-to-pdf texfname options)
     ]
    pdfname
    )
  )

