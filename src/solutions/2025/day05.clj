^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day05
  {:nextjournal.clerk/toc true
   :nextjournal.clerk/open-graph
   {:title "Day 5: Cafeteria"
    :type "article"
    :url "https://elken.github.io/aoc/src/solutions/2025/day05"
    :image "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjpfXKLY6RgQC6EssCHv8UvXSCuVIbCiVenJau8OcW8MxZYrqVca3dL_Afe3TZNOUWHrewzvgfCfgPIafT3BNIBxFVTdTx52MmDW7m8TX9HZ32DhrfVDb8W3NvVaAayqOIUYMg7kxCbJQc/s1600/Screen+Shot+2015-12-25+at+10.46.23+AM.png"}}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "05" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; The difficulty curve giveth, the difficulty curve taketh away. At
;; last an easy day! Sadly, I fell into the trap with this one and
;; tried to expand the ranges thinking I was really smart, but Eric
;; played me for a chump...
;;
;; Today's problem has us checking for membership in ranges for part
;; 1, and in a clever part 2 subversion we have to count how many
;; numbers exist in the ranges. I was expecting to have to track how
;; many times each number appears in the ranges.

;; ## Functions
;;
;; ### parse-range
;;
;; Given a range string like `3-5` just convert it to a vector like
;; `[3 5]`.
(defn parse-range [s]
  (mapv parse-long (re-seq #"\d+" s)))

;; ### parse-input
;;
;; Given our two groups of input, the ranges and the ids, convert the
;; ranges to vector pairs and the ids to just a list of numbers.
;;
;; In an ideal world, we would expand the ranges and put them into a
;; set, but c'est la vie.
(defn parse-input [ranges ids]
  [(map parse-range ranges) (map parse-long ids)])

;; ### in-range?
;;
;; A shorthand function that saves an ugly nested lambda later. Just
;; checks if `id` exists in the range of `l`ower `b`ound and `u`pper
;; `b`ound.
(defn in-range? [id [lb ub]]
  (<= lb id ub))

;; ### count-unique-numbers
;;
;; Our part 2 solver basically. As I've alluded to, you can't just
;; expand the range and expect it to work, Eric's made them too
;; large. Instead we have to count the `total` unique numbers by reducing over
;; them and keeping track of `end` values.
;;
;; For each range, if the lower bound of the range is outside of the
;; current `end`, we add the length of the range to the total and set
;; the `u`pper `b`ound to the new `end`.
;;
;; Otherwise if there's an overlap, we only count the "new" part (from
;; whatever's larger between current `end` and `u`pper `b`ound to the
;; current `end`) and set the `end` to whatever's larger of `u`pper
;; `b`ound and current `end`.
(defn count-unique-numbers [ranges]
  (first
   (reduce (fn [[total end] [lb ub]]
             (if (> lb (inc end))
               [(+ total (inc (- ub lb))) ub]
               [(+ total (- (max ub end) end)) (max ub end)]))
           [0 0]
           (sort ranges))))

;; ## Input
;;
;; Now we can load our input and parse it. I hoped there was a cleaner
;; way to do this, but I think this is it.
(def input (->> (slurp (io/resource "inputs/2025/day05.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (partition-by str/blank?)                     ;; Partition into lists based on blank lines
                (remove (partial every? str/blank?))          ;; The partition also includes the blank line as an element, so remove it
                (apply parse-input)))                         ;; Pass to our parse-input function destructured

;; ## Part 1
;;
;; Part 1 wants us to count the ids that exist in the ranges, so we
;; apply our `<=` check across all ranges for each id and count the
;; `true`s.
(defn part-1
  [input]
  (let [[ranges ids] input]
    (count (filter #(some (partial in-range? %) ranges) ids))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 discards the ids and focuses on just counting how many
;; numbers are in each range, so
;; see [`count-unqiue-numbers`](#count-unique-numbers).
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [[ranges _] input]
    (count-unique-numbers ranges)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
