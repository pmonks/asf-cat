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
            [asf-cat.api  :refer [license-category expression-category category-info category-comparator
                                  license-comparator categories least-category most-category licenses-least-category
                                  licenses-most-category expressions-least-category expressions-most-category]]))

(println "\n☔️ Running tests on Clojure" (clojure-version) "/ JVM" (System/getProperty "java.version"))

(deftest license-category-test
  (testing "Nil, empty or blank license ids"
    (is (nil? (license-category nil)))
    (is (nil? (license-category "")))
    (is (nil? (license-category "       ")))
    (is (nil? (license-category "\n")))
    (is (nil? (license-category "\t"))))
  (testing "Select SPDX license ids"
    (is (= :category-a         (license-category "Apache-2.0")))
    (is (= :category-a         (license-category "         Apache-2.0         ")))  ; Test whitespace
    (is (= :category-a         (license-category "BSD-3-Clause")))
    (is (= :category-a-special (license-category "OGL-UK-3.0")))
    (is (= :category-a-special (license-category "CC0-1.0")))
    (is (= :category-a-special (license-category "CC-PDDC")))
    (is (= :category-b         (license-category "EPL-1.0")))
    (is (= :category-b         (license-category "EPL-2.0")))
    (is (= :creative-commons   (license-category "CC-BY-1.0")))
    (is (= :creative-commons   (license-category "CC-BY-2.0")))
    (is (= :creative-commons   (license-category "CC-BY-SA-2.1-JP")))
    (is (= :creative-commons   (license-category "CC-BY-2.5-AU")))
    (is (= :creative-commons   (license-category "CC-BY-3.0")))
    (is (= :creative-commons   (license-category "CC-BY-4.0")))
    (is (= :creative-commons   (license-category "CC-BY-SA-4.0")))
    (is (= :creative-commons   (license-category "CC-BY-ND-4.0")))
    (is (= :category-b         (license-category "CDDL-1.0")))
    (is (= :category-b         (license-category "CDDL-1.1")))
    (is (= :category-x         (license-category "BSD-4-Clause")))
    (is (= :category-x         (license-category "BSD-4-Clause-Shortened")))
    (is (= :category-x         (license-category "GPL-2.0")))
    (is (= :category-x         (license-category "GPL-2.0-with-classpath-exception")))
    (is (= :category-x         (license-category "AGPL-1.0")))
    (is (= :category-x         (license-category "AGPL-3.0")))
    (is (= :category-x         (license-category "LGPL-2.0")))
    (is (= :category-x         (license-category "LGPL-2.1")))
    (is (= :category-x         (license-category "LGPL-2.1-or-later")))
    (is (= :category-x         (license-category "CC-BY-NC-4.0")))
    (is (= :category-x         (license-category "CC-BY-NC-SA-3.0")))
    (is (= :category-x         (license-category "CC-BY-NC-SA-2.0-UK")))
    (is (= :uncategorised      (license-category "Beerware"))))
  (testing "Non-SPDX license ids"
    (is (= :uncategorised      (license-category "NON-SPDX-JDOM")))
    (is (= :category-a-special (license-category "Public domain")))
    (is (= :category-a-special (license-category "LicenseRef-lice-comb-PUBLIC-DOMAIN")))
    (is (= :category-x         (license-category "LicenseRef-lice-comb-PROPRIETARY-COMMERCIAL")))
    (is (= :uncategorised      (license-category "LicenseRef-lice-comb-UNLISTED")))
    (is (= :uncategorised      (license-category "LicenseRef-lice-comb-UNLISTED-VytN8Wjy")))))   ; Suffix is foobar in base62

