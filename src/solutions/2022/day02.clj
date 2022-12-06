^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day02
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
[:style "em{color: #fff;font-style: normal;text-shadow: 0 0 5px #fff;}.viewer-result:first-child{display: none;}"]

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "02" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Ok, so we have to first compute the scores for a given set of rock, paper,
;; scissors games; then we have to estimate the result given the guide telling
;; us how to play.
;;
;; This can also be solved by using modular arithmetic [like
;; so](https://gist.github.com/elken/6932aebbb5e6527e21c2b850a04b3285), but for
;; simplicity's sake I opted to just use a simple lookup table for both.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2022/day02.txt")) ;; Load the resource
                (str/split-lines)))                           ;; Split into lines

;; As mentioned above, we can compute a simple lookup table of all the results
;; for all games.
;;
;; Win => +6, Draw => +3, Lose +0
;;
;; If you chose Rock => +1, Paper => +2, Scissors => +3
(def scores {"A X" 4   ;; Rock, Rock
             "A Y" 8   ;; Rock, Paper
             "A Z" 3   ;; Rock, Scissors
             "B X" 1   ;; Paper, Rock
             "B Y" 5   ;; Paper, Paper
             "B Z" 9   ;; Paper, Scissors
             "C X" 7   ;; Scissors, Rock
             "C Y" 2   ;; Scissors, Paper
             "C Z" 6}) ;; Scissors, Scissors

;; Similarly for the second part, based on the expected result we can compute
;; what the score would be
(def results {"A X" 3   ;; Scissors, Lose
              "A Y" 4   ;; Rock, Draw
              "A Z" 8   ;; Paper, Win
              "B X" 1   ;; Rock, Lose
              "B Y" 5   ;; Paper, Draw
              "B Z" 9   ;; Scissors, Win
              "C X" 2   ;; Paper, Lose
              "C Y" 6   ;; Scissors, Draw
              "C Z" 7}) ;; Rock, Win

;; Now it's just a simple case of transducing the map of scores over the input
;; and summing them
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (transduce (map scores) + 0 input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Exactly the same for part 2, just with a different map
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (map results) + 0 input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
