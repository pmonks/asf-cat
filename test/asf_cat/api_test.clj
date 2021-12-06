;
; Copyright © 2021 Peter Monks
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
; SPDX-License-Identifier: Apache-2.0
;

(ns asf-cat.api-test
  (:require [clojure.test :refer [deftest testing is]]
            [asf-cat.api  :refer [category category-info category-comparator least-category]]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version"))

(deftest category-test
  (testing "Nil, empty or blank license-ids"
    (is (nil? (category nil)))
    (is (nil? (category "")))
    (is (nil? (category "       ")))
    (is (nil? (category "\n")))
    (is (nil? (category "\t"))))
  (testing "Select SPDX license-ids"
    (is (= :category-a         (category "Apache-2.0")))
    (is (= :category-a-special (category "OGL-UK-3.0")))
    (is (= :category-a-special (category "CC0-1.0")))
    (is (= :category-a-special (category "CC-PDDC")))
    (is (= :category-b         (category "EPL-1.0")))
    (is (= :category-b         (category "EPL-2.0")))
    (is (= :creative-commons   (category "CC-BY-1.0")))
    (is (= :creative-commons   (category "CC-BY-2.0")))
    (is (= :creative-commons   (category "CC-BY-SA-2.1-JP")))
    (is (= :creative-commons   (category "CC-BY-2.5-AU")))
    (is (= :creative-commons   (category "CC-BY-3.0")))
    (is (= :creative-commons   (category "CC-BY-4.0")))
    (is (= :creative-commons   (category "CC-BY-SA-4.0")))
    (is (= :creative-commons   (category "CC-BY-ND-4.0")))
    (is (= :category-x         (category "GPL-2.0")))
    (is (= :category-x         (category "LGPL-2.0")))
    (is (= :uncategorised      (category "Beerware"))))
  (testing "Non-SPDX license-ids"
    (is (= :category-a         (category "MX4J")))
    (is (= :category-a         (category "DOM4J")))
    (is (= :category-a-special (category "Public domain")))))

(deftest category-info-test
  (testing "Nil, invalid, or :uncategorised category"
    (is (nil? (category-info nil)))
    (is (nil? (category-info :invalid-category)))
    (is (nil? (category-info :uncategorized))))
  (testing "Non-nil category"
    (is (not (nil? (category-info :category-a))))
    (is (not (nil? (category-info :category-a-special))))
    (is (not (nil? (category-info :category-b))))
    (is (not (nil? (category-info :creative-commons))))
    (is (not (nil? (category-info :category-x))))))

(deftest category-comparator-test
  (testing "Sorting"
    (is (= '(:category-a :category-a :category-a-special :category-a-special :category-b :category-b :category-x :category-x :uncategorised :uncategorised)
            (sort-by category-comparator [:uncategorised :category-a-special :category-x :category-a :category-a :category-b :category-x :uncategorised :category-b :category-a-special])))))

(deftest least-category-test
  (testing "nil or empty license-ids"
    (is (= nil (least-category nil)))
    (is (= nil (least-category [])))
    (is (= nil (least-category '())))
    (is (= nil (least-category #{}))))
  (testing "Populated license-ids"
    (is (= :category-a         (least-category ["Apache-2.0"])))
    (is (= :category-a         (least-category ["GPL-3.0" "Apache-2.0"])))
    (is (= :category-a-special (least-category ["Public Domain" "GPL-3.0"])))
    (is (= :category-b         (least-category ["EPL-2.0" "GPL-3.0"])))
    (is (= :creative-commons   (least-category ["CC-BY-SA-4.0" "GPL-3.0"])))
    (is (= :category-x         (least-category ["AGPL-2.0" "GPL-3.0"])))
    (is (= :uncategorised      (least-category ["GPL-2.1" "BAR"]))))
  (testing "Large list of license-ids"
    (is (= :category-a
           (least-category [ "MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                            "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                            "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"])))))
