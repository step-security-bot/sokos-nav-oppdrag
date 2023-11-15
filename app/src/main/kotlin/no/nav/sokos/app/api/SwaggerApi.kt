package no.nav.sokos.app.api

import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Routing.swaggerApi() {
    swaggerUI(path = "api/v1/docs", swaggerFile = "openapi/sokos-nav-oppdrag-v1-swagger.yaml")
}