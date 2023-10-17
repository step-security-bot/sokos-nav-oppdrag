plugins {
    kotlin("plugin.serialization") version "1.9.10"
}

val ktorVersion = "2.3.5"
val prometheusVersion = "1.11.5"
val natpryceVersion = "1.6.10.0"
val kotlinLoggingVersion = "3.0.5"
val janionVersion = "3.1.10"
val logbackVersion = "1.4.11"
val logstashVersion = "7.4"
val papertrailappVersion = "1.0.0"
val jacksonVersion = "2.15.3"

dependencies {

    // Ktor server
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")

    // Ktor client
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache-jvm:$ktorVersion")

    // Security
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // Monitorering
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")

    // Config
    implementation("com.natpryce:konfig:$natpryceVersion")

    // Jackson
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    runtimeOnly("org.codehaus.janino:janino:$janionVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    runtimeOnly("com.papertrailapp:logback-syslog4j:$papertrailappVersion")

    // Modules
    implementation(project(":oppdragsinfo"))
    implementation(project(":venteregister"))
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("app")
        manifest {
            attributes["Main-Class"] = "no.nav.sokos.app.ApplicationKt"
            attributes["Class-Path"] =
                configurations.runtimeClasspath.get().joinToString(separator = " ") {
                    it.name
                }
        }
        doLast {
            configurations.runtimeClasspath.get().forEach {
                val fileProvider: Provider<RegularFile> = layout.buildDirectory.file("libs/${it.name}")
                val targetFile = File(fileProvider.get().toString())
                if (!targetFile.exists()) {
                    it.copyTo(targetFile)
                }
            }
        }
    }
}
