^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day03
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "03" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; 3 days in and we're already hitting hard algorithms. Maybe it's
;; just me, but I'm not a fan of these at all. Probably should have
;; put more effort into them at uni...
;;
;; Today's problem has us trying to build the largest possible number
;; from an input of digits (several inputs, then sum them up, average)
;; with an obvious general solution as both parts are the same barring
;; the number of digits for the largest number; part 1 is 2 and part 2
;; is _12_!
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2025/day03.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines

;; ## Functions
;;
;; ### max-joltage
;;
;; This is our only function, and while it's simplicity is impressive,
;; this was _not_ the first pass. Also, it's going to take far more
;; lines/characters to explain what's happening...
;;
;; So essentially what we're doing is doing a greedy search, taking
;; the largest digit we can, and looking as far ahead as we can while
;; ensuring there's enough digits still. We build a shrinking window
;; and over time approach the correct number.
;;
;; Before we do anything, we do a simple conversion of the digits to a
;; vector of numbers and setup our state. If the `rem`aining `n`umber
;; of digits is 0, we are done and we have our result.
;;
;; Otherwise, the main iteration loop is defined thus:
;;
;; #### Create the window
;;
;; Define the maximum size of the search window for the current
;; iteration. As above, this shrinks over time and ensures we're
;; always looking for the correct amount of digits.
;;
;; `(- (count digits) rem)` gives us the amount of digits we can skip,
;; so to know how many to take we also need to add 1 to it.
;;
;; #### Find the best start
;;
;; Now we have our window size, we look for the best digit in the
;; current window and take note of its index as we'll need that in the
;; `subvec` call.
;;
;; #### And then we iterate
;;
;; Now we have all the state we need; the window size, the best digit
;; within the window and the index of the best digit, we can proceed
;; with another iteration.
;;
;; We trigger a new iteration with the list of digits being a slice of
;; a vector of the current digits from the best index (`inc`remented
;; because 0-indexing) and add the new best digit to the current
;; result by adding it and multiplying by 10 (so `9` and `8`
;; becomes `(+ 90 8)`) then we decrement the remaining digits so we
;; can shrink the window.
;;
;; #### Summary
;;
;; So following this, you can see that we look for the biggest digit
;; we can in the window defined by the `n`umber of digits, then shrink
;; the window each iteration. Instead of doing the `* 10` trick, you
;; could also probably just store the result as a list then make it a
;; number later, but this saves that parsing.
(defn max-joltage [n bank]
  (loop [digits (mapv #(Character/digit % 10) bank)
         result 0
         rem n]
    (if (zero? rem)
      result
      (let [window-size (inc (- (count digits) rem))
            best (apply max (take window-size digits))
            idx (.indexOf digits best)]
        (recur (subvec digits (inc idx))
               (+ (* result 10) best)
               (dec rem))))))

{:nextjournal.clerk/visibility {:result :hide}}

;; ## Part 1
;;
;; Wow that was a lot of words...
;;
;; Thankfully, both parts are identical so all we have to do is pass
;; in the number of digits for both.
(defn part-1
  [input]
  (apply + (map (partial max-joltage 2) input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; And part 2 wants 12 digits instead
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (apply + (map (partial max-joltage 12) input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
