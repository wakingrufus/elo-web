plugins{
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("software.amazon.awssdk:elasticbeanstalk:2.31.50")
    implementation("software.amazon.awssdk:s3:2.31.50")
    implementation("software.amazon.awssdk:aws-crt-client:2.31.50")
}

gradlePlugin {
    plugins {
        create("deploy") {
            id = "io.github.wakingrufus.elo.deploy"
            implementationClass = "io.github.wakingrufus.elo.deploy.DeployPlugin"
        }
    }
}
