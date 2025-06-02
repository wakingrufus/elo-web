package io.github.wakingrufus.elo.deploy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register


class DeployPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register<DeployTask>("deploy") {
            jar.set(project.tasks.named<Jar>("bootJar").flatMap { it.archiveFile })
            environment.set("prod")
        }
    }
}