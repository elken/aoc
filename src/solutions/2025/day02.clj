^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day02
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "02" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Today feels easier than yesterday, idk who's writing these tbh. I
;; solved this one in my head while on my morning routine, a nice and
;; simple regex problem with the second part neatly predicted!
;;
;; The problem today has us taking an input of range groups and
;; finding repeated digits within the range. Part 1 wants to check for
;; twice-repeats and part 2 wants _at least_ twice-repeats.
;;
;; First things first, let's load our input and parse it
(def input (-> (slurp (io/resource "inputs/2025/day02.txt")) ;; Load the resource
               (str/split #",")))                            ;; Split into groups

;; ## Functions
;;
;; ### str->range
;;
;; `str->range` takes a string eg `11-22` and returns an inclusive
;; range (`range` by default doesn't include the upper bound) of that
;; group.
(defn str->range [s]
  (let [[start end] (map parse-long (re-seq #"\d+" s))]
    (range start (inc end))))

;; ### find-repeats
;;
;; `find-repeats` takes a regular expression and a range string and
;; looks to apply that regex to get all the matches for that regex.
;;
;; We call out to [`str->range`](#str->range) here too.
(defn find-repeats [re range]
  (filter #(re-find re (str %)) (str->range range)))

;; ### solve
;;
;; `solve` is our general solver, if you've followed my solutions at
;; all you'll know I really aim to build these. I love boiling both
;; parts down to a general solution and then having `part-1` and
;; `part-2` basically just be calling `solve`. Well today is no
;; different.
;;
;; We take the input and the regex we're checking against, find all
;; the repeats then look to sum them up. `mapcat` saves the day here
;; by saving us from having to remove empty lists and applying
;; `flatten`.
(defn solve [input re]
  (->> input
       (mapcat (partial find-repeats re))
       (apply +)))

;; ## Part 1
;;
;; Both parts are identical, so I'll forego specific explanations and
;; just link to regexr as that explains the regex and gives some
;; examples.
;;
;; [Regex explanation](https://www.regexr.com/8ih27)
(defn part-1
  [input]
  (solve input #"\b(\d+)\1(?!\1)\b"))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And as above, just a regexr link to explain the regex and give some
;; examples.
;;
;; [Regex explanation](https://www.regexr.com/8ih2a)
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input #"\b(\d+)\1+\b"))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
