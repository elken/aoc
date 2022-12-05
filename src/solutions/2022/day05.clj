;; # 2022 - Day 05
;;
;; ## Problem
;; ### Part 1
;; The expedition can depart as soon as the final supplies have been unloaded
;; from the ships. Supplies are stored in stacks of marked **crates**, but because
;; the needed supplies are buried under many other crates, the crates need to be
;; rearranged.

;; The ship has a **giant cargo crane** capable of moving crates between stacks. To
;; ensure none of the crates get crushed or fall over, the crane operator will
;; rearrange them in a series of carefully-planned steps. After the crates are
;; rearranged, the desired crates will be at the top of each stack.

;; The Elves don't want to interrupt the crane operator during this delicate
;; procedure, but they forgot to ask her **which** crate will end up where, and they
;; want to be ready to unload them as soon as possible so they can embark.

;; They do, however, have a drawing of the starting stacks of crates **and** the
;; rearrangement procedure (your puzzle input). For example:

;; ```
;;     [D]
;; [N] [C]
;; [Z] [M] [P]
;;  1   2   3
;; ```

;; ```
;; move 1 from 2 to 1
;; move 3 from 1 to 3
;; move 2 from 2 to 1
;; move 1 from 1 to 2
;; ```

;; In this example, there are three stacks of crates. Stack 1 contains two
;; crates: crate `Z` is on the bottom, and crate `N` is on top. Stack 2 contains
;; three crates; from bottom to top, they are crates `M`, `C`, and `D`. Finally, stack
;; 3 contains a single crate, `P`.

;; Then, the rearrangement procedure is given. In each step of the procedure, a
;; quantity of crates is moved from one stack to a different stack. In the first
;; step of the above rearrangement procedure, one crate is moved from stack 2 to
;; stack 1, resulting in this configuration:

;; ```
;; [D]
;; [N] [C]
;; [Z] [M] [P]
;;  1   2   3
;; ```

;; In the second step, three crates are moved from stack 1 to stack 3. Crates
;; are moved **one at a time**, so the first crate to be moved (`D`) ends up below the
;; second and third crates:

;; ```
;;         [Z]
;;         [N]
;;     [C] [D]
;;     [M] [P]
;;  1   2   3
;; ```

;; Then, both crates are moved from stack 2 to stack 1. Again, because crates
;; are moved **one at a time**, crate `C` ends up below crate `M`:

;; ```
;;         [Z]
;;         [N]
;; [M]     [D]
;; [C]     [P]
;;  1   2   3
;; ```

;; Finally, one crate is moved from stack 1 to stack 2:

;; ```
;;         [Z]
;;         [N]
;;         [D]
;; [C] [M] [P]
;;  1   2   3
;; ```

;; The Elves just need to know **which crate will end up on top of each stack**; in
;; this example, the top crates are `C` in stack 1, `M` in stack 2, and `Z` in stack
;; 3, so you should combine these together and give the Elves the message `CMZ`.

;; **After the rearrangement procedure completes, what crate ends up on top of
;; each stack?**

;; ### Part 2
;; As you watch the crane operator expertly rearrange the crates, you notice the
;; process isn't following your prediction.

;; Some mud was covering the writing on the side of the crane, and you quickly
;; wipe it away. The crane isn't a CrateMover 9000 - it's a **CrateMover 9001**.

;; The CrateMover 9001 is notable for many new and exciting features: air
;; conditioning, leather seats, an extra cup holder, and **the ability to pick up
;; and move multiple crates at once**.

;; Again considering the example above, the crates begin in the same
;; configuration:

;; ```
;;     [D]
;; [N] [C]
;; [Z] [M] [P]
;;  1   2   3
;; ```

;; Moving a single crate from stack 2 to stack 1 behaves the same as before:

;; ```
;; [D]
;; [N] [C]
;; [Z] [M] [P]
;;  1   2   3
;; ```

;; However, the action of moving three crates from stack 1 to stack 3 means that
;; those three moved crates **stay in the same order**, resulting in this new
;; configuration:

;; ```
;;         [D]
;;         [N]
;;     [C] [Z]
;;     [M] [P]
;;  1   2   3
;; ```

;; Next, as both crates are moved from stack 2 to stack 1, they retain their
;; order as well:

;; ```
;;         [D]
;;         [N]
;; [C]     [Z]
;; [M]     [P]
;;  1   2   3
;; ```

;; Finally, a single crate is still moved from stack 1 to stack 2, but now it's
;; crate `C` that gets moved:

;; ```
;;         [D]
;;         [N]
;;         [Z]
;; [M] [C] [P]
;;  1   2   3
;; ```

;; In this example, the CrateMover 9001 has put the crates in a totally
;; different order: `MCD`.

;; Before the rearrangement process finishes, update your simulation so that the
;; Elves know where they should stand to be ready to unload the final supplies.
;;
;; **After the rearrangement procedure completes, what crate ends up on top of
;; each stack?**
{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns solutions.2022.day05
    (:require [clojure.java.io :as io]
              [clojure.test :as t :refer [deftest]]
              [clojure.string :as str]))
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
