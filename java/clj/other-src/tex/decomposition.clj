(ns clj.tex.graph
  (:require
    [clj.graph.graph :as graph]
    [clj.graph.decomposition :as dc]
    )
  )

;(defn decomposition-to-tex
;  [decomposition]
;  (assert (dc/decomposition? decomposition))
;  (let
;    [vertices (graph/vertices decomposition)]
;    (map-indexed vertices)
;    )
;  )
