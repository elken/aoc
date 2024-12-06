^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day04
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "04" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; As soon as I saw "Ceres monitoring station" my heart sank...
;;
;; No word of a lie; these are probably my least favourite problems. I
;; don't know what it is about nearest neighbour stuff, but it just
;; shuts my brain down. That day in 2019 was quite painful...
;;
;; Today has us doing a similar thing to that 2019 problem, but this
;; time in the guise of a word search.
;;
;; Part 1 has us simply finding XMAS, whereas part 2 wants us to find
;; an X made of MAS (see the problem definition).
;;
;; First things first, let's load our input and parse it
;;
;; We bring back the good old function to parse the input into a matrix of coords
{:nextjournal.clerk/visibility {:result :hide}}
(defn range->coords
  [matrix]
  (into {}
        (for [x (range (count matrix))
              y (range (count (first matrix)))]
          [[x y] (str (get (get matrix x) y))])))
{:nextjournal.clerk/visibility {:result :show}}

;; Then we parse
(def input (->> (slurp (io/resource "inputs/2024/day04.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse into a grid

;; ## Functions
;; ### directions
;; Our simple generator of all possible coordinates
;;
;; Useful to quickly compute as we can just `map` over this.
(def directions 
  (for [x [-1 0 1]
        y [-1 0 1]
        :when (not= [x y] [0 0])]
    [x y]))
{:nextjournal.clerk/visibility {:result :hide}}

;; ### cells-at
;;
;; Given the grid, a current position and offsets to search; return a string of the string we've found.
(defn cells-at [grid pos offsets]
  (map #(get grid (mapv + pos %) "") offsets))

;; Which looks like
{:nextjournal.clerk/visibility {:result :show :code :hide}}
(let [start-pos [0 0]                    
      offsets [[0 0] [1 0] [2 0] [3 0]]] 
  (apply str (cells-at input start-pos offsets)))
{:nextjournal.clerk/visibility {:result :hide :code :show}}

;; ### check-xmas
;; Our part 1 solver.
;;
;; Given the grid, a position to search from and a direction to search
;; in; return true or false if we found a match.
;;
;; Here we use a transducer to build up the string by getting all
;; valid neighbours in the direction specified and comparing to XMAS
(defn check-xmas [grid pos dir]
  (= "XMAS" 
     (transduce (comp (map #(mapv + pos (map * dir (repeat %))))
                      (map #(get grid % "")))
                str
                ""
                (range 4))))

;; ### check-special
;; Our part 2 solver.
;;
;; This time we have to take a different approach as we're only considering diagonals.
;;
;; We do this by trying to find a MAS in one diagonal and an MS in the orthogonal.
(defn check-special [grid pos [dx dy :as dir]]
  (when (and (not (zero? dx)) 
             (not (zero? dy)))
    (let [orthog [(- dy) dx]
          at #(get grid (mapv + pos %) "")]
      (and (= (str (at dir) (at [0 0]) (at (map - dir))) "MAS")
           (= (str (at orthog) (at (map - orthog))) "MS")))))

;; ### count-matches
;; Last but not least; our general solver function! Takes one of the
;; part's predicates and a grid and computes all the matches.
(defn count-matches [pred grid]
  (->> (for [pos (keys grid)
             dir directions
             :when (pred grid pos dir)]
         1)
       count))


;; ## Part 1
;; Part 1 just wants us to find all the XMAS values
(defn part-1
  [input]
  (count-matches check-xmas input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 instead wants us to find all the MAS values that form an X
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (count-matches check-special input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
