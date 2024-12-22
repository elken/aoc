^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day22
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "22" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; No grid!
;;
;; Today was quite fun, part 1 was another "if I was awake at 5am, I'd
;; be on the leaderboard" day (well, if it weren't for all the AI
;; trolls... seriously, check the leaderboard times) but alas.
;;
;; Today has us computing a "secret number" by passing it through some
;; logic. I had the same realisation everyone else had (sadly, I
;; thought I was really smart for spotting it...) in that the
;; multiplication should actually be bit shifting instead since
;; they're powers of 2. Part 1 has us computing the 2000th steps for
;; our input list of numbers and part 2 has us doing something much
;; more complex that I'll explain when we get there.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day22.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (map parse-long)))                            ;; Parse into numbers
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### prune
;;
;; Given a secret number, simply compute the mod against 16777216.
;; There's probably some significance to this number.
(defn prune [secret]
  (mod secret 16777216))

;; ### mix
;;
;; Compute a bitwise-xor against the previous result and the next one
(defn mix [secret result]
  (bit-xor result secret))

;; ### evolve-step
;;
;; Given a secret number, compute the next one by computing all our
;; bit-shift rules (check the problem definition for what these
;; translate to)
(defn evolve-step [secret]
  (reduce (fn [acc [shift-fn shift-amt]]
            (prune
             (mix (shift-fn acc shift-amt) acc)))
          secret
          [[bit-shift-left 6]
           [bit-shift-right 5]
           [bit-shift-left 11]]))

;; ### evolve
;;
;; Given a secret number, compute the 2000th iteration of applying our
;; evolve-step function
(defn evolve [secret]
  (->> secret
       evolve-step
       (iterate evolve-step)
       (take 2000)))

;; ## Part 1
;;
;; Part 1 just wants us to get all the evolutions and sum them up
(defn part-1
  [input]
  (->> input
       (pmap evolve)
       (map last)
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 was surprisingly meaty this time around.
;;
;; For each of our input numbers, we have to compute the following:
;; - Get the same 2000 iterations from part 1
;; - Get the last digit of all of these (using `mod n 10`)
;; - Compute all the changes between these digits
;;
;; Then given all our changes:
;; - Create a sliding window of length 4
;; - If we've seen the pattern of 4 changes before, add them to our accumulator along with the price at that point
;; - Sum all these up
;;
;; We can then just apply `max-key val` to get the "winning" sequence and return it
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2 [input]
  (->> input
       (pmap (fn [secret]
               (let [nums (evolve secret)
                     prices (map #(mod % 10) nums)
                     changes (map - (rest prices) prices)]
                 (->> changes
                      (partition 4 1)
                      (map vector (drop 4 prices))
                      (reduce (fn [acc [price pattern]]
                                (if (contains? acc pattern)
                                  acc
                                  (assoc acc pattern price)))
                              {})))))
       (reduce (partial merge-with +))
       (apply max-key val)
       last))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
