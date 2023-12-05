^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day05
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "05" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;; Things are starting to get challenging now! This one took me a bit longer,
;; mostly because of silly parsing bugs.
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

;; ## Parsing bins
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

;; ## Moving creates
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

;; ## Processing commands
;; Last step now is creating a generalised wrapper function, as both parts are
;; solved in exactly the same way just differing slightly.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn process-commands [input xf]
  (apply str (map last (reduce (partial move-crates xf)
                               (parse-bins input)
                               (last input)))))

;; ## Part 1
;; Now we can run part 1, passing `reverse` to reverse the crate order
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (process-commands input reverse))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; And part 2, passing `identity` to just return the crates as-is
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (process-commands input identity))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)

;; # Custom Viewer
;;
;; I assume you came from [the blog](https://www.juxt.pro/blog/advanced-clerk-usage) about this, but here's the live version!
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(def steps
  (reductions (partial move-crates identity)
              (parse-bins input)
              (last input)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(def crates-viewer
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [steps]
      (defn transpose-matrix [m]
        (let [max-len (apply max (mapv count m))
              padded-matrix (mapv #(concat % (repeat (- max-len (count %)) nil)) m)]
          (apply map (comp (partial remove nil?) vector) padded-matrix)))

      (reagent.core/with-let
        [reverse? (reagent.core/atom true)
         step (reagent.core/atom (first steps))
         ref (clojure.core/atom nil)]
        [:div.flex.justify-between.flex-col.overflow-none
         [:h1 (str "Step " (inc (.indexOf steps @step)) "/" (count steps))]
         [:div.flex.space-x-2.items-center
          [:label.font-bold {:for "is-reverse"} "Reverse?"]
          [:input {:type :checkbox
                   :id "is-reverse"
                   :checked @reverse?
                   :on-change #(reset! reverse? (.. % -target -checked))}]]
         [:p "Move the slider to adjust the step. To make it easier, the 'top' crate is highlighted in yellow (since it'll be different when reversed!)"]
         [:input {:type :range
                  :value (.indexOf steps @step)
                  :max (dec (count steps))
                  :on-input #(do
                               (when @ref
                                 (set! (.-scrollTop @ref) (.-scrollHeight @ref)))
                               (reset! step (nth steps (.. % -target -valueAsNumber))))}]
         [:div.bg-white.overflow-y-auto.mt-4
          {:class "max-h-[20rem]"
           :ref (partial reset! ref)}
          (into
           [:div.grid.bg-white.overflow-y
            {:style
             {:grid-template-columns (str "repeat(" (count @step) ", minmax(0, 1fr))")}}]
           (for [row (map (if @reverse? reverse identity) @step)]
             (into
              [:div.flex.flex-col.justify-end]
              (for [crate row]
                [:div.border-2.p-2.text-black.text-center.shadow-lg.bg-gray-200.border-gray-400
                 {:class (if @reverse? "first:border-yellow-400" "last:border-yellow-400")}
                 crate]))))]]))})

;; Which looks like

{:nextjournal.clerk/visibility {:code :hide :result :show}}
^{::clerk/sync true ::clerk/no-cache true}
(clerk/with-viewer crates-viewer steps)
