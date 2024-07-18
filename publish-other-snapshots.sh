#!/usr/bin/env bash

# Publish releases of libraries dependent on klogging-jvm-x.x.x.jar
# They are not being published by GitHub Actions yet

./gradlew :slf4j-klogging:publishJvmPublicationToSnapshotsRepository && \
  ./gradlew :klogging-spring-boot-starter:publishPomPublicationToSnapshotsRepository && \
  ./gradlew :jdk-platform-klogging:publishJvmPublicationToSnapshotsRepository && \
  ./gradlew :hexagonkt-klogging-adapter:publishJvmPublicationToSnapshotsRepository
