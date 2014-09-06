(ns subman.helpers-test
  (:require [clojure.test :refer [deftest testing]]
            [test-sugar.core :refer [is= is-do]]
            [subman.const :as const]
            [subman.helpers :as helpers]))

(deftest test-remove-first-0
  (is= "1" (helpers/remove-first-0 "01")))

(deftest test-nil-to-blank
  (is= (helpers/nil-to-blank nil) ""))

(deftest test-make-safe
  (is= :safe ((helpers/make-safe #(throw (Exception. %)) :safe) "danger")))

(deftest test-get-season-episode
  (testing "when appears"
    (is= (helpers/get-season-episode "s01E12") ["1" "12"]))
  (testing "when if 01x01 format"
    (is= (helpers/get-season-episode "12x01") ["12" "1"]))
  (testing "when not"
    (is= (helpers/get-season-episode "0202") ["" ""])))

(deftest test-make-static
  (is= (helpers/make-static "test" "path") [(str const/static-path "test")
                                            (str const/static-path "path")]))

(deftest test-as-static
  (is= (helpers/as-static identity "test")
       (str const/static-path "test")))

(helpers/defsafe safe-fn
  [x y]
  (throw (Exception. (str x y))))

(deftest test-defsafe
  (is-do nil? (safe-fn 1 2)))
