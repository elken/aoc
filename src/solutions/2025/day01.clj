^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day01
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "01" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; It's that time of year again! Some changes to the format this year;
;; only 12 problems instead of 24! And no global leaderboard to care
;; about!
;;
;; But that doesn't impact our solution, so let's get stuck
;; in. Today's day 1 problem feels a bit much for a day oner, so I'm
;; already pre-dreading things...
;;
{:nextjournal.clerk/visibility {:code :hide :result :show}}

(clerk/html [:div.border-l-4.border-yellow-500.bg-yellow-50.dark:bg-yellow-900.rounded.p-4.my-4
             [:p.text-yellow-800.dark:text-yellow-200 (clerk/md "After some thinking, I have come back to this the day after and realized this _is_ a very simple problem after all...

It just boils down to simple division and counting multiples, sorry Eric!")]])

{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; Today has us trying to work out a password from some safe
;; combination logic. We start the dial at `50` and rotate based on
;; the input; so `L68` rotates left by 68 and `R1` rotates right by 1.
;;
;; Part 1 wants us to record how many times the dial stops at 0; and
;; part 2 wants us to record how many times it loops around 0.

;; ## Functions
;;
;; ### parse-input
;;
;; Given a list of all the combinations, convert them to +/- numbers
;; based on direction.
(defn parse-input [lines]
  (map #(read-string (str/replace % #"[LR]" {"L" "-" "R" ""})) lines))

;; ### count-crossings
;;
;; Given a `start` and `end`, compute how many multiples of 100 are
;; between two positions on the dial.
;;
;; The problem is a deceptive division operations hiding as a bigger
;; problem...
(defn count-crossings [start end]
  (count (filter #(zero? (mod % 100))
                 (range start end (compare end start)))))

;; ## Input

;; Now let's load our input and parse it

(def input (->> (slurp (io/resource "inputs/2025/day01.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input                                   ;; Convert to +/- numbers
                (reductions + 50)))                           ;; Map all the positions of applying `+`

;; ## Part 1
;;
;; As above, part 1 wants us to just keep track of when we hit 0 or
;; 100.
;;
;; So we filter all the values that divide equally into 100 and count
;; them.
(defn part-1
  [input]
  (->> input
       (filter #(zero? (mod % 100)))
       count))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 also has us tracking when it loops around the bounds (0-100).
;;
;; This time we partition the dial positions and
;; run [`count-crossings`](#count-crossings) on them to get the count
;; of multiples and sum them up.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> input
       (partition 2 1)
       (map #(apply count-crossings %))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
