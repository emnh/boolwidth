(ns clj.util.introspect
  (:import
    java.lang.reflect.Field
    )
  )

(defmulti get-fields
  "Get fields of Java object"
  class)

(defmethod get-fields Object
  [obj]
  (get-fields (class obj))
  )

(defmethod get-fields Class
  [cls]
  (let
    [classes (take-while #(not= nil %) (iterate #(.getSuperclass #^Class %) cls))]
    (apply concat (map #(seq (.getDeclaredFields #^Class %)) classes))
    )
  )
  
(defn to-map
  "Convert Java object to map with field names as keys"
  [obj]
  (let
    [fields (get-fields obj)
     field-map (fn [a-map #^Field field] (assoc a-map (.getName field) (.get field obj)))]
    (dorun (map #(.setAccessible #^Field % true) fields))
    (reduce field-map {} fields)
    )
  )

