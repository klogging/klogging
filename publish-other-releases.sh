#!/usr/bin/env bash

# Publish releases of libraries dependent on klogging-jvm-x.x.x.jar
# They are not being published by GitHub Actions yet

./gradlew :slf4j-klogging:publishJvmPublicationToReleasesRepository && \
  ./gradlew :klogging-spring-boot-starter:publishPomPublicationToReleasesRepository && \
  ./gradlew :jdk-platform-klogging:publishJvmPublicationToReleasesRepository && \
  ./gradlew :hexagonkt-klogging-adapter:publishJvmPublicationToReleasesRepository
