^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day06
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "06" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Our first whiplash moment of the year! Yesterday's problem was quite tricky, and this one is barely a few lines long...
;;
;; Not a _whole_ lot to say about this one, we create a sliding partition of the
;; input string and check if there's a distinct run of characters there. I spent
;; a bunch of time trying to create a map that kept track of everything we'd
;; seen so far ... seems I should just go with my gut next time.
;;
;; Both parts are probably the most similar they've been, the only difference is the size of the character repetitions.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2022/day06.txt")) ;; Load the resource
                (str/split-lines)                             ;; Split into lines
                first))                                       ;; Only a single line this time

;; Next, let's create a function to handle everything really.
;;
;; Given an input string and a defined length, create a partition that looks like
(partition 4 1 "abcdefgh")

;; As you can see, we have a sliding list of all the characters (a nice trick
;; borrowed from last year) and all we need to do is loop over that with the
;; index to find a run of non-repeating characters. When we find one, the actual
;; number is the length + the current index (since the index is how many groups
;; in we are, not how many chars in).
(defn get-header-index [input length]
  (let [groups (partition length 1 input)]
    (loop [index 0]
      (if (= length (count (distinct (nth groups index))))
        (+ index length)
        (recur (inc index))))))

;; Both parts differ only on length, so we can trivially define part 1
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (get-header-index input 4))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; And finally part 2
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (get-header-index input 14))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
