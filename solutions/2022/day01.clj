(ns solutions.2022.day01
    (:require [clojure.java.io :as io]
              [clojure.string :as str]))

(def input (->> (slurp (io/resource "inputs/2022/day01.txt"))
                (str/split-lines)
                (partition-by #(= "" %))
                (remove #(= "" (first %)))
                (map (fn [calories]
                       (reduce (fn [acc n]
                                 (+ acc (Integer/parseInt n))) 0 calories)))
                sort))

(defn part-1
  [input]
  (last input))

(defn part-2
  [input]
  (reduce + (take-last 3 input)))
