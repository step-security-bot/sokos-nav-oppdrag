import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
}

// Ktor
val ktorVersion = "2.3.7"

// Monitorering
val prometheusVersion = "1.12.1"

// Serialization
val kotlinxSerializationVersion = "1.6.2"

// Config
val natpryceVersion = "1.6.10.0"

// Logging
val kotlinLoggingVersion = "3.0.5"

// Database
val hikariVersion = "5.1.0"
val db2JccVersion = "11.5.9.0"

// Test
val kotestVersion = "5.8.0"
val mockkVersion = "1.13.8"

allprojects {
    group = "no.nav.sokos"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
        maven { url = uri("https://jitpack.io") }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {

        // Ktor server
        implementation("io.ktor:ktor-server-swagger:$ktorVersion")
        implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
        implementation("io.ktor:ktor-server-request-validation:$ktorVersion")

        // Ktor client
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")

        // Security
        implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
        implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

        // Monitorering
        implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

        // Serialization
        implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinxSerializationVersion")

        // Config
        implementation("com.natpryce:konfig:$natpryceVersion")

        // Logging
        implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

        // Database
        implementation("com.zaxxer:HikariCP:$hikariVersion")
        implementation("com.ibm.db2:jcc:$db2JccVersion")

        // Test
        testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")

    }
}

subprojects {

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks {
        withType<Test>().configureEach {
            useJUnitPlatform()

            testLogging {
                showExceptions = true
                showStackTraces = true
                exceptionFormat = TestExceptionFormat.FULL
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            }

            maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
            reports.forEach { report -> report.required.value(false) }
        }

        withType<Wrapper> {
            gradleVersion = "8.4"
        }
    }
}

tasks.jar {
    enabled = false
}
