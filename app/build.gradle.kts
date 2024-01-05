val ktorVersion = "2.3.7"
val janionVersion = "3.1.11"
val logbackVersion = "1.4.14"
val logstashVersion = "7.4"
val papertrailappVersion = "1.0.0"
val konsistVersion = "0.13.0"
val swaggerRequestValidatorVersion = "2.39.0"
val mockOAuth2ServerVersion = "2.1.0"

dependencies {

    // Ktor server
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    // Monitorering
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-host-common-jvm:2.3.7")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.7")

    // Logging
    runtimeOnly("org.codehaus.janino:janino:$janionVersion")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    runtimeOnly("com.papertrailapp:logback-syslog4j:$papertrailappVersion")

    // Test
    testImplementation("com.lemonappdev:konsist:$konsistVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:$swaggerRequestValidatorVersion")
    
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
                }.plus(" /var/run/secrets/db2license/db2jcc_license_cisuz.jar")
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