(deftest expression-category-test
  (testing "Nil, empty or blank expressions"
    (is (nil? (expression-category nil)))
    (is (nil? (expression-category "")))
    (is (nil? (expression-category "       ")))
    (is (nil? (expression-category "\n")))
    (is (nil? (expression-category "\t"))))
  (testing "Invalid SPDX expressions"
    (is (nil? (expression-category "Apache")))
    (is (nil? (expression-category "(Apache-2.0")))
    (is (nil? (expression-category "Apache-2.0 or MIT"))))  ; operators must be uppercase
  (testing "Valid SPDX expressions"
    (is (= :category-a         (expression-category "Apache-2.0")))
    (is (= :category-a         (expression-category "    Apache-2.0  ")))
    (is (= :category-a         (expression-category "Apache-2.0+")))
    (is (= :category-x         (expression-category "GPL-2.0")))
    (is (= :category-x         (expression-category "GPL-2.0-only")))
    (is (= :category-a         (expression-category "Apache-2.0 OR GPL-2.0-only")))
    (is (= :category-x         (expression-category "Apache-2.0 AND GPL-2.0-only")))
    (is (= :category-a         (expression-category "Apache-2.0 OR GPL-2.0-only OR MIT")))
    (is (= :category-x         (expression-category "Apache-2.0 AND GPL-2.0-only AND MIT")))
    (is (= :category-a         (expression-category "Apache-2.0 OR GPL-2.0-only AND MIT")))
    (is (= :category-a         (expression-category "Apache-2.0 AND GPL-2.0-only OR MIT")))
    (is (= :category-a         (expression-category "Apache-2.0 OR (GPL-2.0-only AND MIT)")))
    (is (= :category-a         (expression-category "(Apache-2.0 OR GPL-2.0-only) AND MIT")))
    (is (= :category-x         (expression-category "GPL-3.0 OR (GPL-2.0-only AND MIT)")))
    (is (= :category-a-special (expression-category "Apache-2.0 AND LicenseRef-lice-comb-PUBLIC-DOMAIN")))
    (is (= :category-x         (expression-category "Apache-2.0 AND LicenseRef-lice-comb-PROPRIETARY-COMMERCIAL")))
    (is (= :creative-commons   (expression-category "GPL-3.0 OR CC-BY-4.0")))
    (is (= :category-x         (expression-category "GPL-3.0 AND CC-BY-4.0")))
    (is (= :category-b         (expression-category "EPL-2.0 OR GPL-3.0-or-later WITH Classpath-exception-2.0")))
    (is (= :uncategorised      (expression-category "Apache-2.0 AND Beerware")))
    (is (= :uncategorised      (expression-category "GPL-2.0-only AND LicenseRef-lice-comb-UNLISTED-VytN8Wjy")))))

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
  (testing "nil or empty"
    (is (= nil (least-category nil)))
    (is (= nil (least-category [])))
    (is (= nil (least-category '())))
    (is (= nil (least-category #{}))))
  (testing "Categories"
    (is (= :category-a    (least-category [:category-a])))
    (is (= :category-b    (least-category [:category-b])))
    (is (= :uncategorised (least-category [:uncategorised])))
    (is (= :category-a    (least-category [:category-a :category-a :category-a])))
    (is (= :category-b    (least-category [:category-b :uncategorised :category-x])))))

(deftest most-category-test
  (testing "nil or empty"
    (is (= nil (most-category nil)))
    (is (= nil (most-category [])))
    (is (= nil (most-category '())))
    (is (= nil (most-category #{}))))
  (testing "Categories"
    (is (= :category-a    (most-category [:category-a])))
    (is (= :category-b    (most-category [:category-b])))
    (is (= :uncategorised (most-category [:uncategorised])))
    (is (= :category-a    (most-category [:category-a :category-a :category-a])))
    (is (= :uncategorised (most-category [:category-b :uncategorised :category-x])))))

(deftest licenses-least-category-test
  (testing "nil or empty license ids"
    (is (= nil (licenses-least-category nil)))
    (is (= nil (licenses-least-category [])))
    (is (= nil (licenses-least-category '())))
    (is (= nil (licenses-least-category #{}))))
  (testing "License ids"
    (is (= :category-a         (licenses-least-category ["Apache-2.0"])))
    (is (= :category-a         (licenses-least-category '("GPL-3.0" "Apache-2.0"))))
    (is (= :category-a-special (licenses-least-category #{"Public Domain" "GPL-3.0"})))
    (is (= :category-b         (licenses-least-category ["EPL-2.0" "GPL-3.0"])))
    (is (= :creative-commons   (licenses-least-category ["CC-BY-SA-4.0" "GPL-3.0"])))
    (is (= :category-x         (licenses-least-category ["AGPL-1.0" "GPL-3.0"])))
    (is (= :category-x         (licenses-least-category ["GPL-2.0" "BAR"])))
    (is (= :uncategorised      (licenses-least-category ["FOO" "BAR"]))))
  (testing "Large set of license ids"
    (is (= :category-a
           (licenses-least-category #{"MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                                      "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                                      "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"}))))
  (testing "Vector containing duplicate license ids"
    (is (= :category-a
           (licenses-least-category ["AGPL-3.0" "Apache-2.0" "Apache-2.0" "Apache-2.0" "AGPL-3.0" "AGPL-3.0" "Apache-2.0"
                                     "AGPL-3.0" "AGPL-3.0" "Apache-2.0"])))))

(deftest licenses-most-category-test
  (testing "nil or empty license ids"
    (is (= nil (licenses-most-category nil)))
    (is (= nil (licenses-most-category [])))
    (is (= nil (licenses-most-category '())))
    (is (= nil (licenses-most-category #{}))))
  (testing "License ids"
    (is (= :category-a         (licenses-most-category ["Apache-2.0"])))
    (is (= :category-x         (licenses-most-category '("GPL-3.0" "Apache-2.0"))))
    (is (= :category-a-special (licenses-most-category #{"Public Domain" "Apache-2.0"})))
    (is (= :category-b         (licenses-most-category ["EPL-2.0" "Apache-2.0"])))
    (is (= :creative-commons   (licenses-most-category ["CC-BY-SA-4.0" "Apache-2.0"])))
    (is (= :category-x         (licenses-most-category ["AGPL-1.0" "GPL-3.0"])))
    (is (= :uncategorised      (licenses-most-category ["GPL-2.0" "BAR"])))
    (is (= :uncategorised      (licenses-most-category ["FOO" "BAR"]))))
  (testing "Large set of license ids"
    (is (= :uncategorised
           (licenses-most-category #{"MPL-1.0" "Unlicense" "LGPL-3.0" "CDDL-1.0" "CPL-1.0" "EPL-1.0" "BAR" "IPL-1.0" "GPL-2.0"
                                     "MPL-1.1" "SPL-1.0" "Apache-2.0" "OSL-3.0" "GPL-1.0" "GPL-3.0" "AGPL-3.0" "EPL-2.0" "LGPL-2.0"
                                     "LGPL-2.1" "MPL-2.0" "BSD-4-Clause" "FU" "CC-PDDC" "CDDL-1.1"}))))
  (testing "Vector containing duplicate license ids"
    (is (= :category-x
           (licenses-most-category ["AGPL-3.0" "Apache-2.0" "Apache-2.0" "Apache-2.0" "AGPL-3.0" "AGPL-3.0" "Apache-2.0"
                                    "AGPL-3.0" "AGPL-3.0" "Apache-2.0"])))))

(deftest expressions-least-category-test
  (testing "nil or empty expressions"
    (is (= nil (expressions-least-category nil)))
    (is (= nil (expressions-least-category [])))
    (is (= nil (expressions-least-category '())))
    (is (= nil (expressions-least-category #{}))))
  (testing "Expressions")
    (is (= :creative-commons (expressions-least-category ["GPL-3.0 OR CC-BY-4.0" "Apache-2.0 AND CC-BY-4.0"]))))

(deftest expressions-most-category-test
  (testing "nil or empty expressions"
    (is (= nil (expressions-most-category nil)))
    (is (= nil (expressions-most-category [])))
    (is (= nil (expressions-most-category '())))
    (is (= nil (expressions-most-category #{}))))
  (testing "Expressions")
    (is (= :category-x (expressions-most-category ["GPL-3.0 AND CC-BY-4.0" "Apache-2.0 OR CC-BY-4.0"]))))
