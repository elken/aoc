^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day02
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "02" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Not much difficulty here, just not much to make generic.
;;
;; Given a list of strings $l\times w\times h$, compute the surface area and
;; volume of the regular cuboids described.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day02.txt")) ;; Load the resource
                str/split-lines))                              ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Parse present
;; Quite simply given a present string, get the needed numbers from it, turn
;; them into actual numbers and sort them such that the first two elements
;; always represent the lowest side.
;;
;; From this we can always know what that side will be, and we don't need `min`
;; anywhere.
(defn- parse-present [present]
  (->> present
       (re-seq #"\d+")
       (map parse-long)
       sort))

;; ## Calculate area
;; Apply the formula to calculate the surface area for the cuboid thus $2\times(lw + lh + wh)$
(defn calculate-area [present]
  (let [[l w h] (parse-present present)]
    (+ (* 2 l w)
       (* 2 w h)
       (* 2 h l)
       (* l w))))

;; ## Calculate shortest distance
;; Apply the formula to calculate the shortest distance around the cuboid $(2 \times l) \times (2 \times w)$ plus the volume $l \times w \times h$
(defn calculate-shortest-distance [present]
  (let [[l w h] (parse-present present)]
    (+ l l w w (* l w h))))

;; ## Part 1
;;
;; Lastly we apply the area calculation to all the presents and sum the results
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (apply + (map calculate-area input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And for part 2 we just compute the shortest distance for all the presents and sum the results
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (apply + (map calculate-shortest-distance input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
