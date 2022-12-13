^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day13
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [nextjournal.clerk :as clerk]
   [util :as u]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "13" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Finally, a day built for clojure! As soon as I saw this problem, I was
;; rubbing my hands with glee; thanks to `clojure.edn/read-string`. With it, we
;; don't need any parsing logic at all and we can simply just load the input and
;; have Clojure take care of everything!
;;
;; Today's problem is an extremely simple one, simply a case of comparing values
;; in the input under certain rules. Those rules being:
;;
;; - If `left` and `right` are both numbers, sort as expected
;; - If `left` and `right` are both lists, map compare over and ensure neither runs out of items
;; - Otherwise, make whichever side is an `int` into a list and compare
;;
;; The part difference is probably the biggest difference yet, part 1 is just a
;; simple case of verifying all the rules hold true and summing up the values
;; that are already sorted; whereas part 2 asks you to sort the input data and
;; muliply the indexes of where `[2]` and `[6]` would appear.
;;
;; First things first, let's load our input and parse it. All we need is this
;; simple function and we have a vector of all our input
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn parse-input [input]
  (edn/read-string (str "[" input "]")))

{:nextjournal.clerk/visibility {:code :show :result :show}}
(def input (->> (slurp (io/resource "inputs/2022/day13.txt")) ;; Load the resource
                parse-input))                                 ;; Parse the input into a vector of vectors

;; Next we have our pun-named pair compare function.
;;
;; Given the two lists, check if any are numbers and `compare` the ints or
;; re-run `compair`. In the case of both being vectors, we try and bind the
;; result of mapping `compair` over both lists and if we have one, return it
;; otherwise verify the counts.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn compair [left right]
  (condp = [(number? left) (number? right)]
    [true true] (compare left right)
    [true false] (compair [left] right)
    [false true] (compair left [right])
    ;; else
    (if-let [ordered? (first (filter #(not (= 0 %)) (map compair left right)))]
      ordered?
      (compare (count left) (count right)))))

;; That's all we need!
;;
;; Part 1 we simply `reduce` over the partitioned input
;; (giving us our `left` and `right` to compare) verifying the list is sorted.
;; If it is, we add the index to the total plus 1 (0-indexing) and return it.
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (let [input (partition 2 input)]
    (reduce (fn [total [left right :as vs]]
              (if (= -1 (compair left right))
                (+ (.indexOf input vs) 1 total)
                total))
            0
            input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; For part 2, we have to do things slightly differently. Here we're tasked with
;; sorting the list of input & so-called "divider packets" and then locating
;; where `[2]` and `[6]` would appear.
;;
;; We simply just map over the list of divs to find the associated index in the
;; merged & sorted input with divider packets. We can then just apply `*` over
;; the two results.
;;
;; There's an alternative solution here where you don't sort the list and you
;; just map compair over until you get 1, but I'm happy with this.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [divs [[2] [6]]
        packets (sort compair (concat divs input))]
    (apply * (map #(+ 1 (.indexOf packets %)) divs))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
