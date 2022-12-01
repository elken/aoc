(ns solutions.2022.day01
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
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

(deftest test-answers
  (t/is (= 70296 (part-1 input)))
  (t/is (= 205381 (part-2 input))))
