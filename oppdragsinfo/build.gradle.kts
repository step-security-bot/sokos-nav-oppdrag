import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer

val graphqlClientVersion = "7.0.2"

plugins {
    id("com.expediagroup.graphql") version "7.0.2"
}

dependencies {
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



