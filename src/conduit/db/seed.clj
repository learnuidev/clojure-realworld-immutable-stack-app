(ns conduit.db.seed
  (:require [datahike.api :as d]
            [datahike-postgres.core]))

;; Seed Data
(defn seed-user [conn]
  (d/transact conn [{:user/username "john.doe" :user/email "john.doe@gmail.com"};;
                    {:user/username "jane.doe" :user/email "jane.doe@gmail.com"}]));;


(defn seed-articles [conn]
  (d/transact conn [{:article/title "How to train your dragon"
                     :article/description "Ever wonder how?"
                     :article/author [:user/email "john.doe@gmail.com"]}
                    {:article/title "Beauty and the Beast"
                     :article/description "covid19"
                     :article/author [:user/email "john.doe@gmail.com"]}]))

(defn seed [conn]
  (seed-user conn)
  (seed-articles conn))
