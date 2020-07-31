(ns conduit.db.schema)

;; define schema
(def user [{:db/ident :user/username
            :db/valueType :db.type/string
            :db/unique :db.unique/identity
            :db/cardinality :db.cardinality/one
            :db/doc "Users username"}
           {:db/ident :user/email
            :db/valueType :db.type/string
            :db/unique :db.unique/identity
            :db/cardinality :db.cardinality/one
            :db/doc "Users email address"}
           {:db/ident :user/token
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/doc "JWT token of the user"}
           {:db/ident :user/bio
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/doc "Users bio information"}
           {:db/ident :user/image
            :db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/doc "URL Image of the user"}])

(def article [{:db/ident :article/title
               :db/valueType :db.type/string
               :db/cardinality :db.cardinality/one
               :db/doc "Articles title"}
              {:db/ident :article/description
               :db/valueType :db.type/string
               :db/cardinality :db.cardinality/one
               :db/doc "Articles description"}
              {:db/ident :article/author
               :db/valueType :db.type/ref
               :db/cardinality :db.cardinality/one
               :db/doc "Articles author"}])

(def schema (into [] (concat user article)))
