^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day11
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "11" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; No grid! Just maths! Easy 2-star!
;;
;; Today feels like a breath of fresh air after the recent few
;; days. No grid in sight, not even a ridiculous part 2.
;;
;; Today's problem is a fundamentally simple one; we start with some
;; numbers and each step we transform each number:
;; - If the number is 0, return a 1
;; - If the number of digits is odd, "split" the number into two ints (meaning leading 0s get removed too)
;; - Otherwise, multiply it by 24
;;
;; The parts don't differ too much; part 1 wants 25 steps and part 2
;; wants 75 steps. The real test is how efficient the algorithm is, my
;; first attempt for part 2 did take about 3 minutes to get to step
;; 60...
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day11.txt")) ;; Load the resource
                (re-seq #"\d+")                               ;; Get the numbers
                (map parse-long)))                            ;; Parse into actual numbers
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### split-number
;;
;; "Split" a number using maths. We get the length of the number by
;; using the log10 (we use the same in blink) then we use that to get
;; the half-way point to get our divisor (the 10^half the length).
;;
;; We can then just use division to get both parts of the number.
(defn split-number [stone]
  (let [len (inc (int (Math/log10 stone)))
        half (quot len 2)
        divisor (long (Math/pow 10 half))]
    [(quot stone divisor) (rem stone divisor)]))

;; ### blink
;;
;; Apply the rules to our numbers. We use the same log10 trick from
;; split-number to get the length.
(defn blink [stone]
  (cond
    (zero? stone) [1]
    (odd? (int (Math/log10 stone))) (split-number stone)
    :else [(* stone 2024)]))

;; ### step
;;
;; All hail memoize!
;;
;; Our main step function, we walk the steps and map out what happens
;; to every stone when we apply the step function `index` times thanks
;; to memoize.
;;
;; Since it's memoized, I can't easily demonstrate it with code; but hopefully this example is sufficient
;; ```clojure
;; (fast-forward 12 3) ;; What do we get if we start with 12 and blink 3 times?
;;
;; ;; One step
;; (blink 12)
;; => [1 2]
;;
;; ;; Now we'd call
;; [(fast-forward 1 2) (fast-forward 2 2)] ;; Vector used to demonstrate a grouping here
;; ```
;; And that keeps going until our count is 1, at which point we've hit
;; our base case and we can count. This ripples up as each recursive
;; case completes inside the `map` and all the `count`s are `reduce`d
;; together with `+`.
(def step
  (memoize
   (fn [stone index]
     (let [next (blink stone)]
       (if (= index 1)
         (count next)
         (reduce + (map #(step % (dec index)) next)))))))

;; ### solve
;;
;; The general solver since both parts do the same thing, just with
;; different step counts
(defn solve [nums steps]
  (reduce + (map #(step % steps) nums)))

;; ## Part 1
;;
;; Part 1 wants 25 steps
(defn part-1
  [input]
  (solve input 25))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And part 2 wants 75 steps
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input 75))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
