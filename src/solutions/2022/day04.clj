;; # 2022 - Day 02
;;
;; ## Problem
;; ### Part 1
;; Space needs to be cleared before the last supplies can be unloaded from the
;; ships, and so several Elves have been assigned the job of cleaning up
;; sections of the camp. Every section has a unique **ID number**, and each Elf is
;; assigned a range of section IDs.

;; However, as some of the Elves compare their section assignments with each
;; other, they've noticed that many of the assignments **overlap**. To try to
;; quickly find overlaps and reduce duplicated effort, the Elves pair up and
;; make a **big list of the section assignments for each pair** (your puzzle input).

;; For example, consider the following list of section assignment pairs:

;; ```
;; 2-4,6-8
;; 2-3,4-5
;; 5-7,7-9
;; 2-8,3-7
;; 6-6,4-6
;; 2-6,4-8
;; ```

;; For the first few pairs, this list means:

;;- Within the first pair of Elves, the first Elf was assigned sections `2-4`
;; (sections `2`, `3`, and `4`), while the second Elf was assigned sections `6-8`
;; (sections `6`, `7`, `8`).
;;- The Elves in the second pair were each assigned two sections.
;;- The Elves in the third pair were each assigned three sections: one got
;; sections `5`, `6`, and `7`, while the other also got `7`, plus `8` and `9`.

;; This example list uses single-digit section IDs to make it easier to draw;
;; your actual list might contain larger numbers. Visually, these pairs of
;; section assignments look like this:

;; ```
;; .234.....  2-4
;; .....678.  6-8
;; ```

;; ```
;; .23......  2-3
;; ...45....  4-5
;; ```

;; ```
;; ....567..  5-7
;; ......789  7-9
;; ```

;; ```
;; .2345678.  2-8
;; ..34567..  3-7
;; ```

;; ```
;; .....6...  6-6
;; ...456...  4-6
;; ```

;; ```
;; .23456...  2-6
;; ...45678.  4-8
;; ```

;; Some of the pairs have noticed that one of their assignments **fully contains**
;; the other. For example, `2-8` fully contains `3-7,` and `6-6` is fully contained by
;; `4-6.` In pairs where one assignment fully contains the other, one Elf in the
;; pair would be exclusively cleaning sections their partner will already be
;; cleaning, so these seem like the most in need of reconsideration. In this
;; example, there are `2` such pairs.

;; **In how many assignment pairs does one range fully contain the other?**
;;
;; ### Part 2
;; It seems like there is still quite a bit of duplicate work planned. Instead,
;; the Elves would like to know the number of pairs that **overlap at all**.

;; In the above example, the first two pairs (`2-4,6-8` and `2-3,4-5`) don't
;; overlap, while the remaining four pairs (`5-7,7-9`, `2-8,3-7`, `6-6,4-6`, and
;; `2-6,4-8`) do overlap:

;;- `5-7,7-9` overlaps in a single section, `7`.
;;- `2-8,3-7` overlaps all of the sections `3` through `7`.
;;- `6-6,4-6` overlaps in a single section, `6`.
;;- `2-6,4-8` overlaps in sections `4`, `5`, and `6`.

;; So, in this example, the number of overlapping assignment pairs is `4`.

;; **In how many assignment pairs do the ranges overlap?**
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.2022.day04
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [clojure.string :as str]
            [clojure.set :as set]))
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
;; Which looks like
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

;; Which looks like
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(deftest test-answers
  (t/is (= 305 (part-1 input)))
  (t/is (= 811 (part-2 input))))
