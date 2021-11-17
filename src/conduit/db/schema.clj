(ns conduit.db.schema)

;; define schema
(def users [{:db/ident :user/username
             :db/valueType :db.type/string
             :db/unique :db.unique/identity
             :db/cardinality :db.cardinality/one
             :db/doc "Users username"}
            {:db/ident :user/id
             :db/valueType :db.type/uuid
             :db/unique :db.unique/identity
             :db/cardinality :db.cardinality/one
             :db/doc "Users ID"}
            {:db/ident :user/email
             :db/valueType :db.type/string
             :db/unique :db.unique/identity
             :db/cardinality :db.cardinality/one
             :db/doc "Users email address"}
            {:db/ident :user/bio
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "Users bio information"}
            {:db/ident :user/hash
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "Hashed password"}
            {:db/ident :user/token
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "JWT token"}
            {:db/ident :user/image
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "URL Image of the user"}
            {:db/ident :user/favourites
             :db/valueType :db.type/ref
             :db/cardinality :db.cardinality/many
             :db/doc "List of Articles favourited by the User"}
            {:db/ident :user/following
             :db/valueType :db.type/ref
             :db/cardinality :db.cardinality/many
             :db/doc "List of Users followed by the User"}])

(def articles [{:db/ident :article/title
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/doc "Articles title"}
               {:db/ident :article/description
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/doc "Articles description"}
               {:db/ident :article/id
                :db/valueType :db.type/uuid
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one
                :db/doc "Articles public ID"}
               {:db/ident :article/author
                :db/valueType :db.type/ref
                :db/cardinality :db.cardinality/one
                :db/doc "Articles author"}
               {:db/ident :article/comments
                :db/valueType :db.type/ref
                :db/cardinality :db.cardinality/many
                :db/doc "Article comments"}])

(def comments [{:db/ident :comment/body
                :db/valueType :db.type/string
                :db/cardinality :db.cardinality/one
                :db/doc "Comment's body"}
               {:db/ident :comment/id
                :db/valueType :db.type/uuid
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one
                :db/doc "Comments public ID"}
               {:db/ident :comment/author
                :db/valueType :db.type/ref
                :db/cardinality :db.cardinality/one
                :db/doc "Comment's author"}])

(def schema (into [] (concat users articles comments)))
