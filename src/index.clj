;; # 🎄 Advent of Clerk
;;
;; [Advent of Code](https://adventofcode.com) with
;; [Clerk](https://clerk.vision).
;;
;; Below is a listing of all my solutions grouped by year.
;;
;; All the solutions also include the original problem spec, along with my commentary.
;;
;; Should be obvious, but this will fully spoil any days; so if you haven't yet
;; completed a particular day I would suggest you do so first. But, I'm also not
;; your parent so go nuts.
;;
;; Greatly inspired by [advent of clerk](https://github.com/nextjournal/advent-of-clerk)
(ns index
  {:nextjournal.clerk/visibility {:code :hide :result :hide}}
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [nextjournal.clerk :as clerk]))

(defn build-paths-year
  "Find all solutions and return the paths"
  [year]
  (->> (format "src/solutions/%s" year)
       fs/real-path
       fs/list-dir
       (map #(fs/relativize (fs/real-path ".") %))
       (map str)))

(defn build-paths []
  (->> "src/solutions"
       fs/list-dir
       (map #(map str (fs/list-dir %)))
       first))

{:nextjournal.clerk/visibility {:result :show}}

^::clerk/no-cache
(clerk/html
 (into [:div]
       (mapv (fn [[year]]
               [:section
                [:h1 (str year)]
                (into [:ul]
                      (mapv (fn [path]
                              (when-let [day (second (re-matches #".*day(\d+).clj" path))]
                                [:li [:a {:href (clerk/doc-url path)} "Day " day]]))
                            (build-paths-year (str (first year)))))])
             (->> "src/solutions"
                  fs/real-path
                  fs/list-dir
                  (map (comp fs/components last))
                  reverse))))
