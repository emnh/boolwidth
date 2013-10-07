(ns 
  clj.thesis.background
  (:use
    [clj.thesis.examples]
    [clj.thesis.thesis :only [definition functions examples implementation]]
    ; note that get-source doesn't work on definitions in the current file
    [clojure.contrib.repl-utils :only [get-source]]
    )
  (:require 
    [clojure.pprint :as pprint]
    [clj.tex.graph :as texgraph]
    [clj.util.util :as util]
    [clj.tex.tex :as tex]
    [clj.sets.set :as sets]
    [clj.thesis.thesis :as thesis]
    [clj.graph.graph :as graph]
    [clj.boolwidth.cutbool :as cb]
    [clj.decomposition.decomposition :as dc]
    [clj.decomposition.split :as split]
    [clj.viz.graphviz :as dot]
    )
  )

(util/enable-reflection-warnings)

; todo: remove
;(thesis/spit "example-cut.tex" (tex/to-tex example-cut :adjlist true))
;(thesis/spit "example-adj-graph.tex" (tex/to-tex example-mgraph :adjlist true))
;(thesis/spit "example-mgraph.tex" (tex/to-tex example-mgraph))

;(defn
;  rspit
;  [relative-filename s]
;  (thesis/spit
;    (util/path-join "chapters/background/gen" relative-filename)
;    s
;    )
;  )


; ========================== BACKGROUND CHAPTER ======================

(tex/chapter "Background")

