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
  (let [results (let [src (source-dirs)]
                  (-> ((resolve 'pod.borkdude.clj-kondo/run!) {:lint src})
                      (doto (resolve 'pod.borkdude.clj-kondo/print!))))]
    (when (-> results :findings seq)
      (System/exit 1))))

(defn antq [& args]
  (apply clojure "-Sdeps"
         (pr-str '{:deps {com.github.liquidz/antq {:mvn/version "1.3.0"}}})
         "-M" "-m" "antq.core"
         args))
