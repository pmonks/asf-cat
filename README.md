| | | |
|---:|:---:|:---:|
| [**release**](https://github.com/pmonks/asf-cat/tree/release) | [![CI](https://github.com/pmonks/asf-cat/actions/workflows/ci.yml/badge.svg?branch=release)](https://github.com/pmonks/asf-cat/actions?query=workflow%3ACI+branch%3Arelease) | [![Dependencies](https://github.com/pmonks/asf-cat/actions/workflows/dependencies.yml/badge.svg?branch=release)](https://github.com/pmonks/asf-cat/actions?query=workflow%3Adependencies+branch%3Arelease) |
| [**dev**](https://github.com/pmonks/asf-cat/tree/dev) | [![CI](https://github.com/pmonks/asf-cat/actions/workflows/ci.yml/badge.svg?branch=dev)](https://github.com/pmonks/asf-cat/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/asf-cat/actions/workflows/dependencies.yml/badge.svg?branch=dev)](https://github.com/pmonks/asf-cat/actions?query=workflow%3Adependencies+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/asf-cat)](https://clojars.org/com.github.pmonks/asf-cat/) [![License](https://img.shields.io/github/license/pmonks/asf-cat.svg)](https://github.com/pmonks/asf-cat/blob/release/LICENSE) [![Open Issues](https://img.shields.io/github/issues/pmonks/asf-cat.svg)](https://github.com/pmonks/asf-cat/issues) [![Vulnerabilities](https://github.com/pmonks/asf-cat/workflows/vulnerabilities/badge.svg)](https://pmonks.github.io/asf-cat/nvd/dependency-check-report.html)

<img alt="asf-cat logo: Apache Software Foundation feather logo with a cat in silhouette reaching towards it" align="right" width="25%" src="https://raw.githubusercontent.com/pmonks/asf-cat/main/asf-cat-logo.png">

# asf-cat

A micro library that provides a Clojure implementation of the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## System Requirements

This library uses [`lice-comb`](https://github.com/pmonks/lice-comb), so has the same system requirements as that library.

## Installation

`asf-cat` is available as a Maven artifact from [Clojars](https://clojars.org/com.github.pmonks/asf-cat).

### Trying it Out

#### Clojure CLI

```shell
$ clj -Sdeps '{:deps {com.github.pmonks/asf-cat {:mvn/version "RELEASE"}}}'
```

#### Leiningen

```shell
$ lein try com.github.pmonks/asf-cat
```

#### deps-try

```shell
$ deps-try com.github.pmonks/asf-cat
```

### Demo

```clojure
(require '[asf-cat.api :as asf])

;; Checking the category of a single SPDX license identifier
(asf/license-category "Apache-2.0")
;=> :category-a

(asf/license-category "EPL-1.0")
;=> :category-b

(asf/license-category "GPL-3.0")
;=> :category-x

(asf/license-category "invalid-license-identifier")
;=> :uncategorised

;; Checking the category of an SPDX license expression
(asf/expression-category "Apache-2.0 OR GPL-3.0 WITH Classpath-exception-2.0")
;=> :category-a

(asf/expression-category "Apache-2.0 AND GPL-3.0 WITH Classpath-exception-2.0")
;=> :category-x

;; lice-comb specific LicenseRefs
(asf/license-category "LicenseRef-lice-comb-PUBLIC-DOMAIN")
;=> :category-a-special

(asf/license-category "LicenseRef-lice-comb-PROPRIETARY-COMMERCIAL")
;=> :category-x
```

### Documentation

[API documentation is available here](https://pmonks.github.io/asf-cat/).

[An FAQ is available here](https://github.com/pmonks/asf-cat/wiki/FAQ).

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/asf-cat/blob/release/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/asf-cat/issues)

[Code of Conduct](https://github.com/pmonks/asf-cat/blob/release/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), and the permanent branches are called `release` and `dev`.  Any changes to the `release` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `release` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `release` will be rejected.

### Build Tasks

`asf-cat` uses [`tools.build`](https://clojure.org/guides/tools_build). You can get a list of available tasks by running:

```
clojure -A:deps -T:build help/doc
```

Of particular interest are:

* `clojure -T:build test` - run the unit tests
* `clojure -T:build lint` - run the linters (clj-kondo and eastwood)
* `clojure -T:build ci` - run the full CI suite (check for outdated dependencies, run the unit tests, run the linters)
* `clojure -T:build install` - build the JAR and install it locally (e.g. so you can test it with downstream code)

Please note that the `deploy` task is restricted to the core development team (and will not function if you run it yourself).

## License

Copyright © 2021 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)

The Apache "feather" logo is trademarked by the Apache Software Foundation and distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

The Clip Art Silhouette Of A Cat Reaching Into The Sky image is distributed under the terms of the license documented at [animalclipart.net](https://www.animalclipart.net/animal_clipart_images/clip_art_silhouette_of_a_cat_reaching_into_the_sky_0071-1002-1223-4660.html).
