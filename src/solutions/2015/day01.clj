^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day01
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "01" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Well, here we are! The first ever Advent of Code!
;;
;; This is my 3rd year, so by now I'm pretty familiar with things; and this is a
;; classic first day puzzle. Nothing remotely complex here; but a fun one
;; nonetheless.
;;
;; In short given a list of parens and a counter starting at 0, `(` adds one to
;; a counter and `)` decrements one to a counter; produce both the final floor
;; you end up & the first floor that ends up in the basement (`-1`).
;;
;; Initially, I came up with a neat solution for part 1 to just run
;; `frequencies` and sum the result; but I couldn't come up with something
;; equally clever for part 2 so I opted to just refactor both parts to use
;; `compute-path`.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day01.txt"))
                str/split-lines ;; Split into lines
                first           ;; Get the first input
                vec))           ;; Split into a list

;; Our "solver" function takes the input vector of the brackets and computes the
;; output path; optionally short-circuiting if you pass in a limit. `reduce-kv`
;; is used here as a lazy way to get the position as we go.
{:nextjournal.clerk/visibility {:result :hide}}
(defn- compute-path
  ([input]
   (compute-path input nil))
  ([input limit]
   (reduce-kv
    (fn [acc idx curr]
      (if (= acc limit)
        (reduced idx)
        (+ acc (if (= \( curr) 1 -1))))
    0
    input)))

;; ## Part 1
;; Part 1 just wants us to compute the result, so we can just run like this
(defn part-1
  [input]
  (compute-path input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; ## Part 2
;; For part 2 we have to do something slightly different, and print the position
;; of the first character where the point is in the "basement"; in other words
;; when the count is -1. So we add an optional limit argument and call `reduced`
;; when we hit it.
(defn part-2
  [input]
  (compute-path input -1))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
