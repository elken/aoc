(ns solutions.2022.day01
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [clojure.string :as str]))

(def input (->> (slurp (io/resource "inputs/2022/day01.txt"))
                (str/split-lines)
                (partition-by str/blank?)
                (remove #(str/blank? (first %)))
                (map #(transduce (map parse-long) + 0 %))
                (sort >)))

(defn part-1
  [input]
  (first input))

(defn part-2
  [input]
  (transduce (take 3) + 0 input))

(deftest test-answers
  (t/is (= 70296 (part-1 input)))
  (t/is (= 205381 (part-2 input))))
