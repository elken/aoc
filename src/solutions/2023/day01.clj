^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2023.day01
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "01" "2023"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;; An easy start to the year! This one screams regex (or rather, the curse of
;; regex screams at me constantly keeping me awake...) and doing so; it becomes
;; quite simple!
;;
;; The problem here boils down to parsing numbers from a string, merging the
;; first and last digits and summing the results; part 2 wants us to also
;; include number words.
;;
;; The trick here is to remember that for part 2 we have to use lookahead when
;; matching against things like "eightwo".
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2023/day01.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; Next we define a simple map of word names to digits
(def word->digit
  {"one" "1"
   "two" "2"
   "three" "3"
   "four" "4"
   "five" "5"
   "six" "6"
   "seven" "7"
   "eight" "8"
   "nine" "9"})

;; ## parse-numeric
;; Named well (for once) this just attempts to parse a numeric value or convert
;; to a digit from a word
(defn parse-numeric [s]
  (if (every? #(Character/isDigit %) s)
    s
    (word->digit s)))

;; ## solve
;; Our general solving function, since the only difference between parts is the
;; regex we use.
(defn solve [input regex]
  (->> input
       (map #(let [tokens (map second (re-seq regex %))]
               (->> tokens
                    last
                    parse-numeric
                    (str (parse-numeric (first tokens)))
                    parse-long)))
       (apply +)))

;; ## Part 1
;; Since we need a capture group for part 2, we pointlessly use one for part 1
;; just to make things neater.
;;
;; Otherwise this is as expected, take our input, parse the lines to numbers, sum them up
(defn part-1
  [input]
  (solve input #"(\d)"))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Same as part 1, just with a different regex
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input #"(?=(one|two|three|four|five|six|seven|eight|nine|\d))"))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
