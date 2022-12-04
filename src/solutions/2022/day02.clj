;; # 2022 - Day 02
;;
;; ## Problem
;; ### Part 1
;; The Elves begin to set up camp on the beach. To decide whose tent gets to be
;; closest to the snack storage, a giant Rock Paper Scissors tournament is
;; already in progress.

;; Rock Paper Scissors is a game between two players. Each game contains many
;; rounds; in each round, the players each simultaneously choose one of Rock,
;; Paper, or Scissors using a hand shape. Then, a winner for that round is
;; selected: Rock defeats Scissors, Scissors defeats Paper, and Paper defeats
;; Rock. If both players choose the same shape, the round instead ends in a
;; draw.

;; Appreciative of your help yesterday, one Elf gives you an **encrypted strategy
;; guide** (your puzzle input) that they say will be sure to help you win. "The
;; first column is what your opponent is going to play: `A` for Rock, `B` for Paper,
;; and `C` for Scissors. The second column--" Suddenly, the Elf is called away to
;; help with someone's tent.

;; The second column, you reason, must be what you should play in response: `X`
;; for Rock, `Y` for Paper, and `Z` for Scissors. Winning every time would be
;; suspicious, so the responses must have been carefully chosen.

;; The winner of the whole tournament is the player with the highest score. Your
;; **total score** is the sum of your scores for each round. The score for a single
;; round is the score for the **shape you selected** (1 for Rock, 2 for Paper, and 3
;; for Scissors) plus the score for the **outcome of the round** (0 if you lost, 3
;; if the round was a draw, and 6 if you won).

;; Since you can't be sure if the Elf is trying to help you or trick you, you
;; should calculate the score you would get if you were to follow the strategy
;; guide.

;; For example, suppose you were given the following strategy guide:

;; ```
;; A Y
;; B X
;; C Z
;; ```

;; This strategy guide predicts and recommends the following:

;;- In the first round, your opponent will choose Rock (`A`), and you should
;; choose Paper (`Y`). This ends in a win for you with a score of **8** (2 because
;; you chose Paper + 6 because you won).
;;- In the second round, your opponent will choose Paper (`B`), and you should
;; choose Rock (`X`). This ends in a loss for you with a score of 1 (1 + 0).
;;- The third round is a draw with both players choosing Scissors, giving you a
;; score of 3 + 3 = 6.

;; In this example, if you were to follow the strategy guide, you would get a
;; total score of `15` (8 + 1 + 6).

;; **What would your total score be if everything goes exactly according to your
;; strategy guide?**
;;
;; ### Part 2
;; The Elf finishes helping with the tent and sneaks back over to you. "Anyway,
;; the second column says how the round needs to end: `X` means you need to lose,
;; `Y` means you need to end the round in a draw, and `Z` means you need to win.
;; Good luck!"

;; The total score is still calculated in the same way, but now you need to
;; figure out what shape to choose so the round ends as indicated. The example
;; above now goes like this:

;;- In the first round, your opponent will choose Rock (`A`), and you need the
;; round to end in a draw (`Y`), so you also choose Rock. This gives you a score
;; of 1 + 3 = **4**.
;;- In the second round, your opponent will choose Paper (`B`), and you choose
;; Rock so you lose (`X`) with a score of 1 + 0 = **1**.
;;- In the third round, you will defeat your opponent's Scissors with Rock for a
;; score of 1 + 6 = **7**.

;; Now that you're correctly decrypting the ultra top secret strategy guide, you
;; would get a total score of **12**.

;; Following the Elf's instructions for the second column, **what would your total
;; score be if everything goes exactly according to your strategy guide?**
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.2022.day02
    (:require [clojure.java.io :as io]
              [clojure.test :as t :refer [deftest]]
              [clojure.string :as str]))
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

;; And running it gives
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Exactly the same for part 2, just with a different map
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (map results) + 0 input))

;; And running it gives
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:result :hide}}
(deftest test-answers
  (t/is (= 9759 (part-1 input)))
  (t/is (= 12429 (part-2 input))))
