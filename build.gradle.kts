import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"

    id("com.bmuschko.docker-remote-api") version "9.0.1"
    id("com.avast.gradle.docker-compose") version "0.16.12"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.springframework.boot") version "3.1.1"
}

val dockerRepository = "lexluthor421"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks {
    bootJar {
        archiveClassifier.set("boot")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
        }
    }

    register<DockerBuildImage>("dockerBuildImage") {
        group = "Docker"
        description = "Builds and tags the output Docker image using the current environment architecture."
        inputDir.set(projectDir)
        images.add("$dockerRepository/${project.name}:${project.version}")
        buildArgs.put("JAR_FILE", bootJar.get().archiveFileName.get())
        dependsOn(bootJar)
    }

    register<Exec>("dockerBuildxCreate") {
        group = "Docker"
        description = "Creates a new builder instance using the buildx driver."
        workingDir = projectDir
        executable = "docker"
        args("buildx", "create", "--platform", "linux/amd64,linux/arm64", "--use")
    }

    register<Exec>("dockerBuildxImage") {
        group = "Docker"
        description = "Builds and tags the output Docker image in all target platforms using buildx."
        workingDir = projectDir
        executable = "docker"
        args(
            buildList {
                add("buildx")
                add("build")
                add("--platform")
                add("linux/amd64,linux/arm64")
                add("-t")
                add("$dockerRepository/${project.name}:${project.version}")
                add("-f")
                add("Dockerfile-buildx")
                add("--push")
                add(".")
            }
        )
        dependsOn(bootJar)
    }
}

dockerCompose {
    removeOrphans.set(true)
    waitForHealthyStateTimeout.set(2.minutes.toJavaDuration())
}
