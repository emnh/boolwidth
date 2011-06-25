(ns 
  clj.test.stat
  (:use 
    [incanter.core]
    [incanter.stats]
    [incanter.charts]
    )
  )

(view (histogram (sample-normal 1000)))
