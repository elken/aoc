^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day09
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.core.match :refer [match]]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "09" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; I had a moment of "why did I randomly stop this year on day 9?" until I realised what the problem was...
;;
;; I'm really not a fan of these problems, but after some toing and
;; froing; I managed to wrangle a decent implementation of an [A*
;; search](https://en.wikipedia.org/wiki/A*_search_algorithm).
;;
;; Today in comparison to some later years is actually quite simple,
;; we have a bunch of directions we have to parse to find the shortest
;; route hitting all destinations.
;;
;; First things first, let's load our input and parse it
;; 
;; To simplify the implementation, we parse the input into a hash of
;; all the possible movements.
;;
;; We do this by using `core.match` on every line to get the node
;; names and the distance, and use those to compose the map using
;; `assoc-in`.
(defn parse-input [input]
  (reduce
   (fn [acc line]
     (match (str/split line #"\s+")
            [start _ end _ distance]
            (-> acc
                (assoc-in [start end] (parse-long distance))
                (assoc-in [end start] (parse-long distance)))))
   {}
   input))

;; Then we call that function to end up with a lovely hash
(def input (->> (slurp (io/resource "inputs/2015/day09.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input))                                 ;; Parse the lines into a map
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### permutations
;; 
;; A simple implementation of getting all the possible variations of
;; our hash; to give us the route list.
(defn permutations [s]
  (if (empty? s) 
    '(()) 
    (for [h s t (permutations (disj s h))] (cons h t))))

;; Which looks like
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(permutations (set (keys input)))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; ### shortest-path
;;
;; From our possible list of destinations, we can then walk every
;; route and compute the total distance using reduction.
;;
;; First we partition the permutations into pairs of routes (a
;; journey) and reduce over each paired journey to get the total.
;;
;; We do this for each permutation, then return the results.
(defn shortest-path [distances]
  (reduce
   (fn [acc path]
     (conj acc (->> 
                (reduce
                 (fn [sum [a b]]
                   (+ sum (get-in distances [a b])))
                 0
                 (partition 2 1 path)))))
   []
   (permutations (set (keys distances)))))

;; ## Part 1
;; Part 1 just wants us to find the smallest value, so we apply min to the total list of paths
(defn part-1
  [input]
  (apply min (shortest-path input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 wants us to find the largest value, so we apply max to the total list of paths
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (apply max (shortest-path input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
