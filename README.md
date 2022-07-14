<a href="http://artipie.com"><img src="https://www.artipie.com/logo.svg" width="64px" height="64px"/></a>

[![Join our Telegramm group](https://img.shields.io/badge/Join%20us-Telegram-blue?&logo=telegram&?link=http://right&link=http://t.me/artipie)](http://t.me/artipie)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/artipie/cargo-adapter)](http://www.rultor.com/p/artipie/cargo-adapter)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Javadoc](http://www.javadoc.io/badge/com.artipie/cargo-adapter.svg)](http://www.javadoc.io/doc/com.artipie/cargo-adapter)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/artipie/cargo-adapter/blob/master/LICENSE.txt)
[![codecov](https://codecov.io/gh/artipie/cargo-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/artipie/cargo-adapter)
[![Hits-of-Code](https://hitsofcode.com/github/artipie/cargo-adapter)](https://hitsofcode.com/view/github/artipie/cargo-adapter)
[![Maven Central](https://img.shields.io/maven-central/v/com.artipie/cargo-adapter.svg)](https://maven-badges.herokuapp.com/maven-central/com.artipie/cargo-adapter)
[![PDD status](http://www.0pdd.com/svg?name=artipie/cargo-adapter)](http://www.0pdd.com/p?name=artipie/cargo-adapter)

This Java library turns binary storage (files, S3 objects, anything) into Cargo repository - 
Rust Package Registry. It is a part of [Artipie](https://github.com/artipie) binary artifact management tool 
and provides a fully-functionable Rust Registry, which [cargo](https://doc.rust-lang.org/book/ch01-03-hello-cargo.html) can perfectly understand.

Some valuable references and need-to-know:
- Cargo is a [Rust](https://www.rust-lang.org/) package manager. Cargo manages project dependencies, 
compile sources and distribute arttifdacts to repositories. 
- Rust [package](https://doc.rust-lang.org/cargo/appendix/glossary.html#package)
- Cargo [guide](https://doc.rust-lang.org/cargo/guide/index.html).
- Cargo [reference](https://doc.rust-lang.org/cargo/reference/index.html).
- Default Cargo repository is https://crates.io, Sources of crates.io: https://github.com/rust-lang/crates.io
- Start Crates with docker-compose: https://github.com/rust-lang/crates.io/blob/master/docs/CONTRIBUTING.md#running-cratesio-with-docker

If you have any question or suggestions, do not hesitate to [create an issue](https://github.com/artipie/cargo-adapter/issues/new) or contact us in
[Telegram](https://t.me/artipie).  
Artipie [roadmap](https://github.com/orgs/artipie/projects/3).

# Repository structure
Repository indexes are stored in the git repository, which allows cargo efficiently fetch incremental updates. 
In the repository root `config.json` should be located, this file contains the info for accessing the 
registry:
```json
{
    "dl": "https://crates.io/api/v1/crates",
    "api": "https://crates.io"
}
```
where `dl` is the url for downloading crates and `api` is the base URL for the web API, 
`api` is optional, but without it commands such `cargo publish` will not work.

Download url should send `.crate` file for the requested package, package name and version are appended 
to the base url along with `download` word: `{package_name}/{package_version}/download`. `dl` value
also may have markers in the url, find more information [here](https://doc.rust-lang.org/cargo/reference/registries.html#index-format).

The rest of the repository index contains one file for each package, where each version metadata is
presented in json format. The files are organized in a tier of directories by the following rules:
- Packages with 1 char names are placed in a directory named `1`
- Packages with 2 chars names are placed in a directory named `2`
- Packages with 3 chars names are placed in the directory `3/{first-char}` where `{first-char}` is 
the first character of the package name
- All other packages are stored in directories named `{first-two}/{second-two}` where the top directory 
is the first two chars of the package name, and the next subdirectory is the third and fourth chars of the package name

Package metadata file is named with lowercased package name. For more detailed description check the 
[documentation](https://doc.rust-lang.org/cargo/reference/registries.html#index-format).

Format of the package metadata file is the following: each line contains `json` object, that contains
information about published version of the package. Thus, to be placed on one line, this `json` should
not be pretty-printed (should not contain line-brakes or extra spaces between items). 
Here is the example of one line, pretty-printed for better understanding:
```json
{
    // The name of the package.
    "name": "foo",
    // The version of the package this row is describing.
    "vers": "0.1.0",
    // Array of direct dependencies of the package.
    "deps": [
        {
            // Name of the dependency.
            "name": "rand",
            // The semver requirement for this dependency.
            "req": "^0.6",
            // Array of features (as strings) enabled for this dependency.
            "features": ["i128_support"],
            // Boolean of whether or not this is an optional dependency.
            "optional": false,
            // Boolean of whether or not default features are enabled.
            "default_features": true,
            //etc
        }
    ],
    // A SHA256 checksum of the `.crate` file.
    "cksum": "d867001db0e2b6e0496f9fac96930e2d42233ecd3ca0413e0753d4c7695d289c",
    // Set of features defined for the package.
    // Each feature maps to an array of features or dependencies it enables.
    "features": {
        "extras": ["rand/simd_support"]
    },
    // Boolean of whether or not this version has been yanked.
    "yanked": false,
    // The `links` string value from the package's manifest, or null if not
    // specified. This field is optional and defaults to null.
    "links": null
}
```
The full set of package version properties can be found [here](https://doc.rust-lang.org/cargo/reference/manifest.html). 
The only field, that can be modified in the `json` objects is `yanked`, this field should be set 
to `true` with [`cargo yank`](https://doc.rust-lang.org/cargo/commands/cargo-yank.html) command.

# How to contribute
Fork repository, make changes, send us a pull request. We will review your changes and apply them 
to the master branch shortly, provided they don't violate our quality standards. To avoid frustration, 
before sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use Maven 3.2+ and please read [contributing rules](https://github.com/artipie/artipie/blob/master/CONTRIBUTING.md).
