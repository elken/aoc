^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day08
  {:nextjournal.clerk/toc true}
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.pprint :as pprint]
   [clojure.string :as str]
   [nextjournal.clerk :as clerk]
   [util :as u]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "08" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; Ah lovely, I think these kinds of problems are some of my favourites. Things
;; involving parsing, DSLs. My favourite all-time problems are probably the
;; monkeys or the ASM-lite problem from 2022.
;;
;; The problem boils down to counting string characters vs the actual string
;; _length_, then part 2 adds an extra step of encoding the string _on top_.
;;
;; Thanks to `edn/read-string`, this whole problem is very trivial. All we have
;; to do is handle `\x` since Java doesn't understand it.
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day08.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Replace invalid hex chars
;; As in the intro, Java doesn't handle the `\x27` escape sequence. So we have
;; to instead replace it with `\u0027`, boiling down to `\u` with a 4-character
;; hex string. We capture the hex value, pad it to 4 zeroes and append `\u`.
(defn hex-replace [string]
  (str/replace string #"\\x([a-f0-9]{2})" #(pprint/cl-format nil "\\u~4,'0d" (last %1))))

;; ## General transducer
;; Closest to a general solution we have, transduce over the list with the given
;; `comp` over `input` & add up all the resulting counts.
(defn xduce [comp input]
  (transduce
   comp
   +
   0
   input))

;; ## Escape characters
;; Slightly augment `char-escape-string` to not return `nil` for regular strings.
;;
;; Given a string, escape all the characters in it and return a new string.
(defn escape-string [s]
  (apply
   str
   (map
    #(if-let [parsed (char-escape-string %)]
       parsed
       %)
    s)))

;; ## Count characters
;; Count the characters as they appear lexigraphically
(defn count-chars [input]
  (xduce (comp (map count)) input))

;; ## Count literal characters
;; Count the literal parsed characters, as if they were in-memory
(defn count-literal [input]
  (xduce
   (comp (map #(edn/read-string (hex-replace %)))
         (map count))
   input))

;; ## Count encoded characters
;; Encode the original string as itself another string, and count those
;; lexigraphic characters
(defn count-encoded [input]
  (apply
   +
   (for [s input]
     (->> s
          escape-string
          count
          (+ 2)))))

;; ## Part 1
;; Part 1 is the difference between the lexigraphic counts and the literal
;; counts, nothing fancy here
(defn part-1
  [input]
  (- (count-chars input)
     (count-literal input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; Part 2 is similar, except with the difference being between encoded
;; characters and lexigraphic characters
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (- (count-encoded input)
     (count-chars input)))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
