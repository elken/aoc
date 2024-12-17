^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day13
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "13" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Rejoice, no grid!
;;
;; Today was a great problem. It boils down to a linear programming
;; problem, let's look at the first machine for an example.
;;
;; ```
;; Button A: X+94, Y+34
;; Button B: X+22, Y+67
;; Prize: X=8400, Y=5400
;; ```
;;
;; What does this translate to mathematically?
;;
;; $94X + 34Y = 8400$
;;
;; $22X + 67Y = 5400$
;;
;; $X < 100, Y < 100$
;;
;; We have to satisfy both equations and meet the constraints by
;; finding either the max value or the min value, in this case we're
;; optimising for the minimum value.
;;
;; Part 1 wants us to just do it, and part 2 adds 10,000,000,000,000 to the target values.
;; 
;; First things first, let's load our input and parse it. We split
;; every empty blank line to give our individual groups then parse
;; them into numbers (taking care to ensure we consider negative
;; numbers, an attempt by me to predict part 2).
(defn parse-input [input]
  (->> (str/split input #"\n\n")
       (map #(partition 2
                        (mapv parse-long
                              (re-seq #"\-?\d+" %))))))

{:nextjournal.clerk/visibility {:result :show}}
(def input (->> (slurp (io/resource "inputs/2024/day13.txt")) ;; Load the resource
                parse-input))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### valid-solution?
;;
;; Just a single function for both parts. If we don't pass `k`, we use
;; the constraint to be less than 100; obviously this will fail for
;; things like `k=1` but we don't have to consider that.
;;
;; Our solution solves using Cramer's Rule to find integer solutions
;; for a system of 2 linear equations.
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/tex "\\begin{aligned}
\\text{Given linear equations:} \\\\
ax_1 + by_1 = c_1 \\\\
ax_2 + by_2 = c_2 \\\\
\\text{Solution:} \\\\
x_1 = \\frac{\\det(c_1, b; c_2, d)}{\\det(a, b; x_2, y_2)} \\\\
x_2 = \\frac{\\det(a, c_1; x_2, c_2)}{\\det(a, b; x_2, y_2)} \\\\
\\text{Where } \\det = ad - bc
\\end{aligned}")
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; What this means in English is we first calculate the determinant of
;; the coefficient matrix `det` which we use to check if there is a
;; unique solution. If `det` is zero, there is no unique solution to
;; the problem.
;;
;; If there is, then we can compute the integer coefficients `a` and
;; `b` that satisfy the equations. If they then meet the bounds we
;; define, then we can apply the simple formula for computing the
;; total: $3A + B$
(defn valid-solution?
  ([k [[ax ay] [bx by] [tx ty]]]
   (let [tx (+ tx k)
         ty (+ ty k)
         det (- (* ax by) (* ay bx))]
     (when-not (zero? det)
       (let [a (/ (- (* tx by) (* ty bx)) det)
             b (/ (- (* ax ty) (* ay tx)) det)]
         (when (and (integer? a)
                    (integer? b)
                    (>= a 0)
                    (>= b 0)
                    (if (zero? k) (< a 100) true)
                    (if (zero? k) (< b 100) true))
           (+ (* 3 a) b))))))
  ([machine]
   (valid-solution? 0 machine)))

;; ## Part 1
;;
;; Part 1 just wants us to simply solve the configurations
(defn part-1
  [input]
  (->> input
       (keep valid-solution?)
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 expects us to increase the totals by 10,000,000,000,000
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> input
       (keep (partial valid-solution? 10000000000000))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
