
(defn ^:static bah [n] (inc n))
(defn bah2 [n] (inc n))

(time (dotimes [_ 1e8] (bah 3)))
(time (dotimes [_ 1e8] (bah2 3)))

(defn ^:static fib ^long [^long n]
  (if (>= (long 1) n)
      (long 1)
          (+ (fib (dec n)) (fib (- n (long 2))))))

(dotimes [_ 10] (time (fib 30)))
