^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day15
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "15" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Today's problem has us solving a constraints problem. Under the
;; guise of building the perfect recipe (44 _teaspoons_ of
;; butterscotch?!), we have to find the optimal constraints to satisfy
;; several constraints:
;;
;; - We can't use more than 100 total teaspoons
;;
;; - If any property sum is negative, it becomes zero and thus
;; multiplying by 0 means the iteration is out
;;
;; - Each ingredient must be used (it doesn't explicity say this, but
;; if you multiply one of them by 0 you hit the zero constraint)
;;
;; - In the context of part 2, we also have to ensure we don't make it
;; have more than 500 calories
;;
;; In the case of both parts, we have to find the ratio of ingredients
;; that, when summed and multiplied, produce the biggest number.
;;
;; First things first, let's load our input and parse it
(defn parse-input [input]
  (map #(map parse-long (re-seq #"\-?\d+" %)) input))

(def input (->> (slurp (io/resource "inputs/2015/day15.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input                                   ;; Parse into lists
                (apply mapv vector)))                         ;; Transpose to give all the properties as pair lists
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### combinations-to-sum
;;
;; Given a total some, generate a list of all the possible numbers
;; that add up to that number. 1 per property, and since we handle
;; calories differently we never directly handle it.
(defn combinations-to-sum
  [target-sum]
  (for [a (range (inc target-sum))
        b (range (inc (- target-sum a)))
        c (range (inc (- target-sum a b)))
        :let [d (- target-sum a b c)]
        :when (<= d target-sum)]
    [a b c d]))

;; ### calculate-properties
;;
;; Given the ratios in amounts, calculate the property scores and add
;; them up.
(defn calculate-properties [amounts properties]
  (apply + (map * amounts properties)))

;; ### property-score
;;
;; Calculate the property score for a given set of quantities and
;; properties. If any of them end up being negative, we clamp to
;; zero.
(defn property-score [amounts properties]
  (max 0 (calculate-properties amounts properties)))

;; ### too-calorific?
;;
;; Predicate function to test if the recipe we'll produce will be too
;; calorific. Eliminates all the bad ones for part 2.
(defn too-calorific? [properties amounts]
  (>= 500 (calculate-properties amounts (map last properties))))

;; ### solve
;;
;; The general solver since both parts are solved the same.
;;
;; Take all the valid combinations and calculate the scores for all
;; the properties except calories (the last item). Since 0 is negative
;; in Clojure (don't @ me, mathematicians) we can just filter out any
;; of these that have a single negative value and get the product of
;; the rest.
(defn solve [input combinations]
  (->> combinations
       (map
        (fn [[a b c d]]
          (map (partial property-score [a b c d]) (butlast input))))
       (filter (partial every? pos?))
       (map (partial reduce *))
       (apply max)))

;; ## Part 1
;;
;; Part 1 just wants us to get the highest for any combination
(defn part-1
  [input]
  (solve input (combinations-to-sum 100)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 we filter out any combinations that will produce something
;; too calorific first
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input (filter (partial too-calorific? input) (combinations-to-sum 100))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
