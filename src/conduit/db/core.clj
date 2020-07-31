(ns conduit.db.core
  (:require [datahike.api :as d]
            [conduit.db.utils :as u]
            [mount.core :refer [defstate] :as mount]
            [conduit.db.schema :refer [schema]]
            [datahike-postgres.core]))

;; define config
(def config {:store {:backend :pg
                     :host "localhost"
                     :port "5432/"
                     :user "postgres"
                     :password ""
                     :dbname "conduit_api"}})

;; Create a database
;; (u/create-db config)
;; (u/delete-db config)
;; create conn
(defstate conn
          :start (do (u/create-db config) (d/connect config))
          :stop (d/release conn))

(mount/start)
(mount/stop)

;; ;; The first transaction will be the schema we are using:
(d/transact conn schema)
#_(u/schema conn)

;; Seed Data
(defn seed-user [conn]
  (d/transact conn [{:user/username "john.doe" :user/email "john.doe@gmail.com"};;
                    {:user/username "jane.doe" :user/email "jane.doe@gmail.com"}]));;
#_(seed-user conn)

(defn seed-articles []
  (d/transact conn [{:article/title "How to train your dragon"
                     :article/description "Ever wonder how?"
                     :article/author [:user/email "john.doe@gmail.com"]}]))

#_(seed-articles)

;; Fetch username and email of all the users
(d/q '[:find ?e ?username ?email
       :where
       [?e :user/username ?username]
       [?e :user/email ?email]]
     @conn)

;; Fetch all the titles articles by john.doe@gmail.com
(d/q '[:find ?title
       :where
       [?a :article/title ?title]
       [?a :article/author [:user/email "john.doe@gmail.com"]]]
     @conn)

;;
(defn articles-by-user [conn pattern email]
  (d/q '[:find (pull ?a pattern) .
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))

(articles-by-user conn '[*] "john.doe@gmail.com")
