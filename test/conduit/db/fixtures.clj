(ns conduit.db.fixtures
  (:require [conduit.db.schema :refer [schema]]
            [datahike.api :as d]))

(def config {:store {:backend :pg
                     :host "localhost"
                     :port 5432
                     :user "postgres"
                     :dbname "conduit_test"
                     :password ""
                     :path "/conduit_test"
                     :ssl false}})

(def test-user {:user/username "john.doe" :user/email "john.doe@gmail.com"})

(defn fixture-conn []
  (d/delete-database config)
  (d/create-database config)
  (d/connect config))

(defn fixture-data [conn]
  (d/transact conn schema)
  (d/transact conn [test-user]))
