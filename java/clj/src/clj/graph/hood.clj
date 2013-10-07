(ns
  clj.graph.hood
  (:refer-clojure :exclude [empty? contains? count complement conj])
  (:use
    [clojure.test :only (deftest is)]
    )
  (:require
    [clojure.set :as cljset]
    [clj.sets.set :as sets]
    )
  (:import
    clojure.lang.Seqable
    )
  )

(defprotocol
  PNeighborhoodSetPrivate
  (neighborhoodset-universe [x])
  (neighborhoodset-hood [x])
  (set-bin-op [x f y])
  (bin-op [x f y]))

(defn neighborhoodset? [x] (satisfies? PNeighborhoodSetPrivate x))

(deftype
  NeighborhoodSet
  [_universe hood]
  PNeighborhoodSetPrivate
  (neighborhoodset-universe [x] _universe)
  (neighborhoodset-hood [x] hood)
  (set-bin-op [x f y]
;          (if (not (instance? NeighborhoodSet y)) (println "Y T:"  (type y) " : " y))
          (assert (neighborhoodset? y))
          (NeighborhoodSet. _universe (f hood (neighborhoodset-hood y))))
  (bin-op [x f y]
          (NeighborhoodSet. _universe (f hood y)))

  Seqable
  (seq [x] (seq hood))

  sets/PSet
  (union [x y] (set-bin-op x cljset/union y))
  (intersection [x y] (set-bin-op x cljset/intersection y))
  (difference [x y] (set-bin-op x cljset/difference y))
  (subset? [x y] (set-bin-op x cljset/subset? y))
  (superset? [x y] (set-bin-op x cljset/superset? y))
  (contains? [x y] (clojure.core/contains? hood y))
  (count [x] (clojure.core/count hood))
  (empty? [x] (clojure.core/empty? hood))
  (to-clojure-set [x] hood)
  (conj [x y] (bin-op x clojure.core/conj y))
  (disj [x y] (bin-op x clojure.core/disj y))

  sets/PSubset
  (universe [x] _universe)
  (complement [x] (NeighborhoodSet. _universe (cljset/difference _universe hood)))

  java.lang.Object
  (toString [x] (str "N:" "uhash:" (apply str (map #(.id %) _universe)) " hood:" hood))
  (hashCode [x]
            (hash-combine (.hashCode _universe) (.hashCode hood))
            )
  (equals [x y] (and
                  (= _universe (neighborhoodset-universe y))
                  (= hood (neighborhoodset-hood y))
                  )
          )
  )

(defn new-neighborhood-set
  [universe hood]
  (NeighborhoodSet. (set universe) (set hood))
  )

(deftest neighborhoodset-test
  (let 
    [
     a (new-neighborhood-set #{1 2 3})
     b (new-neighborhood-set #{2 5 6})
    ]
      (is (= (sets/union a b)) (new-neighborhood-set #{1 2 3 5 6}))
    )
  )

; decides which implementation of Neighborhood to be used globally
(def new-neighborhood new-neighborhood-set)
; alias. at least for now
(def new-subset new-neighborhood-set)

(defn
  neighborhood-type?
  [x]
  (sets/subset-type? x))
