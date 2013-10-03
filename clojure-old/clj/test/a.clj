(ns test.a
  (:require
    [clj.util.util :as util]
    )
  (:gen-class)
  )
(util/enable-reflection-warnings)

(defn vrange [n]
    (loop [i 0 v []]
          (if (< i n)
                  (recur (inc i) (conj v i))
                  v)))
 
(defn vrange2 [n]
    (loop [i 0 v (transient [])]
          (if (< i n)
                  (recur (inc i) (conj! v i))
                  (persistent! v))))
 
(defn vrange3 [n]
    (loop [i 0 v (int-array n)]
          (if (< i n)
            (do
              (aset v i i)
              (recur (inc i) v)
              )
            v)))

(defn vrange4 [n]
  (doall (range n)))
 
;(map print (vrange3 10))

(defn -main
  []
  (time (def v  (vrange  1000000)))
  (time (def v2 (vrange2 1000000)))
  (time (def v3 (vrange3 1000000)))
  (time (def v4 (vrange4 1000000)))

  (println "")

  (time (def v  (vrange  1000000)))
  (time (def v2 (vrange2 1000000)))
  (time (def v3 (vrange3 1000000)))
  (time (def v4 (vrange4 1000000)))

  )

(-main)
