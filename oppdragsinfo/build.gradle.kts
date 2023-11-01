val ktorVersion = "2.3.5"
val swaggerRequestValidatorVersion="2.38.1"
val restAssuredVersion="5.3.2"
val mockOAuth2ServerVersion="2.0.0"
val hikariVersion="5.0.1"
val db2_jcc_version="11.5.8.0"
val jooqVersion="3.18.7"

plugins {
    id("nu.studer.jooq") version "8.2.1"
}

dependencies {

    // Ktor server
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    // Monitorering
    implementation("io.ktor:ktor-server-metrics-jvm:$ktorVersion")

    // Database
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("com.ibm.db2:jcc:$db2_jcc_version")

    // Test
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:$swaggerRequestValidatorVersion")
    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

}