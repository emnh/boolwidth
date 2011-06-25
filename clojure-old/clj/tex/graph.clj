(ns clj.tex.graph
  (:use
    [clj.tex.tex]
    [clojure.contrib.def :only [defnk]]
    [clj.util.util :only (sprintf)]
    )
  (:require
    [clj.graph.graph :as graph]
    [clj.viz.graphviz :as dot]
    [clj.common.core :as common]
    [clj.util.util :as util]
    [clj.sets.set :as sets]
    [clojure.contrib.string :as cstr]
;    [clojure.contrib.str-utils2 :as cstr2]
   )
  (:import
    [clj.graph.hood NeighborhoodSet]
    [clj.graph.graph MatrixCut MatrixGraph]
    [clj.decomposition.impl Decomposition]
    )
  )

(defrecord
  TexNodeSubsets
  [graph subsets]

  ; TODO: remove redundancy between the cut tex functions
  ; TODO: check if node-label is ok to use for cut. assumes it is indexed in cut
  PTex
  (to-tex-internal
    [x options]
    ;adjlist true prints adjacency lists, else adjacency matrix
    (let
      [
       adjlist (:adjlist options true)
       adjlist true
       totalset (reduce into #{} subsets)
       vertices totalset ;(graph/vertices x)
  ;     left-vertices (graph/left-vertices x)
       col-ct (count vertices)
       fmt (str "|" (clojure.contrib.string/repeat col-ct "l") "|")
       node-label-f #(graph/node-label graph %)
       bitmap (fn 
                [hood]
                (map 
                  #(if (sets/contains? hood %) 1 0)
                  vertices))
       listmap (fn
                 [hood] 
                 (map 
                   #(if
                      (sets/contains? hood %)
                      (node-label-f %)
                      " ")
                   vertices))
       themap (if adjlist listmap bitmap)
  ;     left-nodenames (map node-label-f (seq left-vertices))
       right-nodenames (map node-label-f (seq vertices))
       row #(apply tabular-row %)
       header (sprintf 
                "\\multicolumn{%d}{|c|}{Right $\\rightarrow$ } \\\\"
                col-ct)
       colheaders (row right-nodenames)
       row-content-f (fn [subset] (str (row (themap subset)) "\n"))
       rows (map row-content-f subsets)
       ]
      (tabular
        ["\\hline\n"
         [header "\n\\hline\n"]
         (if 
           (not adjlist)
           [colheaders "\n\\hline\n"]
           )
         rows
         "\\hline"
         ]
        :format fmt)))
  )

(defn
  new-tex-node-subsets
  [graph subsets]
  (TexNodeSubsets. graph subsets)
  )


; TODO: check if node-label is ok to use for cut. assumes it is indexed in cut
(defn cut-to-tex
  [x options] 
  (let
    [
     adjlist (:adjlist options) ; true if adjacency list, false if adjacency matrix
     vertices_ (graph/vertices x)
     left-vertices_ (graph/left-vertices x)
     right-vertices_ (graph/right-vertices x)
     col-vertices right-vertices_
     col-ct (sets/count col-vertices)
     graph (graph/cut-graph x)
     fmt (str "|l|" (clojure.contrib.string/repeat col-ct "l") "|")
     node-label-f #(graph/node-label graph %)
     bitmap (fn 
              [hood]
              (map 
                #(if (sets/contains? hood %) 1 0)
                col-vertices))
     listmap (fn
               [hood] 
               (map 
                 #(if
                    (sets/contains? hood %)
                    (node-label-f %)
                    " ")
                 col-vertices))
     themap (if adjlist listmap bitmap)
     left-nodenames (map node-label-f (seq left-vertices_))
     right-nodenames (map node-label-f (seq right-vertices_))
     row #(apply tabular-row %)
     left-cell "Left $\\downarrow$"
     header (sprintf 
              "%s & \\multicolumn{%d}{|c|}{Right $\\rightarrow$ } \\\\"
              (if adjlist left-cell "")
              col-ct)
     colheaders (row (cons left-cell right-nodenames))
     row-content-f (fn [vertex] (str
                                  (row
                                    (cons
                                      (node-label-f vertex)
                                      (themap (graph/neighborhood x vertex)))) 
                                  "\n")
                     )
     rows (map row-content-f left-vertices_)
     ]
    (tabular
      ["\\hline\n"
       [header "\n\\hline\n"]
       (if 
         (not adjlist)
         [colheaders "\n\\hline\n"]
         )
       rows
       "\\hline"
       ]
      :format fmt)
    )
  )

