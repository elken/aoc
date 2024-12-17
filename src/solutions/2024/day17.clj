^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day17
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "17" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Waited 17 days for this one!
;;
;; Ah, the inevitable "parse this program" problem. I LOVE this type,
;; and I have been wading through the terrible grid problems just for
;; it.
;;
;; Today's problem has us understand and parse a 3-bit instruction
;; set (0-7) for part 1, and part two uses that logic to try and find
;; a quine (a program that outputs its own input).
;;
;; First things first, let's load our input and parse it. We simply
;; just parse the two groups into two lists of numbers here.
(defn parse-input [input]
  (map #(mapv parse-long (re-seq #"\d+" %)) (str/split input #"\n\n")))

;; and apply it to our input
{:nextjournal.clerk/visibility {:result :show}}
(def input (->> (slurp (io/resource "inputs/2024/day17.txt")) ;; Load the resource
                parse-input))                                 ;; Parse into lists
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### combo-operand
;;
;; Compute our "combo-operand" that's used for some of the
;; instructions, for simplicity and speed we just use a hash lookup.
(defn combo-operand
  [[a b c] operand]
  ({0 0
    1 1
    2 2
    3 3
    4 a
    5 b
    6 c}
   operand))

;; ### adv
;;
;; "a division"
;;
;; Perform $\frac{A}{2^{combo}}$ which we then cast to a `long` for
;; part 2 and store in `A`.
;;
;; Increment Instruction Pointer by 2 as normal.
(defn adv [ip [a b c] operand]
  [(+ ip 2)
   [(long (quot a (Math/pow 2 (combo-operand [a b c] operand)))) b c]
   []])

;; ### bxl
;;
;; "b xor literal"
;;
;; Perform a bitwise-xor of `B` and the literal operand and store in
;; `B`.
;;
;; Increment Instruction Pointer by 2 as normal.
(defn bxl [ip [a b c] operand]
  [(+ ip 2)
   [a (long (bit-xor (long b) (long operand))) c]
   []])

;; ### bst
;;
;; "b shift twice"
;;
;; Perform $literal \mod 8$ and store in `B`.
;;
;; Increment Instruction Pointer by 2 as normal.
(defn bst [ip [a b c] operand]
  [(+ ip 2)
   [a (long (mod (combo-operand [a b c] operand) 8)) c]
   []])

;; ### jnz
;;
;; "jump if not zero"
;;
;; Set the Instruction Pointer to to the literal operand if `A` is not
;; zero, otherwise do nothing.
;;
;; Increment Instruction Pointer by 2 as normal if we don't jump.
(defn jnz [ip [a b c] operand]
  [(if (zero? a) (+ ip 2) operand)
   [a b c]
   []])

;; ### bxc
;;
;; "b xor c"
;;
;; Perform a bitwise-xor of `B` and `C` and store in `B`.
;;
;; Increment Instruction Pointer by 2 as normal if we don't jump.
(defn bxc [ip [a b c] _operand]
  [(+ ip 2)
   [a (long (bit-xor (long b) (long c))) c]
   []])

;; ### out
;;
;; "output"
;;
;; Add the result of $combo \mod 8$ to the list of outputs.
;;
;; Increment Instruction Pointer by 2 as normal if we don't jump.
(defn out [ip [a b c] operand]
  [(+ ip 2)
   [a b c]
   [(mod (combo-operand [a b c] operand) 8)]])

;; ### bdv
;;
;; "b division"
;;
;; Apply the result of `adv` to the `B` register instead.
;;
;; Increment Instruction Pointer by 2 as normal if we don't jump.
(defn bdv [ip [a b c] operand]
  [(+ ip 2)
   [a (->> (adv ip [a b c] operand) second first) c]
   []])

;; ### cdv
;;
;; "c division"
;;
;; Apply the result of `adv` to the `C` register instead.
;;
;; Increment Instruction Pointer by 2 as normal if we don't jump.
(defn cdv [ip [a b c] operand]
  [(+ ip 2)
   [a b (->> (adv ip [a b c] operand) second first)]
   []])

;; ### run-instructions
;;
;; Given the initial register state and our instructions, process
;; until the IP hits the end. When we do, return the outs.
;;
;; Otherwise, recur and process instructions accordingly based on
;; their opcode; ensuring that we handle `long`s where needed.
;;
;; Since we always increase the IP by an even number (assuming the
;; input is valid) we don't have to check for `(inc ip)` existing.
(defn run-instructions
  [[registers program]]
  (loop [ip 0
         registers registers
         outs []]
    (if (= ip (count program))
      outs
      (let [[opcode operand] (map #(long (nth program %)) [ip (inc ip)])
            [new-ip registers new-out]
            (condp = opcode
              0 (adv ip registers operand)
              1 (bxl ip registers operand)
              2 (bst ip registers operand)
              3 (jnz ip registers operand)
              4 (bxc ip registers operand)
              5 (out ip registers operand)
              6 (bdv ip registers operand)
              7 (cdv ip registers operand))]
        (recur
         new-ip
         registers
         (into outs new-out))))))

;; ### dfs
;;
;; Implementation of a depth-first search given the fact that, given
;; it's a 3-bit set, if we keep shifting `A` by 3 we will find correct
;; values faster.
;;
;; For each bit from 0-7 (`range` is exclusive), try a new `A` by
;; left-shifting by 3 and checking the rest of the program from the
;; current bit.
;;
;; We then check 3 conditions:
;;
;; - If the suffix matches, we've found a candidate for `A`. Since we
;; might not yet have found the smallest `A`, we `mapcat` over them
;; to get the full list.
;;
;; - If the suffix matches a prefix (everything before the current
;; bit) then we need to continue the search at another depth as
;; we're close.
;;
;; - Else we found nothing, so return an empty array
;;
;; Our base case ensures we don't over-search and `mapcat` removes the
;; empty results so we're left with all the valid `A` candidates.
(defn dfs
  [program curr bit]
  (if (> bit (count program))
    []
    (mapcat
     #(let [a (bit-or (bit-shift-left curr 3) %)
            result (run-instructions [[a 0 0] program])
            suffix (take (inc bit) (reverse result))]
        (cond
          (and (= (count suffix) (count program))
               (= suffix (take (count program) (reverse program))))
          [a]

          (= suffix (take (count suffix) (reverse program)))
          (dfs program a (inc bit))

          :else
          []))
     (range 8))))

;; ## Part 1
;;
;; Part 1 just wants us to run the program
(defn part-1
  [input]
  (str/join "," (run-instructions input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 wants us to find the smallest `A` that produces a quine.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [[_ program]]
  (apply min (dfs program 0 0)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
