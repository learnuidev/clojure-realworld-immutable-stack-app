(ns conduit.db.article
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]
            [datahike.db :as db]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [conduit.db.utils :as u]))

; (c/to-long (t/now))

; (def db (d/db conn))
; (def hist (d/history db))
;
; (d/q '[:find [?title ...]
;        :in $ ?article-id
;        :where [?aid :article/id ?article-id ?tx ?op]
;               [?aid :article/title ?title]]
;     hist (u/string->uuid "3eaead6b-d2a9-4483-89cd-d28f14eacf5a"))

;;
(defn browse
  "Browse List of articles"
  ([conn] (browse conn (c/to-long (t/now))))
  ([conn index]
   (->> (d/q '[:find ?tx ?id ?title ?description ?email ?username
               :in $ ?index
               :where
               [?aid :article/title ?title ?tx]
               [(> ?index ?tx)]
               [?aid :article/id ?id]
               [?aid :article/description ?description ?tx]
               [?aid :article/author ?uid]
               [?uid :user/email ?email]
               [?uid :user/username ?username]]
             @conn index)
        (sort-by first #(compare %2 %1))
        (take 50)
        (map (fn [[tx id title description email username]]
               {:article/last-updated tx
                :article/id id
                :article/title title
                :article/description description
                :article/author {:user/email email
                                 :user/username username}})))))

; (defn browse
;   "Browse List of articles"
;   [conn pattern]
;   (d/q '[:find [(pull ?aid pattern) ...]
;          :in $ pattern
;          :where [?aid :article/title ?title]]
;        @conn pattern))
#_(browse conn '[*])
;; ===


(defn fetch
  "Fetch a single article by ID"
  [conn article-id pattern]
  (d/q '[:find (pull ?aid pattern) .
         :in $ ?article-id pattern
         :where [?aid :article/id ?article-id]]
       @conn (u/string->uuid article-id) pattern))
#_(fetch "3eaead6b-d2a9-4483-89cd-d28f14eacf5a" '[* {:article/author [*]}])

(defn fetch-by-user
  "Fetch a single article belonging to a user"
  [conn aid email]
  (d/q '[:find (pull ?a [* {:article/author [:user/email]}]) .
         :in $ ?aid ?email
         :where [?a :article/id ?aid]
         [?a :article/author ?author]
         [?author :user/email ?email]]
       @conn (u/string->uuid aid) email))
#_(fetch-by-user conn "3eaead6b-d2a9-4483-89cd-d28f14eacf5a" "john.doe@gmail.com")
;; ===

(defn edit!
  "Edit a article"
  [conn {:keys [aid params]}]
  (let [input (merge {:article/id (u/string->uuid aid)} params)
        _   (d/transact conn [input])]
    (fetch conn aid '[*])))
#_(edit! {:aid "3eaead6b-d2a9-4483-89cd-d28f14eacf5a"
          :email "john.doe@gmail.com"
          :params {:article/title "How not to fly a dragon"}})
;; ===

(defn add!
  "Add a new article"
  [conn {:keys [title description author]}]
  (println "AUTHOR " author)
  (let [id (u/uuid)]
    (d/transact conn [{:article/title title
                       :article/id id
                       :article/description description
                       :article/author [:user/email author]}])
    (fetch conn id '[* {:article/author [:user/email :user/username]}])))
#_(add! {:author "jane.doe@gmail.com"
         :title "Programming Clojure"
         :description "Learn how to program in Clojure"})
;; ===

(defn delete!
  "Delete an article by ID"
  [conn aid]
  (when-let [article (fetch conn aid '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:article/id (u/string->uuid aid)]]]})
    article))
;; ===

(defn articles-by-user [conn email pattern]
  (d/q '[:find [(pull ?a pattern) ...]
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))
#_(articles-by-user "john.doe@gmail.com"
                    '[* {:article/comments [* {:comment/author [*]}]}])
;; ===
