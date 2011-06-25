(ns clj.sets.set
  (:refer-clojure :exclude [set? empty? contains? count complement conj disj])
  )

(defprotocol
  PSet
  (union [x y] "Return a set that is the union of the input sets")
  (intersection [x y] "Return a set that is the intersection of the input sets")
  (difference [x y] "Return a set that is the first set without elements of the remaining sets")
  (empty? [x] "Returns true if the set is empty")
  (contains? [x y] "Returns true if set contains y")
  (count [x] "Return the number of items in the set")
  (to-clojure-set [x] "Return as clojure set")
  (subset? [x y] "Is x a subset of y?")
  (superset? [x y] "Is x a superset of y?")
  (conj [x y] "Return new set with member y added to set x")
  (disj [x y] "Return new set with member y removed from set x")
  )

(defprotocol
  PSubset
  (universe [x] "Return the universe that this Subset lives in")
  (complement [x] "Return the complement of a Subset relative to its universe")
  )

(defprotocol
  PSubsets
  "Set of subsets"
  (subset-universe [x] "Return universe that the subsets live in")
  (subsets [x] [x hoods] "With one arg, return subsets, with 2 return new
                         Subsets with subsets set to hoods")
  (empty-subset [x] "Return the empty Subset with same universe")
;  (apply-subsets [x f] "Return new Subsets with function applied to subsets")
;  (apply-subsets [x y f] "Return new Subsets with function applied to subsets of x and y")
  )

(defn
  set?
  "Return true if x is a set, that is, if x satisfies PSet"
  [x]
  (satisfies? PSet x)
  )

(defn
  subset-type?
  "Return true if x is a subset, that is, x is a set and x satisfies PSubset"
  [x]
  (and
    (set? x)
    (satisfies? PSubset x)
    )
  )
