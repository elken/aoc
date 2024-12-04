^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day11
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "11" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; I hope no actual security firms base their policies on this...
;;
;; Today's problem sees us trying to generate a new password based on
;; a number of rules, and "incrementing" the string each time.
;;
;; The rules are summarised as:
;; - Must contain 3 straight (as in poker terms) characters like "abc"
;; - Must NOT contain `i`, `o` or `l`
;; - Must contain at least 2 different, non-overlapping pairs of letter like "aa"
;;
;; Part 1 has us work this out from a given input, and almost unheard
;; of; part 2 directly uses part 1's result as input to find the NEXT
;; next password.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day11.txt")) ;; Load the resource
                str/trim))                                    ;; Remove the newline
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### increment-string
;;
;; Given a string, correctly increment it.
;;
;; First we reverse the string and count all the z's as `pos`.  We
;; then use this to construct the rest of the string, and increment
;; the appropriate characters.
(defn increment-string [s]
  (let [pos (->> s reverse (take-while #(= % \z)) count)
        base (drop-last pos s)]
    (str (if (empty? base)
           "a"
           (str (apply str (butlast base))
                (char (inc (int (last base))))))
         (apply str (repeat pos \a)))))

;; ### Rules
;; #### Three "straight" characters
;;
;; For those not familiar with straight in the context of poker, it
;; simply means `n` many in a row of increasing rank. So in this case,
;; we want to match "abc" but not "abd".
;;
;; We solve this by reducing over the list (using some destructuring
;; shorthand to skip a pointless iteration, since the first character
;; would just be added anyway) and checking the following:
;;
;; - Is the count of the accumulator 3? If so, we short-circuit
;; - Is the accumulator empty? Add the current character to it
;; - Is the current character equal to the last element + 1? Add the current character to the end of the acc
;; - Otherwise return an empty array, since we didn't find a match.
(defn three-straight-chars? [[head & tail]]
  (= 3
     (count 
      (reduce
       (fn [acc c]
         (cond
           (= (count acc) 3) (reduced acc)
           (empty? acc) [c]
           (= (int c) (inc (int (last acc)))) (conj acc c)
           :else []))
       [head]
       tail))))

;; #### Only accept "valid" letters
;; This one's pretty simple, just ignore any strings with `i`, `o` or `l` in.
(defn only-valid-letters? [s]
  (not (re-find #"[iol]+" s)))

;; #### Two or more repeating pairs?
;;
;; Another simple one conceptually, but we're trying to match a
;; character that is proceeded by itself, then the same again to get 2 pairs.
;;
;; This fails in a lot of ways, but thankfully the problem inputs
;; weren't that complex back then.
(defn repeats? [s]
  (some?
   (re-find #".*(.)\1.*(.)\2.*" s)))

;; ### valid-password?
;; A simple composition of our rules to produce a valid password
(defn valid-password? [s]
  (every? #(% s) [three-straight-chars? only-valid-letters? repeats?]))

;; ## Part 1
;;
;; Both parts are solved in exactly the same way, the only difference
;; being part 2 wants the password after part 1's
;;
;; We can compute our password thus by creating an infinite sequence
;; of incremented strings and getting the first valid one.
(defn part-1
  [input]
  (some #(when (valid-password? %) %)
        (iterate increment-string input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; The code is identical here, so much so that we actually show the code that generates the answers (I normally hide it because it looks cleaner) to show that it does something different
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (some #(when (valid-password? %) %)
      (iterate increment-string input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :show :result :show}}
(part-2 (increment-string (part-1 input)))
