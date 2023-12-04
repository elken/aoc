^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2023.day04
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.java.io :as io]
   [clojure.set :as set]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "04" "2023"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Christ, I'm getting whiplash already.
;;
;; _This_ is what yesterday should have been.
;;
;; But anyway, today has us going through a list of scratchcard outcomes and
;; attempts. We have to get all the wins in both parts, part 1 wants us to get 1
;; point for 1 match then double it $(wins - 1)$ times; with part 2 wanting us
;; to have a win give a copy of the nth card (eg Card 1 wins 3 times, so we get
;; a copy of the next 3 cards) and we sum up the total cards.
;;
;; Today was stupidly simple on the surface, by the time I was done with my
;; morning routine I'd already solved part 1 in my head.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2023/day04.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## count-matches
;; The only function we need, a regex to capture a number then use lookahead to
;; see if it occurs again; crucially ignoring the card number.
(defn count-matches [line]
  (count
   (re-seq
    #"\b(\d+)\b(?=.*\b\1\b)"
    (last (str/split line #": ")))))

;; ## Part 1
;;
;; No general solution for this one, it's quite simple.
;;
;; We map over all the lines, get the matches for each line; then if the matches
;; is over 1 we generate an infinite sequence of all the number doubled and get
;; the index at $n - 1$ (since we _start_ at 1)
(defn part-1
  [input]
  (->> input
       (map
        (fn [line]
          (let [matches (count-matches line)]
            (if (> 1 matches)
              matches
              (nth
               (iterate #(* 2 %) 1)
               (dec matches))))))
       (reduce +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 is more involved, and had me scratching my head for a little bit
;; trying to solve everything with 1 reduction. I caved, and we ended up here.
;;
;; We reduce over the input with an init vector of 1s for every line (since we
;; start with 1 copy of all the cards), compute the matches as normal, then
;; reduce over a range of the next card to either the end or $(n + 1) + matches$
;; to update each value within the accumulator (the total count of instances)
;;
;; Once we have this, it's just a simple `reduce +`
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> input
       (reduce-kv
        (fn [cards idx line]
          (let [matches (count-matches line)]
            (reduce (fn [acc row]
                      (update acc row (partial + (acc idx))))
                    cards
                    (range (inc idx) (min (count cards) (+ (inc idx) matches))))))
        (vec (repeat (count input) 1)))
       (reduce +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
