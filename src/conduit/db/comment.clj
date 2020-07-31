(ns conduit.db.comment
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]
            [conduit.db.article :as article]
            [conduit.db.utils :as u]))

(defn browse
  "Browse the comments of article"
  [aid]
  (article/fetch aid '[{:article/comments [* {:comment/author [*]}]}]))
#_(browse "574ae17e-676f-4822-a902-937f8a6841c4")
;; ===

(defn fetch
  "Get a comment by id"
  [cid pattern]
  (d/q '[:find (pull ?cid pattern) .
         :in $ ?comment-id pattern
         :where [?cid :comment/id ?comment-id]]
       @conn (u/string->uuid cid) pattern))
#_(fetch "f0927998-9494-4828-a036-de8294cf2365" '[* {:comment/author [*]}])
;; ===


(defn add!
  "Add a new comment"
  [aid {:keys [body email]}]
  (let [cid (u/uuid)
        input {:comment/body body
               :comment/id cid
               :comment/author [:user/email email]}]
    (d/transact conn [{:article/id (u/string->uuid aid)
                       :article/comments [input]}])
    (fetch cid '[* {:comment/author [*]}])))

#_(add! "574ae17e-676f-4822-a902-937f8a6841c4"
        {:body "I like Lion King as well"
         :email "john.doe@gmail.com"})
;; ===

(defn delete!
  "Delete an comment by ID"
  [cid]
  (when-let [comment (fetch cid '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:comment/id (u/string->uuid cid)]]]})
    comment))
