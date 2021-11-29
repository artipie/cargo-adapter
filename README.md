<img src="https://www.artipie.com/logo.svg" width="64px" height="64px"/>

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/artipie/cargo-adapter)](http://www.rultor.com/p/artipie/cargo-adapter)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Javadoc](http://www.javadoc.io/badge/com.artipie/cargo-adapter.svg)](http://www.javadoc.io/doc/com.artipie/cargo-adapter)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/com.artipie/cargo-adapter/blob/master/LICENSE.txt)
[![codecov](https://codecov.io/gh/artipie/cargo-adapter/branch/master/graph/badge.svg)](https://codecov.io/gh/artipie/cargo-adapter)
[![Hits-of-Code](https://hitsofcode.com/github/artipie/cargo-adapter)](https://hitsofcode.com/view/github/artipie/cargo-adapter)
[![Maven Central](https://img.shields.io/maven-central/v/com.artipie/cargo-adapter.svg)](https://maven-badges.herokuapp.com/maven-central/com.artipie/cargo-adapter)
[![PDD status](http://www.0pdd.com/svg?name=artipie/cargo-adapter)](http://www.0pdd.com/p?name=artipie/cargo-adapter)

This Java library turns your binary storage (files, S3 objects, anything) into Cargo repository - 
Rust Package Registry. You may add it to your binary storage and it will become a fully-functionable 
repository, which [cargo](https://doc.rust-lang.org/book/ch01-03-hello-cargo.html) will perfectly understand.

Some valuable references and need-to-know:
- Cargo is a [Rust](https://www.rust-lang.org/) package manager. Cargo manages project dependencies, 
compile sources and distribute arttifdacts to repositories. 
- Rust [package](https://doc.rust-lang.org/cargo/appendix/glossary.html#package)
- Cargo [guide](https://doc.rust-lang.org/cargo/guide/index.html).
- Cargo [reference](https://doc.rust-lang.org/cargo/reference/index.html).
- Default Cargo repository is https://crates.io, Sources of crates.io: https://github.com/rust-lang/crates.io
- Start Crates with docker-compose: https://github.com/rust-lang/crates.io/blob/master/docs/CONTRIBUTING.md#running-cratesio-with-docker

# Repository index format
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

# How to contribute
Fork repository, make changes, send us a pull request. We will review your changes and apply them 
to the master branch shortly, provided they don't violate our quality standards. To avoid frustration, 
before sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use Maven 3.2+.
