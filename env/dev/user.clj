(ns user
  (:require [mount.core :as mount]
            [datahike.api :as d]
            [conduit.db.core :refer [config conn]]
            [conduit.db.schema :refer [schema]]
            [conduit.db.seed :refer [seed]]
            [conduit.db.utils :as u]))

;; Mount
(defn start! []
  (mount/start))

(defn stop! []
  (mount/stop))

(defn re-start []
  (stop!)
  (start!))

;; Database
(defn create-database! []
  (u/create-db config))

(defn delete-database! []
  (u/delete-db config))

(defn restart-db []
  (delete-database!)
  (create-database!))

(defn schema! []
  (u/schema conn))

;; Schema Migration
(defn migrate! []
  (d/transact conn schema))

(defn seed! []
  (seed conn))
