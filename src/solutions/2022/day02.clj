(ns solutions.2022.day02
    (:require [clojure.java.io :as io]
              [clojure.test :as t :refer [deftest]]
              [clojure.string :as str]))

(def input (->> (slurp (io/resource "inputs/2022/day02.txt"))
                (str/split-lines)))

(def scores {"A X" 4   ;; Rock, Rock
             "A Y" 8   ;; Rock, Paper
             "A Z" 3   ;; Rock, Scissors
             "B X" 1   ;; Paper, Rock
             "B Y" 5   ;; Paper, Paper
             "B Z" 9   ;; Paper, Scissors
             "C X" 7   ;; Scissors, Rock
             "C Y" 2   ;; Scissors, Paper
             "C Z" 6}) ;; Scissors, Scissors

(def results {"A X" 3   ;; Scissors, Lose
              "A Y" 4   ;; Rock, Draw
              "A Z" 8   ;; Paper, Win
              "B X" 1   ;; Rock, Lose
              "B Y" 5   ;; Paper, Draw
              "B Z" 9   ;; Scissors, Win
              "C X" 2   ;; Paper, Lose
              "C Y" 6   ;; Scissors, Draw
              "C Z" 7}) ;; Rock, Win

(defn part-1
  [input]
  (transduce (map scores) + 0 input))

(defn part-2
  [input]
  (transduce (map results) + 0 input))

(deftest test-answers
  (t/is (= 9759 (part-1 input)))
  (t/is (= 12429 (part-2 input))))
