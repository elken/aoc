;; # 2022 - Day 01
;;
;; ## Problem
;; ### Part 1
;; Santa's reindeer typically eat regular reindeer food, but they need a lot of
;; magical energy to deliver presents on Christmas. For that, their favorite
;; snack is a special type of `star` fruit that only grows deep in the jungle. The
;; Elves have brought you on their annual expedition to the grove where the
;; fruit grows.
;;
;; To supply enough magical energy, the expedition needs to retrieve a minimum
;; of `fifty stars` by December 25th. Although the Elves assure you that the grove
;; has plenty of fruit, you decide to grab any fruit you see along the way, just
;; in case.
;;
;; Collect stars by solving puzzles. Two puzzles will be made available on each
;; day in the Advent calendar; the second puzzle is unlocked when you complete
;; the first. Each puzzle grants `one star`. Good luck!
;;
;; The jungle must be too overgrown and difficult to navigate in vehicles or
;; access from the air; the Elves' expedition traditionally goes on foot. As
;; your boats approach land, the Elves begin taking inventory of their supplies.
;; One important consideration is food - in particular, the number of `Calories`
;; each Elf is carrying (your puzzle input).
;;
;; The Elves take turns writing down the number of Calories contained by the
;; various meals, snacks, rations, etc. that they've brought with them, one item
;; per line. Each Elf separates their own inventory from the previous Elf's
;; inventory (if any) by a blank line.
;;
;; For example, suppose the Elves finish writing their items' Calories and end
;; up with the following list:
;; ```
;; 1000
;; 2000
;; 3000
;; ```
;; ```
;; 4000
;; ```
;; ```
;; 5000
;; 6000
;; ```
;; ```
;; 7000
;; 8000
;; 9000
;; ```
;; ```
;; 10000
;; ```
;; This list represents the Calories of the food carried by five Elves:
;;- The first Elf is carrying food with `1000`, `2000`, and `3000` Calories, a
;; total of `6000` Calories.
;;- The second Elf is carrying one food item with `4000` Calories.
;;- The third Elf is carrying food with `5000` and `6000` Calories, a total of
;; `11000` Calories.
;;- The fourth Elf is carrying food with `7000`, `8000`, and `9000` Calories, a
;; total of `24000` Calories.
;;- The fifth Elf is carrying one food item with `10000` Calories.

;; In case the Elves get hungry and need extra snacks, they need to know which
;; Elf to ask: they'd like to know how many Calories are being carried by the
;; Elf carrying the **most** Calories. In the example above, this is `24000` (carried
;; by the fourth Elf).

;; Find the Elf carrying the most Calories. **How many total Calories is that Elf
;; carrying?**
;;
;; ### Part 2
;; By the time you calculate the answer to the Elves' question, they've already
;; realized that the Elf carrying the most Calories of food might eventually **run
;; out of snacks**.

;; To avoid this unacceptable situation, the Elves would instead like to know
;; the total Calories carried by the **top three** Elves carrying the most Calories.
;; That way, even if one of those Elves runs out of snacks, they still have two
;; backups.

;; In the example above, the top three Elves are the fourth Elf (with `24000`
;; Calories), then the third Elf (with `11000` Calories), then the fifth Elf (with
;; `10000` Calories). The sum of the Calories carried by these three elves is
;; `45000`.

;; Find the top three Elves carrying the most Calories. **How many Calories are
;; those Elves carrying in total?**
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.2022.day01
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Ok, so this looks quite simple; sum all the "groups" of elves.
;;
;; First things first, let's load our input and parse it

(def input (->> (slurp (io/resource "inputs/2022/day01.txt")) ;; Load the resource
                (str/split-lines)                             ;; Split into lines
                (partition-by str/blank?)                     ;; Partition by blank spaces (to give our groups)
                (remove #(str/blank? (first %)))              ;; Remove the blank spaces
                (map #(transduce (map parse-long) + 0 %))     ;; Map a transducer over the list of numbers (more info below)
                (sort >)))                                    ;; Sort by size

;; Great! We now have our list of numbers, and as both parts are the same it's
;; simply a case of treating that list slightly differently.
;;
;; For part 1, we just care about the biggest number

{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (first input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; But what's that "transducer" thing?
;;
;; So, there's two kinds of sequences (for our argument's sake); eager and lazy.
;; Eager sequences evaluate all the elements and hold everything in memory,
;; whereas lazy sequences are evaluated and stored as they're needed.
;;
;; As you can imagine, in an ideal world we'd probably want to use lazy
;; sequences for big, slow data and eager sequences for small, fast data.
;;
;; Later in Clojure's life, Rich Hickey realised that implementing map et al for
;; collections, then for streams, then for observables, then for channels is
;; redundant, too specific and inefficient. So he set out to see if there was a
;; way to write these functions once and for all.
;;
;; Great! ... But how does that help us?
;;
;; Let's start with a simple example

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (take 3) + 0 input))

;; What's going on there? Why does `take` not have a collection argument?
;;
;; Well, the short version is a number of these collection functions return a
;; "transducer"; that is a function that takes some step function and returns
;; that function wrapped in some other logic.
;;
;; In this case, `(take 3)` returns a function that returns the 3 numbers and
;; `transduce` then operates the same as `reduce` except taking the transducer
;; returned. Due to the size of the data and the operation, it's pointless
;; except for demonstration purposes.
;;
;; That transducer could for example be a complex operation like "given a
;; sequence of the first 10 billion numbers, filter out the odd primes bigger
;; than 251" which would operate at `O(1)` as it goes through each element
;; in-turn lazily.
;;
;; For a slightly more in-depth video, I would recommend [Fred Overflow's](https://www.youtube.com/watch?v=TaazvSJvBaw).
;;

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:result :hide}}
(deftest test-answers
  (t/is (= 70296 (part-1 input)))
  (t/is (= 205381 (part-2 input))))
