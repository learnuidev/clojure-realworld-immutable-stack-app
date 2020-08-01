(ns conduit.handler
  (:require [ring.util.http-response :as response]
            [conduit.db.core :refer [conn]]
            [conduit.db.user :as user]))

;; (user/fetch conn "john.doe@gmail.com" '[*])

(defn login [{{:keys [username password]} :body-params}]
  (response/ok {:response {:username username
                           :password password}}))

;;
(defn register [{{:keys [username password email]} :body-params}]
  (if (user/fetch conn {:email email :username username} '[*])
    (response/conflict {:message "Registration failed! User already exists"})
    (let [_new-user (user/add! conn {:username username
                                     :email email
                                     :hash password})] ;; TODO: Convert to hash
      (response/ok {:message "Registration successful! Please login in"}))))
