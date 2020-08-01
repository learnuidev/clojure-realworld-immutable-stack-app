(ns conduit.routes
  (:require [ring.util.http-response :as response]
            [conduit.middlewares :refer [wrap-formats wrap-authorization wrap-auth]]
            [reitit.ring :as reitit]
            [reitit.ring.middleware.parameters :as parameters]
            [conduit.handler :as handler]))

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

(def routes [["/api" {:middleware [wrap-formats
                                   parameters/parameters-middleware]}
              [""
               ["/login"    {:post handler/login}]      ;; DONE
               ["/register" {:post handler/register}]   ;; DONE
               ["/profiles/:username" {:get response}]
               ["/articles" {:get handler/articles-browse  ;; DONE
                             :post {:middleware [wrap-auth ;; DONE
                                                 wrap-authorization]
                                    :handler handler/articles-add!}}]
               ["/articles/:id" {:get handler/articles-read ;; DONE
                                 :put {:middleware [wrap-auth ;; DONE
                                                    wrap-authorization]
                                       :handler handler/articles-edit!}
                                 :delete {:middleware [wrap-auth ;; DONE
                                                       wrap-authorization]
                                          :handler handler/articles-delete!}}]
               ["/articles/:id/comments" {:get response
                                          :post {:middleware [wrap-auth
                                                              wrap-authorization]
                                                 :handler response}}]
               ["/tags" {:get response}]]
              ["" {:middleware [wrap-auth
                                wrap-authorization]}
               ["/user" {:get response :put response}]
               ["/profiles/:username/follow" {:post response :delete response}]
               ["/article/feed" {:get response}]
               ["/articles/:id"
                ["/favourite" {:post response :delete response}]
                ["/comments/:id" {:delete response}]]]]])

(def app
  (reitit/routes
   (reitit/ring-handler
    (reitit/router routes))
   (reitit/create-resource-handler
    {:path "/"
     :root "build"}) ;; default is "public" inside resources folder
   (reitit/create-default-handler
    {:not-found
     (constantly (response/not-found "404 - Page not found"))
     :method-not-allowed
     (constantly (response/method-not-allowed "405 - Not allowed"))
     :not-acceptable
     (constantly (response/not-acceptable "406 - Not acceptable"))})))
