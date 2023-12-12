package no.nav.sokos.app.security

import OPPDRAGSINFO_BASE_API_PATH
import configureTestApplication
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.withMockOAuth2Server
import no.nav.sokos.app.config.AUTHENTICATION_NAME
import no.nav.sokos.app.config.PropertiesConfig
import no.nav.sokos.app.config.authenticate
import no.nav.sokos.app.config.securityConfig
import no.nav.sokos.oppdragsinfo.api.oppdragsInfoApi
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

val oppdragsInfoService: OppdragsInfoService = mockk()

class SecurityTest : FunSpec({

    test("oppdragsinfo - test http POST endepunkt uten token bør returnere 401") {
        withMockOAuth2Server {
            testApplication {
                configureTestApplication()
                this.application {
                    securityConfig(authConfig())
                    routing {
                        authenticate(true, AUTHENTICATION_NAME) {
                            oppdragsInfoApi(oppdragsInfoService)
                        }
                    }
                }
                val response = client.post("$OPPDRAGSINFO_BASE_API_PATH/oppdrag")
                response.status shouldBe HttpStatusCode.Unauthorized
            }
        }
    }
})

private fun MockOAuth2Server.authConfig() =
    PropertiesConfig.AzureAdConfig(
        wellKnownUrl = wellKnownUrl("default").toString(),
        clientId = "default"
    )