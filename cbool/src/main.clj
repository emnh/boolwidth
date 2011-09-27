(ns c
  (:require clojure.contrib.str-utils))

(defn include [x] (str "#include " x))

(defn format-typed-name
  [fdecl]
  (str 
    (:type fdecl) (if (:array fdecl) "[]")
    " "
    (:name fdecl)))

(defn format-arg
  [arg]
  (format-typed-name arg))

(defn format-args
  [args]
  (reduce #(str %1 "," %2) (map format-arg args)))

(defn cdefn
  [fdecl args body]
  [
   (format 
     "%s (%s) {"
     (format-typed-name fdecl)
     (format-args args)
     )
   body
   "}"
   ])

(defn ctype
  [typeName name]
  {:name name, :type typeName})

(defn carray
  [ctype-map]
  (assoc ctype-map :array true))

(defmacro cint [name] `(ctype "int" ~(str name)))
(defmacro cint-array [name] `(carray (ctype "int" ~(str name))))
(defmacro uint [name] `(ctype "unsigned int" ~(str name)))
(defn ar [name] (assoc :array true))

(ns main
  (:require clojure.contrib.seq-utils))

(defn format-program
  [program]
  (reduce 
    str 
    (map #(str "\n" %) (flatten program))))

(c/cint countNeighborHoods)

(def program 
  [
   (c/include "stdio.h")
   (c/include "stdlib.h")
   (c/cdefn
     (c/cint countNeighbourHoods)
     [(c/cint-array neighbourHoods)]
     [
      ]
     )
   ])

(spit 
  "/home/emh/workspace/cbool/src/test.c"
  (format-program program))

"
int countNeighborHoods(uint[] neighborHoods) {
}

int main (int argc, char* argv[]) {
    exit(0);
}
"

