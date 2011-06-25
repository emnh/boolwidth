(ns clj.sets.bitset
  (:use 
    [clj.util.util]
    [clojure.pprint]
    [clojure.test]
    [clojure.contrib.trace])
  (:require
    [clj.sets.set :as set]
    )
  (:import 
    java.util.Random
    clojure.lang.Seqable)
  )

(enable-reflection-warnings)

(defprotocol PRepresent
             "doc"
             (repr [abitset])
             )

;(defprotocol PSeqable
;             (seq [this])
;             )

;(defprotocol PObject
;             (toString [this])
;             )

(defn bitseq 
  "Returns sequence of bits for a number"
  [number]
;  (let [number (int number)]
    (map
      #(bit-and % 1)
      (take-while
        #(not= 0 %)
;        (iterate (fn [#^Number num] (bit-shift-right num 1)) number)))
        (iterate #(bit-shift-right #^Number % 1) number)))
;    )
  )

;(bitseq 239283473948729347293487329487729)

(with-test
  (defn allbits
    "Return a number with all 1-bits. The number of 1-bits is equal
    to the size of ground-set."
    [ground-set]
    (- (bit-shift-left 1 (count ground-set)) 1)
    )
  (is (= (allbits #{1 2 3}) 7))
  )

;(defmacro get-subset [x] `(.subset #^Bitset ~x))

(defprotocol
  PBitsetPrivate
  "Bitset private functions"
  (get-subset [x] "Return bitset")
  (get-ground-set [x] "Return ground-set")
  (validate [x y] "Validate input bitsets for compatibility with binary functions such as union")
  )


; ----- BITSET -----

(deftype
  Bitset 
  [
   #^clojure.lang.IPersistentCollection ground-set
   #^Number subset]

  PBitsetPrivate
  (get-subset [x] subset)
  (get-ground-set [x] ground-set)
  (validate
    [a b] 
    { :pre
     [
       (= (get-ground-set a) (get-ground-set b))
       (not= (get-subset a) nil)
       (not= (get-subset b) nil)
      ]
     }
    true
    )

  set/PSet

  (union
    [a b] 
    { :pre [(validate a b)] }
      (Bitset. (.ground-set a) (bit-or subset (get-subset b)))
    )
  
  (intersection
    [a b] 
    { :pre [(validate a b)] }
      (Bitset. (.ground-set a) (bit-and subset (get-subset b)))
    )

  (difference
    [a b] 
    { :pre [(validate a b)] }
      (Bitset. (.ground-set a) 
               (bit-xor
                 (.subset a)
                 (bit-and (.subset a) (get-subset b)))
               )
    )

  (empty? [x] (zero? (.subset x)))

  set/PSubset

  (universe [_] ground-set)

  (complement
    [x]
    (let [ground-set (.ground-set x)]
      (Bitset.
        ground-set
        (bit-xor (allbits ground-set) (.subset x))
        )
      )
    )

  Seqable
  (seq
   [this]
   (seq (keep
     (fn [[bit element]] (if (= bit 1) element nil))
     (map #(vector %1 %2) (bitseq subset) (reverse ground-set))
     ))
;            (reduce 
;              (fn
;                [acc element]
;                (conj (bit-shift-left acc 1) (if (contains? subset element) 1 0)))
;              (list)
;              ground-set)

   )

  PRepresent
  (repr
    [this]
     {
      :ground-set ground-set
      :subset (java.lang.Integer/toBinaryString subset)
;               :ground-set (.ground-set this)
;               :subset (java.lang.Integer/toBinaryString (.subset this))
      }
    )

  Comparable

  (compareTo
    [b1 b2]
    ((fn [#^Bitset b1 #^Bitset b2]
      {
       :pre
       (
        (not= (.ground-set b1) nil)
        (not= (.subset b1) nil)
        (not= (.ground-set b2) nil)
        (not= (.subset b2) nil)
        (= (.ground-set b1) (.ground-set b2))
        )
       }
      (compare (.subset b1) (.subset b2))
      ) b1 b2)
    )

  java.lang.Object
  ;PObject
  
  (hashCode
    [this]
    (hash-combine
      (.hashCode (.ground-set this))
      (.hashCode (.subset this)))
    )
  
  (equals
    [b1 b2]
    ((fn [#^Bitset b1 #^Bitset b2]
      {
       :pre
       (
        (not= (.ground-set b1) nil)
        (not= (.subset b1) nil)
        (not= (.ground-set b2) nil)
        (not= (.subset b2) nil)
        )
       }
      (and
        (= (.ground-set b1) (.ground-set b2))
        (= (.subset b1) (.subset b2))
        )
      ) b1 b2)
    )

  (toString
    [this]
    (str {:ground-set (.ground-set this) :subset (seq this)})
    )
   )

(defn new-bitset
  "Convert seq subset to bitset using seq ground-set.
   Performance note: Converts subset to set if it's not already a set.
  "
  ; TODO: special optimization when subset is very small
  [ground-set subset]
  (let
    [subset (if (set? subset) subset (set subset))]
    (let
      [subset
       (reduce 
        (fn
          [acc element]
          (bit-or (bit-shift-left #^Number acc 1) (if (contains? subset element) 1 0)))
        0
        ground-set)
      ]
      (Bitset. ground-set subset)
      )
    )
  )

; Override print methods. TODO: find out how to override toString instead?
;(defmethod simple-dispatch Bitset
;    [r]
;    (write-out (.repr r)))
;(defmethod clojure.core/print-method Bitset
;  [r, writer]
;  (.write writer (str r)))
;(defmethod clojure.core/print-dup Bitset
;  [r, writer]
;  (.write writer (.repr r)))


(with-test
  (defn implies [a b] (or (not a) b))
  (is (= (implies false false) true))
  (is (= (implies false true) true))
  (is (= (implies true false) false))
  (is (= (implies true true) true))
  )

(deftest
  set-complement-test
  (is 
    (=
      (set (set/complement (new-bitset #{1 2 3 4 5} #{1 2 4})))
      #{3 5}
      )
    )
  )


(defn new-empty-set
  "Take a ground-set or another Bitset and return an empty Bitset
  on this ground-set or ground-set of the Bitset."
  [ground-set]
  (Bitset.
    (if
      (instance? Bitset ground-set)
      (.ground-set #^Bitset ground-set)
      ground-set) 
    0)
  )

(defn all-subsets
  "Get all bitsubsets of ground-set in increasing order:
  000 001 010 011 100 101 110 111
  "
  [ground-set]
  (map
    #(Bitset. ground-set %)
    (let
      [maxbit (bit-shift-left 1 (count ground-set))]
      (take-while
        #(= (bit-and % maxbit) 0)
        (iterate inc 0))
      )
    )
  )

(deftest
  subsets-test
  (let 
    [
     a (set (map #(set (seq %)) (all-subsets #{1 2 3})))
     b #{#{} #{3} #{2} #{2 3} #{1} #{1 3} #{1 2} #{1 2 3}}
     ]
    (is (= a b))
    )
  )

;(def Random-single
;  (memoize (fn [] (Random.)))
;  )

(def #^Random rnd-single 
  (Random.)
  )

(defn random-subset
  "Get random subset of ground-set"
  ; TODO: no need to use BigInteger if ground-set is small
  (
  [ground-set]
   (Bitset.
     ground-set
     (BigInteger. (count ground-set) rnd-single)
     )
   )
  )

(defn random-subsets
  "Get infinite seq of random subsets of ground-set"
  [ground-set]
  (repeatedly #(random-subset ground-set))
  )

(defn random-combination
  "Get random combination of size k in ground-set."
  ; start from empty set and set random bits if k < n/2
  ; start from full set and clear random bits if k > n/2
  [ground-set #^Integer k]
  { 
   :pre ( 
         (instance? Integer k)
         (<= k (count ground-set))
         ) 
   :runtime '(* 2 k)
   }
  (let
    [n (count ground-set)
;     init-curval (if (> (* k 2) n) (BigInteger. n rnd-single) 0)
     init-curval (if (> (* k 2) n) (dec (bit-set 0 n)) 0)
     init-bitcount (bit-count init-curval)
     compute-next (if (< init-bitcount k) bit-set bit-clear)
     subset (loop [curval init-curval]
              (if 
              (= (bit-count curval) k)
              curval
              (recur (compute-next curval (rand-int n)))
              ))
      ;The following is slightly (~10%) slower
;     subset (first (drop-while
;                     #(not= (bit-count %) k)
;                     (iterate #(compute-next % (rand-int n)) init-curval)
;                     ))
     ]
    (Bitset. ground-set subset)
    )
  )

(defn random-combinations
  "
  Get infinite seq of random subset of size k in ground-set.
  
  Get infinite seq of random subset of size between l and u in ground-set.
  "
  ([ground-set k]
  (repeatedly #(random-combination ground-set k))
   )
  ([ground-set #^Integer l u]
  (repeatedly #(random-combination ground-set (rand-int-range l u)))
   )
  )

;(map #(bit-count (.subset %))
;  (take 10 (random-combinations (range 10) 0 1))
;     )

(defn random-combination-bench
  []
  (let
    [n 1000
     k 500
     ground-set (doall (range n))
     ]

    (println (bit-count (.subset #^Bitset (random-combination ground-set k))))

    (time (dotimes [i 1000]
      (random-combination ground-set k)
            ))

    )
  )
;(random-combination-bench)

;(defn combinations
;  "Get all combinations of k items from ground-set as Bitsets."
;  [ground-set k]
;  0)


;(binding [*test-out* *out*]
;  (run-tests 'clj.sets.bitset)
;  )
;{
;"none" (new-bitset #{1 2 3} #{1 3})
;"str" (str (new-bitset #{1 2 3} #{1 3}))
;"print" (print (new-bitset #{1 2 3} #{1 3}))
;"pprint" (binding [*print-dup* true]
;  (pprint (new-bitset #{1 2 3} #{1 3}))
;  )
; }
