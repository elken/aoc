^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day03
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "03" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Ah, baited again...
;;
;; I thought this was going to be my favourite day, the "make a fake
;; computer that handles these instructions" day; but alas it's just
;; regex. I swear regex is Advent of Code's loss comic...
;;
;; Today we're tasked with finding instructions in a batch of
;; corrupted memory. For part 1, we need to just find any instruction
;; that looks like `mul(\d,\d)` exactly.
;;
;; For part 2, we gain two new rules:
;; - If we find a `don't()`, we stop handling `mul` instructions
;; - Until we see a `do()`, then we go back to handling them.
;;
;; First things first, let's load our input and parse it. Notably for
;; the first time I think, we just treat the input as a single string,
;; I think it's a trap that you need to handle all the lines on their
;; own.
(def input (->> (slurp (io/resource "inputs/2024/day03.txt")))) ;; Load the resource
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### parse-mul
;; A simple helper we use in both parts to parse a `mul` result. To
;; prevent some duplication, we define it as a multimethod since we
;; use it in two different places and thankfully with 2 different
;; inputs.
(defmulti parse-mul type)
(defmethod parse-mul String [s]
  (parse-mul (re-find #"mul\((\d+),(\d+)\)" s)))
(defmethod parse-mul clojure.lang.PersistentVector [[_ n1 n2]]
  (* (parse-long n1) (parse-long n2)))

;; ### find-instructions
;; The general solver for part 1.
;;
;; We simply just match all the `mul` instructions with regex, map
;; over them to get the product from each pair then sum the results.
(defn find-instructions
  [input]
  (->> input
       (re-seq #"mul\((\d+),(\d+)\)")
       (map parse-mul)
       (apply +)))

;; ### find-allowed-instructions
;; The general solver for part 2.
;;
;; This time it's much more involved. We start by getting all the
;; tokens that we care about, either valid `mul` instructions, `do`
;; instructions or `don't` instructions.
{:nextjournal.clerk/visibility {:result :show}}
(re-seq #"mul\(\d+,\d+\)|do\(\)|don't\(\)" input)
{:nextjournal.clerk/visibility {:result :hide}}
;; With this input, we begin a reduction over them and construct a
;; hash over each iteration to keep track of the active state and the
;; sum total. Once we have the total, we just return that.
(defn find-allowed-instructions [memory]
  (let [tokens (re-seq #"mul\(\d+,\d+\)|do\(\)|don't\(\)" memory)]
    (->> tokens
         (reduce (fn [{:keys [active sum]} token]
                   (case token
                     "do()" {:active true :sum sum}
                     "don't()" {:active false :sum sum}
                     {:active active
                      :sum (if active 
                             (+ sum (parse-mul token))
                             sum)}))
                 {:active true :sum 0})
         :sum)))

;; Of which we can see all the intermediate states below
{:nextjournal.clerk/visibility {:result :show :code :hide}}
(let [tokens (re-seq #"mul\(\d+,\d+\)|do\(\)|don't\(\)" input)]
  (->> tokens
       (reductions (fn [{:keys [active sum]} token]
                     (case token
                       "do()" {:active true :sum sum}
                       "don't()" {:active false :sum sum}
                       {:active active
                        :sum (if active 
                               (+ sum (parse-mul token))
                               sum)}))
                   {:active true :sum 0})))
{:nextjournal.clerk/visibility {:result :hide :code :show}}

;; ## Part 1
;; Both parts are solved nice and easy by just calling their functions
;; with the input.
;;
;; For part 1, we want to just find and parse any instruction.
(defn part-1
  [input]
  (find-instructions input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Same as with part 1, we just call the function with the input.
;;
;; For part 2, we want to only parse allowed instructions based on our rules (see [above](#solution))
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (find-allowed-instructions input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
