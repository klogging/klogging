# slf4j-klogging

A simple [SLF4J](https://www.slf4j.org) binding to use with Klogging.

For more details, please see [the documentation](https://klogging.io/docs/java/slf4j)
and using [Klogging with Spring Boot via SLF4J](https://klogging.io/docs/java/spring-boot).

## Quick start

Specify this library as the dependency. Gradle:

```kotlin
    implementation("io.klogging:slf4j-klogging:0.2.2")
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>slf4j-klogging</artifactId>
        <version>0.2.2</version>
    </dependency>
</dependencies>
```

This binding does not currently support Markers.
