^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day05
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "05" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; So at first this one looked quite awkward until I realized everything can be expressed with our lord and saviour; _Regular Expressions_!
;;
;; The long and short of this one is given a number of rules, count which
;; strings match each one. The rules will be explained in detail in their
;; relevant functions, but in the general respect both parts are equivalent and
;; you can't generalize a solution (since both parts use different rules).
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day05.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Rules
;; ### Has more than 3 occurances of vowels
;;
;; This is the only one not solved with a _pure_ regex, purely because what I
;; was producing was getting unwieldly and I'd one day like to be able to read
;; it again; so we just match all vowels and count the results.
(defn has-vowels? [input]
  (>= (count (re-seq #"[aeiou]" input)) 3))

;; ### Has a letter that occurs more than twice in a row
;;
;; Capture any occurance of any single character (`(.)`) that occurs again next
;; to it (the first capture group `\1`) 1 or more times (`+`).
(defn has-repeating-letter? [input]
  (re-find #"(.)\1+" input))

;; ### Doesn't contain a bad string
;;
;; Could be argued that this also isn't _pure_ regex, but who's keeping track.
;; Check for any occurance of the "bad" strings `ab`, `cd`, `pq` and `xy` and
;; make sure `re-find` returns `nil`.
(defn no-bad-strings? [input]
  (nil? (re-find #"ab|cd|pq|xy" input)))

;; ### Has a pair that appaears more twice without overlapping
;;
;; Capture any occurance of 2 characters (`(.{2})`) with other characters after (`.*`) then ensure it occurs once
;; more (`\1`)
(defn non-overlapping-pair? [input]
  (re-find #"(.{2}).*\1" input))

;; ### Has one repeating letter with another letter between it
;;
;; Capture a single character (`(.)`) followed by another letter (`[a-z]`) which
;; is then followed by the first capture (`\1`)
(defn repeat-with-letter? [input]
  (re-find #"(.)[a-z]\1" input))

;; ## Part 1
;;
;; Both parts are identical in that they just apply and count the valid strings,
;; so we can use `every-pred` to find the "nice" strings
(defn part-1
  [input]
  (count
   (filter (every-pred no-bad-strings? has-repeating-letter? has-vowels?) input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And for part 2 we apply different rules with the same outcome
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (count
   (filter (every-pred non-overlapping-pair? repeat-with-letter?) input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
