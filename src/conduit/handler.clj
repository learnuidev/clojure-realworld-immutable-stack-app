(ns conduit.handler
  (:require [ring.util.http-response :as response]
            [conduit.db.core :refer [conn]]
            [conduit.db.user :as user]
            [conduit.db.article :as article]
            [conduit.utils :as utils]))
;;
(defn post-handler [req]
  (response/ok
   {:result {:id (get-in req [:body-params :id])
             :name (get-in req [:body-params :name])
             :age (get-in req [:body-params :age])
             :city (get-in req [:body-params :city])}}))

;; Auth
(defn login [{{:keys [username password]} :body-params}]
  (if-let [user (user/fetch conn {:email username :username username} '[*])]
    (if (utils/check-password password (:user/hash user))
      (let [new-token (utils/generate-token (:user/email user))
            updated-user (user/edit! conn (:user/email user) {:user/token new-token})]
        (response/ok (user/visible-user updated-user)))
      (response/unauthorized {:message "Inorrect password. Please try again"}))
    (response/unauthorized {:message "Inorrect username. Please try again"})))

(defn register [{{:keys [username password confirm email]} :body-params}]
  (if-not (= confirm password)
    (response/bad-request {:message "Password and confirm do not match"})
    (if (user/fetch conn {:email email :username username} '[*])
      (response/conflict {:message "Registration failed! User already exists"})
      (let [_new-user (user/add! conn {:username username
                                       :email email
                                       :token (utils/generate-token email)
                                       :hash (utils/encrypt-password password)})]
        (response/ok {:message "Registration successful! Please login in"})))))

;; == Articles ==
;; BROWSE
; (defn articles-browse [_req]
;   (let [articles (article/browse conn '[:article/title
;                                         :article/description
;                                         :article/id
;                                         {:article/author [:user/username :user/email]}])]
;     (response/ok {:articles articles})))
(defn articles-browse [{{:strs [pagination-index]} :query-params}]
  (let [index (when pagination-index (read-string pagination-index))
        articles (if index (article/browse conn index) (article/browse conn))]
    (response/ok {:articles articles})))

;; READ
(defn articles-read [{{:keys [id]} :path-params}]
  (try
    (let [article (article/fetch conn id '[* {:article/author [:user/username
                                                               :user/email]
                                              :article/comments [* {:comment/author [*]}]}])]
      (response/ok {:article article}))
    (catch Exception e
      (response/not-found {:message "Article not found! Please try another link"}))))

;; ARTICLE EDIT

;; ADD
(defn articles-add! [{{:user/keys [email]} :auth
                      {:keys [title description]} :body-params}]
  (let [new-article (article/add! {:title title
                                   :description description
                                   :author email})]
    (response/ok {:article new-article})))
