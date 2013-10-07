(def neighborhood-union clj.sets.bitset/union)
(def neighborhood-to-empty-set to-empty-set)

(deftype pair [left right])

(defn cb-distinct
  "Returns a lazy sequence of the elements of coll with duplicates removed.
  This is a copy of the distinct function from clojure, except it takes an
  initial set of seens and returns the set of seens.
  TODO: deftype for pair
  "
  {:added "1.0"}
  [coll seen]
    (let [step (fn step [xs seen]
                   (lazy-seq
                    ((fn [[f :as xs] seen]
                      (when-let [s (seq xs)]
                        (if (contains? seen f) 
                          (recur (rest s) seen)
                          (let [newseen (conj seen f)]
                            (cons (pair. f newseen) (step (rest s) newseen))))))
                     xs seen)))]
      (step coll seen)))

;(cb-distinct [1 2 2 3 4 5 6] #{4})
;(map #(.left #^pair %) (cb-distinct [1 2 2 3 4 5 6] #{4}))
;(map #(.right #^pair %) (cb-distinct [1 2 2 3 4 5 6] #{4}))

(deftype 
  hood-state
  [hood generation]
  Object 
  (hashCode [this] (.hashCode (.hood this)))
  (equals [a b] (.equals (.hood #^hood-state a) (.hood #^hood-state b)))
  (toString
    [this]
    (str [(.hood this) (.generation this)])
    )
  )

(defn add-next
  [a-seq initialhoods generation] 
  (let
    [unionmap (fn 
                unionmap
                [#^hood-state hstate]
                  (map (fn [hstate] (unionmap hstate)) hstate)
;                  (if 
;                    (< (.generation hstate) generation)
                    (hood-state. 
                      (neighborhood-union (first initialhoods) (.hood hstate))
                      generation)
;                    )
                )]
    (keep
      identity
      (distinct (lazy-cat
                  (map (fn [hstate] (unionmap hstate))
                     (take-while #(< (.generation #^hood-state %) generation) a-seq)
                     )
                  (if (seq (rest initialhoods))
                    (add-next a-seq (rest initialhoods) (inc generation))
                    )
                ))
      )
    )
  )

(defn neighborhoods-unions-lazy-2
  [initialhoods empty-set]
  (let 
    [first-hood-state [(hood-state. empty-set -1)]]
    (rec-cat myseq first-hood-state
           (filter #(not= (first first-hood-state) %) (add-next myseq initialhoods 0)))
    )
  )

(defn
  neighborhoods-unions-lazy
  "Compute set of unions of all combinations of the set
  initialhoods, which can be a collection of sets or bitsets."
  [initialhoods empty-set]
  (let
    [step
    (fn step
      [all-hoods remaining-hoods ; hoods-seq 
      ] 
        (if (seq remaining-hoods)
          (let
            [all-hoods (force all-hoods)
             new-hood (first remaining-hoods)
             new-hoods (map #(neighborhood-union new-hood %) all-hoods)
             seqpair (cb-distinct new-hoods all-hoods)
             new-hoods-seq (map #(.left #^pair %) seqpair)
             all-hoods-new (delay (or (last (map #(.right #^pair %) seqpair)) all-hoods))
;             all-hoods-new all-hoods
             ]
      ;      (concat new-hoods-seq (recur all-hoods-new (rest remaining-hoods)))
;            (pprint ["NH" (seq new-hood)])
;            (pprint ["NHS" (seq new-hoods)])
  ;          (pprint ["newhood" (seq new-hood)
  ;                   "newhoods" (map seq new-hoods)
  ;                   "AH" all-hoods
  ;                   "AHN" all-hoods-new 
  ;                   "RST" (set (map seq (rest remaining-hoods)))
  ;                   "NSEQ" new-hoods-seq
  ;                   ]
  ;                 )
      (lazy-seq
            (cons new-hoods-seq 
                  (step all-hoods-new (rest remaining-hoods))
;                 (concat hoods-seq new-hoods-seq)
                  )))
          nil
          )
        )
      ]
      (cons empty-set (flatten (step #{empty-set} (seq initialhoods))))
    )
  )

(defn- add-hood
  "Helper for neighborhoods. Compute union of newhood with all hoods in newhood
  and return union of hoods and that."
  [union-f hoods addhood]
  (clojure.set/union
    hoods
    (map #(union-f addhood %) hoods))
  )
;    (keep 
;      (fn [oldhood]
;        (let [newhood (union-f addhood oldhood)]
;          (if (not= newhood oldhood) newhood)
;          )
;        )
;      hoods))
;  )

(defn
  neighborhoods-unions
  "Compute set of unions of all combinations of the set
  initialhoods, which can be a collection of sets or bitsets."
  [initialhoods empty-set]
   (reduce
    (partial add-hood neighborhood-union)
    #{empty-set}
    initialhoods)
  )

(defn move-node-left
  "Get a modified sequence of neighborhoods where a node has been moved from
  right to left."
  [neighborhoods node-bitset node-neighbors]
  (let
    [hoods-with-node-removed
     (reduce 
       (fn [oldhoods oldhood]
         (let 
           [newhood (set-difference oldhood node-bitset)]
           (conj (conj (if (not= oldhood newhood) (disj oldhoods oldhood) oldhoods) newhood) (neighborhood-union newhood node-neighbors))
           ))
      neighborhoods
      neighborhoods)
     a (comment
     (reduce 
       (fn [oldhoods oldhood]
         (let 
           [newhood (set-difference oldhood node-bitset)]
           (if 
             (= newhood oldhood)
             ; no change needed
             oldhoods
             ; replace neighborhood with moved node removed
             (conj (disj oldhoods oldhood) newhood))
           ))
      neighborhoods
      neighborhoods
      ))]
    hoods-with-node-removed
;    (add-hood neighborhood-union hoods-with-node-removed node-neighbors)
    )
  )

(defn- add-hood-mutable
  "Helper for neighborhoods. Compute union of newhood with all hoods in newhood
  and return union of hoods and that."
  [union-f hoods newhood]
  (let
    [newhoods (TreeSet.)]
    (loop [hoods hoods]
      (.add newhoods (union-f newhood (first hoods)))
      (when-let [r (seq (rest hoods))] (recur r))
      )
    newhoods
    )
  )

(defn
  neighborhoods-unions-mutable
  "Compute set of unions of all combinations of the set
  initialhoods, which can be a collection of sets or bitsets."
  [initialhoods empty-set]
   (let
     [hoods (TreeSet.)]
     (.add hoods empty-set)
     (doall 
       (map #(.addAll hoods (add-hood-mutable neighborhood-union hoods %)) initialhoods)
       )
     hoods
    )
  )


(defmulti
  m-neighborhoods
  "Return seq of neighborhoods of all subsets"
  class
  )

(defmulti
  m-neighborhoods-left
  "Return seq of neighborhoods of all subsets on the left"
  class
  )

(defmulti
  m-neighborhoods-right
  "Return seq of neighborhoods of all subsets on the right"
  class
  )



(defn neighborhoods-mutable
  [ & args]
  (binding
;    [add-hood add-hood-mutable]
    [neighborhoods-unions neighborhoods-unions-mutable]
    (apply neighborhoods args)
    )
  )

(defn neighborhoods-lazy
  [ & args]
  (binding
    [neighborhoods-unions neighborhoods-unions-lazy]
    (apply neighborhoods args)
    )
  )

(defn neighborhoods-lazy-2
  [ & args]
  (binding
    [neighborhoods-unions neighborhoods-unions-lazy-2]
    (apply neighborhoods args)
    )
  )



;(defmethod
;  neighborhoods
;  IPersistentSet
;  [initial-hoods]
;  (count (neighborhoods-unions initial-hoods))
;  )


; TODO: this is almost identical to the next method. eliminate redundancy
(defmethod
  neighborhoods
  BiGraph
  [#^BiGraph bigraph]
  (let
    [
     [left-hoods right-hoods] (get-hoods bigraph)
     initial-hoods (min-key count left-hoods right-hoods)
     empty-set (to-empty-set (.ground-set #^bitset (first initial-hoods)))
     ]
    (neighborhoods-unions initial-hoods empty-set)
    )
  )

(defmethod
  neighborhoods-left
  BiGraph
  [#^BiGraph bigraph]
  (let
    [
     [left-hoods right-hoods] (get-hoods bigraph)
     initial-hoods left-hoods
     empty-set (to-empty-set (.ground-set #^bitset (first initial-hoods)))
     ]
    (neighborhoods-unions initial-hoods empty-set)
    )
  )


(defmethod
  neighborhoods
  MatrixCut
    [#^MatrixCut bigraph]
  (let
    [
     [left-hoods right-hoods] (get-hoods bigraph)
     initial-hoods (min-key count left-hoods right-hoods)
     empty-set (to-empty-set (.ground-set #^bitset (first initial-hoods)))
     ]
    (neighborhoods-unions initial-hoods empty-set)
    )
  )

(defn
  count-neighborhoods
  "Return neighborhood count"
  [cut]
  (count (neighborhoods cut))
  )

(deftest count-neighborhoods-test
  (let [
        hoods #{#{1 3} #{2 5} #{7 4} #{2 7} #{4 5} #{8} #{9} #{10} #{}}
        ground-set (reduce clojure.set/union #{} hoods)
        initialhoods-b (set (map #(to-bitset ground-set %) hoods))
        empty-set (to-empty-set ground-set)
        allhoods-b (neighborhoods-unions initialhoods-b empty-set)
        ct-b (count allhoods-b)
;        initialhoods (set hoods)
        allhoods-l (neighborhoods-unions-lazy initialhoods-b empty-set)
        ct-l (count allhoods-l)
        ]
;    (is (= ct-b 20) "Counting number of neighborhoods")
    (pprint
      [ground-set 
       ct-l
       ct-b
       allhoods-l
       ["ALLH-L" (map seq allhoods-l)]
       allhoods-b
      ])
    )
  )

;(pprint (get-fields (graph.Vertex. nil 2)))
;(pprint (to-map (graph.Vertex. nil 2)))

;(defmethod print-dup Object
;  [obj out]
;  (print-dup (to-map obj) out)
;  )

;(defmethod print-dup graph.BiGraph
;  [obj out]
;  (print-dup (to-map obj) out)
;  )


(defn move-benchmark
  []
  (binding 
    [get-hoods get-hoods-full]
    (let 
      [
;       graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib/protein/1brf_graph.dimacs")
       graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib/protein/1aba_graph.dimacs")
;       graph (ControlUtil/getTestGraph "/home/emh/bw/graphLib_ours/hsugrid/hsu-4x4.dimacs")
       v (.vertices graph)
       left-vertices (random-combination v (int (/ (count v) 2)))
       right-vertices (set-complement left-vertices)
       node-to-move (rand-nth (seq right-vertices))
       node-to-move-bitset (to-bitset v #{node-to-move})
       left-vertices2 (neighborhood-union left-vertices node-to-move-bitset)
       right-vertices2 (set-complement left-vertices2)
       node-neighbors (first (get-hoods-full-3 graph 
                                               [node-to-move]
                                               right-vertices2))
       #^BiGraph bg (to-bigraph graph left-vertices)
       #^BiGraph bg2 (to-bigraph graph left-vertices2)
;       a (pprint (get-hoods bg))
       hoods (neighborhoods-left bg)
  ;     [left-hoods right-hoods] (get-hoods bg)
  ;     initial-hoods (min-key count left-hoods right-hoods)
       ]
;      (trace "HOODS" hoods)
      (time (trace "FIRST" (count hoods)))

      (time (trace "NORMAL" (count (neighborhoods bg2))))

;      (pprint [:BG bg :left (.leftVertices bg) :right (.rightVertices bg)])
;      (pprint [:BG bg2 :left (.leftVertices bg2) :right (.rightVertices bg2)])
;      (pprint [:TO-MOVE node-to-move-bitset])
;      (pprint [:TO-MOVE-NEIGHBORS node-neighbors])
;      (pprint [:HOODS hoods])
;      (pprint [:NORMAL_HOODS (neighborhoods-left bg2)])
;      (pprint [:DIFF_HOODS (move-node-left hoods node-to-move-bitset node-neighbors)])

      (time 
        (trace "DIFF" 
               (count (move-node-left hoods node-to-move-bitset node-neighbors))))

  ;    (time (trace "DIFF" (count (move-node-left hoods node-to-move-bitset

      )
    )
  )

(defn
  cutbool-benchmark
  []
  (let 
    [
     #^BiGraph bg (random-bigraph)
     #^BiGraph bg2 (random-bigraph)
;     [left-hoods right-hoods] (get-hoods bg)
;     initial-hoods (min-key count left-hoods right-hoods)
     ]

    (time (p ["NORMAL" (count-neighborhoods bg)]))
    (time (p ["NORMAL" (count-neighborhoods bg2)]))
    (println)

    (time (p ["NORMAL-NEW" (count (neighborhoods-mutable bg))]))
    (time (p ["NORMAL-NEW" (count (neighborhoods-mutable bg2))]))
    (println)

;    (time (p ["LAZY" (neighborhoods-lazy bg)]))
    (time (p ["LAZY" (count (neighborhoods-lazy bg))]))
    (time (p ["LAZY" (count (neighborhoods-lazy bg2))]))
    (println)
 
;    (time (p ["LAZY 2" (neighborhoods-lazy-2 bg)]))
    (time (p ["LAZY 2" (count (neighborhoods-lazy-2 bg))]))
    (time (p ["LAZY 2" (count (neighborhoods-lazy-2 bg2))]))
    (println)

    (time (p ["JAVA" (boolwidth.CutBool/countNeighborhoods bg)]))
    (time (p ["JAVA" (boolwidth.CutBool/countNeighborhoods bg2)]))
    (println)

    (time (p ["JAVA LAZY" (boolwidth.CutBool/countNeighborhoodsLazy bg)]))
    (time (p ["JAVA LAZY" (boolwidth.CutBool/countNeighborhoodsLazy bg2)]))
    nil
    )
  )


