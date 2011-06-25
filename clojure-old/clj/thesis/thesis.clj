(ns 
  clj.thesis.thesis
  (:refer-clojure :exclude [spit])
  (:use
    [clojure.contrib.def :only [defnk]]
    )
  (:require
    [clj.common.core :as common]
    [clj.viz.graphviz :as dot]
    [clj.util.util :as util]
    [clojure.contrib.def :as cdef]
    [clojure.contrib.str-utils :as str-utils]
    [clojure.contrib.string :as cstr]
    [clj.graph.graph :as graph]
    [clj.tex.tex :as tex]
    [clj.tex.graph :as texgraph]
    )
  (:import
    [clj.graph.graph MatrixCut MatrixGraph]
    [clj.graph.hood NeighborhoodSet]
    [clj.decomposition.impl Decomposition]
    )
  )

(defn- getenv
  [envname]
  (get (System/getenv) envname)
  )

(cdef/defvar- home (getenv "HOME"))

(cdef/defvar- thesis-dir
  (util/path-join
    home
    "thesis/tex"
    )
  )

(cdef/defvar- thesis-output-dir
  (util/path-join
    thesis-dir
    "chapters/gen"
    )
  )

(.mkdir (java.io.File. thesis-output-dir))

(defn spit
  [relative-filename & args]
  (let
    [fname (util/path-join thesis-output-dir relative-filename) 
     parent (.getParentFile (java.io.File. thesis-output-dir))]
    (.mkdir parent)
    (apply clojure.core/spit fname args)
    fname
    )
  )

(defn named-to-gviz-tex-str
  [graph & options]
  (let
    [graphfilebasename (util/path-join thesis-output-dir (common/name graph))]
    (binding
      [dot/dotprog (or (:layout graph) dot/dotprog)]
      (apply texgraph/to-gviz-tex-fig-str graphfilebasename graph options)
      )
    )
  )

(defn named-to-gviz-pdf
  [graph & options]
  (let
    [graphfilebasename (util/path-join thesis-output-dir (common/name graph))]
    (binding
      [dot/dotprog (or (:layout graph) dot/dotprog)]
      (apply texgraph/to-gviz-pdf graphfilebasename graph options)
      )
    )
  )

(defn relative-path
  [path]
  (cstr/replace-str (str thesis-dir "/") "" path)
  )

(extend-protocol
  tex/PTex
  MatrixGraph
  (to-tex-internal 
    [x options]
    (str
;      (tex/graphic (relative-path (named-to-gviz-pdf x)) :width "\\textwidth")
;      (tex/graphic (relative-path (named-to-gviz-pdf x)))
      (named-to-gviz-tex-str x)
      (texgraph/graph-to-tex x options)))
  
  Decomposition
  (to-tex-internal 
    [x options]
    (str
      (named-to-gviz-tex-str x)
      ))
;      (texgraph/graph-to-tex x options)))

  )

; ========================== EXAMPLE HELPERS ======================
(defnk tex-fun-ex
  [headers forms results :tail ""]
  (let 
    [
     header-row (apply tex/tabular-row headers)
     result-rows (map 
                   (fn
                     [form result]
                     (tex/tabular-row 
                       (tex/minipage
                         "7cm" 
                         (tex/cljcode-w form :width 35))
                       (tex/minipage
                         "7cm" 
                         (binding
                           [tex/*cljcode-right-margin* 35]
                           (tex/to-tex result)))))
                   forms results
                   )
     body [header-row "\\hline\n" result-rows tail]
     ]
    (tex/tabular body :format "ll")))

(defmacro
  fun-ex
  [& exprs]
  `(tex-fun-ex
     ["Expression" "Result"]
     '[~@exprs]
      [~@exprs]))

(defmacro
  var-ex
  [& exprs]
  `(tex-fun-ex
     ["Variable" "Value"]
     '[~@exprs]
      [~@exprs]))

; ========================== SECTION HELPERS ======================

(defn
  definition
  [& args]
  (apply tex/subsection "Definition" args)
  )

(defn
  functions
  [& args]
  (apply tex/subsection "Functions / protocols" args)
  )

(defn
  examples
  [& args]
  (apply tex/subsection "Examples" args)
  )

(defn
  implementation
  [& args]
  (apply tex/subsection "Implementation" args)
  )


