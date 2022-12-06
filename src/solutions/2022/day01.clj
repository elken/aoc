^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day01
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "01" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;; Ok, so this looks quite simple; sum all the "groups" of elves.
;;
;; First things first, let's load our input and parse it

(def input (->> (slurp (io/resource "inputs/2022/day01.txt")) ;; Load the resource
                (str/split-lines)                             ;; Split into lines
                (partition-by str/blank?)                     ;; Partition by blank spaces (to give our groups)
                (remove #(str/blank? (first %)))              ;; Remove the blank spaces
                (map #(transduce (map parse-long) + 0 %))     ;; Map a transducer over the list of numbers (more info below)
                (sort >)))                                    ;; Sort by size

;; Great! We now have our list of numbers, and as both parts are the same it's
;; simply a case of treating that list slightly differently.
;;
;; For part 1, we just care about the biggest number

{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (first input))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; But what's that "transducer" thing?
;;
;; So, there's two kinds of sequences (for our argument's sake); eager and lazy.
;; Eager sequences evaluate all the elements and hold everything in memory,
;; whereas lazy sequences are evaluated and stored as they're needed.
;;
;; As you can imagine, in an ideal world we'd probably want to use lazy
;; sequences for big, slow data and eager sequences for small, fast data.
;;
;; Later in Clojure's life, Rich Hickey realised that implementing map et al for
;; collections, then for streams, then for observables, then for channels is
;; redundant, too specific and inefficient. So he set out to see if there was a
;; way to write these functions once and for all.
;;
;; Great! ... But how does that help us?
;;
;; Let's start with a simple example

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (transduce (take 3) + 0 input))

;; What's going on there? Why does `take` not have a collection argument?
;;
;; Well, the short version is a number of these collection functions return a
;; "transducer"; that is a function that takes some step function and returns
;; that function wrapped in some other logic.
;;
;; In this case, `(take 3)` returns a function that returns the 3 numbers and
;; `transduce` then operates the same as `reduce` except taking the transducer
;; returned. Due to the size of the data and the operation, it's pointless
;; except for demonstration purposes.
;;
;; That transducer could for example be a complex operation like "given a
;; sequence of the first 10 billion numbers, filter out the odd primes bigger
;; than 251" which would operate at `O(1)` as it goes through each element
;; in-turn lazily.
;;
;; For a slightly more in-depth video, I would recommend [Fred Overflow's](https://www.youtube.com/watch?v=TaazvSJvBaw).
;;

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
