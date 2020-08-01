(ns conduit.core
  (:require [conduit.db.core]
            [conduit.db.user]
            [conduit.db.article]
            [conduit.db.comment]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [conduit.middlewares :refer [wrap-cors]]
            [ring.logger :as logger]
            [conduit.routes :refer [app]]))

(defn -main []
  (jetty/run-jetty
   (-> #'app wrap-reload logger/wrap-with-logger wrap-cors)
   {:port 3000
    :join? false}))
