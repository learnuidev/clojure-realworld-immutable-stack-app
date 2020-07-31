(ns conduit.db.core
  (:require [datahike.api :as d]
            [conduit.db.utils :as u]
            [datahike-postgres.core]))

;; define config
(def config {:store {:backend :pg
                     :host "localhost"
                     :port "5432/"
                     :user "postgres"
                     :password ""
                     :dbname "conduit_api"}})

;; Create a database
(u/create-db config)
;; (u/delete-db config)

;; create conn
(def conn (d/connect config))

;; define schema
(def schema [{:db/ident :user/username
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one
              :db/doc "Users username"}
             {:db/ident :user/email
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one
              :db/doc "Users email address"}
             {:db/ident :user/token
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "JWT token of the user"}
             {:db/ident :user/bio
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "Users bio information"}
             {:db/ident :user/image
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "URL Image of the user"}])

;; ;; The first transaction will be the schema we are using:
(d/transact conn schema)
(u/schema conn)

;; Data transaction
(d/transact conn [{:user/username "john.doe" :user/email "john.doe@gmail.com"}]);;

;; Search the data
(d/q '[:find ?e ?n ?a
       :where
       [?e :user/username ?n]
       [?e :user/email ?a]]
     @conn)
