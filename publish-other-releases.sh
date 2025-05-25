#!/usr/bin/env bash

# Publish releases of libraries dependent on klogging-jvm-x.x.x.jar
# They are not being published by GitHub Actions yet

./gradlew :slf4j-klogging:publishJvmPublicationToMavenCentralRepository && \
  ./gradlew :klogging-spring-boot-starter:publishAllPublicationsToMavenCentralRepository && \
  ./gradlew :jdk-platform-klogging:publishJvmPublicationToMavenCentralRepository && \
  ./gradlew :hexagonkt-klogging-adapter:publishJvmPublicationToMavenCentralRepository
