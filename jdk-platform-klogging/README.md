# JDK Platform Logging implementation that uses Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build-slf4j-klogging.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build-slf4j-klogging.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/slf4j-klogging.svg?label=maven%20central)](https://central.sonatype.com/search?smo=true&q=io.klogging%3Aslf4j-klogging)

A [Java `System.Logger`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/System.Logger.html)
implementation using Klogging. `System.Logger` was introduced in Java 9 and this implementation requires Java 11
or later.

## Quick start

Specify this library as the dependency. Gradle:

```kotlin
    implementation("io.klogging:jdk-platform-klogging:0.9.3")
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>jdk-platform-klogging</artifactId>
        <version>0.9.3</version>
    </dependency>
</dependencies>
```
