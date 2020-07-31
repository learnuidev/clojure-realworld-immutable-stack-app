(ns conduit.db.core
  (:require [datahike.api :as d]
            [conduit.db.utils :as u]
            [mount.core :refer [defstate] :as mount]
            [conduit.db.schema :refer [schema]]
            [datahike-postgres.core]))

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

;; 2. Transact schema - The first transaction will be the schema we are using:
(d/transact conn schema)
#_(u/schema conn)

;; 3. Seed data - dev

;; 4. Fetch username and email of all the users
#_(d/q '[:find ?e ?username ?email]
       :where
       [?e :user/username ?username]
       [?e :user/email ?email]
       @conn)

;; Fetch all the titles articles by john.doe@gmail.com
(d/q '[:find (pull ?a ?title)
       :where
       [?a :article/title ?title]
       [?a :article/author [:user/email "john.doe@gmail.com"]]]
     @conn)

;; Fetch the emails of all the users
(d/q '[:find [?email ...]
       :where
       [?u :user/email ?email]]
     @conn)

(defn articles-by-user [conn pattern email]
  (d/q '[:find [(pull ?a pattern) ...]
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))

#_(articles-by-user conn '[* {:article/comments [* {:comment/author [*]}]}] "john.doe@gmail.com")

;; Find all the users followed by john doe
#_(d/transact! conn [{:user/email "john.doe@gmail.com"
                      :user/following [{:user/email "jane.doe@gmail.com"}]}])

(d/transact! conn [{:user/email "john.doe@gmail.com"
                    :user/following [{:user/email "mike@gmail.com"}]}])

(defn followed-by-user [conn pattern email]
  (d/q '[:find [(pull ?uf pattern) ...]
         :in $ pattern ?email
         :where
         [?u :user/email ?email]
         [?u :user/following ?uf]]
       @conn pattern email))

(followed-by-user conn '[*] "john.doe@gmail.com")
