# Dynamic Graph Connectivity

## [Starting Point: StackExchange](http://cstheory.stackexchange.com/questions/2548/is-there-an-online-algorithm-to-keep-track-of-components-in-a-changing-undirecte)

There are several data structures that support edge insertions, edge deletions, and connectivity queries (Are these two vertices in the same connected component?) in polylogarithmic time:
- [Monika R. Henzinger and Valerie King. Randomized fully dynamic graph algorithms with polylogarithmic time per operation. Journal of the ACM 46(4):502—516, 1999.](http://portal.acm.org/citation.cfm?id=320215)
- [Jacob Holm, Kristian de Lichtenberg, and Mikkel Thorup. Poly-logarithmic deterministic fully-dynamic algorithms for connectivity, minimum spanning tree, 2-edge, and biconnectivity, Journal of the ACM 48(4):723—760, 2001.](http://portal.acm.org/citation.cfm?id=502095)
- [Mikkel Thorup. Near-optimal fully-dynamic graph connectivity. Proc. 32nd STOC 343—350, 2000.](http://portal.acm.org/citation.cfm?id=335345)

## Theory
 - [PDF: Poly-logarithmic deterministic fully-dynamic algorithms for connectivity, minimum spanning tree, 2-edge, and biconnectivity](http://csclub.uwaterloo.ca/~gzsong/papers/Poly-logarithmic%20deterministic%20fully-dynamic%20algorithms%20for%20connectivity,%20minimum%20spanning%20tree,%202-edge,%20and%20biconnectivity.pdf)
 - [In Book Encyclopaedia of Algorithms](http://books.google.no/books?id=i3S9_GnHZwYC&pg=PA335&lpg=PA335&dq=Mikkel+Thorup.+Near-optimal+fully-dynamic+graph+connectivity.&source=bl&ots=nDgnq6EqJz&sig=3xX01XR8vUhahmk-EsRAnBin5DA&hl=en&sa=X&ei=LHWpU_7fDIXQ7AazvYCQBA&ved=0CCsQ6AEwAQ#v=onepage&q=Mikkel%20Thorup.%20Near-optimal%20fully-dynamic%20graph%20connectivity.&f=false)
 - [Wikipedia on Related Data Structure Top Tree](http://en.wikipedia.org/wiki/Top_tree)

## Implementation Studies
 - [An experimental study of polylogarithmic, fully dynamic, connectivity algorithms](http://people.csail.mit.edu/karger/Papers/impconn.pdf)

## Source Code
 - [C Source Code for HK (Henzinger-King): An empirical study of dynamic graph algorithms: p5-alberts.tar](http://dl.acm.org/citation.cfm?id=264223)
 - [C Source Code for HDT_HK (Jacob Holm, Kristian de Lichtenberg, and Mikkel Thorup.) : An Experimental Study of Polylogarithmic, Fully Dynamic, Connectivity Algorithms: p4-iyer.tar](http://dl.acm.org/citation.cfm?id=945398)
 - [Boost C++ Graph Library: Incremental Connectivity](http://www.boost.org/doc/libs/1_55_0/libs/graph/doc/incremental_components.html) Sadly, this is only incremental and doesn't support deletions.
