^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day07
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "07" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; The classic trend of the weekend being simple maintains!
;;
;; Yesterday was a bit of a doozy (as of writing I still haven't quite
;; done it) so having the weekend be simpler is a nice breather.
;;
;; Today's problem has us taking a total and a list of numbers and
;; determining if you can use combinations of `+` and `*` to get the
;; total. Part 2 extends this by introducing a `cat` which takes 2
;; numbers and concatenates them.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day07.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (map (partial re-seq #"\d+"))                 ;; Grab all the numbers
                (map (partial map parse-long))))              ;; Parse into ints
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### cat
;;
;; Used in part 2 to concatenate numbers, can't really beat this for
;; simplicity...
(defn cat [a b] 
  (parse-long (str a b)))

;; ### correct-sequence?
;;
;; Given a list of valid operations and a destructured list of
;; numbers, recursively try each op on each set of lists until we
;; create a tree of all possible op combinations. When we use a
;; number, remove it.
;;
;; By doing this, we can check when the nums list is 2 if the total is
;; correct and if not, the `some` will prune that recursive journey;
;; leaving either the only valid route (an assumption not made obvious
;; by the problem here is that only 1 combination works) or `nil` if
;; the sequence isn't correct.
(defn correct-sequence? [ops [total a b & rest :as nums]]
  (if (= (count nums) 2)
    (= total a)
    (some #(correct-sequence? ops (into [total (% a b)] rest)) ops)))

;; ### solve
;;
;; Since both parts do the same thing, we write a general solver to
;; make it look cleaner
(defn solve [ops input]
  (->> input
       (filter (partial correct-sequence? ops))
       (apply map +)
       first))

;; ## Part 1
;;
;; Part 1 just wants us to handle `+` and `*`
(defn part-1
  [input]
  (solve [+ *] input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 introduces `cat`, which we handle neatly using strings
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve [+ * cat] input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
