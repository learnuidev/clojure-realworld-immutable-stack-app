(ns conduit.db.comment
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]
            [conduit.db.article :as article]
            [conduit.db.utils :as u]))

(defn browse
  "Browse the comments of article"
  [conn aid]
  (article/fetch conn aid '[{:article/comments [* {:comment/author [:user/username :user/email :user/id]}]}]))
#_(browse "574ae17e-676f-4822-a902-937f8a6841c4")
;; ===

(defn fetch
  "Get a comment by id"
  [conn cid pattern]
  (d/q '[:find (pull ?cid pattern) .
         :in $ ?comment-id pattern
         :where [?cid :comment/id ?comment-id]]
       @conn (u/string->uuid cid) pattern))
#_(fetch "f0927998-9494-4828-a036-de8294cf2365" '[* {:comment/author [:user/username :user/email :user/id]}])
;; ===


(defn add!
  "Add a new comment"
  [conn aid {:keys [body email]}]
  (let [cid (u/uuid)
        input {:comment/body body
               :comment/id cid
               :comment/author [:user/email email]}]
    (d/transact conn [{:article/id (u/string->uuid aid)
                       :article/comments [input]}])
    (fetch conn cid '[* {:comment/author [:user/username :user/email :user/id]}])))

#_(add! "574ae17e-676f-4822-a902-937f8a6841c4"
        {:body "I like Lion King as well"
         :email "john.doe@gmail.com"})
;; ===

(defn delete!
  "Delete an comment by ID"
  [conn cid]
  (when-let [comment (fetch conn cid '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:comment/id (u/string->uuid cid)]]]})
    comment))
