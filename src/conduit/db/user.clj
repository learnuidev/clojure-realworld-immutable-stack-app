(ns conduit.db.user
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]
            [conduit.db.utils :as u]))

(defn browse
  "Browse List of users"
  [conn pattern]
  (d/q '[:find [(pull ?uid pattern) ...]
         :in $ pattern
         :where [?uid :user/email ?email]]
       @conn pattern))
#_(browse conn '[*])
;; ===

(defn fetch
  "Fetch a single user by email"
  [conn email pattern]
  (d/q '[:find (pull ?uid pattern) .
         :in $ pattern ?email
         :where [?uid :user/email ?email]]
       @conn pattern email))
#_(fetch conn "john.doe@gmail.com" '[*])
;; ===

(defn edit!
  "Edit a user"
  [conn email params]
  (let [input (merge {:user/email email} params)
        _ (d/transact conn [input])]
    (fetch conn email '[*])))
#_(edit! conn "john.doe@gmail.com" {:user/username "johnny.doe"})
;; ===


(defn add!
  "Add a new user"
  [conn {:keys [username email]}]
  (d/transact conn [{:user/username username :user/email email}]))
#_(add! "jane" "jane.doe@gmail.com")
;; ===

(defn delete!
  "Delete a user by email"
  [conn email]
  (when-let [user (fetch conn email '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:user/email email]]]})
    user))
#_(delete! conn "john.doe@gmail.com")
;; ===

(defn follow!
  "Follow a user"
  [email following-email]
  (d/transact conn [{:user/email email
                     :user/following [{:user/email following-email}]}]))
#_(follow! "john.doe@gmail.com" "mike@gmail.com")
;; ===

(defn unfollow!
  "Unfollow a user"
  [email following-email]
  (d/transact conn {:tx-data [[:db/retract [:user/email email] :user/following [:user/email following-email]]]}))
#_(d/entity @conn [:user/email "mike@gmail.com"])
#_(unfollow! "john.doe@gmail.com" "mike@gmail.com")
;; ===

(defn followed-by-user
  "Find all the users followed by User"
  [pattern email]
  (d/q '[:find [(pull ?uf pattern) ...]
         :in $ pattern ?email
         :where
         [?u :user/email ?email]
         [?u :user/following ?uf]]
       @conn pattern email))
#_(followed-by-user '[*] "john.doe@gmail.com")
;; ===


(defn favourite!
  "Favourite an article"
  [email article-id]
  (d/transact conn [{:user/email email
                     :user/favourites [{:article/id (u/string->uuid article-id)}]}]))
#_(favourite! "john.doe@gmail.com" "574ae17e-676f-4822-a902-937f8a6841c4")
;; ===

(defn unfavourite!
  "Un favourite an article"
  [email article-id]
  (d/transact conn {:tx-data [[:db/retract [:user/email email] :user/favourites [:article/id (u/string->uuid article-id)]]]}))
#_(unfavourite! "john.doe@gmail.com" "574ae17e-676f-4822-a902-937f8a6841c4")
