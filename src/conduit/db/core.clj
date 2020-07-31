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
;; (u/schema conn)

;; Seed Data
(defn seed [conn]
  (d/transact conn [{:user/username "john.doe" :user/email "john.doe@gmail.com"};;
                    {:user/username "jane.doe" :user/email "jane.doe@gmail.com"}]));;
#_(seed conn)

;; Search the data
(d/q '[:find ?e ?n ?a
       :where
       [?e :user/username ?n]
       [?e :user/email ?a]]
     @conn)
