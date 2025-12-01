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
{:nextjournal.clerk/visibility {:code :show :result :show}}

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
;; Today has us trying to work out a password from some safe
;; combination logic. We start the dial at `50` and rotate based on
;; the input; so `L68` rotates left by 68 and `R1` rotates right by 1.
;;
;; Part 1 wants us to record how many times the dial stops at 0; and
;; part 2 wants us to record how many times it loops around 0.
;;
;; First things first, let's load our input and parse it

(defn replace-chars [input]
  (str "50"
       (-> input
           (str/join)
           (str/replace "L" " -")
           (str/replace "R" " "))))

(def input (->> (slurp (io/resource "inputs/2025/day01.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines

{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;;
;; We define a simple function to handle the rotation parsing for both
;; parts. I'd love to just be able to do `[head & tail]` destructuring
;; here but Clojure treats the output as a list always so I'd have to
;; do the parse-long trick anyway.
(defn parse-rotation [rot]
  [(first rot) (parse-long (apply str (rest rot)))])

;; ## Part 1
;;
;; As above, part 1 wants us to just keep track of when we hit 0.
;;
;; We compute the new total by using the rotation to move the dial. We
;; don't have to track the direction yet, so it ends up being very
;; simple.
(defn part-1
  [input]
  (:zeroes
   (reduce (fn [{:keys [total zeroes]} rot]
             (let [[new-dir amount] (parse-rotation rot)
                   new-total (mod ((case new-dir \L - \R +) total amount) 100)]
               {:total new-total
                :zeroes (+ zeroes (if (zero? new-total) 1 0))}))
           {:total 50 :zeroes 0}
           input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 also has us tracking when it loops around the bounds (0-100).
;;
;; Similar to part 1, but now we have to keep track of the direction
;; so we know how to "flip" the dial and work from the other side.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (:zeroes
   (reduce (fn [{:keys [dial dir zeroes]} rot]
             (let [[new-dir amount] (parse-rotation rot)
                   dial (if (not= dir new-dir) (- 100 dial) dial)
                   dial (+ (mod dial 100) amount)]
               {:dial dial :dir new-dir :zeroes (+ zeroes (quot dial 100))}))
           {:dial 50 :dir \R :zeroes 0}
           input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
