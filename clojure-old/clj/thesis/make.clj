(ns clj.thesis.make
  (:require
    [clj.thesis.thesis :as thesis]
    [clojure.contrib.shell-out :as shell]
    ))

(defn background
  []
  (with-out-str (load "background"))
  )

(defn heuristics
  []
  (with-out-str (load "heuristics"))
  )

(defn htext
  []
  (with-out-str (load "htext"))
  )

;(thesis/spit "background.tex" (background))
;(thesis/spit "heuristics.tex" (heuristics))
(thesis/spit "htext.tex" (htext))
