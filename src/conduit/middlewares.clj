(ns conduit.middlewares
  (:require [muuntaja.middleware :refer [wrap-format]]
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

;;
(defn wrap-authorization [handler]
  (fn [req]
    (if (:auth-user req)
      (handler req)
      {:status 401
       :body   {:errors {:authorization "Authorization required."}}})))

(defn wrap-formats [handler]
  (-> handler
      (wrap-format)))
