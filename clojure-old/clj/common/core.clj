(ns clj.common.core
  (:refer-clojure :exclude [name])
  )

(defn name
  "Return name of object for human identification"
  [x]
  (:name x)
  )

(defn named
  "Return object with name"
  [x name]
  (assoc x :name name)
  )

(defprotocol
  PIndexed
  (index [x collection] "Return indexed position of object in collection")
  )
