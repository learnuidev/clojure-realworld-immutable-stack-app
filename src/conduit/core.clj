(ns conduit.core
  (:require [conduit.db.core]
            [conduit.db.user]
            [conduit.db.article]
            [conduit.db.comment]
            [ring.adapter.jetty :as jetty]
            [reitit.ring :as reitit]
            [ring.util.http-response :as response]
            [ring.middleware.reload :refer [wrap-reload]]
            [muuntaja.middleware :refer [wrap-format]]))

;;
(defn wrap-formats [handler]
  (-> handler
      (wrap-format)))

(defn response-handler [request-map]
  (response/ok
   (str "<h1> " (:remote-addr request-map) "</h1>")))

(defn post-handler [req]
  (response/ok
   {:result {:id (get-in req [:body-params :id])
             :name (get-in req [:body-params :name])
             :age (get-in req [:body-params :age])
             :city (get-in req [:body-params :city])}}))

(def routes [["/" {:get response-handler
                   :post post-handler}]
             ["/echo/:id" {:get (fn [req]
                                  (let [id (-> req :path-params :id)]
                                    (response/ok {:id id})))}]
             ["/api"
              {:middleware [wrap-formats]}
              ["/multiply" {:post (fn [{body-params :body-params}]
                                    (response/ok {:result  body-params}))}]]])

;;
(def handler
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

(defn -main []
  (jetty/run-jetty
   (-> #'handler wrap-reload)
   {:port 3000
    :join? false}))
