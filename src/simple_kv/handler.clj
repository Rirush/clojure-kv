(ns simple-kv.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]])
  (:import (java.io InputStream InputStreamReader)))

(def state (atom {}))

(defroutes app-routes
           (POST "/set/:key" req
             (let [key (-> req :params :key)
                   ^InputStream body (:body req)
                   value (slurp body)]
               (swap! state assoc key value)
               {:status 200
                :body   (str "SET " key " TO " value " OK")}))
           (GET "/get/:key" req
             (let [key (-> req :params :key)
                   cont (contains? @state key)]
               (if cont
                 {:status 200 :body (get @state key)}
                 {:status 404 :body "NIL"})))
           (DELETE "/del/:key" req
             (let [key (-> req :params :key)
                   cont (contains? @state key)]
               (if cont
                 (do (swap! state dissoc key)
                     {:status 200
                      :body   (str "DEL " key " OK")})
                 {:status 404 :body "NIL"})))
           (GET "/len" []
             {:status 200
              :body   (str (count @state))})
           (GET "/find" [q]
             (let [r (re-pattern q)]
               {:status 200
                :body   (map #(str "KEY " (first %) " VALUE " (second %) "\n")
                             (into {}
                                   (filter #(re-matches r (first %)) @state)))}))
           (GET "/" [] "very awesome kv server")
           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
