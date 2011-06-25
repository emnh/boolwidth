(ns
  clj.graph.hoods
  (:refer-clojure :exclude [empty? contains? count complement])
  (:use
    [clojure.test :only (deftest is)]
    )
  (:require
    [clj.graph.hood :as hood]
    [clj.sets.set :as sets]
    )
  (:import
    clojure.lang.Seqable
    )
  )

(deftype
  Neighborhoods
  [universe hoods]

  Seqable
  (seq [x] (seq hoods))

  sets/PSubsets
  (subset-universe [x] (.universe x))
  (subsets [x] (.hoods x))
  (subsets [x newhoods] (Neighborhoods. universe (set newhoods)))
  (empty-subset [x] (hood/new-neighborhood universe #{})) 

  sets/PSet
  (union 
    [x y]
    (let
      [xhoods hoods
       yhoods (sets/subsets y)]
      (assert (set? xhoods))
      (assert (set? yhoods))
      (Neighborhoods. universe (clojure.set/union xhoods yhoods))
      )
    )
  (intersection [x y] 
                (Neighborhoods. 
                  universe 
                  (clojure.set/intersection hoods (sets/subsets y))))
  (difference [x y] 
              (Neighborhoods. 
                universe 
                (clojure.set/difference hoods (sets/subsets y))))
  (contains? [x y] (clojure.core/contains? hoods y))
  (count [x] (clojure.core/count hoods))
  (empty? [x] (clojure.core/empty? hoods))
  (to-clojure-set [x] hoods)


;  sets/Set
;  (union 
;    [x y]
;    (do
;      (assert (= universe (sets/subset-universe y)))
;      (Neighborhoods. 
;        universe
;        (clojure.set/union hoods (sets/subsets y)))
;      )
;    )
;  (count [x] (clojure.core/count x))
  )

(defn new-neighborhoods
  [universe hoods]
  (Neighborhoods. universe hoods)
  )
