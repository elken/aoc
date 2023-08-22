^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day06
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.core.match :as match]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "06" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Things are slowing down a bit now! This one threw me for a loop for a while,
;; mostly because of silly issues (namely `range` being exclusive; not
;; inclusive).
;;
;; In short, we're giving a number of instructions coupled with co-ordinates; eg
;; `turn on 0,0 through 4,0` which means for all those points (`1, 0`, `2, 0`,
;; `3, 0` and `4, 0`) "turn them on" (the exact meaning of that differs between
;; parts).
;;
;; What we do here is use `core.match` to match on whether the action starts
;; with `turn` or `toggle` and use that to pull a function from a map of actions
;; we pass in; an example of which being:
;;
;; ```clojure
;;{"on" inc
;;  "off" dec
;;  "toggle" #(+ 2 %)}
;;```
;; Both parts follow the same structure, so a general solution here was easy to find.
;;
;; This function is then applied onto every point defined in the co-ordinate
;; range, which after one step looks something like:
;; ```clojure
;; {[0 0] 1
;;  [0 1] 0}
;; ```
;; Where this map's key is the co-ordinate and the value is what's referred in
;; the second part as "brightness" (though not relevant to part 1, as it
;; represents on or off)
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day06.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Expand an instruction into points
;;
;; Given the second half of an instruction (`0,0 through 4,0`), split out the
;; numbers into x's and y's, loop through a range of those numbers and return
;; the expanded co-ordinates.
(defn expand-line [instruction]
  (let [[x1 y1 x2 y2] (map parse-long (re-seq #"\d+" (str instruction)))]
    (for [x (range x1 (+ 1 x2))
          y (range y1 (+ 1 y2))]
      [x y])))

;; Which looks like

{:nextjournal.clerk/visibility {:result :show}}
(expand-line "0,0 through 4,0")
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Clamp a value between bounds
;;
;; Simple function to "clamp" a value between bounds, which we use to make sure
;; the `dec` function doesn't go into negatives.
(defn clamp
  ([value]
   (clamp value 0 Integer/MAX_VALUE))
  ([value min]
   (clamp value min Integer/MAX_VALUE))
  ([value min max]
   (cond
     (< value min) min
     (> value max) max
     :else value)))

;; ## Update current state
;;
;; Reduce over the new `points` and update the `state` with `action`. Clamp the
;; value if needed.
(defn update-state [state action points]
  (reduce
   (fn [state point]
     (->> 0
          (get state point)
          action
          clamp
          (assoc state point)))
   state
   points))

;; ## Perform a step of instruction
;;
;; Apply an instruction, parsing the action and returning the new state
(defn step [actions state instruction]
  (match/match [(str/split instruction #" ")]
    [["turn" direction & rst]]
    (update-state state (actions direction) (expand-line rst))
    [["toggle" & rst]]
    (update-state state (actions "toggle") (expand-line rst))))

;; ## Solver
;;
;; Generic solver, since both parts function the same here. Partially apply
;; `step` over every instruction, get the values and sum them
(defn solve [actions input]
  (->> input
       (reduce
        (partial step actions)
        {})
       (map second)
       (apply +)))

;; ## Part 1
;;
;; Phew! So for part 1, it's just a simple case of "on" and "off"; which looks like
{:nextjournal.clerk/visibility {:result :show :code :hide}}
(clerk/table (clerk/use-headers [["State" "Action"]
                                 ["turn on" "1"]
                                 ["turn off" "0"]
                                 ["toggle" "Set 1 to 0 and vice versa"]]))
{:nextjournal.clerk/visibility {:result :hide :code :show}}
;; Apply those actions to the input
(defn part-1
  [input]
  (solve
   {"on" (fn [_] 1)
    "off" (fn [_] 0)
    "toggle" (fn [n] (if (= n 1) 0 1))}
   input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Phew! So for part 1, it's just a simple case of "on" and "off"; which looks like
{:nextjournal.clerk/visibility {:result :show :code :hide}}
(clerk/table (clerk/use-headers [["State" "Action"]
                                 ["turn on" "Add 1"]
                                 ["turn off" "Minus 1, to a lower bound of 0"]
                                 ["toggle" "Add 2"]]))
{:nextjournal.clerk/visibility {:result :hide :code :show}}
;; Apply those actions to the input
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve
   {"on" inc
    "off" dec
    "toggle" #(+ 2 %)}
   input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
