;; # YEAR - Day DAY
;;
;; ## Problem
;; ### Part 1
;; ### Part 2
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.YEAR.dayDAY
    (:require [clojure.java.io :as io]
              [clojure.test :as t :refer [deftest]]
              [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/YEAR/dayDAY.txt")) ;; Load the resource
                (str/split-lines)))                            ;; Split into lines

{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (println "Part 1"))

;; And running it gives
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (println "Part 2"))

;; Running part 2 gives
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:result :hide}}
(deftest test-answers
  (t/is (= 4 (part-1 input)))
  (t/is (= 4 (part-2 input))))
