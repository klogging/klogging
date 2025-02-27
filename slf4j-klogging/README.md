# SLF4J provider for Klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/klogging/actions/workflows/build-slf4j-klogging.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build-slf4j-klogging.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.klogging/slf4j-klogging.svg?label=maven%20central)](https://central.sonatype.com/search?smo=true&q=io.klogging%3Aslf4j-klogging)

An [SLF4J](https://www.slf4j.org) provider to use with Klogging.

For more details, please see [the Klogging SLF4J documentation](https://klogging.io/docs/java/slf4j)
and [using Klogging with Spring Boot via SLF4J](https://klogging.io/docs/java/spring-boot).

## Quick start

Specify this library as the dependency. Gradle:

```kotlin
    implementation("io.klogging:slf4j-klogging:0.9.3")
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>slf4j-klogging</artifactId>
        <version>0.9.3</version>
    </dependency>
</dependencies>
```

This provider does not support Markers.
