;; # 2022 - Day 03
;;
;; ## Problem
;; ### Part 1
;; One Elf has the important job of loading all of the rucksacks with supplies
;; for the jungle journey. Unfortunately, that Elf didn't quite follow the
;; packing instructions, and so a few items now need to be rearranged.

;; Each rucksack has two large **compartments**. All items of a given type are meant
;; to go into exactly one of the two compartments. The Elf that did the packing
;; failed to follow this rule for exactly one item type per rucksack.

;; The Elves have made a list of all of the items currently in each rucksack
;; (your puzzle input), but they need your help finding the errors. Every item
;; type is identified by a single lowercase or uppercase letter (that is, `a` and
;; `A` refer to different types of items).

;; The list of items for each rucksack is given as characters all on a single
;; line. A given rucksack always has the same number of items in each of its two
;; compartments, so the first half of the characters represent items in the
;; first compartment, while the second half of the characters represent items in
;; the second compartment.

;; For example, suppose you have the following list of contents from six
;; rucksacks:

;; ```
;; vJrwpWtwJgWrhcsFMMfFFhFp
;; jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
;; PmmdzqPrVvPwwTWBwg
;; wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
;; ttgJtRGJQctTZtZT
;; CrZsJsPPZsGzwwsLwLmpwMDw
;; ```

;;- The first rucksack contains the items `vJrwpWtwJgWrhcsFMMfFFhFp`, which means
;; its first compartment contains the items `vJrwpWtwJgWr`, while the second
;; compartment contains the items `hcsFMMfFFhFp`. The only item type that appears
;; in both compartments is lowercase `p`.
;;- The second rucksack's compartments contain `jqHRNqRjqzjGDLGL` and
;; `rsFMfFZSrLrFZsSL`. The only item type that appears in both compartments is
;; uppercase `L`.
;;- The third rucksack's compartments contain `PmmdzqPrV` and `vPwwTWBwg`; the only
;; common item type is uppercase `P`.
;;- The fourth rucksack's compartments only share item type `v`.
;;- The fifth rucksack's compartments only share item type `t`.
;;- The sixth rucksack's compartments only share item type `s`.

;; To help prioritize item rearrangement, every item type can be converted to a
;; **priority**:

;;- Lowercase item types `a` through `z` have priorities 1 through 26.
;;- Uppercase item types `A` through `Z` have priorities 27 through 52.

;; In the above example, the priority of the item type that appears in both
;; compartments of each rucksack is 16 (`p`), 38 (`L`), 42 (`P`), 22 (`v`), 20 (`t`), and
;; 19 (`s`); the sum of these is **157**.

;; Find the item type that appears in both compartments of each rucksack. **What
;; is the sum of the priorities of those item types?**
;;
;; ### Part 2
;; As you finish identifying the misplaced items, the Elves come to you with
;; another issue.

;; For safety, the Elves are divided into groups of three. Every Elf carries a
;; badge that identifies their group. For efficiency, within each group of three
;; Elves, the badge is the **only item type carried by all three Elves**. That is,
;; if a group's badge is item type `B`, then all three Elves will have item type `B`
;; somewhere in their rucksack, and at most two of the Elves will be carrying
;; any other item type.

;; The problem is that someone forgot to put this year's updated authenticity
;; sticker on the badges. All of the badges need to be pulled out of the
;; rucksacks so the new authenticity stickers can be attached.

;; Additionally, nobody wrote down which item type corresponds to each group's
;; badges. The only way to tell which item type is the right one is by finding
;; the one item type that is **common between all three Elves** in each group.

;; Every set of three lines in your list corresponds to a single group, but each
;; group can have a different badge item type. So, in the above example, the
;; first group's rucksacks are the first three lines:

;; ```
;; vJrwpWtwJgWrhcsFMMfFFhFp
;; jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
;; PmmdzqPrVvPwwTWBwg
;; ```

;; And the second group's rucksacks are the next three lines:

;; ```
;; wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
;; ttgJtRGJQctTZtZT
;; CrZsJsPPZsGzwwsLwLmpwMDw
;; ```

;; In the first group, the only item type that appears in all three rucksacks is
;; lowercase `r`; this must be their badges. In the second group, their badge item
;; type must be `Z`.

;; Priorities for these items must still be found to organize the sticker
;; attachment efforts: here, they are 18 (`r`) for the first group and 52 (`Z`) for
;; the second group. The sum of these is **70**.

;; Find the item type that corresponds to the badges of each three-Elf group.
;; **What is the sum of the priorities of those item types?**
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.2022.day03
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [clojure.string :as str]
            [clojure.set :as set]))
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

;; And running it gives
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

;; Running part 2 gives
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

;; And to show some of the intermediate steps again
{:nextjournal.clerk/visibility {:code :show :result :show}}
(first (partition 3 input))
(check-items (first (partition 3 input)))
(get-priority (check-items (first (partition 3 input))))

{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(deftest test-answers
  (t/is (= 7845 (part-1 input)))
  (t/is (= 2790 (part-2 input))))
