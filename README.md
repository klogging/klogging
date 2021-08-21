# slf4j-klogging

A simple [SLF4J](https://www.slf4j.org) binding to use with Klogging.

For more details, please see [the documentation](https://klogging.io/docs/java/slf4j).

## Quick start

Currently you need to specify all three imports. Gradle:

```kotlin
    implementation("io.klogging:slf4j-klogging:0.1.0")
    implementation("io.klogging:klogging-jvm:0.3.0")
    implementation("org.slf4j:slf4j-api:1.7.32")
```

Maven:

```xml
<dependencies>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>klogging-jvm</artifactId>
        <version>0.3.0</version>
    </dependency>
    <dependency>
        <groupId>io.klogging</groupId>
        <artifactId>slf4j-klogging</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.32</version>
    </dependency>
</dependencies>
```

This binding does not support Markers.
