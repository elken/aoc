(ns solutions.2022.day04
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn inclusive-range [start end]
  (range start (inc end)))

(defn group-coords [coord]
  (partition 2 (re-seq #"\d+" coord)))

(defn coord->ranges [coord]
  (apply inclusive-range (map parse-long coord)))

(defn create-sets [coord]
  (->> coord
       group-coords
       (map (comp set coord->ranges))))

(def input (->> (slurp (io/resource "inputs/2022/day04.txt"))
                (str/split-lines)
                (map create-sets)))

(defn compute-sets [input op]
  (transduce (comp (map #(apply op %))
                   (filter true?)
                   (map {false 0 true 1})) + 0 input))

(defn part-1
  [input]
  (compute-sets input #(or (set/subset? %1 %2)
                           (set/subset? %2 %1))))

(defn part-2
  [input]
  (compute-sets input #(> (count (set/intersection %1 %2)) 0)))

(deftest test-answers
  (t/is (= 305 (part-1 input)))
  (t/is (= 811 (part-2 input))))
