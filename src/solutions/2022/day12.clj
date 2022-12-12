^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day12
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "12" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; I was dreading this day, not a fan of these. But there's always a Dijsktra day...
;;
;; Today's problem is a case of finding the shortest path from one or many
;; node(s) to another given node. Both parts differ slightly and I was able to
;; luck out and guess part 2, meaning no refactor!
;;
;; Not much more to say about it, we just compute the shortest path from start
;; to end by adding up all the valid neighbours and finding the shortest route.
;;
;; Ended up borrowing a lot of code from [Day 8](https://elken.github.io/aoc/src/solutions/2022/day08.html) which helped.

;; First we need a function to convert a character to a "height" value, which
;; mostly boils down to the char code; with the `S`tart and `E`nd values being
;; `0` and `25` respectively.
{:nextjournal.clerk/visibility {:result :hide}}
(defn elevation->height [elevation]
  (condp = elevation
    \S 0
    \E 25
    (- (int elevation) (int \a))))

;; Similar to [Day 8](https://elken.github.io/aoc/src/solutions/2022/day08.html), we parse the
;; list of values into a map that looks like
{:nextjournal.clerk/visibility {:code :hide :result :show}}
{[0 0] {:elevation 0
        :char \S}}
{:nextjournal.clerk/visibility {:code :show :result :hide}}

(defn range->coords
  [matrix]
  (into {}
        (for [x (range (count matrix))
              y (range (count (first matrix)))
              :let [c (get (get matrix x) y)
                    e (elevation->height c)]]
          [[x y] {:elevation e
                  :char c}])))

;; Now we can parse out input
(def input (->> (slurp (io/resource "inputs/2022/day12.txt")) ;; Load the resource
                str/split-lines                               ;; Split into lines
                range->coords))                               ;; Parse to matrix

;; The main "computation" function that uses a [PersistentQueue](https://github.com/danielmiladinov/joy-of-clojure/blob/master/src/joy-of-clojure/chapter5/how_to_use_persistent_queues.clj)
;; that I was lucky enough to have a physical copy of the associated book in front of me.
;;
;; The basic iteration for each loop looks like:
;; - Get the first node in the list of nodes to check
;; - Return the `seen` value if it's the last node
;; - Otherwise get all the neighbours by checking the cardinal directions for
;;  unseen, unvisited nodes we can move to
;; - Recur with these valid neighbours applied and update the `seen` values
(defn find-shortest-path [input from to]
  (loop [unvisited (reduce conj clojure.lang.PersistentQueue/EMPTY from)
         seen (zipmap from (repeat 0))]
    (let [node (peek unvisited)]
      (if (= to node)
        (seen node)
        (let [neighbours (->> neighbour
                             (for [direction [[-1 0] [1 0] [0 -1] [0 1]]
                                   :let [neighbour (mapv + node direction)]
                                   :when (and (input neighbour)
                                              (not (seen neighbour))
                                              (not (.contains neighbour unvisited))
                                              (>=
                                               (:elevation (input node))
                                               (dec (:elevation (input neighbour)))))])
                             vec)]
          (recur (into (pop unvisited) neighbours)
                 (merge seen (zipmap neighbours (repeat (inc (seen node)))))))))))

;; Lastly we need a small helper method to give us the locations of all
;; instances of a given set of characters
(defn find-chars [input & chars]
  (keys
   (first ((juxt filter remove)
           (fn [[_ v]] (.contains chars (:char v))) input))))

;; Part 1 is just a simple case of finding the path from `S` to `E`
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (find-shortest-path
   input
   (find-chars input \S)
   (first (find-chars input \E))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 is a little bit more involved, now we have to also consider all `a`
;; paths as well as `S`, but as above we lucked out and got this part for free.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (find-shortest-path
   input
   (find-chars input \a \S)
   (first (find-chars input \E))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
