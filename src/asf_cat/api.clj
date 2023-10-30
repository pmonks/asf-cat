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
  "A micro library that provides a Clojure implementation of the Apache Software
  Foundation's 3rd Party License Policy (https://www.apache.org/legal/resolved.html)."
  (:require [clojure.string     :as s]
            [clojure.java.io    :as io]
            [clojure.edn        :as edn]
            [spdx.expressions   :as sexp]
            [lice-comb.matching :as lcm]))

(def ^:private category-data (edn/read (java.io.PushbackReader. (io/reader (io/resource "asf-cat/categories.edn")))))

(def policy-uri
  "The URI (as a string) of the Apache Software Foundation's 3rd Party License
  Policy"
  "https://www.apache.org/legal/resolved.html")

(def ^{:arglists '([category])} category-info
  "Returns information on a category as a map with the keys :name and :url (both
  strings)."
  {:category-a         {:name "Category A"                :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-a-special {:name "Category A (with caveats)" :url "https://www.apache.org/legal/resolved.html#category-a"}
   :category-b         {:name "Category B"                :url "https://www.apache.org/legal/resolved.html#category-b"}
   :creative-commons   {:name "Creative Commons Licenses" :url "https://www.apache.org/legal/resolved.html#cc-by"}
   :category-x         {:name "Category X"                :url "https://www.apache.org/legal/resolved.html#category-x"}
   :uncategorised      {:name "Uncategorised"             :url "https://www.apache.org/legal/resolved.html#criteria"}})

(def ^:private category-order
  {:category-a         0
   :category-a-special 1
   :category-b         2
   :creative-commons   4
   :category-x         5
   :uncategorised      6})

(defn category-comparator
  "A comparator for ASF category keywords, defined as being this ordering:
  1. :category-a
  2. :category-a-special
  3. :category-b
  4. :creative-commons
  5. :category-x
  6. :uncategorised"
  [l r]
  (compare (get category-order l 99) (get category-order r 99)))

(def categories
  "The set of categories, ordered by category-comparator."
  (apply (partial sorted-set-by category-comparator) (keys category-order)))

(defn license-category
  "Given an SPDX license identifier (or 'Public Domain', which is not a valid
  SPDX identifier but is special cased by asf-cat), returns one of:

  nil                 - when license-id is nil, empty or blank
  :category-a         - see https://www.apache.org/legal/resolved.html#category-a
  :category-a-special - see https://www.apache.org/legal/resolved.html and
                        scroll to the appropriate section
  :category-b         - see https://www.apache.org/legal/resolved.html#category-b
  :creative-commons   - see https://www.apache.org/legal/resolved.html#cc-by
                        (may be any category - further manual investigation
                        required)
  :category-x         - see https://www.apache.org/legal/resolved.html#category-x
  :uncategorised      - the ASF category of license-id could not be determined"
  [license-id]
  (when-not (s/blank? license-id)
    (if-let [asf-cat (get category-data (s/trim license-id))]
      asf-cat
      (cond (lcm/public-domain? license-id)               :category-a-special  ; See https://www.apache.org/legal/resolved.html#handling-public-domain-licensed-works
            (= "public domain" (s/lower-case license-id)) :category-a-special  ; ditto
            (lcm/proprietary-commercial? license-id)      :category-x          ; See https://www.apache.org/legal/resolved.html#criteria (specifically point #1)
            (s/starts-with? license-id "CC-BY-NC-")       :category-x          ; See https://www.apache.org/legal/resolved.html#category-x
            (s/starts-with? license-id "CC-BY-")          :creative-commons    ; Various categories; see https://www.apache.org/legal/resolved.html#cc-by
            :else                                         :uncategorised))))

(defn license-comparator
  "A comparator for SPDX license identifiers, based on their ASF categories (see
  `category-comparator`)."
  [l r]
  (compare (get category-order (license-category l) 99) (get category-order (license-category r) 99)))

(defn- expression-category-impl
  "Internal implementation of expression-category."
  [parsed-expr]
  (cond
    (sequential? parsed-expr)
      (let [op         (first parsed-expr)
            exprs      (rest  parsed-expr)
            categories (sort-by category-order (distinct (map expression-category-impl exprs)))]
        (case op
          :or  (first categories)
          :and (last  categories)))

    (map? parsed-expr)
      (license-category (first (sexp/extract-ids parsed-expr)))))

(defn expression-category
  "Given an SPDX license expression, returns the overall ASF category of that
  expression.  Results are as for `category`."
  [spdx-expr]
  (when-let [parsed-expr (sexp/parse spdx-expr)]
    (expression-category-impl parsed-expr)))

(defn least-category
  "Returns the lowest (best) category in the given sequence of categories."
  [categories]
  (when (seq categories)
    (first (sort-by category-order categories))))

(defn most-category
  "Returns the highest (worst) category for the given sequence of categories."
  [categories]
  (when (seq categories)
    (last (sort-by category-order categories))))

(defn licenses-least-category
  "Returns the lowest (best) category in the given sequence of SPDX license
  identifiers."
  [license-ids]
  (when (seq license-ids)
    (least-category (distinct (filter identity (map license-category (distinct (seq license-ids))))))))

(defn licenses-most-category
  "Returns the highest (worst) category for the given sequence of SPDX license
  identifiers."
  [license-ids]
  (when (seq license-ids)
    (most-category (distinct (filter identity (map license-category (distinct (seq license-ids))))))))

(defn expressions-least-category
  "Returns the lowest (best) category in the given sequence of SPDX license
  expressions."
  [expressions]
  (when (seq expressions)
    (least-category (distinct (filter identity (map expression-category (distinct (seq expressions))))))))

(defn expressions-most-category
  "Returns the highest (worst) category for the given sequence of SPDX license
  expressions."
  [expressions]
  (when (seq expressions)
    (most-category (distinct (filter identity (map expression-category (distinct (seq expressions))))))))
