(ns conduit.routes
  (:require [ring.util.http-response :as response]
            [conduit.middlewares :refer [wrap-formats wrap-authorization]]))

(defn response-handler [request-map]
  (response/ok
   (str "<h1> " (:remote-addr request-map) "</h1>")))

(defn post-handler [req]
  (response/ok
   {:result {:id (get-in req [:body-params :id])
             :name (get-in req [:body-params :name])
             :age (get-in req [:body-params :age])
             :city (get-in req [:body-params :city])}}))

(defn response [_req]
  (response/ok {:response "TODO"}))

(def routes [["/api" {:middleware [wrap-formats]}
              [""
               ["/users/login"    {:post response}]
               ["/users/register" {:post response}]
               ["/profiles/:username" {:get response}]
               ["/articles" {:get response
                             :post {:middleware [wrap-authorization]
                                    :handler response}}]
               ["/articles/:slug" {:get response
                                   :put {:middleware [wrap-authorization]
                                         :handler response}
                                   :delete {:middleware [wrap-authorization]
                                            :handler response}}]
               ["/articles/:slug/comments" {:get response
                                            :post {:middleware [wrap-authorization]
                                                   :handler response}}]
               ["/tags" {:get response}]]
              ["" {:middleware [wrap-authorization]}
               ["/user" {:get response :put response}]
               ["/profiles/:username/follow" {:post response :delete response}]
               ["/article/feed" {:get response}]
               ["/articles/:slug"
                ["/favourite" {:post response :delete response}]
                ["/comments/:id" {:delete response}]]]]])
