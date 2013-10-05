(ns clj.util.json
  (:use
    [clj.util.introspect]
    [clojure.contrib.string :only (as-str)]
    [clojure.contrib.pprint :only (write formatter-out)]
    )
  (:import 
    graph.BiGraph
    interfaces.IGraph
    (java.io PrintWriter StringWriter)
    )
)

; TODO: references (don't write duplicates and support cyclical structures)


; start of clojure.contrib.json, license: EPL

;;; JSON PRINTER

(defprotocol Write-JSON
  (write-json [object out]
              "Print object to PrintWriter out as JSON"))

(defn- write-json-string [#^CharSequence s #^PrintWriter out]
  (let [sb (StringBuilder. #^Integer (count s))]
    (.append sb \")
    (dotimes [i (count s)]
      (let [cp (Character/codePointAt s i)]
        (cond
         ;; Handle printable JSON escapes before ASCII
         (= cp 34) (.append sb "\\\"")
         (= cp 92) (.append sb "\\\\")
         (= cp 47) (.append sb "\\/")
         ;; Print simple ASCII characters
         (< 31 cp 127) (.append sb (.charAt s i))
         ;; Handle non-printable JSON escapes
         (= cp 8) (.append sb "\\b")
         (= cp 12) (.append sb "\\f")
         (= cp 10) (.append sb "\\n")
         (= cp 13) (.append sb "\\r")
         (= cp 9) (.append sb "\\t")
         ;; Any other character is Hexadecimal-escaped
         :else (.append sb (format "\\u%04x" cp)))))
    (.append sb \")
    (.print out (str sb))))

(defn- write-json-object [m #^PrintWriter out] 
  (.print out \{)
  (loop [x m]
    (when (seq m)
      (let [[k v] (first x)]
        (when (nil? k)
          (throw (Exception. "JSON object keys cannot be nil/null")))
        (.print out \")
        (.print out (as-str k))
        (.print out \")
        (.print out \:)
        (write-json v out))
      (let [nxt (next x)]
        (when (seq nxt)
          (.print out \,)
          (recur nxt)))))
  (.print out \}))

(defn- write-json-array [s #^PrintWriter out]
  (.print out \[)
  (loop [x s]
    (when (seq x)
      (let [fst (first x)
            nxt (next x)]
        (write-json fst out)
        (when (seq nxt)
          (.print out \,)
          (recur nxt)))))
  (.print out \]))

(defn- write-json-bignum [x #^PrintWriter out]
  (.print out (str x)))

(defn- write-json-plain [x #^PrintWriter out]
  (.print out x))

(defn- write-json-null [x #^PrintWriter out]
  (.print out "null"))

(defn- write-json-named [x #^PrintWriter out]
  (write-json-string (name x) out))

(defn- write-json-generic [x out]
  (if (.isArray (class x))
    (write-json (seq x) out)
    (throw (Exception. (str "Don't know how to write JSON of " (class x))))))
  
(extend nil Write-JSON
        {:write-json write-json-null})
(extend clojure.lang.Named Write-JSON
        {:write-json write-json-named})
(extend java.lang.Boolean Write-JSON
        {:write-json write-json-plain})
(extend java.lang.Number Write-JSON
        {:write-json write-json-plain})
(extend java.math.BigInteger Write-JSON
        {:write-json write-json-bignum})
(extend java.math.BigDecimal Write-JSON
        {:write-json write-json-bignum})
(extend java.lang.CharSequence Write-JSON
        {:write-json write-json-string})
(extend java.util.Map Write-JSON
        {:write-json write-json-object})
(extend java.util.Collection Write-JSON
        {:write-json write-json-array})
(extend clojure.lang.ISeq Write-JSON
        {:write-json write-json-array})
;(extend java.lang.Object Write-JSON
;        {:write-json write-json-generic})

(defn json-str
  "Converts x to a JSON-formatted string."
  [x]
  (let [sw (StringWriter.)
        out (PrintWriter. sw)]
    (write-json x out)
    (.toString sw)))

(defn print-json
  "Write JSON-formatted output to *out*"
  [x]
  (write-json x *out*))


;;; JSON PRETTY-PRINTER

;; Based on code by Tom Faulhaber

(defn- pprint-json-array [s] 
  ((formatter-out "~<[~;~@{~w~^, ~:_~}~;]~:>") s))

(defn- pprint-json-object [m]
  ((formatter-out "~<{~;~@{~<~w:~_~w~:>~^, ~_~}~;}~:>") 
   (for [[k v] m] [(as-str k) v])))

(defn- pprint-json-generic [x]
  (if (.isArray (class x))
    (pprint-json-array (seq x))
    (print (json-str x))))
  
(defn- pprint-json-dispatch [x]
  (cond (nil? x) (print "null")
        (instance? java.util.Map x) (pprint-json-object x)
        (instance? java.util.Collection x) (pprint-json-array x)
        (instance? clojure.lang.ISeq x) (pprint-json-array x)
        :else (pprint-json-generic x)))

(defn pprint-json
  "Pretty-prints JSON representation of x to *out*"
  [x]
  (write x :dispatch pprint-json-dispatch))

; end of clojure.contrib.json

(defn write-java-object
  [m #^PrintWriter out]
  (.print out "(from-map ")
  (.print out (.getName (class m)))
  (.print out \ )
  (write-json-object (to-map m) out)
  (.print out \)))

(defn pprint-java-object
  [m]
  ; TODO: wrap map
;  (.print out "(from-map ")
;  (.print out (.getName (class m)))
;  (.print out \ )
  (pprint-json-object (to-map m))
  )
;  (.print out \)))


;(extend 
;  graph.Vertex 
;  Write-JSON {
;              :write-json (fn [obj out] (write-java-object obj out))
;              }
;  )

(extend 
  BiGraph
  Write-JSON {
              :write-json (fn [obj out] (write-java-object obj out))
              }
  )

(extend 
  java.lang.Object 
  Write-JSON {
              :write-json (fn [obj out] (write-java-object obj out))
              }
  )

(defn- pprint-json-dispatch [x]
  (cond (nil? x) (print "null")
        ; TODO: multimethod
        (instance? BiGraph x) (pprint-java-object x)
        (instance? java.util.Map x) (pprint-json-object x)
        (instance? java.util.Collection x) (pprint-json-array x)
        (instance? clojure.lang.ISeq x) (pprint-json-array x)
        :else (pprint-json-generic x)))

(defn pprint-json
  "Pretty-prints JSON representation of x to *out*"
  [x]
  (write x :dispatch pprint-json-dispatch))


