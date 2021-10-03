# Contribution guide

Welcome and thanks for thinking about contributing to Klogging.

This guide is a work in progress.

## What to contribute?

Have a look at the [current issues](https://github.com/klogging/klogging/issues),
or create a new issue or [pull request](https://github.com/klogging/klogging/pulls)
if you see something that needs attention.

You can also create a [pull request](https://github.com/klogging/klogging.io/pulls)
with changes to [Klogging’s documentation](https://github.com/klogging/klogging.io)
if you see something that needs fixing or changing.

## How to get started?

Prerequisites:

- Java version 11 or above.
- Git

> Klogging currently only supports building for the JVM.

Run tests with:

```shell
./gradlew jvmTest
```

Fix linting with:

```shell
./gradlew spotlessApply
```

Build the library jar with:

```shell
./gradlew jvmJar
```

## Pull request guidelines

- All commits must be signed.

- All code should have passing unit tests in the `jvmTest` module.

- All code should have comprehensive [KDoc](https://kotlinlang.org/docs/kotlin-doc.html)
  comments. We [publish all KDoc](https://dokka.klogging.io), including non-public code.

- Please limit PRs to one bugfix or feature.
