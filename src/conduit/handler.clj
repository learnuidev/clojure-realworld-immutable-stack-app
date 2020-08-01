(ns conduit.handler
  (:require [reitit.ring :as reitit]
            [ring.util.http-response :as response]
            [conduit.routes :refer [routes]]))

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
