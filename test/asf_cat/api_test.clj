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
            [asf-cat.api  :refer [category category-info category-comparator license-comparator categories least-category]]))

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
    (is (= :category-a         (category "         Apache-2.0         ")))  ; Test whitespace
    (is (= :category-a         (category "BSD-3-Clause")))
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
    (is (= :category-x         (category "BSD-4-Clause")))
    (is (= :category-x         (category "BSD-4-Clause-Shortened")))
    (is (= :category-x         (category "GPL-2.0")))
    (is (= :category-x         (category "GPL-2.0-with-classpath-exception")))
    (is (= :category-x         (category "LGPL-2.0")))
    (is (= :category-x         (category "LGPL-2.1")))
    (is (= :category-x         (category "LGPL-2.1-or-later")))
    (is (= :category-x         (category "CC-BY-NC-4.0")))
    (is (= :category-x         (category "CC-BY-NC-SA-3.0")))
    (is (= :category-x         (category "CC-BY-NC-SA-2.0-UK")))
    (is (= :uncategorised      (category "Beerware"))))
  (testing "Non-SPDX license-ids"
    (is (= :uncategorised      (category "NON-SPDX-JDOM")))
    (is (= :category-a-special (category "Public domain")))
    (is (= :category-a-special (category "NON-SPDX-Public-Domain")))))

(deftest category-info-test
  (testing "Nil or invalid category"
    (is (nil? (category-info nil)))
    (is (nil? (category-info :invalid-category))))
  (testing "Non-nil category"
    (is (not (nil? (category-info :category-a))))
    (is (not (nil? (category-info :category-a-special))))
    (is (not (nil? (category-info :category-b))))
    (is (not (nil? (category-info :creative-commons))))
    (is (not (nil? (category-info :category-x))))
    (is (= {:name "Category B" :url "https://www.apache.org/legal/resolved.html#category-b"}
           (category-info :category-b)))))

(deftest category-comparator-test
  (testing "Sorting categories - sort"
    (is (= '(:category-a :category-a :category-a-special :category-a-special :category-b :category-b :creative-commons :creative-commons :category-x :category-x :uncategorised :uncategorised)
            (sort category-comparator [:uncategorised :creative-commons :category-a-special :category-x :category-a :category-a :category-b :category-x :uncategorised :creative-commons :category-b :category-a-special]))))
  (testing "Sorting categories - sort-by"
    (is (= '(:category-a :category-a :category-a-special :category-a-special :category-b :category-b :creative-commons :creative-commons :category-x :category-x :uncategorised :uncategorised)
            (sort-by identity category-comparator [:uncategorised :creative-commons :category-a-special :category-x :category-a :category-a :category-b :category-x :uncategorised :creative-commons :category-b :category-a-special])))))

(deftest license-comparator-test
  (testing "Sorting licenses by their category - sort"
    (is (= '("Apache-2.0")
            (sort license-comparator ["Apache-2.0"])))
    (is (= '("Apache-2.0" "Apache-2.0")
            (sort license-comparator ["Apache-2.0" "Apache-2.0"])))
    (is (= '("Apache-2.0" "GPL-3.0")
            (sort license-comparator ["Apache-2.0" "GPL-3.0"])))
    (is (= '("Apache-2.0" "GPL-3.0")
            (sort license-comparator ["GPL-3.0" "Apache-2.0"])))
    (is (= '("Unlicense" "Apache-2.0" "CC-PDDC" "MPL-1.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "IPL-1.0" "MPL-1.1" "SPL-1.0" "OSL-3.0"
             "EPL-2.0" "MPL-2.0" "CDDL-1.1" "LGPL-3.0" "GPL-2.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "LGPL-2.0" "LGPL-2.1" "BSD-4-Clause"
             "BAR" "FU")
            (sort license-comparator ["MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                                      "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                                      "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"]))))
  (testing "Sorting licenses by their category - sort-by"
    (is (= '("Unlicense" "Apache-2.0" "CC-PDDC" "MPL-1.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "IPL-1.0" "MPL-1.1" "SPL-1.0" "OSL-3.0"
             "EPL-2.0" "MPL-2.0" "CDDL-1.1" "LGPL-3.0" "GPL-2.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "LGPL-2.0" "LGPL-2.1" "BSD-4-Clause"
             "BAR" "FU")
            (sort-by identity license-comparator ["MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                                                  "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                                                  "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"])))))

(deftest categories-test
  (testing "Categories"
    (is (= (sorted-set :category-a :category-a-special :category-b :creative-commons :category-x :uncategorised)
           categories))))

(deftest least-category-test
  (testing "nil or empty license-ids"
    (is (= nil (least-category nil)))
    (is (= nil (least-category [])))
    (is (= nil (least-category '())))
    (is (= nil (least-category #{}))))
  (testing "Populated license-ids (vectors)"
    (is (= :category-a         (least-category ["Apache-2.0"])))
    (is (= :category-a         (least-category ["GPL-3.0" "Apache-2.0"])))
    (is (= :category-a-special (least-category ["Public Domain" "GPL-3.0"])))
    (is (= :category-b         (least-category ["EPL-2.0" "GPL-3.0"])))
    (is (= :creative-commons   (least-category ["CC-BY-SA-4.0" "GPL-3.0"])))
    (is (= :category-x         (least-category ["AGPL-2.0" "GPL-3.0"])))
    (is (= :category-x         (least-category ["GPL-2.0" "BAR"])))
    (is (= :uncategorised      (least-category ["FOO" "BAR"]))))
  (testing "Populated license-ids (sets)"
    (is (= :category-a         (least-category #{"Apache-2.0"})))
    (is (= :category-a         (least-category #{"GPL-3.0" "Apache-2.0"})))
    (is (= :category-a-special (least-category #{"Public Domain" "GPL-3.0"})))
    (is (= :category-b         (least-category #{"EPL-2.0" "GPL-3.0"})))
    (is (= :creative-commons   (least-category #{"CC-BY-SA-4.0" "GPL-3.0"})))
    (is (= :category-x         (least-category #{"AGPL-2.0" "GPL-3.0"})))
    (is (= :category-x         (least-category #{"GPL-2.0" "BAR"})))
    (is (= :uncategorised      (least-category #{"FOO" "BAR"}))))
  (testing "Large set of license-ids"
    (is (= :category-a
           (least-category #{"MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                            "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                            "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"}))))
  (testing "Vector containing duplicate license-ids"
    (is (= :category-a
           (least-category ["AGPL-3.0" "Apache-2.0" "Apache-2.0" "Apache-2.0" "AGPL-3.0" "AGPL-3.0" "Apache-2.0"
                            "AGPL-3.0" "AGPL-3.0" "Apache-2.0"])))))
