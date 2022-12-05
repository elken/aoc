^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day05
    (:require
     [clojure.java.io :as io]
     [clojure.string :as str]
     [clojure.test :as t :refer [deftest]]
     [nextjournal.clerk :as clerk]
     [util :as u]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
[:style "em{color: #fff;font-style: normal;text-shadow: 0 0 5px #fff;}.viewer-result:first-child{display: none;}"]

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "05" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Things are starting to get challenging now! This one took me a bit longer, mostly because of silly parsing bugs.
;;
;; This time around we're given a 2-part input; an ascii diagram of the initial
;; state and a set of commands to apply to advance the state. Both parts are
;; _very_ similar, the only difference is the order in which the crates are
;; moved; part 1 takes the crates FILO (first-in-last-out) and part 2 is FIFO
;; (first-in-first-out).
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2022/day05.txt")) ;; Load the resource
                (str/split-lines)                             ;; Split into lines
                (partition-by #(str/starts-with? % "move")))) ;; Split into commands and bins

;; This gives us a 2-tuple of the unparsed initial board state and all the
;; movement commands we'll need.

;; Next, parsing the ascii diagram into a useful data structure. Making them
;; into a nice matrix would almost trivialise the movement commands later. What
;; can we do there...
;;
;; So, each "cell" is 4 characters wide; 3 for either a crate or blank spaces
;; and another space to separate each cell. From this, we can then simply
;; partition each line by 4 and that gives us an indexed list of all the crates.
(partition 3 4 (str/split (nth (first input) 2) #""))

;; Then it's just a case of removing the rubbish (blank spaces and []), leaving
;; us with a list of all the crates!
(map
 #(keep (partial re-find #"\w") %)
 (partition 3 4 (str/split (nth (first input) 2) #"")))

;; ... along the wrong axis. Working with the data in this format is going to be
;; a big headache, so let's [rotate the board](https://youtu.be/ZH-cXBhkl-E?t=66).
{:nextjournal.clerk/visibility {:result :hide}}
(defn parse-bins [input]
  (vec
   (apply map (comp vec reverse #(remove nil? %) list)
         (let [bins (first input)]
           (for [line bins
                 :when (str/includes? line "[")]
             (let [tokens (partition 3 4 (str/split line #""))
                   chars (map #(keep (partial re-find #"\w") %) tokens)]
               (map first chars)))))))

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(parse-bins input)

;; Much better!

;; Now we have to parse a movement command, e.g. "move 4 from 9 to 6".
;;
;; This can be parsed simply by getting all the numbers and using those
;; positionally to get the number of crates being moved, the tower to move from
;; and the tower to move to.
;;
;; We also have to (annoyingly) pass a transformer function `xf` to reverse the
;; crates for part 1 and no-op for part 2.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn move-crates [xf bins command]
  (let [[crates from to] (map parse-long (re-seq #"\d+" command))
        to-idx (- to 1)
        from-idx (- from 1)
        bin-to (nth bins to-idx)
        bin-from (nth bins from-idx)]
    (-> (assoc bins to-idx (apply conj bin-to (xf (take-last crates bin-from))))
        (assoc from-idx (vec (take (- (count bin-from) crates) bin-from))))))

;; Which looks like
{:nextjournal.clerk/visibility {:code :show :result :show}}
(move-crates reverse (parse-bins input) (first (last input)))

;; See how the crates moved?

;; Last step now is creating a generalised wrapper function, as both parts are
;; solved in exactly the same way just differing slightly.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn process-commands [input xf]
  (apply str (map last (reduce (partial move-crates xf)
                               (parse-bins input)
                               (last input)))))

;; Now we can run part 1, passing `reverse` to reverse the crate order
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (process-commands input reverse))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; And part 2, passing `identity` to just return the crates as-is
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (process-commands input identity))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

{:nextjournal.clerk/visibility {:result :hide}}
(deftest test-answers
  (t/is (= "LBLVVTVLP" (part-1 input)))
  (t/is (= "TPFFBDRJD" (part-2 input))))
