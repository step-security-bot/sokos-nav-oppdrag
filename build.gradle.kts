import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version "1.9.20"
}

val ktorVersion = "2.3.6"
val kotestVersion = "5.8.0"
val mockkVersion = "1.13.8"

allprojects {
    group = "no.nav.sokos"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {

        // Ktor server
        implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")

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
