(ns util
  (:require
   [clj-http.client :as client]
   [hickory.core :as h]
   [hickory.render :as hr]
   [hickory.select :as s]
   [babashka.fs :as fs]))

(defn load-problem [day year]
  (let [day (str (parse-long day))
        file-name (format "day%s.html" day)
        path (fs/path (fs/temp-dir) file-name)]
    (when-not (fs/exists? path)
      (let [resp (client/get (format "https://adventofcode.com/%s/day/%s" year day) {:headers {"Cookie" (str "session=" (System/getenv "AOC_TOKEN"))}})]
        (when (= 200 (:status resp))
          (spit (str path) (:body resp)))))

    (let [doc (h/as-hickory (h/parse (slurp (str path))))
          parts (map #(hr/hickory-to-html %) (s/select (s/child (s/tag :article)) doc))]
      (apply str (mapcat str parts)))))
