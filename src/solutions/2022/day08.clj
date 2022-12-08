^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day08
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "08" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Ho boy, this was a _pain_. I couldn't find a decent library to handle
;; matrices/co-ordinates so writing most of this took **_paper_**.
;;
;; It's a standard "maximum neighbour" type problem, but this stuff always goes
;; over my head. To simplify, we have to count the number of trees visible in at
;; least one direction (visibility here being whether or not a tree has the
;; largest height to a given edge).
;;
;; First things first, let's load our input and parse it
;;
;; We have to use a function to create the map format we need to solve this
;; problem, which lets us treat the matrix as a simple map without worrying
;; about columns or rows.
{:nextjournal.clerk/visibility {:result :hide}}
(defn range->coords
  [matrix]
  (into {}
        (for [x (range (count matrix))
              y (range (count (first matrix)))]
          [[x y] (parse-long (str (get (get matrix x) y)))])))

{:nextjournal.clerk/visibility {:code :hide :result :show}}
;; Where a call like
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(range->coords ["123" "456"])
;; Looks like
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/table (vec (range->coords ["123" "456"])))

;; Ok, now we can load our input
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(def input (->> (slurp (io/resource "inputs/2022/day08.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse to a map of '(1 2 3) => {[0 0] 1 [0 1] 2 [0 2] 3}

;; Now let's solve it! First we create a convenience vector of all the
;; "neighbour" vectors.
;;
;; We made a guess that part 2 wouldn't involve diagonals, and we called it!
(def directions [[-1 0] [1 0] [0 -1] [0 1]])

;; Then we compute all the trees from a given tree to the edge in a given direction.
;;
;; Given the coordinate list, a tree to compute from and a direction vector;
;; iterate until we hit an edge.
(defn trees-in [coords tree direction]
  (let [[_ & trees] (map coords (iterate (partial mapv + direction) tree))]
    (take-while some? trees)))

;; Compute in all directions if a tree can see an edge in a given direction.
(defn visible?
  [coords tree]
  (some #(every? (partial > (coords tree)) (trees-in coords tree %)) directions))

;; Compute the viewing distance; that is how many trees the current tree can see.
(defn viewing-distance
  [coords tree]
  (transduce
   (map (fn [direction]
          (reduce (fn [distance cand] (if (>= cand (coords tree)) (reduced (inc distance)) (inc distance)))
                  0
                  (trees-in coords tree direction))))
   *
   directions))

;; That's everything! Now we can run through part 1. Here, all we care about is
;; how many trees can see an edge. A simple count of all the trees that have a
;; visible edge.
;;
;; There's almost certainly a cleaner transducer way to do this, maybe I'll
;; revisit after the fact.
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (count (filter (partial visible? input) (keys input))))

;; Running this part gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 is a bit more involved, now we have to find the most scenic tree by
;; multiplying together all the viewing distances and finding the biggest
;; number.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (map (partial viewing-distance input)) max 0 (keys input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
