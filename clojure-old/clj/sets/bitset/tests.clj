(ns clj.sets.bitset.tests
  (:use
    [clj.sets.bitset]
    [clojure.test]
    [clojure.pprint]
    )
  )

(defmacro
  def-vars
  []
  (apply vector (for [x ['ground-set 'a 'b 'a-bit 'a2-bit 'b-bit]] `(def ~x)))
  )

(def-vars)
;(macroexpand-1 '(def-vars))

(defn
  bit-test-setup
  [f]
  (binding
   [
     a #{1 2}
     b #{2 3}
     ground-set #{1 2 3 4}
    ]
    (binding [
       a-bit (to-bitset ground-set a)
       a2-bit (to-bitset ground-set a)
       b-bit (to-bitset ground-set b)
       ]
      (f)
      )
    )
  )

(use-fixtures :once bit-test-setup)

(deftest
  difference-test
  (let
    [
     u (clojure.set/difference a b)
     u_bit (set (difference a-bit b-bit))
    ]
    (is (= u u_bit))
    (report ["diff" u u_bit])
    )
  )

(deftest
  union-test
  (let
    [
     u (clojure.set/union a b)
     u_bit (set (union a-bit b-bit))
    ]
    (is (= u u_bit))
    (report ["union" u u_bit])
    )
  )

(deftest
  equals-test
  (is (= a-bit a2-bit))
  (is (.equals a-bit a2-bit))
  (is (= (.hashCode a-bit) (.hashCode a2-bit)))
  )

(deftest
  inverse-bitset
  (let
    [ground-set #{1 2 3 4}
     subset #{1 3}
    ]
    (is (= (set (to-bitset ground-set subset)) subset))
    )
  )

(binding [*test-out* *out*]
  (run-tests 'clj.sets.bitset.tests)
  )
