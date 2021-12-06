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

(def ^:private categories (edn/read (java.io.PushbackReader. (io/reader (io/resource "asf-cat/categories.edn")))))

(def policy-uri
  "The URI (as a string) of the Apache Software Foundation's 3rd Party License Policy"
  "https://www.apache.org/legal/resolved.html")

(defn category
  "Returns the ASF 'category' for the given license-id (which should be a SPDX license id or one of a very small number of supported non-SPDX license ids), which will be one of:

  nil                 - when license-id is nil, empty or blank
  :category-a         - see https://www.apache.org/legal/resolved.html#category-a
  :category-a-special - see https://www.apache.org/legal/resolved.html and scroll to the appropriate section
  :category-b         - see https://www.apache.org/legal/resolved.html#category-b
  :creative-commons   - see https://www.apache.org/legal/resolved.html#cc-by (may be any category - further manual investigation required)
  :category-x         - see https://www.apache.org/legal/resolved.html#category-x
  :uncategorised      - the ASF category could not be determined for this license"
  [license-id]
  (when-not (s/blank? license-id)
    (let [asf-cat (get categories license-id)]
      (cond asf-cat                                       asf-cat
            (= "public domain" (s/lower-case license-id)) :category-a-special  ; Non-SPDX identifier; see https://www.apache.org/legal/resolved.html#handling-public-domain-licensed-works
            (s/starts-with? license-id "CC-BY-")          :creative-commons
            :else                                         :uncategorised))))

(def ^{:arglists '([category])} category-info
  "Returns information on a category as a map with the keys :name and :url."
  {:category-a         {:name "Category A"                :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-a-special {:name "Category A (with caveats)" :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-b         {:name "Category B"                :url "https://www.apache.org/legal/resolved.html#category-b"}
   :creative-commons   {:name "Creative Commons Licenses" :url "https://www.apache.org/legal/resolved.html#cc-by"}
   :category-x         {:name "Category X"                :url "https://www.apache.org/legal/resolved.html#category-x"}
   :uncategorised      {:name "Uncategorised"             :url "https://www.apache.org/legal/resolved.html#criteria"}})

(def ^:private category-order
  "Category ordering."
  {:category-a         0
   :category-a-special 1
   :category-b         2
   :creative-commons   4
   :category-x         5
   :uncategorised      6})

(defn category-comparator
  "A comparator for ASF category keywords."
  [l r]
  (compare (get category-order l 99) (get category-order r 99)))

(def ordered-categories
  "The set of categories, ordered by category-comparator."
  (apply (partial sorted-set-by category-comparator) (keys category-order)))

(defn least-category
  "Returns the least category for the given sequence of license-ids."
  [license-ids]
  (when (seq license-ids)
    (first (sort-by category-order (distinct (map category (distinct license-ids)))))))
