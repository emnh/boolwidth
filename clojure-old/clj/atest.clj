(ns
  clj.atest
  (:use
    [clojure.pprint :only [pprint]]
    )
  (:import
    [java.lang Thread]
    )
  )

(defn helo
  [t i]
  (println "helo" t i)
  )

(defn ttest
  [checker]
  (try
    (let
      [t (Thread/currentThread)
       tid (.getId t)
       ]
      (dorun
        (map 
          #(do
             (Thread/sleep (+ 100 (rand-int 50)))
             ;           (helo tid %)
             (@checker %))
          (range 3))))
    (catch RuntimeException e 
      (if (instance? InterruptedException (.getCause e))
        (println "interrupted" e)))))

(let
  [ct 2
   checkers (map 
              (fn [i] 
                (atom
                  #(println "checker" % i)))
              (range 3))
   threads (map 
             (fn
               [checker]
               (Thread. #(ttest checker)))
             checkers)

   ]
  (dorun (map #(.start %) threads))
  (Thread/sleep 160)
  (dorun 
    (map-indexed 
    (fn
      [i checker]
      (reset!
        checker
        #(do 
           (println "nuchecker" % i)
           (throw (InterruptedException. "timeout")))))
    checkers)))

;(pprint 
;  (.dumpAllThreads
;    (java.lang.management.ManagementFactory/getThreadMXBean) false false))

;(time (apply + (range 1000000)))
