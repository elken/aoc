^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2022.day07
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.core.match :as match]
            [clojure.string :as str]))
{:nextjournal.clerk/visibility {:code :show :result :show}}

^{::clerk/viewer :html ::clerk/visibility :hide}
(u/css)

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "07" "2022"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; ## Solution
;; Another whiplash back to difficulty. This one stumped me for a little bit,
;; took a while to have the sizes compute recursively cleanly. But we made it,
;; so let's run through everything.
;;
;; Today, we have an interesting one; we have to understand a directory tree
;; from a listing of commands!
;;
;; The demo input explains things quite well, but in essence we have to create a
;; mapping of all the directory sizes, then reason on it. The first thought here
;; screams "build a tree!", but it turns out to actually be quite overkill. All
;; we need to care about is the total size of a directory, so we can maintain
;; everything in a simple map.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2022/day07.txt")) ;; Load the resource
                (str/split-lines)))                            ;; Split into lines

;; A helper function to help compute the meat of the problem.
;;
;; Given the current directory stack and a size, recurse calling with an empty size map and parsing the size.
;;
;; Given the current directory stack, a map of all the current sizes and a new
;; size; recurse up the directory stack and update the size for every entry.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn compute-size
  ([dir-stack size] (compute-size dir-stack {} (parse-long size)))
  ([dir-stack size-map size]
   (if (empty? dir-stack)
     size-map
     (compute-size (pop dir-stack)
                   (merge-with + size-map {(apply str dir-stack) size})
                   size))))

;; The main function to give us our directory sizes.
;;
;; Using [core.match](https://github.com/clojure/core.match) to greatly simplify
;; the parsing logic, we determine what needs to happen given each kind of
;; input.

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/table [["$ cd <dir>" "Update the directory stack. If <dir> is .., pop the stack"]
              ["$ ls" "Do nothing. This is a trap, we can safely ignore this command and the output"]
              ["dir <dir>" "Do nothing. Also a trap, since we need to see what's in the directory"]
              ["<size> <file>" "Compute the size given the current directory stack"]])

;; The table above summarises the match conditions, the bulk of the work happens
;; when we find a file with a size. The size of the file is computed and merged
;; into the size map, along with anything else in the current directory stack.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn compute-sizes [input]
  (loop [size-map {}
         dir-stack []
         cmds input]
    (if (empty? cmds)
      size-map
      (let [[cmd & rst] cmds]
        (match/match [(str/split cmd #" ")]
          [["$" "cd" ".."]]                 (recur size-map (pop dir-stack) rst)
          [["$" "cd" dir]]                  (recur size-map (conj dir-stack dir) rst)
          [(:or ["$" "ls"] ["dir" _])]      (recur size-map dir-stack rst)
          [[val _]]                         (recur (merge-with + size-map (compute-size dir-stack val)) dir-stack rst))))))

;; Part 1 is just a case of getting all the directories with a size less than
;; 100kb and summing the sizes.
;;
;; You know the drill here, transducers!
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (transduce
   (filter (partial >= 100000))
   +
   0
   (vals (compute-sizes input))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; Part 2 we have to find the smallest directory that when deleted frees up
;; enough space.
;;
;; Let-binding variables so we don't have to compute them more than once
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [sizes (vals (compute-sizes input))
        max (apply max sizes)]
    (->> sizes
         (filter (partial <= (- max 40000000)))
         sort
         first)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
