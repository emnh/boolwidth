(ns clj.util.util
  (:use
    [clojure.test]
    [clojure.pprint]
    )
  (:require
   [clojure.string :as cstr]
    )
  (:import
;    (java.security MessageDigest)
    )
;           (java.io File LineNumberReader InputStreamReader PushbackReader)
;           (java.lang.reflect Modifier Method Constructor)
;           (clojure.lang RT Compiler Compiler$C)
  )

(defn enable-reflection-warnings
  []
  (try
    (set! *warn-on-reflection* true)
    (catch java.lang.IllegalStateException e nil)
    )
  )

; from http://svn.bendiken.net/clojure/util.clj
; --- snip ---
(defn sprintf
    "Returns a formatted string."
    [fmt & args]
      (.. (new java.util.Formatter)
                (format (str fmt) (to-array args))
                (toString)))
; --- snip ---

(defn p
  "Print and return"
  [a]
  (pprint a)
  a)

(defn np
  "No-op with 1-char difference in name from p, for easily turning on/off printing"
  [a]
  a)

(defn rand-int-range
  "Return random int (max 32-bit) in range l u, both inclusive"
  [l u]
  (let [l (int l) u (int u)] (+ l (rand-int (inc (- u l)))))
  )

(defmulti
  bit-count
  "Count bits in a number"
  class)

(defmethod
  bit-count
  Integer
  [num]
  (Integer/bitCount num)
  )

(defmethod
  bit-count
  Long
  [num]
  (Long/bitCount num)
  )

(defmethod
  bit-count
  BigInteger
  [#^BigInteger num]
  (.bitCount num)
  )

(deftest bit-count-test
  (is (= (bit-count 255) 8))
  (is (= (bit-count (bit-shift-left 255 32))))
  (is (= (bit-count (bit-shift-left 255 100))))
  )

(defn avg
    "Get the average of a sequence of numbers"
    [& numbers]
    (/ (apply + numbers) (count numbers)))

(deftest
  avg-test
  (is (= 2 (avg 1 2 3)))
  )

;(defmacro defexample
;  [name value]
;  `(def
;     ~(with-meta name {:b 3})
;     ~value)
;  )
;(macroexpand '(defexample a 2))

(defn
  enumproto
  "Enumerate protocols in namespace"
  []
  (println "=====================")
  (let
    [
     pub (ns-publics 'clj.graph.graph)
  ;   maps (filter (fn [[x y]] (instance? clojure.lang.PersistentArrayMap (var-get y))) pub)
     maps (filter (fn [[x y]] (map? (var-get y))) pub)
     protocols (filter (fn [[x y]] (:on-interface (var-get y))) maps)
     lines (map (fn [[x y]] (cstr/join "\t" [x (type (var-get y)) y])) protocols)
     ]
    (println (cstr/join "\n" lines))
    )
  (println "=====================")
  )

(defn path-join
  [& args]
  (cstr/join "/" args)
  )

(defn strip-ext
  [s]
  (cstr/join "." (butlast (cstr/split #"\." s)))
  )

(defn replace-ext
  [s ext]
  (str (strip-ext s) "." ext)
  )

