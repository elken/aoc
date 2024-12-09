^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2024.day09
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "09" "2024"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Boy oh _boy_, did today suck...
;;
;; This takes the cake as the worst day this year, and it's some stiff
;; competition. First problem I've genuinely just given up trying to
;; optimise and make it nice and functional, the double ended pointer
;; tracking nonsense makes Clojure a bad pick for today, but we ride.
;;
;; Today has us trying to defragment a pretend hard drive. See the
;; problem above for more info, I really just want this solution up so
;; I can forget about it.
;;
;; First things first, let's load our input and parse it
(defn parse-memory [compressed]
  (->> compressed
       (map #(Character/digit % 10))
       (map-indexed (fn [idx length]
                      (repeat length
                              (when (even? idx)
                                (quot idx 2)))))
       (mapcat identity)
       vec))

(def input (->> (slurp (io/resource "inputs/2024/day09.txt")) ;; Load the resource
                str/trim
                parse-memory))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Functions
;; ### compact
;;
;; Yeah, don't expect these to be great explanations...
;;
;; Our part 1 solver that has to resort to using transient arrays to
;; prevent loads of slowdowns.
(defn compact [sparse-mem]
  (let [size (count sparse-mem)]
    (loop [read-pos 0
           write-pos 0
           last-valid (loop [pos (dec size)]
                        (cond
                          (neg? pos) 0
                          (get sparse-mem pos) pos
                          :else (recur (dec pos))))
           result (transient [])]
      (if (> read-pos last-valid)
        (persistent! result)
        (if-let [current-val (get sparse-mem read-pos)]
          (recur (inc read-pos)
                 (inc write-pos)
                 last-valid
                 (conj! result current-val))
          (recur (inc read-pos)
                 (inc write-pos)
                 (loop [new-last (dec last-valid)]
                   (cond
                     (neg? new-last) 0
                     (get sparse-mem new-last) new-last
                     :else (recur (dec new-last))))
                 (conj! result (get sparse-mem last-valid))))))))

;; ### locate-fitting-space
;;
;; Given a list of available space, a target and a size try and find
;; the first available slot for a file. Part 2 uses this to determine
;; if we have a safe slot we can move the file to.
(defn locate-fitting-space [available-spaces target-position required-size]
  (->> available-spaces
       (filter (fn [[pos size]]
                 (and (< pos target-position)
                      (>= size required-size))))
       first))

;; ### consolidate-spaces
;;
;; Merges adjacent free spaces, used in part 2 
(defn consolidate-spaces [space-map start-pos block-size]
  (let [next-pos (+ start-pos block-size)]
    (if-let [adjacent-size (get space-map next-pos)]
      (-> space-map
          (dissoc next-pos)
          (assoc start-pos (+ block-size adjacent-size)))
      (assoc space-map start-pos block-size))))

;; ### analyze-memory-layout
;;
;; Try and identify available spaces in the memory segments, used in
;; part 2
(defn analyze-memory-layout [memory-segments]
  (reduce
   (fn [[free-map file-map pos] segment]
     (let [block-id (first segment)
           size (count segment)]
       (if block-id
         [free-map
          (assoc file-map pos [block-id size])
          (+ pos size)]
         [(assoc free-map pos size)
          file-map
          (+ pos size)])))
   [(sorted-map) (sorted-map) 0]
   memory-segments))

;; ### reorganize-memory
;;
;; Part 2 solver, I'll be honest this mostly works by accident and was
;; definitely write-only.
(defn reorganize-memory [memory-state]
  (let [segments (vec (partition-by identity memory-state))
        [initial-spaces initial-files] (analyze-memory-layout segments)]
    (loop [files (reverse initial-files)
           spaces initial-spaces
           result initial-files]
      (if (empty? files)
        result
        (let [[curr-pos [file-id size]] (first files)
              target-space (locate-fitting-space spaces curr-pos size)]
          (if target-space
            (let [[target-pos target-size] target-space
                  new-spaces (cond-> (dissoc spaces target-pos)
                               (> target-size size)
                               (consolidate-spaces (+ target-pos size)
                                                   (- target-size size))
                               true (assoc curr-pos size))]
              (recur (rest files)
                     new-spaces
                     (-> (dissoc result curr-pos)
                         (assoc target-pos [file-id size]))))
            (recur (rest files)
                   spaces
                   result)))))))

;; ## Part 1
(defn part-1
  [input]
  (->> (compact input)
       (reduce-kv #(+ %1 (* %2 %3)) 0)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (->> (reorganize-memory input)
       (reduce-kv (fn [sum start [id len]]
                    (reduce #(+ %1 (* %2 id))
                            sum
                            (range start (+ start len))))
                  0)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
