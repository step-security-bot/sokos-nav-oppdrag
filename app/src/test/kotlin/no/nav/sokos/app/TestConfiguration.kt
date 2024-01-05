package no.nav.sokos.app

import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.app.api.metricsApi
import no.nav.sokos.app.api.naisApi
import no.nav.sokos.app.api.swagger.oppdragsInfoSwaggerApi
import no.nav.sokos.app.config.commonConfig

const val APPLICATION_JSON = "application/json"
internal const val BASE_API_PATH = "/api/v1"

internal const val OPPDRAGSINFO_API_PATH = "/oppdragsinfo"

fun ApplicationTestBuilder.configureTestApplication() {
    val mapApplicationConfig = MapApplicationConfig()
    environment {
        config = mapApplicationConfig
    }

    application {
        commonConfig()
        val applicationState = ApplicationState(ready = true)

        routing {
            naisApi({ applicationState.initialized }, { applicationState.running })
            metricsApi()
            oppdragsInfoSwaggerApi()
        }
    }
}