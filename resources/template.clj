^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.YEAR.dayDAY
  (:require [clojure.java.io :as io]
            [clojure.test :as t :refer [deftest]]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
[:style "em{color: #fff;font-style: normal;text-shadow: 0 0 5px #fff;}.viewer-result:first-child{display: none;}"]

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "DAY" "YEAR"))
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

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (println "Part 2"))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:result :hide}}
(deftest test-answers
  (t/is (= 4 (part-1 input)))
  (t/is (= 4 (part-2 input))))