(tex/section
  "Introduction"
  (tex/par 
    "This chapter will introduce the basic concepts, such as set and subset,
    and protocols representing a programming interface for the concepts. These
    basic concepts and functions are needed for the algorithms to be presented
    later. Each section will consist of a Definition, Functions, Examples and
    Implementation tex/subsection. Before we begin you will find some general notes
    on the choices of presentation and the contents of each standard section,
    titled the same as the section it describes, and a basic introduction to
    Clojure.")
  )

(tex/subsection
  "Why Clojure instead of pseudocode"
  (tex/par
    "All code is presented in the \\href{http://clojure.org/}{Clojure language}.
    Our first implementations were in Java, but fighting the type system turned
    out to be a big headache. The facilities for representing abstractions
    using static typing were just too limited, so a dynamic language was
    needed. In additition, Java code is very verbose and would need to be
    translated to pseudocode for presentation in a thesis, so to avoid this
    redundancy, a concise language more similar to pseudocode was needed. To
    retain some compatibility with our previous code base, I chose Clojure,
    since it runs on top of the Java Virtual Machine.  I did not want to
    present pseudocode because of the ambiguities that might arise by having to
    build and explain constructs from scratch without being able to rely on a
    rigorous specification. The lack of ability to test the pseudocode by
    running it would also increase the chance of errors.")
  (tex/par "As a LISP Clojure is a functional and very terse language. The basic
       syntax is very simple, as code consists mostly of space-separated lists
       enclosed in paranthesis, for example (+ 2 3), where the first expression
       in the list is a function and the remaining are arguments. An added
       bonus is that Clojure code mostly runs in time within a small constant
       factor, say 4, of the equivalent Java code, which means that only the
       functions called in the innermost loops need to be implemented in Java
       to achieve near maximum constant factor efficiency.")
  )

(implementation
  (tex/par "This thesis favors simpler and more elegant Clojure versions of our
       optimized Java code that may be less efficient by a constant factor, but
       this does not affect relative comparison of heuristics. Implementations
       of well-known basic concepts like sets and adjaceny matrix graphs are
       omitted, or at most roughly described, and only the protocols
       (interfaces) they implement are specified.")
  )

(functions
  (tex/par "A clojure protocol is basically a group of functions that all take the same
  type of object as the first argument. I wil use a convention of x as the name
  of the first argument by convention for brevity. It is also commonly named
  this or self.")
  (tex/par "A protocol is similar to a Java interface. It's a convenient
  way of unifying multiple implementations for polymorphism.")
  )

(tex/texinput "chapters/background/clojure")

; ========================== SET ======================

(tex/section
  "Set"
  (tex/par
    "Sets are used primarily for collections of nodes, especially
    neighborhoods, which are represented as subsets. Subset implementations
    implement the set protocol by convention. Union is the most important
    function, heavily used in tight loops."
    )
  )

(definition
  (tex/par "A set is a collection of distinct objects. I will use the clojure literal notation
  for sets: $\\#\\{\\cdots\\}$.")
  )

(functions
  (tex/proto-tex sets/PSet)
  )

(examples
  (tex/par "There shouldn't be any surprises with these simple functions. See "
       (tex/texref :tab_set_examples) ".")

  (binding
    [texgraph/neighborhood-to-tex texgraph/neighborhood-to-tex-as-set]
    (tex/table
      [(thesis/var-ex
         example-set 
         example-set-2
         empty-set
         )
       (thesis/fun-ex
         (type example-set)

         (sets/count example-set)

         (sets/contains? example-set 2)
         (sets/contains? example-set 5)

         (sets/empty? empty-set)
         (sets/empty? example-set)

         (sets/difference example-set example-set-2)
         (sets/difference example-set-2 example-set)

         (sets/intersection example-set example-set-2)
         (sets/intersection example-set-2 example-set)

         (sets/union example-set example-set-2)
         (sets/union example-set-2 example-set)

         (sets/union example-set-2 example-set)
         )]
      :caption "Set functions examples"
      :label :tab_set_examples
      )
    )
  )

;(println sets/Set)

(implementation
  (tex/par
    "The set implementations are of little interest, they're just building
    blocks needed for the algorithms which concern this thesis. Therefore they
    are not included here.")
  )


; ========================== SUBSET ======================

(tex/section
  "Subset"
  (tex/par "I will now introduce subsets, since neighborhoods in graphs are
       represented as subsets of the nodes in the graph.")
  )

(definition
  (tex/par "A subset is a collection of distinct objects contained in a set, called
       the universe of the subset. Subsets are presented like fractions, with
       the denominator being the universe and the numerator being the subset.")
  )

(functions
  (tex/proto-tex sets/PSubset)
  )

(examples
  (tex/par "Simple functions. See table " (tex/texref :tab_subset_examples) ".")
  (tex/table
    [(thesis/var-ex
       example-set
       )
     (thesis/fun-ex
       (type example-set)
       (sets/universe example-set)
       (sets/complement example-set)
       )
     ]
    :caption "Subset functions examples"
    :label :tab_subset_examples
    )
  )

(implementation
  (tex/par
    "We implemented subsets as bitsets so that on a 64-bit computer, functions
    on subsets can process 64 elements at once in constant time (in fact 128
    bit if we implement using the SSE instruction sets, for example using the
    publicly available \\href{http://bmagic.sourceforge.net/}{Bitmagic
    library}, but this would require calls out of the JVM to native code). For
    the union function, which is most heavily used, this is achieved by using
    the OR instruction on the bitsets.  This is quite a significant constant
    factor speedup, theoretically dividing running time by 64 (or some such
    fixed size, commonly 32 for 32-bit machines or 128 using SSE, depending on
    the size of the operands to the CPU instructions used)."
    )
  (tex/par
    "Since most of our graphs in the test library have at most a few hundred
    nodes, unions are very fast. Bitsets do not improve big-O runtime
    complexity however.")
  )

; ========================== GRAPH ======================

(tex/section
  "Graph"
  (tex/par "I will introduce the basic graph definitions and interface for reading
       a graph.")
  )

(definition
  (tex/par
    "A graph G is an ordered pair $G = (V, E)$ comprising a set V of vertices
    or nodes together with a set E of edges, where an edge is a pair of nodes.
    Unless otherwise specified, a graph is simple and undirected, see below."
    )

  (tex/texinput "chapters/background/graphdefs")
  )

(functions
  (tex/par "This is the interface for reading a simple undirected graph."
       "Interfaces for building graphs will be defined later \\todo{decide}")
  (tex/proto-tex graph/PGraph)
  )

(implementation
  (tex/par "The implementation is a standard matrix adjacency graph, with the rows
       being bitsets, so no conversion is needed when computing unions of
       vertices efficiently. The implementation will be extended later to a
       hybrid of a matrix adjacency graph and adjacency list graph, storing
       both and using the fastest function available, so that the running time
       of enumerating neighbors of vertex v in graph G is
       O(count(neighbors(v))) instead of O(count(vertices(G))).")
  )


;(defn neato-render
;  )

;(thesis/named-to-gviz-pdf example-mgraph :prog "neato")
;(def example-graph-pdf
;  (thesis/named-to-gviz-pdf example-mgraph :prog "neato")
;  )


(examples
;  (tex/graphic 
;    (thesis/relative-path example-graph-pdf)
;    )

  (tex/table
    [(thesis/var-ex
       example-mgraph
       example-mgraph-vertex
       )
     (thesis/fun-ex
       (graph/graph? example-mgraph)
       (graph/vertices example-mgraph)
       (graph/neighbors example-mgraph example-mgraph-vertex)
       )
     ]
    :caption "Graph examples"
    :label :tab_graph_examples
    )
  )

; ========================== NEIGHBORHOOD ======================

(tex/section
  "Neighborhood"
  (tex/par "I will define neighborhood in a graph, and specify which interfaces
           are implemented by neighborhoods. Note that neighbors and
           neighborhood are distinct terms, with the difference being that
           neighbors is just a collection, while neighborhood specified a set
           of protocols that are implemented, but functions will also
           explicitly state which protocols are implemented its return type for
           increased clarity, since it is easy to mix up the terms.")
  )

(definition
  (tex/par "A neighborhood of a vertex $v_1$ in a graph $G$ is the set of nodes
           $S$ such that for every vertex $v_2$ in G, $v_2$ is in S if and only
           if there is an edge between $v_1$ and $v_2$.")
  (tex/par "A neighborhood of a subset of vertices $V$ in a graph $G$ is the
           set of nodes $S$ such that for every vertex $v_2$ in G, $v_2$ is in S
           if and only if there exists $v_1 \\in V$ such that there is an edge
           between $v_1$ and $v_2$.")
  (tex/par "If neighborhood is unqualified with respect to vertex or vertex
           set, it is a neighborhood of a vertex set.")
  )

(functions
  (tex/par "A neighborhood is a type that by convention implements the PSet and
           PSubset protocols, so those functions can be called on it.")
  (tex/par "The PGraphHoods protocol is implemented on graph types for getting
           neighborhoods of vertices in graphs. The default implementation uses
           the PGraph protocol, but more efficient implementations may be used
           for specific graph types, such as matrix adjacency graph, which
           already represents its adjacency matrix as neighborhoods and can
           return them directly.")
  (tex/proto-tex graph/PGraphHoods)
  )

(examples
  (tex/par "\\todo{examples for GraphHoods}")
  )

; ========================== NEIGHBORHOODS ======================

(tex/section
  "Neighborhoods collection (PSubsets)"
  )

(definition
  (tex/par "Neighborhoods implements PSubsets by convention. 
           PSubsets is a set of elements implementing PSubset, used
           for passing around a set of subsets, neighborhoods in this case,
           having the same universe. The PSubsets protocol handles the case
           where a function takes an set of neighborhoods, which may be empty,
           but the functions need to know the universe to construct new subsets
           with that universe. \\todo{The Neighborhoods protocol can also be
           used to reduce memory usage by not having to reference the universe
           from each Subset, if implementations use the Subsets protocol on a
           collection of subsets instead of the Subset protocol on each
           subset.}")
  )

(functions
  (tex/proto-tex sets/PSubsets)
  )

(examples
  (tex/par "\\todo{examples for PSubsets}")
  (tex/table
    []
    :caption "Neighborhoods / Subsets examples"
    :label :tab_hoods_examples
    )
  )


; ========================== CUT ======================

(tex/section
  "Cut (disjoint)"
  (tex/par "Cuts are the building blocks of decompositions, and represent the
           division in divide and conquer algorithms on graphs.")
  )

(definition
  (tex/par "A cut on a graph represents a 2-partition of the vertices of a
          graph, it has functions for getting the left vertices and the right
          vertices. The union of left and right vertices is the full set of
          vertices of the graph. The set of the left and right set is
          unordered, but we shall define the canonical cut to be the one where
          the left side is smallest, or if they are of equal size, the
          lexicographically smallest (the one containing vertex of lowest
          vertex index in the graph). This is useful for comparing cuts and
          caching cuts in hash maps, and for defining canonical decomposition
          for similar purposes later.")
  )

(functions
  (tex/proto-tex graph/PCut)
  )

(examples
  (tex/par "\\todo{examples for PCut}")
  (thesis/var-ex
    example-cut
    )
  (tex/table
    []
    :caption "Cut examples"
    :label :tab_hoods_examples
    )
  )


; ========================== CUTBOOL ======================

(tex/section
  "Neighborhoods of a cut, listing and counting"
  (tex/par "I will now introduce neighborhoods of a cut. They are central to
           \\boolw because the optimization idea in divide and conquer is that two
           subsets of vertices $V_1$ and $V_2$ that have the same neighborhood
           on the other side of the cut are identical in their relation to the
           other side, therefore we only have to keep the best solution among
           the set of such subsets of vertices for combining with the solutions
           of the other side \\todo{figure example to show this}.")
  (tex/par "An important open research problem is to find fast approximation
           algorithms for counting neighborhoods, because the algorithm is
           called frequently from most heuristics. Without an approximation
           algorithm such heuristics will not scale to bigger graphs.
           Obviously, the number of neighborhoods of a cut generally increases
           with the size of the cut, by which I mean that for larger cuts there
           exist more cuts with a large number of neighborhoods, and the upper
           bound which impacts worst-case runtime.")
  )

(definition
  (tex/par "left-subset-neighborhoods of a cut is the neighborhoods of all
           subsets of the left vertices of the cut. right-subset-neighborhoods
           is defined similarly.")
  (tex/par "count-neighborhoods of a cut is defined to be the number of
           left-subset-neighborhoods of the cut, which is equal to the number
           of right-subset-neighborhoods. cut-bool, aka \\Boolw of a cut, is defined to be the
           2-logarithm of count-neighborhoods wherever count-neighborhoods is
           defined. As \\cite{boolwidth-of-graphs} notes, 
           \\begin{quote}
           It is known from boolean matrix theory that cut-bool is symmetric
           \\\\[17, Theorem 1.2.3\\\\]}, 
           \\end{quote}
           and of course this is true for count-neighborhoods as well
           since taking the logarithm doesn't change that.")
  (tex/par "count-neighborhoods of an arbitrary set of neighborhoods S
           implementing PNeighborhoods, which is assumed to be (left-hoods C)
           or (right-hoods C) for some cut C, is defined to be the number of
           neighborhoods of the cut C, or equivalently, the number of distinct
           neighborhoods D, where d is in D if and only if d is the union of a
           subset of neighborhoods in S.")
  (tex/par "Counting neighborhoods of a cut is currently done by just listing
           all neighborhoods, of either the left or right side, and counting
           them. We will try to choose the side of the cut that will generate
           the list of neighborhoods faster.")
  (tex/par "Some are logarithmic: \\Boolw of a cut
           is the logarithm of the number of neighborhoods, CutBool or cut-bool
           is an abbreviation for this. \\Boolw is logarithmic in the
           mathematical definition because it allows a nicer side-by-side
           comparison of the formulae for running times of \\boolw algorithms
           with \\tw and other width parameters, since those are exponential in
           the width parameter. In the implementations we mostly deal with and
           pass around number of neighborhoods as integers, not the logarithm
           of that, because it is more efficient and floating point loses
           precision.")
  )

(functions
  (tex/proto-tex cb/PListNeighborhoodsForCounting)
  (tex/proto-tex cb/PCountNeighborhoods)
  )

(examples
  (tex/par "\\todo{remove tex wrapper function}")
  (thesis/var-ex
    example-cut)
  (thesis/fun-ex
    (texgraph/new-tex-node-subsets
      example-mgraph 
      (cb/counting-seq-of-neighborhoods example-cut)))
  )

(implementation
  (tex/par
    (tex/cljcode
      (get-source 'cb/add-hood)))
  (tex/par
    (tex/cljcode
      (get-source 'cb/cutbool-list-src)))
  (tex/par
    (tex/cljcode
      (get-source 'cb/least-cuts)))
  (tex/par
    (tex/cljcode
      (get-source 'cb/least-splits))))

; ========================== DECOMPOSITION ======================
;
(tex/section
  "Decomposition"
  (tex/par "A decomposition represents the whole process of dividing a graph
           into parts recursively for divide and conquer, and can be defined in
           various ways. The most intuitive might be to view it as a collection
           of cuts satisfying certain rules that guarantee there are 2 cuts
           with a smaller side inside each cut. The standard rigorous
           definition views it as a tree with degree constraints and leaves in
           bijection with the nodes in the graph, thus representing the
           divisions and cuts implicitly.")
  (tex/par "I will introduce functions for working with a decomposition and
           functions for constructing decompositions, which will be used by the
           heuristics. I will consider only boolean decompositions here and
           refer to boolean- decomposition simply as decomposition without
           boolean prefix without ambiguity."))

(definition
  (tex/par "\\todo{definition}"))

(functions
  (tex/proto-tex split/PSplit)

  (tex/proto-tex dc/PDecomposition)

  (tex/par "\\todo{(immutable) decomposition builder protocol}"))

(def dc example-decomposition)

(examples
  (tex/par "run functions on simple even decomposition")
  (binding
    [dot/*tikz-scale* 0.3]
    (thesis/var-ex
      dc
      ))
  (thesis/fun-ex
    (dc/root dc)
    (dc/graph dc)
    (dc/decomposition? dc)
    (dc/children dc (dc/root dc))
    (dc/left dc (dc/root dc))
    (dc/right dc (dc/root dc))
    (dc/parent dc (dc/left dc (dc/root dc)))))
