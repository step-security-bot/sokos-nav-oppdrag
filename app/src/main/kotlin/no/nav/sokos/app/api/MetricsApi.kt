package no.nav.sokos.app.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.prometheus.client.exporter.common.TextFormat
import no.nav.sokos.app.metrics.prometheusMeterRegistry

fun Routing.metricsApi() {
    route("metrics") {
        get {
            call.respondText(ContentType.parse(TextFormat.CONTENT_TYPE_004)) { prometheusMeterRegistry.scrape() }
        }
    }
}