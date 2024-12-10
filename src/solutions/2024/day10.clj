^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day10
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "10" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Alas, an easy grid problem in Advent of Grids...
;;
;; Today is yet another grid problem in this year's Advent of
;; ~~Grids~~ Code. Thankfully, this one is the easiest one yet. I
;; actually managed to solve part 2 first by accident; so thanks to my
;; undo history I was able to get my answer to part 2 up 6 seconds
;; after part 1. After the stress
;; of [yesterday](/src/solutions/2024/day09), this was VERY welcome.
;;
;; Today asks us to solve a simple traversal problem; starting at a 0
;; and moving in ascending order evenly, find all the 9s that it can
;; reach. Part 2 complicates it slightly by having you count all the
;; paths, which is what I initially misread the problem to be...
;;
;; First things first, let's load our input and parse it.
;;
;; Good old trusty range->coords is back again, with an additional
;; `:when` to handle the demo inputs that have dots in.
(defn range->coords
  [matrix]
  (into {}
        (for [x (range (count matrix))
              y (range (count (first matrix)))
              :when (not= \. (get (get matrix x) y))]
          [[x y] (Character/digit (get (get matrix x) y) 10)])))

(def input (->> (slurp (io/resource "inputs/2024/day10.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse into grid
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### starting-points
;;
;; From the grid, get all the valid points we can start at (the
;; zeroes)
(defn starting-points [coords]
  (->> coords
       (filter #(zero? (second %)))
       (map first)))

;; ### get-valid-neighbors
;;
;; From a point, get a vector of all the neighbors that meet the
;; conditions to be a valid next node. It should exist (duh) and it
;; should be equal to the current value + 1.
(defn get-valid-neighbors [coords current]
  (reduce
   (fn [neighbours point]
     (let [next-point (map + current point)
           next (get coords next-point)]
       (if (and next
                (= next (inc (get coords current))))
         (conj neighbours next-point)
         neighbours)))
   []
   [[1 0] [-1 0] [0 1] [0 -1]]))

;; ### paths-to-nine
;;
;; From a starting point, find all the paths to a 9. We do this by
;; walking the path recursively checking all valid neighbours and
;; keeping track of where we've been and our current total so we can
;; quit when we see a 9.
(defn paths-to-nine
  ([coords start]
   (paths-to-nine coords start 0 #{start} #{}))
  ([coords pos curr-val visited endpoints]
   (if (= curr-val 9)
     (conj endpoints pos)
     (->> (get-valid-neighbors coords pos)
          (reduce
           #(paths-to-nine coords %2 (inc curr-val) (conj visited %2) %1)
           endpoints)))))

;; ### count-paths-from
;;
;; The part 2 solver. Recurse through the path from each starting
;; point and count any path to a 9.
(defn count-paths-from [coords pos curr-val visited]
  (if (= curr-val 9)
    1
    (->> (get-valid-neighbors coords pos)
         (map #(count-paths-from coords % (inc curr-val) (conj visited %)))
         (reduce + 0))))

;; ## Part 1
;;
;; Part 1 wants us to count all the "trailheads"; that is all 9s
;; that have a valid path from a 0.
(defn part-1
  [input]
  (->> input
       starting-points
       (mapcat #(paths-to-nine input %))
       count))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 instead wants us to count all the paths to a trailhead.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> input
       starting-points
       (map #(count-paths-from input % 0 #{%}))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
