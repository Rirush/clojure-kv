(ns simple-kv.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [simple-kv.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "very awesome kv server"))))

  (testing "creating key"
    (let [response (app (-> (mock/request :post "/set/key")
                            (mock/body "value")))]
      (is (= (:body response) (str "SET key TO value OK")))))

  (testing "getting key"
    (let [response (app (mock/request :get "/get/key"))]
      (is (= (:body response) (str "value")))))

  (testing "getting amount of keys"
    (let [resp (app (mock/request :get "/len"))]
      (is (= (:body resp) "1"))))

  (testing "deleting key"
    (let [response (app (mock/request :delete "/del/key"))]
      (is (= (:status response) 200))))

  (testing "getting a deleted key"
    (let [response (app (mock/request :get "/get/key"))]
      (is (and (= (:status response) 404) (= (:body response) "NIL")))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
