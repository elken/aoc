^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2025.day07
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "07" "2025"))
{:nextjournal.clerk/visibility {:code :show :result :hide}}

;; # Solution
;;
;; Oh great, another grid problem and _another_ grid problem with a
;; lazy part 2 that just adds the word quantum in. I know Eric doesn't
;; read this, but if you ever do, I beg please stop these.
;;
;; On the plus side we have a "neat" general solution as both parts
;; are identical barring part 2 having the requirement to compute all
;; paths.
;;
;; First things first, let's load our input and parse it

(def input (->> (slurp (io/resource "inputs/2025/day07.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines

;; ## Functions
;;
;; ### solve
;;
;; Given our input and whether or not we're doing the quantum check,
;; compute line-by-line and map all the paths.
;;
;; Starting at `S`, go through all the rows one-by-one and check if we
;; have any `^` in active positions. If we find one, add a split
;; either side (`(dec j)` and `(inc j)`) and remove `j` from the
;; active positions.
;;
;; If `quantum?` is false, all we do is count the splits.
;;
;; If `quantum?` is true however, we count all the path counts.
;;
;; I want it on record I'm not proud of this, I think it's really
;; messy, but I loathe graph problems so much I don't care enough to
;; clean this up.
(defn solve [input quantum?]
  (let [start (.indexOf (first input) "S")]
    (loop [rows (drop 2 input)
           positions #{start}
           counts (when quantum? {start 1})
           splits 0]
      (if-let [row (first rows)]
        (let [hits (filter #(and (= (get row %) \^) (positions %))
                           (range (count row)))
              new-pos (reduce #(-> %1 (disj %2) (conj (dec %2) (inc %2)))
                              positions hits)
              new-counts (when quantum?
                           (reduce (fn [cnt j]
                                     (let [c (cnt j)]
                                       (-> cnt (dissoc j)
                                           (update (dec j) (fnil + 0) c)
                                           (update (inc j) (fnil + 0) c))))
                                   counts hits))]
          (recur (rest rows) new-pos new-counts (+ splits (count hits))))
        (if quantum?
          (reduce + (vals counts))
          splits)))))

{:nextjournal.clerk/visibility {:result :hide}}

;; ## Part 1
;;
;; Part 1 we solve without quantum on, just count the splits.
(defn part-1
  [input]
  (solve input false))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 we solve _with_ quantum on, so we count all the paths.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (solve input true))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
