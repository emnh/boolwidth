(ns 
  clj.tex.tex
  (:use
    [clojure.pprint :only [pprint]]
    [clojure.contrib.str-utils]
    [clojure.contrib.def]
    [clojure.string :only [join split]]
    [clj.util.util :only (sprintf)]
    )
  (:require 
    [clj.sets.set :as sets]
    [clojure.pprint :as pprint]
    [clj.util.util :as util]
    [clojure.contrib.string :as cstr]
    [clojure.contrib.repl-utils :as repl-utils]
    )
  )

; TODO: move
(defn #^String capitalize
  "Converts first character of the string to upper-case, all other
  characters are not modified."
  [#^String s]
  (if (< (count s) 2)
    (.toUpperCase s)
    (str (.toUpperCase #^String (subs s 0 1))
         (subs s 1))))

(defn
  pprint-w
  [obj width]
  (binding
    [pprint/*print-right-margin* width]
    (.trim (with-out-str (pprint/pprint obj)))))

; intended for graphic renderings
(defprotocol
  PTex
  (to-tex-internal [x options] "Convert object x to tex, using options in map options")
  )

; intended to be marked up in thesis as code
(defprotocol
  PThesisForm
  (to-thesis-form-internal [x options] "Convert object x to tex, using options in map options")
  )

(defnk env
  [envname body :env-args nil :opts nil]
  "body is a vector of strings. \begin{env-args}[opts]"
  (->>
    [
     [
      "\\begin{" (name envname) "}"
      (if env-args (mapcat #(vector "{" % "}") env-args) "")
      (if opts ["[" opts "]"] "")
      ]
     [body]
     ["\\end{" (name envname) "}"]
     ]
    (map #(vector % "\n"))
    (flatten)
    (apply str)
    )
  )

; TODO: reduce figure and table redundancy

(defnk figure
  [body :caption nil :placement nil :label nil]
  (env 
    "figure" 
    [
     (if caption (sprintf "\\caption{%s}\n" caption) "")
     (if label (sprintf "\\label{%s}\n" (name label)) "")
     body
     ]
    :opts [placement]
    )
  )

(defnk table
  [body :caption nil :placement nil :label nil]
  (env 
    "table" 
    [
     (if caption (sprintf "\\caption{%s}\n" caption) "")
     (if label (sprintf "\\label{%s}\n" (name label)) "")
     body
     ]
    :opts [placement]
    )
  )


(defn minipage
  [width body]
  (env "minipage" body :env-args [width])
  )

(defnk tabular
  [body :format nil]
  (assert (not (nil? format)))
  (env "tabular" body :env-args [format])
  )

(defnk tabular*
  [body :format nil :width nil]
  (assert (not (nil? format)))
  (assert (not (nil? width)))
  (env "tabular*" body :env-args [width format])
  )

(defnk tabularx
  [body :format nil :width nil]
  (assert (not (nil? format)))
  (assert (not (nil? width)))
  (env "tabularx" body :env-args [width format])
  )

(defn tabular-row
  [& items]
  (str (str-join " & " items) " \\\\ \n")
  )

(def tabular-hline "\\hline\n")

(def nl "\n")

;(defn tex-f
;  [fname & args]
;  (sprintf "\\%s" (map #(sprintf "{}")))
;  )

(defn texinput
  [path]
  (sprintf "\\input{%s}" path)
  )

(defn graphic
  [fname & options]
  (let
    [opts (apply hash-map options)
     opts (cstr/join "," (map (fn [[key val]] (str (name key) "=" val)) opts))
     opts (if (seq opts) (str "[" opts "]") "")
     fname (util/strip-ext fname)
     ]
    (sprintf "\\includegraphics%s{%s}\n" opts fname)
    )
  )

;(defn label
;  )

; TODO: remove redundancy

(defn chapter
  [title & body]
  (printf "\\chapter{%s}\n" title)
  (doseq [i body] (println i))
  (println)
  )

(defn section
  [title & body]
  (printf "\\section{%s}\n" title)
  (doseq [i body] (println i))
  (println)
  )

(defn subsection
  [title & body]
  (printf "\\subsection{%s}\n" title)
  (doseq [i body] (println i))
  (println)
  )

(defn subsubsection
  [title & body]
  (printf "\\subsubsection*{%s}\n" title)
  (doseq [i body] (println i))
  (println)
  )

(defn cljcode
  [& code]
  (env "minted" (str-join "\n" code) :env-args ["clojure"])
  )

(def *cljcode-right-margin* 70)

(defnk cljcode-w
  [code :width *cljcode-right-margin*]
  (env "minted" 
       (pprint-w code width)
       :env-args ["clojure"])
  )

;(println (cljcode '(what is that you said my setnece is verly logn?)))
;(println (cljcode-w '(what is that you said my setnece is verly logn?) 20))

(defn to-thesis-form
  [x options]
  (str x)
  )

(extend-protocol
  PTex
  Object
  (to-tex-internal 
    [x options] 
;    (cljcode-w (to-thesis-form x options))
    (cljcode-w x)))

(defn tex-dispatch
  [obj & options]
  (cond 
    (and
      (coll? obj)
      (sets/subset-type? (first obj)))
      ::subsets
    true (class obj)
    )
  )

(defmulti
  to-tex
  "convert to tex"
  tex-dispatch
  )

(defmethod to-tex
  :default
  [x & options]
  (let
    [texrepr (to-tex-internal x (apply hash-map options))]
    texrepr))

(defn par
  [& body]
  (str
    "\\par" nl
    (apply str body) nl))

; TODO: make register
(defn texref
  "label is a keyword"
  [label]
  (let
    [label (name label)
     lbltype (cond 
               (.startsWith label "tab") "table "
               (.startsWith label "fig") "figure "
               true ""
               )
     ]
    (sprintf
      "%s\\ref{%s}"
      lbltype
      label
      )
    )
  )

(defmacro proto-tex2
  [proto]
  `(cljcode
    (repl-utils/get-source '~proto)
    )
  )

(defn proto-tex
  [proto]
  (let 
    [colct 3
     fmt "|llX|" ;(str "|" (cstr/repeat colct "X") "|")
     fullname (:on proto)
     title (str "Protocol " fullname)
;     header (sprintf "\\multicolumn{%d}{|c|}{%s} \\\\ \n" colct title)
     colheaders (tabular-row "Function" "Arguments" "Description")
     row (fn
           [{:keys [name arglists doc]}] 
           (tabular-row (str name) (str arglists) doc)
           )
     fns (apply str (map row (vals (:sigs proto))))
     body (str colheaders tabular-hline fns tabular-hline)
     ]
    (figure
      (tabularx body :width "\\textwidth" :format fmt)
      :caption title
      :placement "h"
      )
    )
  )

(defn
  extract-option
  [option args]
  (reduce 
    (fn
      [[new-opts new-args] arg]
      (cond
       (= arg option) [:last-was-opt new-args]
       (= new-opts :last-was-opt) [arg new-args]
       true [new-opts (conj new-args arg)]))
    [nil []]
    args))

(defmacro 
  def-tex-cmd
  [cmd]
  `(defn 
     ~cmd
     [& ~'more]
     (let
       [[opts# ~'more] (extract-option :opts ~'more)
        opts# (if 
               opts#
               (str "[" (cstr/join "," (map str opts#)) "]")
               "")
        ]
       (str
         "\\" ~(name cmd)
         opts#
         (apply str (map #(str "{" % "}") ~'more))))))

(def-tex-cmd todo)
(def-tex-cmd caption)
(def-tex-cmd label)
(def-tex-cmd hyperref)
(def-tex-cmd fraction)
(def-tex-cmd ensuremath)

(defn tex-def-label-name
  [label]
  (str "def-" label))

(defn tex-def-label
  [label_]
  (label (tex-def-label-name label_)))

(defn write-def
  [title label definition]
  (env 
    "defconcept"
    [(tex-def-label label) "\n" definition]
    :opts title))

(defn write-defn
  [title label definition]
  (env 
    "defn"
    [(tex-def-label label) "\n" definition]
    :opts title))

(defn ref-def
  [label text]
  (let
    [[href-text rest-text] (split text #"\(\\hyperref" 2)
     rest-text (if rest-text (str "(\\hyperref" rest-text) nil)]
    (str 
      "("
      (hyperref :opts [(tex-def-label-name label)] href-text) 
      rest-text
      ") ")))

;(ref-def "helo" (str "" (ref-def "root" "of something")))

(defn pluralize
  [phrase]
  (str phrase "s"))

(defn 
  def-tex-def-helper
  [label text definition]
  (let
    [text-plural (pluralize text)
     text-capital (capitalize text)
     text-capital-plural (pluralize text-capital)
     label-def (str label "-def")
     label-plural (pluralize label)
     label-capital (capitalize label)
     label-capital-plural (pluralize label-capital)
     def-title text-capital
     make-var #(intern *ns* %1 %2)
     ]
    (do
       (make-var (symbol label-def) (write-def def-title label definition))
       ; standard, e.g. (def graph...)
       (make-var (symbol label) (ref-def label text))
       ; plural, e.g. (def graphs ...)
       (make-var (symbol label-plural) (ref-def label text-plural))
       ; capitalized, e.g. (def Graph ...)
       (make-var (symbol label-capital) (ref-def label text-capital))
       ; capital pluralized, e.g. (def Graphs ...)
       (make-var (symbol label-capital-plural) #(ref-def label text-capital-plural)))))

(defmacro
  def-tex-def
  [label text definition]
  `(def-tex-def-helper ~(name label) ~text ~definition))

(defmacro
  def-tex-defn
  [fname letargs textbody definitionbody]
  (let
    [fname-def (symbol (str (name fname) "-def"))
     texlabel (name fname)
     args (apply vector (keys (apply hash-map letargs)))
     ]
    `(do
       (defn ~fname ~args (ref-def ~texlabel ~textbody))
       (def
         ~fname-def
         (let
           ~letargs
           (write-defn
             (capitalize ~textbody)
             ~texlabel
             ~definitionbody))))))

;(pprint
;(macroexpand-1 
(defmacro
  v
  "Tex variable"
  [varname]
  `(ensuremath ~(name varname)))

; unused, I think
(defmacro
  f
  "Tex function call"
  [form]
  `(math )
  )

;(print (to-tex false))
;(print (to-tex 2))
;(print (to-tex #{1 2 3}))
