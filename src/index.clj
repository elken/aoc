;; # 🎄 Advent of Clerk
;;
;; [Advent of Code](https://adventofcode.com) with
;; [Clerk](https://clerk.vision).
;;
;; Solutions authored by [@elken](https://github.com/elken/) with love in Clojure. 💕
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
  (:require
   [babashka.fs :as fs]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.view :as clerk.view]
   [clojure.java.io :as io]
   [clojure.string :as str]))

(alter-var-root #'clerk.view/include-css+js
                (fn [include-css+js-orig extra-includes]
                  (fn [state]
                    (concat (include-css+js-orig state)
                            extra-includes)))
                (list [:style#extra-styles (slurp (clojure.java.io/resource "style.css"))]))

(defn build-paths-year
  "Find all solutions and return the paths"
  [year]
  (->> (format "src/solutions/%s" year)
       fs/real-path
       fs/list-dir
       (map #(fs/relativize (fs/real-path ".") %))
       (map str)
       sort))

(defn build-paths []
  (->> "src/solutions"
       fs/list-dir
       (map #(map str (fs/list-dir %)))
       first
       sort))

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
                                [:li [:a {:href (-> path
                                                    (str/replace ".clj" "")
                                                    clerk/doc-url)} "Day " day]]))
                            (build-paths-year (str (first year)))))])
             (->> "src/solutions"
                  fs/real-path
                  fs/list-dir
                  (map (comp fs/components last))
                  reverse))))
