^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day02
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "02" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;; And it begins already...
;;
;; This one was deceptively hard at first glance, and I /definitely/
;; got myself confused trying to be too clever...
;;
;; Today's problem sees us trying to parse a list of lists and work
;; out which lists meet these 2 conditions:
;;
;; - Each list is sorted (either in ascending or descending order)
;; - Each list's difference obeys `(<= 1 difference 3)`
;;
;; Part 1 has us calculate this, and part 2 has us calculate with the
;; caveat of also handling the case of removing a single number.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day02.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (map #(map read-string (re-seq #"\d+" %)))))  ;; Parse into a list of number lists

{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; We need to define a couple of helper functions to make solving this easy.
;;
;; ### safe?
;; First off we have the general `safe?` function to identify if a list is safe.
;;
;; We use a clever `map` trick (since `map` lets you iterate over n-many
;; lists at once) to get all the differences, and we ensure every
;; difference is either negative or positive (thanks again to the
;; commutative property of subtraction).
;;
;; We then do a simple check to verify if the difference is between
;; our range, taking care to ensure we pass to `abs` first to prevent
;; false positives.
(defn safe? [report]
  (let [differences (map - (rest report) report)]
    (and
     (or (every? pos? differences)
         (every? neg? differences))
     (every? #(<= 1 (abs %) 3) differences))))

;; ### almost-safe?
;; Next we have the part 2 solving `almost-safe?`
;; function, which first checks if it passes the `safe?` function.
;;
;; We then compute all the combinations of the list with 1 element
;; removed by generating a list of all the indexes with `(range (count
;; report))`
{:nextjournal.clerk/visibility {:result :show}}
(range (count (first input)))
{:nextjournal.clerk/visibility {:result :hide}}
;; After that we `map` over that list and `concat` another list of
;; every element before that and after that index.
{:nextjournal.clerk/visibility {:result :show}}
(let [report (first input)]
  (map #(concat (take % report) (drop (inc %) report))
       (range (count report))))
{:nextjournal.clerk/visibility {:result :hide}}
;; With all of these combinations, we then simple do `some safe?` over
;; all of them, which will tell us if any results are truthy.
(defn almost-safe?
  [report]
  (or (safe? report)
      (some safe?
            (map #(concat (take % report) (drop (inc %) report))
                 (range (count report))))))
;; ### solve
;; Since both parts operate the same way, we can write a
;; simple `solve` function to run through and prevent some
;; duplication.
(defn solve
  [input fn]
  (->> input
       (map fn)
       (filter true?)
       count))

;; ## Part 1
;; Part 1 just wants us to check if any of them are safe, so we solve thus:
(defn part-1
  [input]
  (solve input safe?))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 wants us to check if any of them are almost safe, so we solve thus:
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input almost-safe?))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
