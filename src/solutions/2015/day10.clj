^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day10
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "10" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; There seems to be a trend of some of these problems being almost built for clojure.
;;
;; I started down a rabbit hole of `frequencies` before realising how
;; to read; and ended up with a far simpler solution anyway...
;;
;; Today's problem has us parsing a "look-and-say" sequence `n` number
;; of times, 40 for part 1 and 50 for part 2.
;;
;; By that, you take a string and "say" what it is; so given the
;; example "1211" you have "one one, one two, two one's" which then
;; becomes 111221.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day10.txt")) ;; Load the resource
                str/trim))                                    ;; Trim the newline
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### compute-next
;; Computing the next iteration is all we really do here.
;;
;; We start by partitioning the input by identity, which gives us the
;; sequence split into unique numbers:
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(partition-by identity input)
{:nextjournal.clerk/visibility {:code :show :result :hide}}
;; Next we use `mapcat` to `map` over each unique set of numbers and
;; produce the `count` and the `first` element:
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(mapcat (juxt count first) (partition-by identity input))
{:nextjournal.clerk/visibility {:code :show :result :hide}}
;; And finally we convert to a string to make the parsing in huge
;; lists simpler.
(defn compute-next [input]
  (->> input
       (partition-by identity) 
       (mapcat (juxt count first))
       (apply str)))
;; ### solve
;;
;; Both parts are the same, only differing on how many iterations to
;; perform; so we have a general solver to take the limit and `inc`
;; it (since the first iteration would just be the value itself) and
;; count the last element.
(defn solve [input limit]
  (->> input
       (iterate compute-next)
       (take (inc limit))
       last
       count))

;; ## Part 1
;; Part 1 wants 40 iterations
(defn part-1
  [input]
  (solve input 40))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; And part 2 wants 50 iterations
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input 50))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
