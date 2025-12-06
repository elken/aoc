^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day06
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "06" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Today was surprisingly a rough one purely because of all the
;; wrangling I had to do for part 2. Conceptually it's very simple and
;; I solved both parts before I'd even finished them. But we've
;; reached the halfway mark now!
;;
;; Our problem today has us parsing a grid of numbers and operations
;; and applying the operations to said numbers. Part 2 wants us to
;; transpose the lists and apply the operations thus.
;;
;; No clean parsing or solution today, the only simplification we can
;; do between parts is pre-resolving the list of function symbols.
;;
;; First things first, let's load our input and parse it

(defn parse-input [input]
  [(butlast input)
   (keep #(resolve (symbol (str %))) (last input))])

(def input (->> (slurp (io/resource "inputs/2025/day06.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input))                                 ;; Parse into numbers and operations

;; ## Part 1
;;
;; Part 1 has us simply splitting the grid into the lines of numbers,
;; transposing it to get the columns as rows then converting
;; everything to numbers.
;;
;; Then we do the same final logic by mapping over the `op`eration`s`
;; and the `col`umn`s` to call the `op` on the `col` and sum the
;; results.
(defn part-1 [input]
  (let [[lines ops] input
        cols (->> lines
                  (map #(str/split (str/trim %) #"\s+"))
                  (apply mapv vector)
                  (map (partial map parse-long)))]
    (apply + (map apply ops cols))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 has us doing something more complex by transposing the
;; number groups first then handling them the same as part 1.
;;
;; Then we do the same final logic by mapping over the `op`eration`s`
;; and the `col`umn`s` to call the `op` on the `col` and sum the
;; results.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [[lines ops] input
        cols (->> lines
                  (apply map str)
                  (map #(parse-long (apply str (remove #{\space} %))))
                  (partition-by nil?)
                  (take-nth 2))]
    (apply + (map apply ops cols))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
