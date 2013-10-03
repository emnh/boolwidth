(defn disp
  ([first & rest]
   (class first)
    )
  )

(defmulti 
  get-hoods
  "[#^BiGraph bigraph]: Get [left right] neighbor sets of bigraph as bitsets."
  disp
  )

(defmulti 
  get-hoods-3
   "[#^IGraph graph vertices ground-set]: Get sequence of bitsets representing neighbor sets of vertices in ground-set."
  disp
  )

(defmulti 
  get-hoods-full
  "[#^BiGraph bigraph]: Get [left right] neighbor sets of bigraph as bitsets, with all vertices in graph as ground-set."
  disp
  )

(defmulti 
  get-hoods-full-3
   "[#^IGraph graph vertices ground-set]: Get sequence of bitsets representing neighbor sets of vertices in ground-set, with all vertices in graph as ground-set."
  disp
  )

(extend-type
  BiGraph
  Cut

; Returns [left right] each a seq of bitsets
  (get-hoods
  (
    [#^BiGraph bigraph] 
    (let
      [
       left (.leftVertices bigraph)
       right (.rightVertices bigraph)
       ]
      [
      (set (get-hoods-3 bigraph left right))
      (set (get-hoods-3 bigraph right left))
       ]
      )
    )
  )

; Returns seq of bitsets
(defmethod
  get-hoods-3
  IGraph
  (
   [#^IGraph graph vertices ground-set]
    (for [vertex vertices]
      (let
        [neighbors
         (map
          #(.opposite graph vertex %1)
          (.incidentEdges graph vertex)
          )
        ]
        (new-bitset ground-set neighbors)
        )
      )
   )
  )

; Returns [left right] each a seq of bitsets
(defmethod 
  get-hoods-full
  BiGraph
  (
    [#^BiGraph bigraph] 
    (let
      [
       v (.vertices bigraph)
       left (.leftVertices bigraph)
       right (.rightVertices bigraph)
       ]
      [
      (set (get-hoods-full-3 bigraph left right))
      (set (get-hoods-full-3 bigraph right left))
       ]
      )
    )
  )

; Returns seq of bitsets
(defmethod
  get-hoods-full-3
  IGraph
  (
   [#^IGraph graph vertices ground-set]
   (let 
     [allvertices (.vertices graph)
      remove-bitset (set-complement (new-bitset allvertices ground-set))
     ]
     (for [vertex vertices]
       (let
         [neighbors
          (map
           #(.opposite graph vertex %1)
           (.incidentEdges graph vertex)
           )
         ]
         (set-difference (new-bitset allvertices neighbors) remove-bitset)
         )
       )
     )
   )
  )

(defn new-bigraph
  "takes graph, an IGraph, and bitsubset of left vertices, left
   Returns a bigraph cut.
  "
 (
  [#^IGraph graph left]
  (let 
    [left #^java.util.Collection (seq left)]
    (BiGraph. left graph)
     )
    )
  )

; Returns [left right] each a seq of bitsets
(defmethod
  get-hoods
  MatrixCut
  [#^MatrixCut mview]
  (let 
    [
     #^MatrixGraph mgraph (.MatrixGraph mview)
     left (.left-vertices mview)
     right (.right-vertices mview)
     leftseq (get-hoods-3 mgraph left right)
     rightseq (get-hoods-3 mgraph right left)
     ]
    [leftseq rightseq]
    )
  )

; Returns seq of bitsets
(defmethod
  get-hoods-3
  MatrixGraph
  [#^MatrixGraph graph vertices ground-set]
  (map
    (fn [#^graph.Vertex vertex]
      (let
        [neighbors
         (nth (.edge-matrix graph) (.id vertex))
        ]
        (set-difference neighbors vertices)
        )
    )
    (seq vertices)
;    (.edge-matrix graph)
    )
  )

