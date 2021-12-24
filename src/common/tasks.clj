(ns common.tasks
  (:require [babashka.pods :as pods]
            [babashka.tasks :refer [clojure]]))

(defn clj-kondo []
  (pods/load-pod "clj-kondo")
  (require 'pod.borkdude.clj-kondo)
  (let [results (eval '(let [src (-> (slurp "project.edn")
                                     edn/read-string
                                     (:src-dirs ["src"]))]
                         (-> (pod.borkdude.clj-kondo/run! {:lint src})
                             (doto pod.borkdude.clj-kondo/print!))))]
    (when (-> results :findings seq)
      (System/exit 1))))

(defn antq [& args]
  (apply clojure "-Sdeps"
         (pr-str '{:deps {com.github.liquidz/antq {:mvn/version "1.3.0"}}})
         "-M" "-m" "antq.core"
         args))
