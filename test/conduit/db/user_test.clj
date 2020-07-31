(ns conduit.db.user-test
  (:require [conduit.db.user :as user]
            [conduit.db.fixtures :refer [fixture-conn fixture-data test-user]]
            [clojure.test :refer [testing is deftest]]))

;;
(deftest user-browse
  (let [conn (fixture-conn)
        _  (fixture-data conn)]
    (testing "browse"
      (is (= (count (user/browse conn '[*])) 1)))))

(deftest user-fetch
  (let [conn (fixture-conn)
        _  (fixture-data conn)]
    (testing "fetch"
      (let [user (user/fetch conn (:user/email test-user) '[*])]
        (is (= (:user/email user) (:user/email test-user)))
        (is (= (:user/username user) (:user/username test-user)))))

    (testing "edit!"
      (let [params {:user/username "johnny.doe"}
            new-user (user/edit! conn "john.doe@gmail.com" params)]
        (is (= (:user/email new-user) (:user/email test-user)))
        (is (= (:user/username new-user) "johnny.doe"))))

    (testing "add!"
      (let [new-user {:username "jane.doe" :email "jane@gmail.com"}
            _ (user/add! conn new-user)]
        (is (= (count (user/browse conn '[*])) 2))))

    (testing "delete!"
      (let [deleted-user (user/delete! conn "john.doe@gmail.com")]
        (is (= (:user/email deleted-user) (:user/email test-user)))
        (is (= (count (user/browse conn '[*])) 1))))))

(deftest user-edit
  (let [conn (fixture-conn)
        _  (fixture-data conn)]
    (testing "edit!"
      (let [params {:user/username "johnny.doe"}
            new-user (user/edit! conn "john.doe@gmail.com" params)]
        (is (= (:user/email new-user) (:user/email test-user)))
        (is (= (:user/username new-user) "johnny.doe"))))

    (testing "add!"
      (let [new-user {:username "jane.doe" :email "jane@gmail.com"}
            _ (user/add! conn new-user)]
        (is (= (count (user/browse conn '[*])) 2))))

    (testing "delete!"
      (let [deleted-user (user/delete! conn "john.doe@gmail.com")]
        (is (= (:user/email deleted-user) (:user/email test-user)))
        (is (= (count (user/browse conn '[*])) 1))))))

(deftest user-add
  (let [conn (fixture-conn)
        _  (fixture-data conn)]
    (testing "add!"
      (let [new-user {:username "jane.doe" :email "jane@gmail.com"}
            _ (user/add! conn new-user)]
        (is (= (count (user/browse conn '[*])) 2))))))

(deftest user-delete
  (let [conn (fixture-conn)
        _  (fixture-data conn)]
    (testing "delete!"
      (let [deleted-user (user/delete! conn "john.doe@gmail.com")]
        (is (= (:user/email deleted-user) (:user/email test-user)))
        (is (= (count (user/browse conn '[*])) 0))))))
