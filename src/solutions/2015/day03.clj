^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day03
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "03" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Technically this should be brand new to us, but this is similar to a couple
;; of problems from 2022; so this was a breeze to work out. Couple of fiddly
;; issues at the end with getting the total exact though, as is always the case
;; with these coordinate problems.
;;
;; The problem boils down to an input of directions denoted by arrows, and given
;; this input you first work out how many points are visited more than once;
;; then given another visitor that performs the alternate action compute the
;; same.
;;
;; Part 2 had me confused for _quite_ a while on exactly how the robot is meant
;; to move, but I finally realized they're just alternating; meaning we can use
;; our glorious `transpose` hack of `(apply map list)`.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day03.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                first))                                       ;; Get the directions

;; ## Move to a house in a given direction
;; Copied almost verbatim from a later year, given a direction compute the next
;; move.
(defn move-house [[x y] direction]
  (condp = (str direction)
    "^" [x (inc y)]
    ">" [(inc x) y]
    "v" [x (dec y)]
    "<" [(dec x) y]
    [x y]))

;; ## Compute a route for an input
;; Given a list of inputs (so we can use a general solution for both parts)
;; compute the path and return the count of all distinct houses.
(defn compute-route [& inputs]
  (->> inputs
       (map
        (fn [input]
          (reduce
           (fn [history direction]
             (conj history (move-house (last history) direction)))
           [[0 0]]
           input)))
       (apply concat)
       distinct
       count))

;; ## Part 1
;; Part 1 is just computing the input as given to use, nothing else needed
(defn part-1
  [input]
  (compute-route input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 however requires us to transpose the input to give us alternating
;; partitions which we can then simply apply `compute-route` over
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> input
       (partition 2)
       (apply map list)
       (apply compute-route)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
