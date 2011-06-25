(ns 
  clj.tex.algo
  (:refer-clojure :exclude [while])
  (:use
    [clj.tex.tex :only [def-tex-cmd]]
    )
  (:require 
    [clj.tex.tex :as tex]
    [clj.sets.set :as sets]
    [clojure.pprint :as pprint]
    [clj.util.util :as util]
    [clojure.contrib.string :as cstr]
    [clojure.contrib.repl-utils :as repl-utils]
    )
  )


(defn algorithm
  [& body]
  (tex/env
    "bwfunction"
    body))


(defn s [& statement] (str (apply str statement) "\\;"))
; algorithm environment
(def-tex-cmd Input)
(def-tex-cmd Return)
(def-tex-cmd Data)
(def-tex-cmd While)
(def-tex-cmd Begin)
