(ns luminare.cli
  (:require
   [babashka.process :as p]
   [clojure.string :as str]
   [docopt.core :as docopt])
  (:refer-clojure :exclude [cycle]))

(defn $
  [& args]
  (-> args
      p/sh
      p/check
      :out
      str/split-lines))

(def LEVELS 3)

(defn ->level
  [value]
  (-> value
      (/ (/ 100 LEVELS))
      int
      (min (dec LEVELS))))

(defn ->percent
  [level]
  (-> (/ (dec LEVELS))
      (* level 100.00)
      (->> (format "%.2f"))))

(defn get-level
  []
  (-> ($ "light")
      first
      Float/parseFloat
      ->level))
#_(get-level)

(defn set-level
  [level]
  ($ "light" "-S" (->percent level)))

(defn cycle
  [level]
  (-> level
      inc
      (mod LEVELS)))

;; Ref: http://docopt.org/
(def usage
  "lum - manage lumination

Usage:
  lum cycle [--levels=<levels>]

Options:
  --levels=<levels>  Number of levels to cycle through [default: 3]
  ")

(defn dispatch
  [arg-map]
  (prn :arg-map arg-map)

  (when-let [levels (arg-map "--levels")]
    (alter-var-root #'LEVELS (constantly (Integer/parseInt levels))))
  (prn LEVELS)

  (cond
    (arg-map "cycle")
    (-> (get-level) cycle set-level)))

(defn -main
  [& args]
  (prn :args args)
  (docopt/docopt
   usage
   args
   dispatch))
