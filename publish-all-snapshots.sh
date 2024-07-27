#!/usr/bin/env bash

# Publish all snapshots of libraries in order
# They are not being published correctly by GitHub Actions yet

./gradlew :klogging:publishJvmPublicationToSnapshotsRepository && \
  ./gradlew :slf4j-klogging:publishJvmPublicationToSnapshotsRepository && \
  ./gradlew :klogging-spring-boot-starter:publishPomPublicationToSnapshotsRepository && \
  ./gradlew :jdk-platform-klogging:publishJvmPublicationToSnapshotsRepository && \
  ./gradlew :hexagonkt-klogging-adapter:publishJvmPublicationToSnapshotsRepository
