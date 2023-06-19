package io.klogging.build

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.getByType

val Project.publishing: PublishingExtension
    get() = extensions.getByType()

fun getEnvironmentVariableOrThrow(name: String): String = System.getenv().getOrElse(name) {
    throw RuntimeException("Environment variable '$name' not set.")
}
