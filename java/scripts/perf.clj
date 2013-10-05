(defprotocol P
  (m1 [this p11])
  (m2 [this p21 p22]))

(defrecord simple-P-impl []
  P
  (m1 [this p11] (str "simple m1 called with p11=" p11) nil ) ;; returning nil on all methods so that I don't get a lot of output and end up measuring how fast is Emacs at scrolling the REPL.
  (m2 [this p21 p22] (str "simple m2 called with p21=" p21 "and p22=" p22) nil))

(defrecord empty-record [])

(extend empty-record
  P {:m1 (fn [this p11] (str "extended m1 called with p11=" p11) nil)
     :m2 (fn [this p21 p22] (str "extended m2 called with p21=" p21 "and p22=" p22) nil)})

(def my-empty-record (empty-record.))
(def my-simple-P (simple-P-impl.))

(dotimes [_ 4] (time (dotimes [_ 1000000] (m1 my-simple-P "hello")))) 

(dotimes [_ 4] (time (dotimes [_ 1000000] (m1 my-empty-record "hello"))))
