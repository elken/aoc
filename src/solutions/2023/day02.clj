^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2023.day02
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "02" "2023"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;; Phew, for a weekend problem this one had me both over _and_ underthink this one...
;;
;; First I missed the key differentiating issue in that ; denotes a turn within
;; the game, I was trying to group everything together and see if the total was
;; less than the limit...
;;
;; After realizing, I swapped over to a regex based solution; and then I started
;; trying to make sure _each turn in sync passed_...
;;
;; But I digress; once I _**actually**_ understood the problem, this was cake.
;;
;; The crux of it is we're given a series of games of cubes, in which we have to
;; see if each turn was possible given the total amount of cubes in the bag.
;; Part 2 wants us to instead compute a value based on the minimum amount of
;; cubes to satisfy each turn.
;;
;; First things first, we need a simple function to parse each line into a more
;; usable format. We do this by using regex to split up the steps into pairs and
;; then we partition them together.
{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-game [game]
  (->> game
       (re-seq #"\d+|blue|green|red")
       rest
       (partition 2)))
{:nextjournal.clerk/visibility {:result :show}}

;; Which looks like

(parse-game "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green")

;; Now let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2023/day02.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                (map parse-game)))                            ;; Parse each line
{:nextjournal.clerk/visibility {:result :hide}}

;; ## limit
;; Thanks to Clojure allowing maps to be called like functions (seriously, thank
;; you Rich) we can simply define our limits like this and easily check them
;; later.
(def limit
  {"red" 12
   "green" 13
   "blue" 14})

;; ## game-possible?
;; Given a game state, ascertain if it meets the constraints of the cubes above
;; by filtering out all the turns that exceed the limits and assuring that the
;; counts match up still.
(defn game-possible? [game]
  (= (count game)
     (count
      (filter
       (fn [[count color]]
          (<= (parse-long count) (limit color)))
       game))))

;; ## Part 1
;; That's all we need utility function wise, if you've been following these
;; solutions for a while you'll know I like to favour finding a general solution
;; for both parts. I'm sure there is a clear one here, but I'm happy with what I
;; have.
;;
;; Part 1 is simply a case of mapping all the valid game states and summing the
;; indexes of valid games. Thanks to reduce-kv we don't have to keep track of
;; the IDs.
(defn part-1
  [input]
  (reduce-kv
   (fn [acc id possible?]
     (if possible?
       (+ (inc id) acc)
       acc))
   0
   (mapv game-possible? input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 asks us to instead find the minimum value of cubes to satisfy a valid
;; game, multiply all of the resuls in a game together, and then sum all of
;; those.
;;
;; Unfortunately there's not much scope for a clean transducer here, so I have
;; to settle for nested reductions
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (reduce
   (fn [sum game]
     (+ sum
        (->> game
             (reduce (fn [acc [count color]]
                       (assoc acc color (max (parse-long count) (acc color))))
                     (zipmap (keys limit) (repeat (count limit) 0)))
             vals
             (apply *))))
          0
          input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
