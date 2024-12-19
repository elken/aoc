^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day19
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.set :as set]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "19" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; It's non-grid day!
;;
;; Today is a really good one, we're tasked with finding all the valid
;; arrangements of one list that come from another list. We have a
;; list of valid arrangements and we have to check if we can construct
;; the items from the second list.
;;
;; Part 1 has us solving like that, and part 2 wants us to sum up all
;; the valid combinations we can do. Another day where I accidentally
;; solved part 2 before part 1...
;;
;; First things first, let's load our input and parse it
{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-input [input]
  (let [[available designs] (str/split input #"\n\n")]
    [(set (re-seq #"\w+" available)) (str/split-lines designs)]))
{:nextjournal.clerk/visibility {:result :show}}

;; And apply it to our input
(def input (->> (slurp (io/resource "inputs/2024/day19.txt")) ;; Load the resource
                parse-input))                                 ;; Parse the input
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### valid-substring?
;;
;; A helper function we use in both parts, given a substring, a
;; position to apply the substring at and a string to apply to; check
;; if we can "fit" the substring in by ensuring it fits within the
;; bounds and matches the substring at `pos`.
(defn valid-substring? [sub pos s]
  (let [end (+ pos (count sub))]
    (and (<= end (count s))
         (= sub (subs s pos end)))))

;; ### valid-design?
;;
;; We start by creating a list of false the length of the design,
;; except at position 0 because the empty string is always blank. This
;; list represents all the valid starting positions from which we can
;; reach a valid end.
;;
;; So for each of these positions, we check if the current position is
;; `true`. Since 0 starts off as true, for the first iteration we run
;; the `reduce`. For each available configuration, work out where it
;; would end and check if the configuration fits within the bounds of
;; the string and that it matches the substring. If it matches, we
;; mark the last position of where the configuration would go as
;; `true`, otherwise just carry on.
;;
;; This gives us a list of all the valid positions that can make a
;; valid design, so we check the last position to ensure that we can
;; reach the end.
(defn valid-design? [available design]
  (let [length (count design)]
    (-> (reduce
         (fn [reachable pos]
           (if (reachable pos)
             (reduce
              (fn [reachable word]
                (if (valid-substring? word pos design)
                  (assoc reachable (+ pos (count word)) true)
                  reachable))
              reachable
              available)
             reachable))
         (assoc (vec (repeat (inc length) false)) 0 true)
         (range length))
        (last))))

;; ### count-combinations
;;
;; Using a similar approach to part 1, we work backwards from the end
;; of the string to find how many valid ways we can split each suffix
;; into available configurations.
;;
;; The first position is 0, which only has 1 way to make it ("") and
;; we create a range from the end of the string to the start.
;;
;; For each `pos`, we check each word fits within the bounds of the
;; string and matches the substring. If it does, we get all the valid
;; ways after the substring and sum them up ot get the total
;; combinations from the current position.
;;
;; Since we work backwards, we have to get the first value (which
;; would be all the valid combinations from that position).
(defn count-combinations
  [available design]
  (let [len (count design)]
    (->
     (reduce
      (fn [counts pos]
        (let [sum (->> available
                       (keep #(when (valid-substring? % pos design)
                                (get counts (+ pos (count %)))))
                       (reduce + 0))]
          (assoc counts pos sum)))
      {len 1}
      (range (dec len) -1 -1))
     vals first)))

;; ## Part 1
;;
;; Part 1 just parallel maps over all the valid designs and counts all
;; the `true`s.
(defn part-1
  [[available designs]]
  (->> designs
       (pmap #(valid-design? available %))
       (filter true?)
       count))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 wants us to instead count all the combinations we can make
;; words from and sum them up
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [[available designs]]
  (->> designs
       (pmap #(count-combinations available %))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
