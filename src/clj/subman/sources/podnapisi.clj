(ns subman.sources.podnapisi
  (:require [swiss.arrows :refer [-<>]]
            [net.cgrand.enlive-html :as html]
            [subman.helpers :as helpers]
            [subman.const :as const]))

(defn- make-url [url] (str "http://www.podnapisi.net" url))

(defn- download-element
  "Get download element"
  [item]
  (-> item
      (html/select [[:td html/first-child] :a.subtitle_page_link])
      first))

(defn- season-episode-part
  "Get item from season episode part"
  [item pos]
  (helpers/nil-to-blank (some-> item
                                (html/select [[:td html/first-child]
                                              :div.list_div2 :b])
                                vec
                                (get pos)
                                :content
                                first
                                clojure.string/trim
                                helpers/remove-first-0)))

(defn- create-subtitle-map
  "Create subtitle map from list page item"
  [item]
  {:show (-> (download-element item)
             :content
             first
             clojure.string/trim)
   :url (-> (download-element item)
            :attrs
            :href
            make-url)
   :season (season-episode-part item 1)
   :episode (season-episode-part item 2)
   :version (-> item
                (html/select [[:td html/first-child] :span.release])
                first
                :content
                last)
   :name ""
   :lang (-> item
             (html/select [[:td (html/nth-child 3)]
                           :div.flag])
             first
             :attrs
             :alt
             (clojure.string/split #" ")
             first)})

(defn- parse-list-page
  "Parse page with subtitles list"
  [url]
  (-<> (helpers/fetch url)
       (html/select [:div#content_left
                     :table.list
                     [:tr (html/has [:td])]])
       (map create-subtitle-map <>)))

(defn- get-release-page-url
  "Get release page url"
  [page]
  (-> "/en/ppodnapisi/search/sJ/-1/sS/time/sO/desc/sT/-1/sM/0/sA/0/sK//sOA/0/sOT/0/sOL/0/sOI/0/sOE/0/sOD/0/sOH/0/sY//sOCS/0/sFT/0/sR//sTS//sTE//sAKA/1/sH//sI//tbsl/1/asdp/0/page//page/"
      (str page)
      make-url))

(defn get-release-page-result
  "Get release page result"
  [page]
  (-<> (get-release-page-url page)
       parse-list-page
       flatten
       (map #(assoc % :source const/type-podnapisi) <>)))
