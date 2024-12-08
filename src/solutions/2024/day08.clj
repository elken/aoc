^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day08
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "08" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Happened to wake up early enough to attempt a leaderboard run, of
;; course THIS is the day I do so...
;;
;; If you've been following these as I do them, you'll know I really
;; don't enjoy these coordinate puzzles. Day 6 this year took me way
;; too long to solve cleanly, and while today didn't take quite as
;; long I'm still not quite happy with it. It took me about the length
;; of time it would take to get on the leaderboard to even be able to
;; understand the problem.
;;
;; Today's problem has us trying to find and map points on a
;; grid. Given a pair of coordinates that both share an antenna (a
;; character) we have to find places where a bunch of other nodes can
;; exist given conditions based on the parts. Part 1 has us checking
;; if the positions are 1 step away and part 2 has us checking for any
;; positions.
;;
;; First things first, let's load our input and parse it. We create a
;; little helper here to generate all the unique pairs we need.
;;
;; We also keep the bounds for bounds checks later
(defn all-pairs [lst]
  (for [i (range (count lst))
        j (range (inc i) (count lst))]
    [(nth lst i) (nth lst j)]))

(defn parse-input [input]
  (let [positions (for [y (range (count input))
                        x (range (count (nth input y)))
                        :let [c (get-in input [y x])]
                        :when (not= c \.)]
                    [c [x y]])
        width (apply max (map count input))
        height (count input)]
    [(->> positions
          (group-by first)
          (map #(mapv second (second %)))
          (mapcat all-pairs))
     (dec width)
     (dec height)]))

(def input (->> (slurp (io/resource "inputs/2024/day08.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                parse-input))                                 ;; Parse into pairs and bounds                         
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### in-bounds?
;;
;; Uses the same bound checker from Day 6, just verifying if we're
;; inside the grid.
(defn in-bounds? [[x y] w h]
  (and (<= 0 x w)
       (<= 0 y h)))

;; ### process-path
;;
;; Given a pair of coordinates and the bounds of the grid, verify if
;; the points are collinear by calculating their displacement,
;; figuring out a starting direction and generate steps until we run
;; out of valid points.
;;
;; We do this for both diagonal directions and `concat` the results.
(defn process-path [[x1 y1] [x2 y2] w h]
  (->> (for [dir [-1 1]
             :let [dx (* dir (- x2 x1))
                   dy (* dir (- y2 y1))
                   start-x (if (pos? dir) x2 x1)
                   start-y (if (pos? dir) y2 y1)]]
         (->> (iterate (fn [[x y s]] [(+ x dx) (+ y dy) (inc s)])
                       [start-x start-y 0])
              (take-while #(in-bounds? % w h))))
       (apply concat)))

;; ### solve
;;
;; Both parts do the same thing here, the only difference is the
;; predicate we use to determine if we should count a valid position
;; for an antinode or not.
;;
;; For all our combinations, we find all the valid paths and filter
;; out ones we don't care about.
;;
;; Since the results of `process-path` are a vector of `[x y step]`,
;; after filtering by step we then only care about the first two
;; results. We can then just filter out duplicates with `set` and
;; count the totals.
(defn solve [[combinations w h] pred]
  (->> combinations
       (mapcat #(apply process-path (concat % [w h])))
       (filter #(pred (nth % 2)))
       (map (partial take 2))
       set
       count))

;; ## Part 1
;;
;; Part 1 just wants us to find antinode positions that are 1 step apart
(defn part-1
  [input]
  (solve input (partial = 1)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 wants us to find any of them, so we just make the predicate
;; return `true` all the time
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input (constantly true)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
