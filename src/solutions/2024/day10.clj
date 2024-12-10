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
              y (range (count (first matrix)))]
          [[x y] (Character/digit (get (get matrix x) y) 10)])))

(def input (->> (slurp (io/resource "inputs/2024/day10.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse into grid
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions

;; ### get-valid-neighbors
;;
;; From a point, get a vector of all the neighbors that meet the
;; conditions to be a valid next node. It should exist (duh) and it
;; should be equal to the current value + 1.
;;
;; We use complex numbers here to simplify.
(defn get-valid-neighbours [visited grid level]
  (reduce
   (fn [acc [position d]]
     (let [next (vec (map + position d))]
       (if (= (grid next -1) level)
         (conj acc next)
         acc)))
   []
   (for [position visited
         direction [[1 0] [-1 0] [0 1] [0 -1]]]
     [position direction])))

;; ### walk
;;
;; Walk each direction with valid neighbours until we find a 9, then
;; recur until we find them all. Apply processing function `f` when we
;; determine what the next set of coords should be.
(defn walk [visited coords level f]
  (let [next (f (get-valid-neighbours visited coords level))]
    (if (or (empty? next) (= level 9))
      (count next)
      (recur next coords (inc level) f))))

;; ### solve
;;
;; General solver since both parts work the same. Get all the starting
;; points and walk them, applying our processing function `f` during
;; `walk`. Then sum the results.
(defn solve [input f]
  (->> input
       (filter (comp zero? val))
       (map #(walk [(key %)] input 1 f))
       (reduce +)))

;; ## Part 1
;;
;; Part 1 wants us to count all the "trailheads"; that is all 9s that
;; have a valid path from a 0 so we use `distinct` to get all the
;; unique endpoints.
(defn part-1
  [input]
  (solve input distinct))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 instead wants us to count all the paths to a trailhead so we
;; use `identity` to get all the paths.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input identity))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
