plugins {
    application
    id("org.springframework.boot") version ("3.+")
    kotlin("jvm")
    jacoco
    id("io.github.wakingrufus.elo.deploy")
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(libs.funk.htmx)
    implementation(libs.funk.webmvc)
    implementation(libs.spring.boot.jetty)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.actuator)
    implementation("com.github.wakingrufus:lib-elo:0.4.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation(platform(libs.spring.bom))
    implementation(libs.spring.security.web)

    testImplementation(libs.spring.boot.test)
    testImplementation("org.springframework.boot:spring-boot-starter-webflux:3.3.1")
    testImplementation(libs.spring.security.test)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass = "io.github.wakingrufus.elo.EloApplicationKt"
}