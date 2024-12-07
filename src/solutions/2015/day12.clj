^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day12
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.data.json :as json]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "12" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; "Oh, this is done already?"
;;
;; I see the trend of these older years being quite easy continues to
;; hold. Solving this after Day 6 of 2024 is /quite/ the
;; difference. If I was doing this at the time, I'd probably be on the
;; leaderboard for part 1...
;;
;; Today has us doing some fun with JSON; part 1 wants us to find all
;; the numbers and sum them and part 2 wants to do the same but ignore
;; any object that has a property value of "red".
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day12.txt")) ;; Load the resource
                str/trim))                                    ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### solve
;;
;; Both parts are solved in the exact same way, the only difference is
;; part 2 strips out some of the numbers. Both are trivially solved by
;; ignoring the JSON completely and instead just using regex to get
;; all the numbers and sum them up.
(defn solve
  [input]
  (apply + (map parse-long (re-seq #"\-?\d+" input))))

;; ### remove-red-objects
;;
;; The change to part 2; we walk through the JSON and only return an
;; object if it doesn't have a property value of "red".
(defn remove-red-objects [data]
  (walk/postwalk
   #(when-not (and (map? %) (some (partial = "red") (vals %)))
      %)
   data))

;; ## Part 1
;;
;; Solve using our general solver
(defn part-1
  [input]
  (solve input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Solve with the extra addition of removing red objects
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve (-> input json/read-str remove-red-objects json/write-str)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
