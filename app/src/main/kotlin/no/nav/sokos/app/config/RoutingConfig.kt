package no.nav.sokos.app.config

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import no.nav.sokos.app.ApplicationState
import no.nav.sokos.app.api.metricsApi
import no.nav.sokos.app.api.naisApi
import no.nav.sokos.app.api.swagger.oppdragsInfoSwaggerApi
import no.nav.sokos.oppdragsinfo.api.oppdragsInfoApi
import no.nav.sokos.venteregister.api.venteregisterApi

fun Application.routingConfig(
    applicationState: ApplicationState,
    useAuthentication: Boolean
) {
    install(Resources)
    routing {
        naisApi({ applicationState.initialized }, { applicationState.running })
        metricsApi()
        oppdragsInfoSwaggerApi()
        authenticate(useAuthentication, AUTHENTICATION_NAME) {
            oppdragsInfoApi()
            venteregisterApi()
        }
    }
}

fun Route.authenticate(useAuthentication: Boolean, authenticationProviderId: String? = null, block: Route.() -> Unit) {
    if (useAuthentication) authenticate(authenticationProviderId) { block() } else block()
}