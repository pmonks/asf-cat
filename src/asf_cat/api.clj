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

(ns asf-cat.api
  "A micro library that provides a Clojure implementation of the Apache Software Foundation's 3rd Party License Policy (https://www.apache.org/legal/resolved.html)."
  (:require [clojure.string  :as s]
            [clojure.java.io :as io]
            [clojure.edn     :as edn]))

; ASF license categories
(def ^:private categories         (edn/read (java.io.PushbackReader. (io/reader (io/resource "asf-cat/categories.edn")))))
(def ^:private category-a         (:category-a         categories))
(def ^:private category-a-special (:category-a-special categories))
(def ^:private category-b         (:category-b         categories))
(def ^:private category-x         (:category-x         categories))

(defn category
  "Returns the ASF 'category' for the given license-id (which should be a SPDX license id or one of a very small number of supported non-SPDX license ids), which will be one of:

  :category-a         - see https://www.apache.org/legal/resolved.html#category-a
  :category-a-special - see https://www.apache.org/legal/resolved.html and scroll to the appropriate section
  :category-b         - see https://www.apache.org/legal/resolved.html#category-b
  :creative-commons   - see https://www.apache.org/legal/resolved.html#cc-by (may be any category - further manual investigation required)
  :category-x         - see https://www.apache.org/legal/resolved.html#category-x
  :uncategorised      - the ASF category could not be determined for this license"
  [license-id]
  (if (contains? category-a license-id)
    :category-a
    (if (contains? category-a-special license-id)
      :category-a-special
      (if (contains? category-b license-id)
        :category-b
        (if (s/starts-with? license-id "CC-BY-")   ; TODO: consider finer grained categories for different CC-BY- license types
          :creative-commons
          (if (contains? category-x license-id)
            :category-x
            :uncategorised))))))

(def ^:private category-order
  {:category-a         0
   :category-a-special 1
   :category-b         2
   :creative-commons   4
   :category-x         5
   :non-osi-approved   6
   :uncategorised      7})

(def third-party-license-uri "https://www.apache.org/legal/resolved.html")

(def category-info
  "Information on each category."
  {:category-a         {:name "Category A"                :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-a-special {:name "Category A (with caveats)" :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-b         {:name "Category B"                :url "https://www.apache.org/legal/resolved.html#category-b"}
   :creative-commons   {:name "Creative Commons Licenses" :url "https://www.apache.org/legal/resolved.html#cc-by"}
   :category-x         {:name "Category X"                :url "https://www.apache.org/legal/resolved.html#category-x"}
   :non-osi-approved   {:name "Non-OSI Approved Licenses" :url "https://www.apache.org/legal/resolved.html#criteria"}
   :uncategorised      {:name "Uncategorised"             :url "https://www.apache.org/legal/resolved.html#criteria"}})

(def category-comparator
  "A comparator for ASF category keywords."
  (comparator
    (fn [l r]
      (compare (get category-order l 99) (get category-order r 99)))))

(def least-to-most-problematic-categories
  "A sequence of categories in least to most problematic order."
  (sort-by category-order (keys category-order)))

(defn least-problematic-category
  "Returns the least problematic category for the given sequence of SPDX license identifiers."
  [licenses]
  (first (sort-by category-order (map category licenses))))
