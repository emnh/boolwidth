(ns clj.graph.create
  (:use
    [clj.util.util]
  )
  )

(defn random-bigraph
  []
  (let 
    [
     n 20
     leftsize n
     rightsize n
     prob 0.1
     probedge #(> prob (rand 1))
     bg (graph.BiGraph. leftsize rightsize)
     left (map #(.insertLeft bg %1) (range leftsize))
     right (map #(.insertRight bg %1) (range rightsize))
     edges (doall (for
                    [a left b right]
                    (if (probedge) (.insertEdge bg a b nil))
                    ))
    ]
    (np [left right edges])
    (.insertEdge bg (first left) (first right) nil)
    bg
    )
  )

