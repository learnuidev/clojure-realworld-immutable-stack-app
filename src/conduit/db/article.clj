(ns conduit.db.article
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]
            [conduit.db.utils :as u]))

(defn browse
  "Browse List of articles"
  [pattern]
  (d/q '[:find [(pull ?aid pattern) ...]
         :in $ pattern
         :where [?aid :article/title ?title]]
       @conn pattern))
#_(browse '[*])
;; ===


(defn fetch
  "Fetch a single article by ID"
  [article-id pattern]
  (d/q '[:find (pull ?aid pattern) .
         :in $ ?article-id pattern
         :where [?aid :article/id ?article-id]]
       @conn (u/string->uuid article-id) pattern))
#_(fetch "ad50d05f-c958-45c2-8492-c17298628909" '[* {:article/author [*]}])
;; ===

(defn edit!
  "Edit a article"
  [aid params]
  (let [input (merge {:article/id (u/string->uuid aid)} params)
        _ (d/transact conn [input])]
    (fetch aid '[*])))
#_(edit! "ad50d05f-c958-45c2-8492-c17298628909"
         {:article/title "Tales Pin"})
;; ===

(defn add!
  "Add a new article"
  [{:keys [title description author]}]
  (let [id (u/uuid)]
    (d/transact conn [{:article/title title
                       :article/id id
                       :article/description description
                       :article/author [:user/email author]}])
    (fetch id '[*])))
#_(add! {:author "jane.doe@gmail.com"
         :title "Programming Clojure"
         :description "Learn how to program in Clojure"})
;; ===

(defn delete!
  "Delete an article by ID"
  [aid]
  (when-let [article (fetch aid '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:article/id (u/string->uuid aid)]]]})
    article))
;; ===

(defn articles-by-user [email pattern]
  (d/q '[:find [(pull ?a pattern) ...]
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))
#_(articles-by-user "john.doe@gmail.com"
                    '[* {:article/comments [* {:comment/author [*]}]}])
;; ===
