^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day06
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "06" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Another of my most hated of all the days...
;;
;; No matter how many times I do these, I always end up with an ugly
;; imperative solution. These "compute some grid" problems always
;; throw me through a loop (pun somewhat intended...).
;;
;; For that reason, I apologise if some of the explanations are a bit
;; terse; I did give up a bit.
;;
;; Today has us trying to work out a guard's movements based on:
;; - He can only move in the facing direction
;; - He moves until he hits an obstacle `#`
;; - When he hits one, he turns 90 degrees clockwise
;;
;; The pattern continues until he hits an edge of the grid. Part 1 has
;; us calculate all the unique spots the guard moves over; and part 2
;; then uses this as input to try and work out where we can place
;; another obstacle to get the guard stuck in a loop.
;;
;; First things first, let's load our input and parse it
;;
;; We end up with a map containing all the walls, the starting
;; position and the bounds of the grid (used for bounds checks).
{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-input [input]
  (let [lines (str/split-lines input)
        [width height] [(count (first lines)) (count lines)]
        positions (for [y (range height)
                        x (range width)
                        :let [char (get-in lines [y x])]
                        :when (not= \. char)]
                    [[y x] char])]
    {:walls (->> positions
                 (filter #(= \# (second %)))
                 (map first)
                 set)
     :start (->> positions
                 (filter #(= \^ (second %)))
                 ffirst)
     :bounds [(dec height) (dec width)]}))
{:nextjournal.clerk/visibility {:result :show}}
;; Then we can just parse
(def input (->> (slurp (io/resource "inputs/2024/day06.txt")) ;; Load the resource
                parse-input))                                 ;; Parse the grid

;; We also keep a simple map of all the rotations
(def rotations {[0 1] [1 0], 
                [-1 0] [0 1], 
                [1 0] [0 -1], 
                [0 -1] [-1 0]})
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### in-bounds?
;;
;; A simple bounds checker to ensure the coordinates are inside the
;; grid.
(defn in-bounds? [[y x] [max-y max-x]]
  (and (<= 0 y max-y)
       (<= 0 x max-x)))

;; ### positions-in-dir
;;
;; Starting at the current position, compute all the valid positions
;; in the given direction.
(defn positions-in-dir [pos dir bounds]
  (->> (iterate #(mapv + % dir) pos)
       (take-while #(in-bounds? % bounds))))

;; ### check-loop?
;;
;; Used in part 2 to understand if we can place a wall at the target
;; to create a loop. There's a fair amount of duplication between this
;; and part 1 but honestly I gave up once I got a solution that ran
;; fast enough.
;;
;; We basically just walk the path as in part 1 but with an extra wall
;; in place, then follow it to see if we end up in a loop (by walking
;; the same as a previous path)
(defn check-loop? [{:keys [walls start bounds]} target-pos]
  (let [test-walls (conj walls target-pos)]
    (loop [pos start
           dir [-1 0]
           stops #{}]
      (let [next-pos (first (rest (positions-in-dir pos dir bounds)))]
        (if (nil? next-pos)
          false
          (if (test-walls next-pos)
            (if (stops [pos dir])
              true
              (recur pos (rotations dir) (conj stops [pos dir])))
            (recur next-pos dir stops)))))))

;; ## Part 1
;;
;; Part 1 has us just walk the path and count all the unique stops. Since we also use this info in part 2, we make the function return the data
(defn part-1
  [{:keys [walls bounds start]}]
  (loop [pos start
         dir [-1 0]
         seen #{start}]
    (let [next-pos (first (rest (positions-in-dir pos dir bounds)))]
      (if (nil? next-pos)
        seen
        (if (walls next-pos)
          (recur pos (rotations dir) seen)
          (recur next-pos dir (conj seen next-pos)))))))

;; Which gives our countable answer
{:nextjournal.clerk/visibility {:code :show :result :show}}
(count (part-1 input))

;; ## Part 2
;;
;; Part 2 has us test each of these stops for a new obstacle to insert
;; and testing if we end up in a loop. We can speed this up with
;; `pmap` since none of the data is linked.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> (part-1 input)
       (pmap #(check-loop? input %))
       (filter true?)
       count))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
