(ns conduit.db.core
  (:require [datahike.api :as d]
            [conduit.db.utils :as u]
            [mount.core :refer [defstate] :as mount]
            [conduit.db.schema :refer [schema]]
            [datahike-postgres.core]))

;; define config
(def config {:store {:backend :pg
                     :host "localhost"
                     :port 5432
                     :user "postgres"
                     :dbname "conduit_api"
                     :password ""
                     :path "/conduit_api"
                     :ssl false}})

;; 1. Create a conn
(defstate conn
          :start (do (u/create-db config) (d/connect config))
          :stop (d/release conn))

;; 2. Transact schema
;; The first transaction will be the schema we are using:
(d/transact conn schema)
#_(u/schema conn)

;; 3. Seed data

;; 4. Fetch username and email of all the users
#_(d/q '[:find ?e ?username ?email]
       :where
       [?e :user/username ?username]
       [?e :user/email ?email]
       @conn)

;; Fetch all the titles articles by john.doe@gmail.com
#_(d/q '[:find ?title]
       :where
       [?a :article/title ?title]
       [?a :article/author [:user/email "john.doe@gmail.com"]]
       @conn)

(defn articles-by-user [conn pattern email]
  (d/q '[:find [(pull ?a pattern) ...]
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))

#_(articles-by-user conn '[*] "john.doe@gmail.com")
