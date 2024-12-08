^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day13
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "13" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; If only we could pre-compute happiness scores for wedding table planners...
;;
;; Today has us taking a list of statements about how happy people
;; would be sitting next to each other. Part 1 wants us to compute
;; the "happiest" configuration, and part 2 wants us to include us as
;; a guest, of which our happiness scores are 0 because we're so
;; easy-going.
;;
;; First things first, let's load our input and parse it. Here we use
;; regex to split up the statements into the groups we care about.
(defn parse-input [input]
  (let [pattern #"(\w+) would (gain|lose) (\d+) .* (\w+)\."]
    (into {}
          (for [line input
                :let [[_ p1 action n p2] (re-matches pattern line)]]
            [[p1 p2] (* (parse-long n) (if (= action "gain") 1 -1))]))))

(def input (->> (slurp (io/resource "inputs/2015/day13.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input))                                 ;; Parse the statements
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### permutations
;;
;; Slightly improperly named; this computes all the one-way
;; permutations of a set of strings (our guest list). This is to
;; ensure we don't pointlessly calculate any arrangements we've
;; already seen.
(defn permutations [s]
  (->> (for [x s
             :let [xs (disj s x)]
             p (if (seq xs) (permutations xs) [[]])]
         (conj p x))
       (map #(if (pos? (compare (first %) (last %))) (reverse %) %))
       set))

;; ### guests
;;
;; Get a listing of all the guests by getting the first person from
;; each ordering and creating a set.
(defn guests [input]
  (->> input keys (map first) set))

;; ### solve
;;
;; Both parts are solved in the same way, the main difference is what
;; the guest list is like.
;;
;; We start by getting all the one-way permutations for our guest list
;; and start a parallel `map` over each one to find the happiness score.
;;
;; We compute the happiness score by using a transducer over `reduce`
;; generating our scores.
;;
;; We can't just consider the permutation we get, as it's not
;; complete. Take this example permutation.
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(first (permutations (guests input)))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; This gives us everyone at the table, but we have to generate all
;; the pairs
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(partition 2 (first (permutations (guests input))))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; This is an improvement! However, we also need to consider the
;; opposite arrangements; to get the other set of scores. We generate
;; all the valid pairs and use the `reduce` to sum up the happiness
;; score.
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(let [seating (first (permutations (guests input)))]
  (for [i (range (count seating))
        :let [j (mod (inc i) (count seating))]]
    [(nth seating i) (nth seating j)]))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; Putting it all together
(defn solve [input guests]
  (transduce
   (comp
    (map
     (fn [seating]
       (reduce
        (fn [total-happiness person-index]
          (let [next-person-index (mod (inc person-index) (count seating))
                pair [(nth seating person-index) (nth seating next-person-index)]]
            (+ total-happiness
               (get input pair 0)
               (get input (reverse pair) 0))))
        0
        (range (count seating)))))
    (completing max))
   max
   0
   (permutations guests)))

;; ## Part 1
;;
;; Part 1 just uses the default guest list
(defn part-1
  [input]
  (solve input (guests input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 also includes "me" as a guest with a default score of
;; 0 (which we handle inside the reduction of solve)
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input (conj (guests input) "me")))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
