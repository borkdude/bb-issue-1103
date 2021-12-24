(ns common.tasks
  (:require [babashka.fs :as fs]
            [babashka.pods :as pods]
            [babashka.tasks :refer [clojure]]
            [clojure.edn :as edn]))

(defn source-dirs []
  (if (fs/exists? "project.edn")
    (-> (slurp "project.edn")
        edn/read-string
        (:src-dirs ["src"]))
    ["src"]))

(defn clj-kondo []
  (pods/load-pod "clj-kondo")
  (require 'pod.borkdude.clj-kondo)
  (let [lint-fn (resolve 'pod.borkdude.clj-kondo/run!)
        print-fn (resolve 'pod.borkdude.clj-kondo/print!)
        results (let [src (source-dirs)]
                  (-> (lint-fn {:lint src})
                      (doto print-fn)))]
    (when (-> results :findings seq)
      (throw (ex-info "Lint warnings found, exiting with status code 1" {:babashka/exit 1})))))

(defn foo [x]) ;; causes lint warning

(defn antq [& args]
  (apply clojure "-Sdeps"
         (pr-str '{:deps {com.github.liquidz/antq {:mvn/version "1.3.1"}}})
         "-M" "-m" "antq.core"
         args))
