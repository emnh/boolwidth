(defproject boolwidth "1.0"
  :description "FIXME: write"
  :plugins [
            ;[lein-localrepo "0.5.2"]
            [lein-light "0.0.34"]
            [lein-tarsier "0.10.0"]
            ]
  :java-source-paths ["../src"]
  :source "src"
  ;:aot :all
  ; new dependencies can be found at http://search.maven.org/ or http://clojars.org
  :dependencies
          [
           [methods-a-la-carte "0.1.1"]
           [org.clojure/clojure "1.5.1"]
;           [org.clojure/clojure-contrib "1.2.0"]
           ;[clojure.contrib.java-utils ""]
           [net.java.dev.glazedlists/glazedlists_java15 "1.8.0"]
           [org.prefuse/prefuse "beta-20071021"]
           [xmlpull/xmlpull "1.1.3.1"]
           [xpp3/xpp3_min "1.1.4c"] ; implicit
           [com.thoughtworks.xstream/xstream "1.3.1"]
;           [org.clojure/clojure "1.3.0-master-SNAPSHOT"]
;           [org.clojure.contrib/complete "1.3.0-SNAPSHOT" :classifier "bin"]
          ]
  :dev-dependencies
  [
;   [org.clojars.ibdknox/lein-nailgun "1.1.1"]
;   [org.clojars.ato/nailgun "0.7.1"]
;   [vimclojure/server "2.3.6"]
;   [org.clojars.brandonw/lein-nailgun "1.0.0"]
;   [org.clojars.emh/vimclojure "2.2.0-SNAPSHOT"]
;   [clojuresque "1.4.0-SNAPSHOT"]
  ]
  )
