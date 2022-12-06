^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day03
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
(clerk/html (u/load-problem "03" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Ok, we're getting to a more meaty problem now.
;;
;; This time around, we're doing string manipulation and set operations. Given a
;; set `#{1 2 3}` and another set `#{2 3 4}`; the intersection of those sets is
;; `#{2 3}`.
;;
;; Seem familiar? Yeah, that's what we have to do here! Identify common items
;; between given sets.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2022/day03.txt")) ;; Load the resource
                (str/split-lines)))                           ;; Split into lines

;; Next we have to create a couple of utility functions to help us solve this.
;;
;; #### Parsing priority
;; Given an item `[a-zA-z]`, get the ascii code and subtract the relevant
;; amount. Another potential here would be a `zipmap` of all the letters and
;; indexes.
{:nextjournal.clerk/visibility {:result :hide}}
(defn get-priority [item]
  (- (int item)
     (if (= (str item) (str/upper-case item))
       38
       96)))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show}}
(get-priority \A)

;; #### Splitting a rucksack
;; Given a "rucksack" (a line of input), split down the middle and return as a
;; list
{:nextjournal.clerk/visibility {:result :hide}}
(defn split-rucksack [rucksack]
  (partition (/ (count rucksack) 2) rucksack))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show}}
(split-rucksack "vJrwpWtwJgWrhcsFMMfFFhFp")

;; #### Get the duplicates
;; Given a list of rucksacks, convert them to sets and apply `set/intersection`
;; on them. For the purposes of this assignment, this always returns a single
;; element so we can "cheat" and just call `first`.
{:nextjournal.clerk/visibility {:result :hide}}
(defn check-items [rucksacks]
  (first (apply set/intersection (map set rucksacks))))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show}}
(check-items (split-rucksack "vJrwpWtwJgWrhcsFMMfFFhFp"))

;; Now we can run part 1! This is now a good usage of a transducer, as we can
;; apply a single function to every line _and_ keep track of the result of the
;; reduction.
;;
;; Put simply; no need to pass through the list multiple times. We can just run
;; the various utility functions to transform a given line.
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (transduce (map (comp get-priority check-items split-rucksack)) + 0 input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; And just to show the intermediate steps:
{:nextjournal.clerk/visibility {:code :show :result :show}}
(first input)
(split-rucksack (first input))
(check-items (split-rucksack (first input)))
(get-priority (check-items (split-rucksack (first input))))

;; Part 2 is only _slightly_ different; the grouping is now vertical rather than
;; horizontal. So we partition the input into groups of 3 and then handle it
;; exactly the same as part 1, minus the string splitting.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (map (comp get-priority check-items)) + 0 (partition 3 input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

;; And to show some of the intermediate steps again
{:nextjournal.clerk/visibility {:code :show :result :show}}
(first (partition 3 input))
(check-items (first (partition 3 input)))
(get-priority (check-items (first (partition 3 input))))
