(ns conduit.middlewares
  (:require [muuntaja.middleware :refer [wrap-format]]
            [ring.util.http-response :as response]
            [conduit.db.user :as user]
            [conduit.db.core :refer [conn]]
            [clojure.string :as str]))

(defn create-access-control-header [origin]
  (let [allowed-origins "http://localhost:8080"
        origins        (str/split allowed-origins #",")
        allowed-origin (some #{origin} origins)]
    {"Access-Control-Allow-Origin"  allowed-origin
     "Access-Control-Allow-Methods" "POST, GET, PUT, OPTIONS, DELETE"
     "Access-Control-Max-Age"       "3600"
     "Access-Control-Allow-Headers" "Authorization, Content-Type, x-requested-with"}))

(defn wrap-cors [handler]
  (fn [req]
    (let [origin   (get (:headers req) "origin")
          response (handler req)]
      (assoc response :headers (merge (:headers response) (create-access-control-header origin))))))

(defn wrap-auth [handler]
  (fn [{{:strs [authorization]} :headers :as req}]
    (let [token (when authorization (-> (str/split authorization #" ") last))]
      (if-let [user (user/fetch-by-token conn token '[*])]
        (handler (assoc req :auth user))
        (handler req)))))

(defn wrap-authorization [handler]
  (fn [req]
    (println "AUTH_USER" (:auth req))
    (if (:auth req)
      (handler req)
      (response/unauthorized {:message "Authorization required."}))))

(defn wrap-formats [handler]
  (-> handler
      (wrap-format)))
