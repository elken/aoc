^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day04
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str])
  (:import java.security.MessageDigest))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "04" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Tried looking for a clever solution here, but I don't think one exists so
;; sadly this will have to be extremely trivial.
;;
;; How trivial? Given a pre-determined key, give the lowest possible integer to
;; produce an MD5 hash that starts with 5 then 6 zeroes.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day04.txt")) ;; Load the resource
                str/split-lines ;; Split into lines
                first))                                       ;; Get the secret
{:nextjournal.clerk/visibility {:result :hide}}

;; ## MD5 function
;; No messing around here, extremely bare-bones hash compute.
(defn md5
  [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

;; ## Compute the hash
;; Compute the MD5 hash that satisfies the constraints, with a minor
;; optimization to only compute the limit once.
(defn compute-hash [limit]
  (let [limit (apply str (repeat limit "0"))]
    (first
     (filter
      #(str/starts-with? (md5 (str input %)) limit)
      (range)))))

;; ## Part 1
;; Check for a hash starting with 5 zeroes
{:nextjournal.clerk/visibility {:result :hide}}
(defn part-1
  [input]
  (let [result (compute-hash 5)]
    (list
     result
     (md5 (str input result)))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Check for a hash starting with 6 zeroes
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [result (compute-hash 6)]
    (list
     result
     (md5 (str input result)))))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
