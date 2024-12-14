^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day14
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "14" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Looks like every other day is a grid now...
;;
;; Okay you know the drill by now. Grid problem, write-only, yadda
;; yadda.
;;
;; Today has us computing paths for a bunch of robots that can
;; warp (if they hit an edge, they move to the opposite edge) for 100
;; steps and counting them by quadrant. Part 2 wants us to,
;; quote, "find where they make a christmas tree". Yes, really.
;;
;; First things first, let's load our input and parse it

(def input (->> (slurp (io/resource "inputs/2024/day14.txt")) ;; Load the resource
                str/split-lines ;; Split into lines
                (map #(vec (map parse-long (re-seq #"-?\d+" %))))))  ;; Parse the input into a list of lists                          
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### move-robots
;;
;; The bulk of the logic in the problem. Compute the new vectors for
;; the robots by using modulus to clamp the values.
(defn move-robots [x y n robots]
  (map (fn [[px py vx vy]]
         [(mod (+ px (* vx n)) x)
          (mod (+ py (* vy n)) y)
          vx
          vy])
       robots))

;; ### by-quad
;;
;; Determines which quadrant a robot is in by comparing its position
;; to the midpoint of the grid. Any points ON a quanrant boundary are
;; considered ignored.
(defn by-quad
  [x y [rx ry]]
  (let [mid-x (quot x 2)
        mid-y (quot y 2)]
    (cond
      (and (< rx mid-x) (< ry mid-y)) 1
      (and (> rx mid-x) (< ry mid-y)) 2
      (and (< rx mid-x) (> ry mid-y)) 3
      (and (> rx mid-x) (> ry mid-y)) 4
      :else 0)))

;; ### count-by-quadrant
;;
;; Counts how many robots are in each quadrant and multiplies these
;; counts together.
(defn count-by-quadrant [x y robots]
  (->> robots
       (map (comp #(by-quad x y %) (partial take 2)))
       frequencies
       (filter #(pos? (first %)))
       vals
       (reduce *)))

;; ### detect-line
;;
;; Checks if there are 20 consecutive robots in a horizontal line at a
;; given y-coordinate. Cheat code for part 2 basically.
(defn detect-line [max-x y robots]
  (let [x-points (set (map first (filter #(= (second %) y) robots)))]
    (some #(>= (count (take-while x-points (range % max-x))) 20)
          (range max-x))))

;; ## Part 1
;;
;; Part 1 just wants us to move the robots and count them
(defn part-1
  [input]
  (->> input
       (move-robots 101 103 100)
       (count-by-quadrant 101 103)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 expects us to find where the endpoints of the robots make a
;; christmas tree, which we use a cheat code for by just checking for
;; a 20 robot line.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [x 101
        y 103]
    (loop [current-robots input
           seconds 0]
      (if (reduce (fn [acc [start-y]]
                    (or acc
                        (detect-line x start-y current-robots)))
                  nil
                  (partition-all 11 (range 0 y)))
        seconds
        (recur (move-robots x y 1 current-robots)
               (inc seconds))))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
