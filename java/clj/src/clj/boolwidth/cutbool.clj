(ns clj.boolwidth.cutbool
  (:use 
;    [clj.graph.create]
    [clojure.pprint]
    [clojure.test :only (deftest is)]
;    [clojure.contrib.trace]
    )
  (:require 
    [clojure.contrib.greatest-least :as g-least]
    [clj.graph.hood :as hood]
    [clj.graph.hoods :as hoods]
    [clj.graph.graph :as graph]
    [clj.decomposition.decomposition :as dc]
    [clj.decomposition.split :as split]
    [clojure.set]
    [clj.util.util :as util]
    [clj.sets.set :as sets]
    )
  (:import 
    [clj.graph.hoods Neighborhoods]
    [clj.graph.graph MatrixCut]
    )
  )

(util/enable-reflection-warnings)

;(defmulti neighborhood-union
;  "Compute union of two neighborhoods (subsets)"
;  (fn [a b] (class a))
;  )

;(defmethod neighborhood-union
;  bitset
;  [a b]
;  (clj.sets.bitset/union a b)
;  )

(defprotocol 
  PListNeighborhoodsForCounting
  (counting-seq-of-neighborhoods 
    [x] "Return a lazy sequence s such that (count s) returns the same value as
        (count-neighborhoods x). This function may be used for listing
        neighborhoods for the purposes of counting them, and supports a
        step-by-step iteration of this counting algorithm controlled by the
        outer algorithm. One example of such usage is early termination when it
        has been determined that the count is too large for the cut to be
        interesting. Another example is running the listing algorithm on
        multiple cuts, call them cut-coll, simultaneously and terminating as soon
        as the first listing algorithm terminates, in order to find the cut
        with the fewest number of neighborhoods in time linear in the fewest
        number of neighborhoods, that is, O(min(map count-neighborhoods
        cuts)).")
  )

(defprotocol 
  PCountNeighborhoods
  (count-neighborhoods [x] "Count number of neighborhoods of all subsets in x,
                           where x implements PCut or PNeighborhoods or another
                           object for which the number of neighborhoods has
                           been defined.")

  )

(defn add-hood
  "Helper for neighborhoods. Compute union of newhood with all hoods in newhood
  and return union of hoods and that."
  [hoods addhood]
  (sets/union
    hoods
    (sets/subsets hoods (map #(sets/union addhood %) hoods))
    )
  )

(defn
  cutbool-list-src
  []
  (extend-protocol
    PListNeighborhoodsForCounting

    Neighborhoods
    (counting-seq-of-neighborhoods
      [initialhoods]
      ; TODO: make a note that this function is not actually lazy, but the same
      ; algorithm is used and the non-lazy version is simpler, so it's used for
      ; explanation
      (seq 
        (reduce
          add-hood
          (sets/subsets initialhoods [(sets/empty-subset initialhoods)])
          initialhoods)))

    MatrixCut
    (counting-seq-of-neighborhoods
      [cut]
      (let
        [
         lefts (graph/left-hoods cut)
         rights (graph/right-hoods cut)
         smallest (min-key sets/count lefts rights)
         ]
        ; using the smallest initial set of neighborhoods creates a smaller upper
        ; bound for the number of unions that must be computed and thus for the
        ; runtime of the algorithm. in practice it may be slower or not matter much.
        (counting-seq-of-neighborhoods smallest))))
  )
(cutbool-list-src)

(extend-protocol
  PCountNeighborhoods

  Neighborhoods
  (count-neighborhoods [x] (count (counting-seq-of-neighborhoods x)))

  MatrixCut
  (count-neighborhoods [x] (count (counting-seq-of-neighborhoods x)))
  )

(defn
  least-cuts
  "Return the cuts in <cuts> of least boolean-width. More strictly, return a
  subsequence C of cuts in <cuts> such that for every cut c in C and every cut
  d in <cuts>, (<= (count-neighborhoods c) (count-neighborhoods d))."
  [cuts]
  ; TODO: exploit laziness to make O(smallest count) instead of O(largest count)
  (let
    [counted-cuts
     (map 
       (fn [cut] (assoc cut :cutbool (count-neighborhoods cut)))
       cuts)]
    (apply g-least/all-least-by #(:cutbool %) counted-cuts)))

(defn
  least-splits
  "Return the splits in <splits> of least boolean-width. More strictly, return a
  subsequence C of splits in <splits> such that for every split c in C and every split
  d in <splits>, (<= (count-neighborhoods c) (count-neighborhoods d))."
  [splits]
  ; TODO: exploit laziness to make O(smallest count) instead of O(largest count)
  (let
    [counted-splits
     (map 
       (fn [split] (assoc 
                     split 
                     :splitbool
                     (min
                       (count-neighborhoods (split/left-cut split))
                       (count-neighborhoods (split/right-cut split)))))
       splits)]
    (apply g-least/all-least-by #(:splitbool %) counted-splits)))

;(move-benchmark)
;(cutbool-benchmark)
;(binding [*test-out* *out*]
;  (run-tests 'clj.boolwidth.cutbool)
;  )
