^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day04
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "04" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; The inevitable graph problem... I have been dreading this day, but
;; it surprised me for once. I am still praying that Eric isn't cruel
;; enough to add any _more_ in, but I didn't hate it today.
;;
;; Today's problem has us trying to find paper rolls with no more than
;; 4 neighbours for part 1, and part 2 complicates it by adding the
;; fact rolls can be removed...
;;
;; ## Functions
;;
;; ### range->coords
;;
;; The classic graph problem input parser I use all the time in
;; different variations.
;;
;; This time is not really unique, barring the fact I've found a way
;; to further simplify it. And we only care about tracking where the
;; paper is, not the gaps.
;;
;; We also want the input to be a `set` for performance later (see [`remove-rolls`](#remove-rolls))
(defn range->coords [input]
  (set (for [[y line] (map-indexed vector input)
             [x char] (map-indexed vector line)
             :when (= char \@)]
         [x y])))

;; ### neighbours
;;
;; For all 8 directions (4 cardinals, 4 diagonals) compute all the
;; neighbours of a given `roll` in the `input`.
(defn neighbours [input roll]
  (->> [[-1 -1] [0 -1] [1 -1] [-1 0] [1 0] [-1 1] [0 1] [1 1]]
       (map #(mapv + roll %))
       (filter input)))

;; ### remove-rolls
;;
;; For the given input, find and remove all rolls that have 4 or more
;; neighbouring rolls and convert to a set.
;;

(defn remove-rolls [input]
  (set (filter #(>= (count (neighbours input %)) 4) input)))

;; The reason we use a set here is accessing it is `O(1)` instead of
;; `O(n)` for a vector/list, as well as being able to call them like a
;; function to check membership easily.
;;
;; e.g.

{:nextjournal.clerk/visibility {:result :show}}
(#{1 2 3} 2)
(#{1 2 3} 5)
{:nextjournal.clerk/visibility {:result :hide}}

;; ### fixed-point
;;
;; Our part-2 solver that simply calls the part-1 function
;; recursively (using `recur` for TCO, yes Clojure [does support
;; it](https://clojure.org/about/functional_programming#_recursive_looping)).
;;
;; I was fully expecting this to be super complex.
(defn fixed-point [input]
  (let [result (remove-rolls input)]
    (if (= result input) result (recur result))))

;; ## Input
;;
;; Now we can load and parse our input

(def input (->> (slurp (io/resource "inputs/2025/day04.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse to coords

;; ## Part 1
;;
;; Part 1 just wants us to count how many rolls have fewer than 4
;; neighbours.
(defn part-1
  [input]
  (- (count input) (count (remove-rolls input))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And part 2 is similar in structure to part 1, except this time we
;; count how many rolls function as more rolls are removed.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (- (count input) (count (fixed-point input))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
