(ns aoc.YEAR.dayDAY
    (:require [clojure.java.io :as io]
              [clojure.string :as str]))

(def input (->> (slurp (io/resource "inputs/dayDAY.txt"))
                (str/split-lines)))

(defn part-1
  [input]
  (println "Part 1"))

(defn part-2
  [input]
  (println "Part 2"))

(deftest test-answers
  (t/is (= 4 (part-1 input)))
  (t/is (= 4 (part-2 input))))
