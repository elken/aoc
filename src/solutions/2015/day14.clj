^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day14
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "14" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Today has us trying to calculate a sequence of movement for a
;; reindeer; given the restriction that the reindeer is either moving
;; or resting.
;;
;; We have instructions on how long a deer should move and rest for,
;; as well as the speed; and we can use this to calculate what the
;; distance should be at a given time with multiplication.
;;
;; Part 1 wants us to just realise the sequence at a given time and
;; dictate the winner; but for part 2 we have to keep track of who's
;; winning at every second, allocating 1 point to the winner at every
;; second and then finding the highest score.
;;
;; First things first, let's load our input and parse it.
;;
;; We don't have to care about the names, so we can just parse out the numbers line-by-line
(defn parse-input [input]
  (map #(map parse-long (re-seq #"\d+" %)) input))

{:nextjournal.clerk/visibility {:result :show}}
;; And load it
(def input (->> (slurp (io/resource "inputs/2015/day14.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input))                                 ;; Parse the input
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### distance-at-time
;;
;; The main heavy lifting for the problem. Both parts have us try and
;; realise the sequence at a point in time, so we can use the formula
;; below to work that out.
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/tex "speed \\cdot \\left(duration \\cdot \\left\\lfloor\\frac{time}{duration + rest}\\right\\rfloor + \\min(duration, time \\bmod (duration + rest))\\right)")
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn distance-at-time
  [time [speed duration rest]]
  (* speed
     (+ (* duration (quot time (+ duration rest)))
        (min duration (rem time (+ duration rest))))))

;; ## Part 1
;;
;; Part 1 just wants to realise the sequences after 2503 seconds and
;; find the winner
(defn part-1
  [input]
  (->> input
       (map (partial distance-at-time 2503))
       (apply max)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; For part 2, we have to use all the `distance-at-time`s to find the
;; winner and allocate points accordingly.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> (reduce
        (fn [acc time]
          (map + acc
               (let [ds (map (partial distance-at-time time) input)
                     max-d (apply max ds)]
                 (map #(if (= % max-d) 1 0) ds))))
        (repeat (count input) 0)
        (range 1 2504))
       (apply max)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
