(ns clj.main.test
  (:use
    [clojure.test]
    [clojure.pprint]
    [clj.boolwidth.cutbool]
    [clj.sets.bitset]
    )
  (:require
    [clj.util.util :as util]
    )
  (:import
    graph.BiGraph
    )
  )

;(run-tests 'clj.sets.bitset)

(binding [clojure.pprint/*print-pretty* true])
;(set! clojure.pprint/*print-pretty* true)
;(set! *print-pretty* true)
(util/enable-reflection-warnings)


