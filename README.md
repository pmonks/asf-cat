| | | |
|---:|:---:|:---:|
| [**main**](https://github.com/pmonks/asf-cat/tree/main) | [![CI](https://github.com/pmonks/asf-cat/workflows/CI/badge.svg?branch=main)](https://github.com/pmonks/asf-cat/actions?query=workflow%3ACI+branch%3Amain) | [![Dependencies](https://github.com/pmonks/asf-cat/workflows/dependencies/badge.svg?branch=main)](https://github.com/pmonks/asf-cat/actions?query=workflow%3Adependencies+branch%3Amain) |
| [**dev**](https://github.com/pmonks/asf-cat/tree/dev) | [![CI](https://github.com/pmonks/asf-cat/workflows/CI/badge.svg?branch=dev)](https://github.com/pmonks/asf-cat/actions?query=workflow%3ACI+branch%3Adev) | [![Dependencies](https://github.com/pmonks/asf-cat/workflows/dependencies/badge.svg?branch=dev)](https://github.com/pmonks/asf-cat/actions?query=workflow%3Adependencies+branch%3Adev) |

[![Latest Version](https://img.shields.io/clojars/v/com.github.pmonks/asf-cat)](https://clojars.org/com.github.pmonks/asf-cat/) [![Open Issues](https://img.shields.io/github/issues/pmonks/asf-cat.svg)](https://github.com/pmonks/asf-cat/issues) [![License](https://img.shields.io/github/license/pmonks/asf-cat.svg)](https://github.com/pmonks/asf-cat/blob/main/LICENSE)

<img alt="asf-cat logo: Apache Software Foundation feather logo with a cat in silhouette reaching towards it" align="right" width="25%" src="https://raw.githubusercontent.com/pmonks/asf-cat/main/asf-cat-logo.png">

# asf-cat

A micro library that provides a Clojure implementation of the [Apache Software Foundation's 3rd Party License Policy](https://www.apache.org/legal/resolved.html).

## Using the library

### Documentation

[API documentation is available here](https://pmonks.github.io/asf-cat/).

[An FAQ is available here](https://github.com/pmonks/asf-cat/wiki/FAQ).

### Dependency

Express the correct maven dependencies in your `deps.edn`:

```edn
{:deps {com.github.pmonks/asf-cat {:mvn/version "LATEST_CLOJARS_VERSION"}}}
```

### Require the namespace

```clojure
(ns your.ns
  (:require [asf-cat.api :as asf]))
```

## Contributor Information

[Contributing Guidelines](https://github.com/pmonks/asf-cat/blob/main/.github/CONTRIBUTING.md)

[Bug Tracker](https://github.com/pmonks/asf-cat/issues)

[Code of Conduct](https://github.com/pmonks/asf-cat/blob/main/.github/CODE_OF_CONDUCT.md)

### Developer Workflow

This project uses the [git-flow branching strategy](https://nvie.com/posts/a-successful-git-branching-model/), with the caveat that the permanent branches are called `main` and `dev`, and any changes to the `main` branch are considered a release and auto-deployed (JARs to Clojars, API docs to GitHub Pages, etc.).

For this reason, **all development must occur either in branch `dev`, or (preferably) in temporary branches off of `dev`.**  All PRs from forked repos must also be submitted against `dev`; the `main` branch is **only** updated from `dev` via PRs created by the core development team.  All other changes submitted to `main` will be rejected.

## License

Copyright © 2021 Peter Monks

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0)

The Apache "feather" logo is trademarked by the Apache Software Foundation and distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

The Clip Art Silhouette Of A Cat Reaching Into The Sky image is distributed under the terms of the license documented at [animalclipart.net](https://www.animalclipart.net/animal_clipart_images/clip_art_silhouette_of_a_cat_reaching_into_the_sky_0071-1002-1223-4660.html).
