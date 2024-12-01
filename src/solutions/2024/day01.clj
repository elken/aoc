^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day01
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "01" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; It's that time of year once again! Time to dust off all the fancy
;; automations and coding tricks for a week or so until the inevitable
;; brick wall problem.
;;
;; This year is starting off somewhat simply with a list counting
;; issue. Part 1 has us computing distance between each sorted value
;; in the list; and part 2 wants us to find the "similarity
;; score". Nothing too complex this time yet, so let's jump right into
;; it.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day01.txt"))          ;; Load the resource
                str/split-lines                                        ;; Split into lines
                (map (fn [s] (map parse-long (str/split s #"\s+"))))   ;; Split into number pairs
                (apply mapv vector)))                                  ;; Transpose the pairs into the 2 lists
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Part 1
;; Nothing too complex here, we have to sort the lists and
;; then compute the distance between each. Thanks to the commutative
;; property of subtraction, we don't have to check if either side is
;; bigger we can just do the subtraction then `abs`.
;;
;; We can then just `(apply +)` to add all the numbers up.
(defn part-1
  [input]
  (->> input
       (map sort)
       (apply map (comp abs -))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; As always, the second part is a bit more complex in some
;; ways. This time around, we have to compute the similarity score by
;; iterating through the first list and for each number, multiply it
;; by the amount of times it occurs in the second list.
;;
;; Thanks to `frequencies`, we can cache the total sums of all the
;; numbers. The function takes a collection and returns a hash where
;; the key is the element, and the value is the number of times it
;; occurs. Because we can call hashes like a function, we can simply
;; map over all the elements in the first list and try and return the
;; number of occurances. If we don't find one, return 0.
;;
;; Then we just `(apply +)` to add all the numbers up.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [[lhs rhs]]
  (let [freq (frequencies rhs)]
    (->> lhs
         (map #(* % (or (freq %) 0)))
         (apply +))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
