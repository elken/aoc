^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day04
  (:require
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
[:style "em{color: #fff;font-style: normal;text-shadow: 0 0 5px #fff;}.viewer-result:first-child{display: none;}"]

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "04" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; So, at first this problem seems quite complex. Overlapping co-ordinates? Okay
;; so we need to create some kind of tree? Maybe try and plot a line?
;;
;; Well, what is a line? A plotted function. What's plotted? Numbers. Sounds
;; like it's just a range of numbers to me.
;;
;; All we have to really do here is compute the ranges, and check for overlaps.
;;
;; Slightly different this time since we need a couple of functions to
;; efficiently setup our input; we have a few utility functions first.
;;
;; #### Inclusive range
;;
;; By default, the range function is exclusive on the end. That means we miss out on the upper bound, no es bueno.
{:nextjournal.clerk/visibility {:result :hide}}
(defn inclusive-range [start end]
  (range start (inc end)))

;; Which when compared, looks like
{:nextjournal.clerk/visibility {:result :show}}
(range 1 5)
(inclusive-range 1 5)

;; #### Group co-ordinates
;; Given a line of input `2-4,5-8`, create a 2-tuple list of all the numbers, e.g. `((2 4) (5 8))`
{:nextjournal.clerk/visibility {:result :hide}}
(defn group-coords [coord]
  (partition 2 (re-seq #"\d+" coord)))

;; Which when compared, looks like
{:nextjournal.clerk/visibility {:result :show}}
(group-coords "2-4,5-8")

;; #### Convert a 2-tuple co-ordinate into ranges
;; Compute the two ranges from the result of the previous function
{:nextjournal.clerk/visibility {:result :hide}}
(defn coord->ranges [coord]
  (apply inclusive-range (map parse-long coord)))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show}}
(map coord->ranges (group-coords "2-4,5-8"))

;; #### Create the sets
;; Now, we can at last use the previous steps to create our sets.
;;
;; These have to be sets as the `clojure.set` functions expect both arguments to
;; be sets, and don't behave when that's not the case.
{:nextjournal.clerk/visibility {:result :hide}}
(defn create-sets [coord]
  (->> coord
       group-coords
       (map (comp set coord->ranges))))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show}}
(create-sets "2-4,5-8")

;; Now, we can setup our input
(def input (->> (slurp (io/resource "inputs/2022/day04.txt")) ;; Load the resource
                (str/split-lines)                             ;; Split into lines
                (map create-sets)))                           ;; Create sets from all those lines

;; Since both parts can be generalised to the same operation (apply a transducer
;; to trigger some condition over them and count the truthy ones) we can extract
;; this to a single function.
;;
;; Given an input and an operation `op` which expects the two sets as arguments;
;; apply `op`, filter out truthy values and count them.
;;
;; The count works here because all the `true` values are mapped to `1`, and
;; then `transduce` applies `+` to them.
{:nextjournal.clerk/visibility {:result :hide}}
(defn compute-sets [input op]
  (transduce (comp (map #(apply op %))
                   (filter true?)
                   (map {false 0 true 1})) + 0 input))

;; Part 1 wants us to check if one set contains the other, so we can just do `set/subset?`
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (compute-sets input #(or (set/subset? %1 %2)
                           (set/subset? %2 %1))))
;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 wants us to check if there's _any_ overlap; which in set terms is just
;; "check for an intersection".
;;
;; We can do that by computing it and checking if the length is 0 or not.
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-2
  [input]
  (compute-sets input #(> (count (set/intersection %1 %2)) 0)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
