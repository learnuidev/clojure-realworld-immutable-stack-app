(ns conduit.utils
  (:require [buddy.sign.jwt :as jwt]
            [buddy.hashers :as hashers]
            [clj-time.core :as t]))

(defonce token-secret "86bae26023208e57a5880d5ad644143c567fc57baaf5a942")

;; Helpers
(defn generate-token [email]
  (let [claims {:user email
                :exp (t/plus (t/now) (t/days 7))}]
    (jwt/sign claims token-secret)))

(defn encrypt-password [password]
  (hashers/derive password))

(defn check-password [password hash]
  (hashers/check password hash))
