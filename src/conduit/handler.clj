(ns conduit.handler
  (:require [ring.util.http-response :as response]
            [buddy.sign.jwt :as jwt]
            [conduit.db.core :refer [conn]]
            [conduit.db.user :as user]
            [buddy.hashers :as hashers]
            [clj-time.core :as t]))

;; (str (hash/sha256 "mysecret"))
(defonce token-secret "86bae26023208e57a5880d5ad644143c567fc57baaf5a942")

;; Helpers
(defn generate-token [email]
  (let [claims {:user email
                :exp (t/plus (t/now) (t/days 7))}]
    (jwt/sign claims token-secret)))

(defn encrypt-password [password]
  (hashers/derive password))

(defn user->visible-user [{:user/keys [username email token bio image]}]
  {:user {:username username
          :email email
          :bio bio
          :image image
          :token token}})

(defn login [{{:keys [username password]} :body-params}]
  (if-let [user (user/fetch conn {:email username :username username} '[*])]
    (if (hashers/check password (:user/hash user))
      (let [new-token (generate-token (:user/email user))
            updated-user (user/edit! conn (:user/email user) {:user/token new-token})]
        (response/ok (user->visible-user updated-user)))
      (response/unauthorized {:message "Inorrect password. Please try again"}))
    (response/unauthorized {:message "Inorrect username. Please try again"})))

;;
(defn register [{{:keys [username password email]} :body-params}]
  (if (user/fetch conn {:email email :username username} '[*])
    (response/conflict {:message "Registration failed! User already exists"})
    (let [_new-user (user/add! conn {:username username
                                     :email email
                                     :token (generate-token email)
                                     :hash (encrypt-password password)})] ;; TODO: Convert to hash
      (response/ok {:message "Registration successful! Please login in"}))))
