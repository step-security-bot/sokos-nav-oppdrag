import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

val graphqlClientVersion = "7.0.2"
val mockOAuth2ServerVersion = "2.1.1"

plugins {
    id("com.expediagroup.graphql") version "7.0.2"
}

dependencies {

    testImplementation("no.nav.security:mock-oauth2-server:$mockOAuth2ServerVersion")

    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlClientVersion") {
        exclude("com.expediagroup:graphql-kotlin-client-jackson")
    }
}

graphql {
    client {
        packageName = "no.nav.sokos.oppdragsinfo.integration.pdl"
        schemaFile = file("$projectDir/src/main/resources/pdl/schema.graphql")
        queryFileDirectory = "$projectDir/src/main/resources/pdl"
        serializer = GraphQLSerializer.KOTLINX
    }
}



