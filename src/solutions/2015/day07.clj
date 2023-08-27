^{:nextjournal.clerk/visibility :hide-ns}
(ns solutions.2015.day07
  {:nextjournal.clerk/toc true}
  (:require [clojure.java.io :as io]
            [util :as u]
            [nextjournal.clerk :as clerk]
            [clojure.string :as str]
            [clojure.core.match :as match]
            [clojure.edn :as edn]))

;; # Problem
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html (u/load-problem "07" "2015"))
{:nextjournal.clerk/visibility {:code :show :result :show}}

;; # Solution
;;
;; A strange day this one. On paper, the solution seems trivial; but getting
;; everything exactly right took some time.
;;
;; In short, the problem is a simple DSL that uses bit operations to store
;; values in registers called "wires", with the value called "signals". The
;; instruction set is very small, the full set being:
;;
;; ```
;; 123 -> x             ;; Initialize "x" register to 123
;; NOT y -> i           ;; Set "i" register to the result of bitwise-complement of "y"
;; x AND y -> d         ;; Set "d" register to the result of bitwise-and "x" and "y" registers
;; x OR y -> e          ;; Set "e" register to the result of bitwise-or "x" and "y" registers
;; x LSHIFT 2 -> f      ;; Set "f" register to the result of left-shifting the "x" register by 2
;; y RSHIFT 2 -> g      ;; Set "g" register to the result of right-shifting the "y" register by 2
;; ```
;;
;; Some of these values can be numbers or registers, so both cases have to be handled there.
;;
;; Given an input, part 1 asks you to find the result of a particular wire. Part
;; 2 is an interesting twist that asks you to replace the value of another wire
;; with the result of part 1 (the first time the parts are directly linked!)
;;
;; First things first, let's load our input and parse it
(def input (->> (slurp (io/resource "inputs/2015/day07.txt")) ;; Load the resource
                str/split-lines))                             ;; Split into lines
{:nextjournal.clerk/visibility {:result :hide}}

;; ## Get a value or return
;; A very poorly named function which given a cache-retreival function and an `n`, attempt the following:
;; - If `n` is a function, run it and return the value
;; - Otherwise try and parse it, if it's a number then return it; otherwise it's a register key so get the value in that register
(defn number-or-fn [get-cached n]
  (if (fn? n)
    (n)
    (let [n (edn/read-string n)]
      (if (number? n)
        n
        (get-cached (str n))))))

;; ## Parse all the instructions
;; Given a state argument to be initialized here and a cache-retreival function,
;; parse all the instructions and set up the functions to be called later. Since
;; we have to defer execution, we have to use a local atom to ensure that the
;; values are correctly captured first then processed.
;;
;; `core.match` is an absolute godsend for these problems, quite effectively
;; trivialising the parsing we do here. Each instruction has a simple mapping to
;; a bitwise function, `NOT` needing a small adjustment to handle 16 bit ints.
;;
;; Lastly we reduce over the instructions and `reset!` the atom to ensure
;; there's no leftover state and the function is as idempotent as it can be.
(defn parse-instructions [state get-cached instructions]
  (let [number-or-fn (partial number-or-fn get-cached)]
    (reset!
     state
     (reduce
      (fn [state instruction]
        (apply assoc state
               (match/match [(str/split instruction #" ")]
                 [[value "->" input]] [input (if (number? (edn/read-string value)) (constantly (number-or-fn value)) #(number-or-fn value))]
                 [["NOT" input "->" output]] [output  #(bit-and-not 16rFFFF (number-or-fn input))]
                 [[input1 "OR" input2 "->" output]] [output #(bit-or (number-or-fn input1) (number-or-fn input2))]
                 [[input1 "AND" input2 "->" output]] [output #(bit-and (number-or-fn input1) (number-or-fn input2))]
                 [[input "LSHIFT" value "->" output]] [output #(bit-shift-left (number-or-fn input) (number-or-fn value))]
                 [[input "RSHIFT" value "->" output]] [output #(bit-shift-right (number-or-fn input) (number-or-fn value))])))
      {}
      instructions))))

;; ## Swap signal values
;; Given the instruction lines, the name of a wire and a signal value to swap
;; to; set the new signal.
;;
;; Also ensure the old version is removed, and pull through the delim
(defn swap-signals [lines name value]
  (let [signal (first (filter #(str/ends-with? % (str "-> " name)) lines))
        [_ delim input] (str/split signal #" ")]
    (into
     (remove (partial = signal) lines)
     [(str/join " " [value delim input])])))

;; ## Part 1
;; Part 1 is a simple case of finding the value of `a`, which is deliberately
;; designed to be very recursive; hence the *need* to memoize the getter.
;; Because the only things going into the cached map are functions, we can just
;; call them straight away.
;;
;; So we initialize the atom, create the cache-retreival function and setup the
;; instructions. We can then call the memoized getter to get `a`, only computing
;; the relevant paths.
(defn part-1
  [input]
  (let [state (atom {})
        get-cached (memoize #((get @state (str %1))))]
    (parse-instructions state get-cached input)
    (get-cached "a")))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-1 input)

;; ## Part 2
;; And for part 2, it's identical other than the minor adjustment to the
;; signals. We setup `b` to be the value from part 1, otherwise this is
;; identical to part 1.
{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn part-2
  [input]
  (let [state (atom {})
        get-cached (memoize #((get @state (str %1))))]
    (parse-instructions state get-cached (swap-signals input "b" (part-1 input)))
    (get-cached "a")))

;; Which gives our answer
{:nextjournal.clerk/visibility {:code :hide :result :show}}
(part-2 input)
