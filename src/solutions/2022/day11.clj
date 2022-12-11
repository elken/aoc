^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day11
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.core.match :as match]
            [clojure.math :as math]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "11" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Seems as if the Advent of Code Gods are nice to us on weekends, this one was
;; _relatively_ simple and quite fun. As is the case with these kinds of
;; puzzles, it took me longer to parse the input than to solve it.
;;
;; Today we're tasked with some monkey shenanigans. Given an input of starting
;; conditions and a number of rules, advance the state a number of times and
;; compute a result. These rules are:
;;
;; - The monkey begins with a list of worry levels for given items; `Starting items`
;; - `Operation` is what change happens to the worry level of each item during each step
;; - `Test` creates a boolean condition which moves that item to a given monkey
;;  if the test passes
;; - For part 1, _after_ the monkey inspects it but _before_ it tests your worry
;;  level, the worry level is divided by 3
;; - The number of times a monkey has inspected an item has to be tracked
;;
;; A round consists of iterating through each monkey and performing operations.
;; Part 1 we run through 20 rounds and part 2 we run through 10,000 rounds (more
;; on that in part 2). After a round, multiply the two biggest inspection totals
;; and that's the answer.
;;
;; First things first, let's load our input and parse it
;;
;; This is probably bordering in the edge of needing something like
;; [Instaparse](https://github.com/Engelberg/instaparse) but I'm trying to limit
;; myself to clojure.core dependencies only (outside of clerk for the notebook
;; and a few others for clerk utilities)
;;
;; First we have the function that, given a vector of lines, returns a valid
;; initial state for a monkey.
;;
;; - First line is used to set the initial inspected value
;; - Then we translate the starting numbers to a list
;; - Then we create a worry function that takes the old value and uses `resolve` and `symbol` to return a function
;; - Lastly we create the conditions for the movement later

{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-monkey [monkey]
  (reduce (fn [m line]
            (apply assoc m
                   (match/match [(str/split (str/trim line) #" ")]
                     [["Monkey" _]] [:inspected 0]
                     [(["Starting" "items:" & items] :seq)] [:items (map (comp parse-long (partial re-find #"\d+")) items)]
                     [["Operation:" _ _ _ op val]] [:worry-fn (fn [old] ((resolve (symbol op)) old (if (= "old" val) old (parse-long val))))]
                     [["Test:" "divisible" "by" n]] [:divisible-by (parse-long n)]
                     [["If" "true:" "throw" "to" "monkey" n]] [:on-true (parse-long n)]
                     [["If" "false:" "throw" "to" "monkey" n]] [:on-false (parse-long n)])))
          {}
          monkey))

;; Lastly, parse our input
(def input (->> (slurp (io/resource "inputs/2022/day11.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (partition-by #(str/blank? %))                ;; Partition by blank lines (which gives of a vector of monkey lines)
                (remove #(= % '("")))                         ;; Remove the empty lines
                (mapv parse-monkey)))                         ;; Parse the monkey lines and return a vector (needed for `assoc`)

;; Great, we have our vector of initial monkeys!
;;
;; Next, we need to compute the inspection.
{:nextjournal.clerk/visibility {:result :hide}}
(defn inspect [monkey inspect-fn item]
  ((comp int math/floor inspect-fn (:worry-fn monkey)) item))

{:nextjournal.clerk/visibility {:result :show}}
;; Now we have to create something to move the items for a given monkey index.
(:items (first input))

;; First we compute all the inspect values
(map (partial inspect (first input) (fn [old] (/ old 3))) (:items (first input)))

;; Then we group them by the relevant conditions
(group-by #(if (= 0 (mod % (:divisible-by (first input))))
             (:on-true (first input))
             (:on-false (first input)))
          (map (partial inspect (first input) (fn [old] (/ old 3))) (:items (first input))))

{:nextjournal.clerk/visibility {:result :hide}}
;; Lastly we reduce over the list of monkeys and update the items to reflect
;; changes, as well as update the inspection counts
(defn move-items [monkeys monkey-idx inspect-fn]
  (let [monkey (nth monkeys monkey-idx)]
    (if (empty? (:items monkey))
      monkeys
      (->> (map (partial inspect monkey inspect-fn) (:items monkey))
           (group-by #(if (= 0 (mod % (:divisible-by monkey)))
                        (:on-true monkey)
                        (:on-false monkey)))
           (reduce (fn [monkeys [idx items]]
                     (let [next-monkey (nth monkeys idx)]
                       (-> (assoc monkeys idx (assoc next-monkey :items ((comp vec flatten cons) (:items next-monkey) items)))
                           (assoc monkey-idx (assoc monkey :items [] :inspected (+ (:inspected monkey) (count (:items monkey))))))))
                   monkeys)))))

;; Nearly there now, here we create a function to advance a single round.
;;
;; By that, I mean we go through each monkey and apply all the movement commands
;; of each monkey once using a reduce over the indexes.
(defn advance-round [inspect-fn monkeys]
  (reduce #(move-items %1 %2 inspect-fn)
          monkeys
          (range 0 (count monkeys))))

;; Next we advance rounds by creating an infinite sequence of all possible
;; rounds and returning the round we care about
(defn advance-rounds [n inspect-fn input]
  (nth (iterate (partial advance-round inspect-fn) input) n))

;; Lastly since both parts are identical, we can use a general compute function
;; to handle both
(defn compute-score [n inspect-fn input]
  (->> (advance-rounds n inspect-fn input)
       (map :inspected)
       sort
       (take-last 2)
       (apply *)))

;; Now the parts!
;;
;; Part 1 runs for 20 rounds and takes the `/ 3` function during the inspect
;; step
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (compute-score 20 #(/ % 3) input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 runs for _10,000_ rounds and takes what looks like dark magic during
;; the inspect step.
;;
;; Due to the level of computation required to handle such infeasibly large
;; numbers (try this without the function and see how many steps you get), we
;; can't just compute the values as expected. We have to use some clever modular
;; arithmetic.
;;
;; In reality we don't need the full value, just the greatest common divisor of
;; all monkeys. This guarantees that divisibility is always correct due to
;; [congruence](https://en.wikipedia.org/wiki/Congruence_relation), meaning we
;; can specifically add and multiply without affecting the divisibility.
;;
;; Nifty stuff! Thanks maths. We can now safely perform as many rounds as we
;; want, but for the answer we only care about 10,000
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (compute-score 10000 (fn [n] (mod n (reduce #(* (:divisible-by %2) %1) 1 input))) input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
