(ns question
  (:use [clojure.walk] [clojure.template])
  )

;(use 'clojure.template)

(defprotocol
  A
  (foo [x])
  )

(defprotocol
  B
  (bar [x])
  )

;  '(bar [x] (+ (foo x) 1))

(macroexpand '(B-impl))

;(extend
;  ::A
;  B
;  {
;  :bar (fn [x] (+ (foo x) 1))
;   }
;  )

(defn p-instantiate-template
  [substitution-map form]
  (clojure.walk/prewalk
   (fn [x] (if (and (sequential? x) (= (first x) 'clojure.core/unquote))
         (substitution-map (second x))
         x))
   form))

(defmacro p-template
  [substitutions form]
  (let [substitution-map (into {} (map (fn [[a b]]
                     [(list 'quote a) b])
                       (partition 2 substitutions)))]
    `(p-instantiate-template ~substitution-map (quote ~form))))

(defmacro p-etemplate [s f] `(eval (p-template ~s ~f)))

(def B-impl '(bar [x] (+ (foo x) 3)))

;(eval (p-template
(p-etemplate
;  [impl '(bar [x] (+ (foo x) 3))]
  [impl B-impl]
  (deftype
    C [d]
    A
    (foo [x] 2)
    B
    ~impl
    )
    )
;)

(bar (C. 5))

(defmacro p-apply-template [a e v] `(eval (apply-template '~a '~e ~v)))

(p-apply-template
  [impl] 
  (deftype
    D [d]
    A
    (foo [x] 2)
    B
    impl
    )
  [B-impl]
  )

(bar (D. 5))


; what's the easiest way of using mixin in deftype? I want to write (deftype T [d] B (B-impl)) and have B-impl expand to the default implementation of B.
; the problem seems to be that deftype is a macro and is evaluated before B-impl
; is it easy to write a macro that takes a form, expands all macros ending with -impl, and then returns the form for further evaluation?
