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
{:nextjournal.clerk/visibility {:code :show :result :hide}}

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
;; We solve this using [Complex
;; Numbers](https://en.wikipedia.org/wiki/Complex_number) to easier
;; keep handle positions with direction. This gives us a massive
;; speedup as we have less to compute, and GREATLY simplifies the
;; traversal logic.

;; First things first, let's load our input and parse it
;;
;; We end up with a map containing all the walls, the starting
;; position and the bounds of the grid (used for bounds checks).
(defn parse-input [input]
  (let [lines (str/split-lines input)
        [w h] [(count (first lines)) (count lines)]
        points (for [y (range h)
                     x (range w)
                     :let [c (get-in lines [y x])]
                     :when (not= \. c)]
                 [[x y] c])]
    {:walls (->> points (filter #(= \# (second %))) (map first) set)
     :start (->> points (filter #(= \^ (second %))) ffirst)
     :bounds [(dec w) (dec h)]}))

{:nextjournal.clerk/visibility {:result :show}}
;; Then we can just parse
(def input (->> (slurp (io/resource "inputs/2024/day06.txt")) ;; Load the resource
                parse-input))                                 ;; Parse the grid

{:nextjournal.clerk/visibility {:result :hide}}
;; First things first we need to setup our complex number system.
;; Here `i` is the "imaginary unit".
;;
;; For both position and direction vectors `[x y]` represents `x + yi`
;; where `x` is the horizontal position and `y` is the vertical
;; position.
(def i [0 1])

;; In the case of "rotating" (multiplying two complex numbers in
;; reality), we have two complex numbers `a + bi` and `c + di`.
;;
;; The outcome of trying to calculate this works out to the below. By
;; rotating a complex number against `i`, we end up with the next
;; right-facing direction.
(defn rotate [[a b] [c d]]
  [(- (* a c) (* b d))
   (+ (* a d) (* b c))])

;; ## Functions
;; ### in-bounds?
;;
;; A simple bounds checker to ensure the coordinates are inside the
;; grid.
(defn in-bounds? [[x y] [max-x max-y]]
  (and (<= 0 x max-x)
       (<= 0 y max-y)))

;; ### traverse
;;
;; The "walker" function we use to walk the path and identify everything we've seen.
;;
;; Two main paths based on whether we're checking for loops or not:
;;
;; #### No loops (part 1)
;;
;; We record all the positions `seen` and stops in `stops`, when we
;; hit a grid boundary we return them. If the next position is a wall,
;; we recur with the new state and rotate, otherwise we just move
;; again.
;;
;; #### With loops (part 2)
;;
;; Here we return `true` as soon as we identify that we've found a
;; loop, and `false` if we hit a boundary. Otherwise, we use the same
;; movement logic as the first part.
(defn traverse
  [{:keys [walls bounds]} start loop?]
  (loop [pos start
         dir [0 -1]
         seen #{start}
         stops #{}]
    (let [next (map + pos dir)]
      (cond
        (or (not (in-bounds? next bounds))
            (and loop? (> (count stops) (* 4 (count walls)))))
        (if loop? false [seen stops])

        (walls next)
        (let [new-stops (conj stops [pos dir])]
          (if (and loop? (stops [pos dir]))
            true
            (recur pos (rotate dir i) seen new-stops)))

        :else (recur next dir (conj seen next) stops)))))

;; ### check-loop?
;;
;; Used in part 2 to understand if we can place a wall at the target
;; to create a loop.
;;
;; We basically just walk the path as in part 1 but with an extra wall
;; in place, then follow it to see if we end up in a loop (by walking
;; the same as a previous path)
(defn check-loop? [input pos]
  (traverse (update input :walls conj pos) (:start input) true))

;; ## Part 1
;;
;; Part 1 has us just walk the path and count all the unique stops. Since we also use this info in part 2, we make the function return the data
(defn part-1 [input]
  (first (traverse input (:start input) false)))

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
