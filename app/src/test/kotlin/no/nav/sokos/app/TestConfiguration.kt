import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import no.nav.sokos.app.ApplicationState
import no.nav.sokos.app.api.metricsApi
import no.nav.sokos.app.api.naisApi
import no.nav.sokos.app.config.commonConfig
import no.nav.sokos.oppdragsinfo.api.oppdragsInfoSwaggerApi

internal const val OPPDRAGSINFO_BASE_API_PATH = "/api/v1/oppdragsinfo"

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