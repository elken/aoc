^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day09
  (:require
   [clojure.java.io :as io]
   [clojure.math :as math]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "09" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; From spotting trees to rope physics! Can't see these problems are dull at
;; least...
;;
;; This one stumped me for a while too, doesn't bode well for the rest
;; of the week but c'est la vie. Onwards and upwards.
;;
;; Today we're tasked with tracing "rope physics", given the head and the tail
;; of a rope and certain rules; map out all the spots the rope has been. Those
;; rules are defined as:
;;
;; - The head and tail of the rope must always be touching (adjacent and overlapping count)
;; - If the head is two steps away cardinally, the tail must move one step in that direction
;; - If they're not touching or in the same row/colum, the tail moves diagonally once to keep up
;;
;; That's explained more with examples above, so let's dive right into solving it.
;;
;; First things first, let's load our input and parse it
;;
;; Given the list of movement commands `("R 4" "L 2")`, this is exactly the same
;; as `("R" "R" "R" "R" "L" "L")`; so let's transform the input to look like that
;; to make things easier and let us easily handle the tail movement rules.
;;
(def input (->> (slurp (io/resource "inputs/2022/day09.txt"))            ;; Load the resource
                str/split-lines                                          ;; Split into lines
                (mapcat #(let [[dir c] (str/split % #" ")]
                           (repeat (parse-long (str c)) (str dir))))))   ;; Spread commands out individually

;; Smashing. Now we have a flat input, it's just a case of reducing over it to
;; advance the state.

;; Next we define a function to move the head of the rope. Since we have
;; single-step movements now, we can just use `inc` and `dec`.
{:nextjournal.clerk/visibility {:result :hide}}
(defn move-head [[x y] direction]
  (condp = direction
    "R" [x (inc y)]
    "U" [(dec x) y]
    "L" [x (dec y)]
    "D" [(inc x) y]))

;; Then we have a function to mvoe the tail of the rope.
;;
;; This handles all our rules, ensuring that the tail is always at least next to
;; the head by updating the co-ordinate using `clojure.math/signum` to return
;; the sign of the change (new in 1.11).
{:nextjournal.clerk/visibility {:result :hide}}
(defn move-tail [[head-x head-y] [tail-x tail-y]]
  (let [dx (- head-x tail-x)
        dy (- head-y tail-y)]
    (if (and (< (abs dx) 2)
             (< (abs dy) 2))
      [tail-x tail-y]
      [(+ tail-x (math/signum dx)) (+ tail-y (math/signum dy))])))

;; Next we have a function to perform a movement command given a rope vector
;; (more on that in the next function) and a single-step movement command.
;;
;; We loop over the rope vector and update the vector based on how far through
;; it we are. The first element is the head, and once we reach the end we can
;; return the new vector.
{:nextjournal.clerk/visibility {:result :hide}}
(defn move [rope-vec direction]
  (loop [ropes rope-vec
         index 0]
    (let [next-pos (inc index)]
      (cond
        (= index (count rope-vec)) ropes
        (= index 0) (recur (assoc ropes index (move-head (ropes index) direction)) next-pos)
        :else (recur (assoc ropes index (move-tail (ropes (dec index)) (ropes index))) next-pos)))))

;; Lastly we have a general solution for both parts, since they share a common
;; _thread_ (I'm here all week) in that part 1 has 2 knots and part 2 has 10
;; knots, so we can abstract the implementation to use a vector instead and act
;; accordingly.
;;
;; So we take the length of the vector and the movement commands, and iterate
;; through the commands until none are left. We use a set for `past-tails` so we
;; don't have to worry about handling duplicates later.
{:nextjournal.clerk/visibility {:result :hide}}
(defn move-length [length input]
  (loop [rope-vec (vec (repeat length [0 0]))
         past-tails #{}
         cmds input]
    (let [tails (conj past-tails (last rope-vec))
          [cmd & rst] cmds]
      (if (empty? cmds)
        (count tails)
        (recur (move rope-vec cmd) tails rst)))))

;; As above, part 1 is just 2 knots
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (move-length 2 input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; And part 2 is _10_ knots
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (move-length 10 input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
