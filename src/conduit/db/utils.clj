(ns conduit.db.utils
  (:require [datahike.api :as d]
            [datahike.db :as ddb]
            [clojure.pprint :refer [pprint]]))

(defn uuid [] (java.util.UUID/randomUUID))

(defn string->uuid [string]
  (if (uuid? string)
    string
    (java.util.UUID/fromString string)))

(defn create-db [cfg]
  (when-not (d/database-exists? cfg)
    (d/create-database cfg)
    [true "Database successfully created"]))

(defn delete-db [cfg]
  (when (d/database-exists? cfg)
    (d/delete-database cfg)
    [true "Database successfully deleted"]))

(defn schema [conn]
  (pprint (ddb/-schema @conn)))
