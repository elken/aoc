^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day10
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "10" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Back to simplicity! This one was cake, I think I spent longer doing the fancy
;; viewer for part 2 than I did actually solving the damn thing...
;;
;; Today's problem wants us to, given a simple assembly-lite language, keep
;; track of the state of a register.
;;
;; This register (called `X`) starts at 1 and is incremented by a single
;; instruction `addx`, which takes 2 cycles to complete. The only other
;; instruction in the instruction set is a `noop`, so we can remove all this
;; parsing and simplify everything.
;;
;; First things first, let's load our input and parse it
;; 
;; "It takes 2 cycles to complete" is the same as saying "do a `noop` before
;; every `addx`"; which is _also_ the same as saying "add nothing here". So
;; let's replace all the instructions with just the value to add, and before
;; every `addx` let's add a 0 (our new `noop`)
;;
;; We can just check here if we have a number in the instruction, in which case
;; it's an `addx` and we can append a `noop` before it.
(def input (->> (slurp (io/resource "inputs/2022/day10.txt"))                               ;; Load the resource
                str/split-lines                                                             ;; Split into lines
                (mapcat #(if-let [step (re-find #"-*\d+" %)] [0 (parse-long step)] [0]))))  ;; Parse commands to numbers

;; The only "helper" method here, since both parts revolve around handling this
;; slightly differently, but in reality all we're doing here is updating the
;; value of `X` based on the value from the input tape
(defn compute-pixels [input]
  (reduce #(conj %1 (+ (last %1) %2)) [1] input))

;; Part 1 is just getting the state of `X` at specific points, multiplying by
;; that step count and summing all those values.
;;
;; We can just map those indexes over the `X` values and reduce the values
{:nextjournal.clerk/visibility {:result :show}}
(defn part-1
  [input]
  (let [pixels (compute-pixels input)]
    (reduce + 0 (map #(* (nth pixels (dec %)) %) [20 60 100 140 180 220]))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 is the infamous "drawing" answer, which has been spiced up here via a nifty clerk viewer.
;;
;; This part took me _many_ read attempts, but we're basically checking for if
;; the current value of `X` overlaps with the index of the current line, and if
;; it does draw a `#` otherwise draw a `.`
;;
;; Using modulus, we can work out if we need to print a new line or not
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> (compute-pixels input)
       (map-indexed (fn [output x] (if (<= -1 (- (mod output 40) x) 1) "#" ".")))
       (partition 40)
       (map (partial apply str))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
^::clerk/no-cache
(clerk/with-viewer
  '(fn [result]
     [:div.grid {:style {:grid-template-columns "repeat(40, 1fr)" :grid-template-rows "repeat(6, 1fr)"}}
      (map-indexed (fn [idx val]
                     (when-not (str/blank? val)
                       [:div.inline-block {:id idx
                                           :style {:width 16 :height 16}
                                           :class (if (= "#" val) "bg-black dark:bg-white" "bg-white dark:bg-black")}]))
                   (str/join "\n" (map :nextjournal/value result)))])
  (part-2 input))

;; Note: view the [raw code](https://github.com/elken/aoc/blob/master/src/solutions/2022/day10.clj) to see the code for the viewer
