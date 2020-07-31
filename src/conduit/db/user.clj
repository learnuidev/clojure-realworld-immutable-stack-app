(ns conduit.db.user
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]))

(defn browse
  "Browse List of users"
  [pattern]
  (d/q '[:find [(pull ?uid pattern) ...]
         :in $ pattern
         :where [?uid :user/email ?email]]
       @conn pattern))
#_(browse '[*])
;; ===

(defn fetch
  "Fetch a single user by email"
  [email pattern]
  (d/q '[:find (pull ?uid pattern) .
         :in $ pattern ?email
         :where [?uid :user/email ?email]]
       @conn pattern email))
#_(fetch "john.doe@gmail.com" '[*])
;; ===

(defn edit!
  "Edit a user"
  [email params]
  (let [input (merge {:user/email email} params)
        _ (d/transact conn [input])]
    (fetch email '[*])))
#_(edit! "john.doe@gmail.com" {:user/username "johnny.doe"})
;; ===

;; Create/Add user
(defn add!
  "Add a new user"
  [username email]
  (d/transact conn [{:user/username username :user/email email}]))
#_(add! "jane" "jane.doe@gmail.com")

;; Delete a user
(defn delete!
  "Delete a user by email"
  [email]
  (when-let [user (fetch email '[*])]
    (d/transact conn {:tx-data [[:db/retractEntity [:user/email email]]]})
    user))
#_(delete! "john.doe@gmail.com")
;;
