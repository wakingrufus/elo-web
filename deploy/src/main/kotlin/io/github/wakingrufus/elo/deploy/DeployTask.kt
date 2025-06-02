package io.github.wakingrufus.elo.deploy

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import software.amazon.awssdk.http.crt.AwsCrtHttpClient
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionSetting
import software.amazon.awssdk.services.elasticbeanstalk.model.EnvironmentDescription
import software.amazon.awssdk.services.elasticbeanstalk.model.EnvironmentStatus
import software.amazon.awssdk.services.elasticbeanstalk.model.S3Location
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import kotlin.io.path.Path

abstract class DeployTask : DefaultTask() {

    @get:InputFile
    abstract val jar: RegularFileProperty

    @get:Input
    abstract val environment: Property<String>

    @TaskAction
    fun run() {
        ElasticBeanstalkClient.builder().httpClient(AwsCrtHttpClient.builder().build()).build().use { client ->
            val s3Bucket = client.createStorageLocation {}.s3Bucket()
            val s3Key = "elo-app-" + project.version + ".jar"
            S3Client.builder().httpClient(AwsCrtHttpClient.builder().build()).build().use { s3 ->
                s3.putObject(
                    PutObjectRequest.builder().bucket(s3Bucket).key(s3Key).build(),
                    Path(jar.get().asFile.absolutePath)
                )
                logger.lifecycle("Boot jar uploaded")
            }

            val apps = client.describeApplications { request -> request.applicationNames("elo") }
            if (apps.applications().isEmpty()) {
                logger.lifecycle("elo application not found. Creating")
                val createResponse = client.createApplication { createRequest ->
                    createRequest.applicationName("elo").description("ELO league")
                }
                logger.lifecycle("elo application ${createResponse.application().applicationArn()} created")
            } else {
                logger.lifecycle("elo application ${apps.applications().first().applicationArn()} found")
            }

            val env = client.describeEnvironments {
                it.applicationName("elo").environmentNames(environment.get())
            }

            if (env.environments().none { !it.status().equals(EnvironmentStatus.TERMINATED) }) {
                logger.lifecycle("env '${environment.get()}' not found, creating")
                client.createEnvironment {
                    it.applicationName("elo")
                        .description("ELO league")
                        .versionLabel(project.version.toString())
                        .environmentName(environment.get())
                        .optionSettings(environmentConfig())
                        .solutionStackName("64bit Amazon Linux 2023 v4.5.2 running Corretto 21")
                }.also {
                    logger.lifecycle("Created environment $it")
                }
            }

            while (getActiveEnv(client)?.let { it.status() != EnvironmentStatus.READY } == true) {
                logger.lifecycle("waiting 30s for env to be READY")
                Thread.sleep(30_000)
            }

            client.updateEnvironment {
                logger.lifecycle("updating environment configuration")
                it.applicationName("elo")
                    .environmentName(environment.get())
                    .optionSettings(environmentConfig())
            }

            while (getActiveEnv(client)?.let { it.status() != EnvironmentStatus.READY } == true) {
                logger.lifecycle("waiting 30s for env to be READY")
                Thread.sleep(30_000)
            }

            client.createApplicationVersion {
                logger.lifecycle("creating new application version")
                it.applicationName("elo")
                    .versionLabel(project.version.toString())
                    .sourceBundle(S3Location.builder().s3Bucket(s3Bucket).s3Key(s3Key).build())
            }
            client.updateEnvironment {
                it.applicationName("elo").environmentName(environment.get()).versionLabel(project.version.toString())
            }
        }
    }

    private fun option(namespace: String, name: String, value: String): ConfigurationOptionSetting {
        return ConfigurationOptionSetting.builder().namespace(namespace).optionName(name).value(value).build()
    }

    private fun environmentConfig(): List<ConfigurationOptionSetting> =
        listOf(
            option("aws:elasticbeanstalk:application", "Application Healthcheck URL", "/index"),
            option("aws:autoscaling:launchconfiguration", "InstanceType", "t4g.medium"),
            option("aws:autoscaling:launchconfiguration", "IamInstanceProfile", "aws-elasticbeanstalk-ec2-role"),
            option("aws:elasticbeanstalk:environment", "LoadBalancerType", "application"),
            option("aws:elasticbeanstalk:environment:process:default", "HealthCheckPath", "/index"),
            option("aws:elasticbeanstalk:command", "DeploymentPolicy", "TrafficSplitting"),
            option("aws:elasticbeanstalk:healthreporting:system", "SystemType", "enhanced"),
            option("aws:elasticbeanstalk:trafficsplitting", "NewVersionPercent", "15"),
            option("aws:elasticbeanstalk:trafficsplitting", "EvaluationTime", "10")
        )

    private fun getActiveEnv(client: ElasticBeanstalkClient): EnvironmentDescription? {
        return client.describeEnvironments {
            it.applicationName("elo").environmentNames(environment.get())
        }.environments().firstOrNull { !it.status().equals(EnvironmentStatus.TERMINATED) }
    }
}