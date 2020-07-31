(ns conduit.db.core
  (:require [datahike.api :as d]
            [conduit.db.utils :as u]
            [mount.core :refer [defstate] :as mount]
            [datahike-postgres.core]))

(def config {:store {:backend :pg
                     :host "localhost"
                     :port 5432
                     :user "postgres"
                     :dbname "conduit_api"
                     :password ""
                     :path "/conduit_api"
                     :ssl false}})

;; Create a conn
(defstate conn
          :start (do (u/create-db config) (d/connect config))
          :stop (d/release conn))

;
; (def db (d/db conn))
; (def hist (d/history db))
;
; (d/q '[:find ?aid ?tx ?title ?added
;        :where [?aid :article/title ?title ?tx ?added]]
;     hist)
;
;
