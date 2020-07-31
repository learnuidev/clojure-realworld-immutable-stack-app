(ns conduit.db.article
  (:require [conduit.db.core :refer [conn]]
            [datahike.api :as d]))

(defn articles-by-user [email pattern]
  (d/q '[:find [(pull ?a pattern) ...]
         :in $ pattern ?email
         :where
         [?a :article/title ?title]
         [?a :article/author ?email]]
       @conn pattern [:user/email email]))
#_(articles-by-user "john.doe@gmail.com"
                    '[* {:article/comments [* {:comment/author [*]}]}])