(defn graph-to-tex
  [x options] 
  (let
    [vertices_ (graph/vertices x)
     ct (count vertices_)
     fmt (str "|l" (clojure.contrib.string/repeat ct "l") "|")
     bitmap (fn
              [hood]
              (map 
                #(if (sets/contains? hood %) 1 0)
                vertices_))
     listmap (fn
               [hood] 
               (map 
                 #(if (sets/contains? hood %) 
                    (graph/node-label x %)
                    " ")
                 vertices_))
     themap (if (:adjlist options) listmap bitmap)
     nodelabels (map #(graph/node-label x %) vertices_)
     row #(apply tabular-row %) ;#(str (cstr/join " & " %) " \\\\")
     colheaders (row (cons " " nodelabels))
     rows (map #(str (row (cons %2 (themap (graph/neighborhood x %1)))) "\n") vertices_ nodelabels)
     ]
    (tabular
      ["\\hline\n"
       (if 
         (not (:adjlist options))
         [
         colheaders 
         "\n\\hline\n"
          ]
         )
       rows
       "\\hline"
       ]
      :format fmt)
    )
  )

; incomplete
(defn tex-escape
  [s]
  ; see http://www.personal.ceu.hu/tex/specchar.htm for char list
  (cstr/replace-by
    #"[#$%&~_^\\{}]"
    (fn [groups] (str "\\\\" (first groups)))
    s)
  )

;(tex-escape (str #{1 2 3}))

(defn neighborhood-to-tex-as-set
  [x options]
  ; TODO: cljcode-w
  (cljcode (str (sets/to-clojure-set x)))
  )

(defn neighborhood-to-tex-as-subset
  [x options]
  (let
    [texset #(tex-escape (sprintf "#{%s}" (cstr/join "," %)))]
    (sprintf 
      "$\\frac{%s}{%s}$"
      (texset (sets/to-clojure-set x))
      (texset (sets/universe x))
      )
    )
  )

(def neighborhood-to-tex neighborhood-to-tex-as-subset)

;(defmethod 
;  to-tex
;  ::subsets
;  [coll & options]
;  (let
;    [clj-sets (map sets/to-clojure-set coll)
;     totalset (reduce into #{} clj-sets)
;     ]
;    (subsets-to-tex totalset coll)
;    )
;  )

(extend-protocol
  PTex

  MatrixCut
  (to-tex-internal [x options] (cut-to-tex x options))

  MatrixGraph
  (to-tex-internal [x options] (graph-to-tex x options))

  NeighborhoodSet
  (to-tex-internal [x options] (neighborhood-to-tex x options))
  )

(defn
  to-gviz-tex-fig-str
  [graphfilebasename graph & options]
  (let
    [
     dotfname (str graphfilebasename ".dot")
     _ (spit dotfname (dot/to-dot graph))
     texfname (apply dot/dot-to-tex dotfname options)
     tex-fig-str (slurp texfname)
     ]
    tex-fig-str
    )
  )

(defn
  to-gviz-pdf
  [graphfilebasename graph & options]
  (let
    [dotfname (str graphfilebasename ".dot")]
    (spit dotfname (dot/graph-to-dot graph))
    (apply dot/dot-to-pdf dotfname options)
    )
  )

