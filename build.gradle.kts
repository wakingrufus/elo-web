plugins {
    kotlin("jvm") version ("2.1.20") apply (false)
    id("jacoco-report-aggregation")
}

tasks.wrapper {
    gradleVersion = "8.14"
    distributionType = Wrapper.DistributionType.BIN
}

allprojects {
    group = "io.github.wakingrufus"
}

repositories {
    mavenCentral()
}

dependencies {
    subprojects.forEach {
        jacocoAggregation(project(":" + it.name))
    }
}
reporting {
    reports {
        val testCodeCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName.set("test")
        }
    }
}
tasks.register<Delete>("clean") {
    setDelete(layout.buildDirectory)
}