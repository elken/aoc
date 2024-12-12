^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day12
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "12" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; The wall is _fast_ approaching...
;;
;; Ugh, as soon as I saw another grid I groaned very loudly. Not a
;; fan, and they're slowly wearing me down. I gave up for part 2 and
;; came back to completely refactor everything I did. As with the last
;; grid problem, the explanation will be quite terse and I consider
;; this write-only code.
;;
;; Today sees us trying to calculate area and perimeter of shapes from
;; a grid, which we have to define by flood fill. Part 2 (cursed)
;; wants us to count the sides instead of perimeter.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2024/day12.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; And store our valid movement directions
(def directions [[0 -1] [0 1] [-1 0] [1 0]])

;; ## Functions
;; ### valid?
;;
;; Check if a point is valid within the confines of the grid.
(defn valid? [grid [x y]]
  (and (>= x 0) (< x (count (first grid)))
       (>= y 0) (< y (count grid))))

;; ### flood-fill
;;
;; Implementation of a flood fill algorithm.
(defn flood-fill [grid [x y :as start]]
  (let [val (get-in grid [y x])
        next-points (fn [[cx cy]]
                      (->> directions
                           (map (fn [[dx dy]] [(+ cx dx) (+ cy dy)]))
                           (filter #(and (valid? grid %)
                                         (= val (get-in grid [(second %) (first %)]))))))]
    (loop [seen #{start}
           [curr & rest] [start]]
      (if-not curr
        seen
        (let [neighbors (filter #(not (seen %)) (next-points curr))]
          (recur (into seen neighbors)
                 (concat rest neighbors)))))))

;; ### find-areas
;;
;; Find all the areas in our grid. We can't just do a group-by to find
;; this quickly because there can be multiple letters having
;; regions (imagine if it wasn't...)
(defn find-areas [grid]
  (let [positions (for [y (range (count grid))
                        x (range (count (first grid)))]
                    [x y])]
    (->> positions
         (reduce
          (fn [{:keys [areas seen]} pos]
            (if (seen pos)
              {:areas areas :seen seen}
              (let [area (flood-fill grid pos)]
                {:areas (conj areas area)
                 :seen (into seen area)})))
          {:areas [] :seen #{}})
         :areas)))

;; ### perimeter
;;
;; For a given area in the grid, calculate the perimeter. For each
;; point in the area, look at its four adjacent positions then count
;; how many of those positions are either outside the grid or have a
;; different value. Sum up all these "missing neighbor" counts to get
;; the perimeter
(defn perimeter [grid area]
  (let [val (get-in grid [(second (first area)) (first (first area))])
        valid-point? (fn [[x y]]
                       (and (valid? grid [x y])
                            (= val (get-in grid [y x]))))
        missing-neighbors (fn [pos]
                            (->> directions
                                 (map #(mapv + pos %))
                                 (remove valid-point?)
                                 count))]
    (->> area
         (map missing-neighbors)
         (reduce +))))

;; ### count-sides
;;
;; The dreaded part 2. The saving grace that got me to finish this was
;; the sheer amount of people who haven't. The trick here is looking
;; for points that are left sides, in that there isn't a point to the
;; left and either no point below or a point at the diagonal. We then
;; count all these left edges in the current orientation, then we
;; rotate about both axis and apply the same logic there. Adding all
;; these counts gives us our total.
(defn count-sides [area]
  (let [area-set (set area)
        make-transform (fn [f] (set (map f area-set)))
        transforms [(make-transform identity)
                    (make-transform (fn [[x y]] [(- x) y]))
                    (make-transform (fn [[x y]] [y x]))
                    (make-transform (fn [[x y]] [(- y) x]))]
        left-sides (fn [points]
                     (count
                      (filter (fn [[x y]]
                                (and (not (points [(dec x) y]))
                                     (or (not (points [x (dec y)]))
                                         (points [(dec x) (dec y)]))))
                              points)))]
    (->> transforms
         (map left-sides)
         (reduce +))))

;; ### solve
;;
;; Our general solver since both parts do the same thing.  Map out the
;; areas, apply our `f` from either part then add them up.
(defn solve [grid f]
  (->> (find-areas grid)
       (map #(* (count %) (f grid %)))
       (reduce +)))

;; ## Part 1
;;
;; Part 1 wants perimeter
(defn part-1
  [input]
  (solve input perimeter))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 wants us to count the sides
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input #(->> %2 count-sides)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
