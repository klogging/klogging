# slf4j-klogging

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/klogging/slf4j-klogging/actions/workflows/build.yml/badge.svg)](https://github.com/klogging/klogging/actions/workflows/build.yml)

A simple [SLF4J](https://www.slf4j.org) binding to use with Klogging.

⚠️ **Please use version 0.3.0. The build is broken: Gradle is not assembling the JARs correctly.** ⚠️️ 

For more details, please see [the documentation](https://klogging.io/docs/java/slf4j)
and using [Klogging with Spring Boot via SLF4J](https://klogging.io/docs/java/spring-boot).

## Quick start

Specify this library as the dependency. Gradle:

```kotlin
    implementation("io.klogging:slf4j-klogging:0.3.0")
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>slf4j-klogging</artifactId>
        <version>0.3.0</version>
    </dependency>
</dependencies>
```

This binding does not currently support Markers.
