^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day05
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.math :as math]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "05" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; "Oh this will be easy, I did part 1 so fast..."
;;
;; You get a sense for some of these problems. When you solve part 1
;; really fast, you can tell that part 2 will probably have some
;; horrible trick that makes your entire solution wrong. And today is
;; no exception.
;;
;; Today has us taking 2 kinds of input, one half being a number of
;; rules on how to sort numbers; and a bunch of lists with those
;; numbers. Some are sorted, some aren't; and depending on the which
;; part we're on you have to consider sorted lists for part 1 and
;; unsorted for part 2, but for either case you have to find the
;; middle number of the lists and sum them.
;;
;; First things first, let's load our input and parse it
;;
;; Since both parts follow the same sort of structure, we can parse them in the same way to give us a list of the rules and a list of the update lists.
{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-numbers [s split]
  (->> s
       str/split-lines
       (mapv #(mapv parse-long (str/split % split)))))

(defn parse-input [[rules updates]]
  [(parse-numbers rules #"\|")
   (parse-numbers updates #",")])

{:nextjournal.clerk/visibility {:result :show}}
(def input (-> (slurp (io/resource "inputs/2024/day05.txt")) ;; Load the resource
               (str/split #"\n\n")                           ;; Split in half
               parse-input))                                 ;; Parse into lines


;; ## Functions
;; ### ordered-update?
;;
;; We use this in both parts to determine if our lists are sorted or
;; not. Part 1 uses this to handle the lists we want, part 2 uses it
;; to remove the sorted lists.
;;
;; In either case, we simply partition the list and check that every
;; partition equals a rule in the list of rules.
(defn ordered-update? [rules update]
  (every? (fn [pair] (some #(= pair %) rules)) (partition 2 1 update)))

;; ### middle-number
;;
;; Given a list of all the numbers, get the number that's in the
;; middle.
(defn middle-number [ns]
  (nth ns (math/floor (/ (count ns) 2))))

;; ### less-than?
;;
;; Use the rules to determine if we have a rule to say if a is "less
;; than" b.
(defn less-than? [rules a b]
  (contains? (into #{} rules) [a b]))

;; ### quick-select
;;
;; This function will easily get more explanation than most of the rest, so buckle in...
;;
;; An implementation of a quick select algorithm that takes the list
;; of rules, a position `k` that determines the position we're looking
;; for (in this case it's the midpoint of the list) and an update
;; list (this problem struggles with naming...).
;;
;; We start with a pivot (which can be any element, but we use the
;; first since we can destructure) and we get the elements that have
;; lower rank than the pivot (specifically, how many rules have the
;; pivot element on the right) and the elements that have the same
;; rank as the pivot (anything remaining will be greater than, which
;; we don't compute we just filter later).
;;
;; Then we have a cond that does the following rules. Thanks to the
;; conditions of this puzzle, we can guarantee it will finish so we
;; don't have to handle an exit condition for the recursion.
;;
;; #### The position `k` has a lower count than `less` (the amount of rules with lower rank than the pivot)
;;
;; In this case, our target element is in this group so this becomes
;; the new group to search in. We pass the same `k` (since `k` is
;; smaller than the count of lesser elements).
;;
;; #### The position `k` has a lower count than the sum of `less` and `equal`
;;
;; This means we have our element, since it has to be at the midpoint
;; of the list. We can just return the pivot
;;
;; #### The position `k` is anything else
;;
;; That means it's in what would otherwise be the "greater" list which
;; we don't compute (since we can't guarantee we'll always need to
;; check it) until we need it. We compute it by just filtering out
;; anything not in `less` or `equal`. We also adjust `k` to reflect
;; the new position, since we're not checking `less` or `equal`
;; anymore.
(defn quick-select [rules k [pivot :as update]]
  (let [less (filterv #(less-than? rules % pivot) update)
        equal (count (filter #(= % pivot) update))]
    (cond
      (< k (count less)) (quick-select rules k less)
      (< k (+ (count less) equal)) pivot 
      :else (quick-select rules 
                          (- k (count less) equal)
                          (filterv #(and (not (less-than? rules % pivot)) 
                                         (not= % pivot)) update)))))

;; ## Part 1
;;
;; Part 1 is a simple case of getting the valid lists, getting the
;; midpoint elements (we could technically still use the quick-select
;; but there's no need) and add them together.
(defn part-1
  [[rules updates]]
  (->> updates
       (filter (partial ordered-update? rules))
       (map middle-number)
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;;
;; Part 2 uses our complicated sounding quick-select algorithm to find
;; the midpoints of the lists without sorting or any graph
;; shenanigans.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [[rules updates]]
  (->> updates
       (remove (partial ordered-update? rules))
       (map #(quick-select rules (quot (count %) 2) %))
       (apply +)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
