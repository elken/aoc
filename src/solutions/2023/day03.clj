^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2023.day03
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "03" "2023"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; What the _hell_ was today??
;;
;; Considering the weekends are meant to be easier **and** it's day 3 I'm
;; shocked how rough today was. If this carries on, I might have to stop this
;; very early...
;;
;; In any case, we got there in the end after many massive refactors.
;;
;; Today's problem has us trying to find part numbers using everyone's favourite
;; nearest-neighbour shenanigans. A part number is defined as a number in which
;; one of the digits neighbours a non-period special symbol.
;;
;; Part 1 wants us to just find them all and sum them, part 2 has us trying to
;; find gears which are a `*` neighboured by two numbers; but we made a sneaky
;; guess that that'd be the only character to be doubled-up (and it paid off!)
;;
;; The basic logic here is finding all the locations of special characters,
;; using regex to find all the numbers in a line (thank you
;; java.util.regex/Matcher for also sending back the match location), then
;; finding all the valid bisections with symbols.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2023/day03.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## find-special-chars
;;
;; First we have to get all of the special characters, which we can use a simple
;; regex for to filter out anything not a dot or a number
(defn find-special-chars [board]
  (for [x (range (count board))
        y (range (count (first board)))
        :let [ch (str (get-in board [x y]))]
        :when (and ch (re-matches #"[^0-9\.]" ch))]
    [x y]))

;; which looks like

{:nextjournal.clerk/visibility {:result :show}}
(def specials (find-special-chars input))
{:nextjournal.clerk/visibility {:result :hide}}

;; ## locate-matches
;;
;; Basically just a wrapper around re-matcher and re-find to cleanly give us the
;; match locations which we'll use later to compute the ranges for the
;; neighbours
(defn locate-matches [pattern string]
  (let [matcher (re-matcher pattern string)]
    (loop [matches []]
      (if (.find matcher)
        (recur (conj matches
                     {:match (.group matcher)
                      :start (.start matcher)
                      :end (.end matcher)}))
        matches))))

;; which looks like
;;
{:nextjournal.clerk/visibility {:result :show}}
(def matches (locate-matches #"\d" (first input)))
{:nextjournal.clerk/visibility {:result :hide}}

;; ## valid-neighbours
;;
;; Given a list of special character co-ordinates and other indexes, compute a
;; valid list of neighbours; that is a list of neighbours that also intersect
;; with a special character.
(defn valid-neighbours [specials row start end]
  (for [s [-1 0 1]
        d [-1 0 1]
        c (range start end)
        :let [coord [(+ row s) (+ c d)]]
        :when (some #(= % coord) specials)]
    coord))

;; which looks like
;;
{:nextjournal.clerk/visibility {:result :show}}
(apply valid-neighbours specials 0 (vals (select-keys (first matches) [:start :end])))
{:nextjournal.clerk/visibility {:result :hide}}

;; ## find-part-numbers
;;
;; Our general solver function, given an input compute everything.
;;
;; - Get a list of all the co-ordinates with special chars
;; - For every line of input
;;     - Use reduce-kv so we can also have the row index
;;     - Get all of the numbers on the current line
;;     - For every match
;;         - Find all the valid neighbours for every co-ordinate along the match
;;         - Update the accumulator to have the key be the location of the
;;           special character and the value be a list of all the numbers that neighbour it
(defn find-part-numbers [input]
  (let [specials (find-special-chars input)]
    (reduce-kv (fn [acc row line]
                 (reduce
                  (fn [acc [neighbour match]]
                    (update acc neighbour #(conj (set (or % [])) match)))
                  acc
                  (mapcat (fn [{:keys [start end match]}]
                            (let [neighbours (valid-neighbours specials row start end)
                                  parsed-match (parse-long match)]
                              (map (fn [neighbour] [neighbour parsed-match]) neighbours)))
                          (locate-matches #"\d+" line))))
               {}
               input)))

;; ## solve
;;
;; Since we're able to get a single solving function, we can make a trivial
;; transducer wrapper here since the only difference is the fact Part 2 needs to
;; filter.
(defn solve [input xform]
  (transduce
   xform
   +
   0
   (vals (find-part-numbers input))))

;; ## Part 1
;;
;; After all that nonsense, all we have to do here is get the values and add
;; them together
(defn part-1
  [input]
  (solve
   input
   (comp (mapcat identity) (map #(+ %)))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Similarly here, all we have to do is filter out anything with 2
;; intersections, get the product of the 2 values and add the resulting numbers
;; together
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve
   input
   (comp
    (filter #(= 2 (count %)))
    (map #(apply * %)))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
