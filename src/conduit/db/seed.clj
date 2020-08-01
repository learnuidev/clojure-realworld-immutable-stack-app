(ns conduit.db.seed
  (:require [datahike.api :as d]
            [datahike-postgres.core]
            [conduit.db.utils :as u]))

;; Seed Data
(defn seed-user [conn]
  (d/transact conn [{:user/username "john.doe"
                     :user/id (u/uuid)
                     :user/email "john.doe@gmail.com"};;
                    {:user/username "jane.doe"
                     :user/id (u/uuid)
                     :user/email "jane.doe@gmail.com"}
                    {:user/username "mike.fro"
                     :user/id (u/uuid)
                     :user/email "mike@gmail.com"}]));;


(defn seed-articles [conn]
  (d/transact conn [{:article/title "How to train your dragon"
                     :article/id (u/uuid)
                     :article/description "Ever wonder how?"
                     :article/author [:user/email "john.doe@gmail.com"]}
                    {:article/title "Beauty and the Beast"
                     :article/description "covid19"
                     :article/id (u/uuid)
                     :article/author [:user/email "jane.doe@gmail.com"]
                     :article/comments [{:comment/body "this is so cool"
                                         :comment/author [:user/email "jane.doe@gmail.com"]}]}]))

(defn seed [conn]
  (seed-user conn)
  (seed-articles conn))
