package no.nav.sokos.app.api

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.routing
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.RestAssured
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.sokos.app.APPLICATION_JSON
import no.nav.sokos.app.BASE_API_PATH
import no.nav.sokos.app.OPPDRAGSINFO_API_PATH
import no.nav.sokos.app.config.AUTHENTICATION_NAME
import no.nav.sokos.app.config.authenticate
import no.nav.sokos.app.config.commonConfig
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoRequest
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoResponse
import no.nav.sokos.oppdragsinfo.api.oppdragsInfoApi
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

internal const val PORT = 9090

lateinit var server: NettyApplicationEngine

val validationFilter = OpenApiValidationFilter("openapi/oppdragsinfo-v1-swagger.yaml")
val oppdragsInfoService: OppdragsInfoService = mockk()
val mockOAuth2Server = MockOAuth2Server()

internal class OppdragsInfoApiTest : FunSpec({

    beforeEach {
        server = embeddedServer(Netty, PORT, module = Application::myApplicationModule).start()
    }

    afterEach {
        server.stop(1000, 10000)
    }

    test("henter oppdrag med riktig gjelderId") {

        val oppdrag = Oppdrag(
            fagsystemId = "12345678901",
            oppdragsId = 1234556,
            faggruppeNavn = "faggruppeNavn",
            fagomraadeNavn = "fagomraadeNavn",
            kjorIdag = "kjorIdag",
            bilagsType = "bilagsType",
            status = "PASS",
        )

        val oppdragsInfo = OppdragsInfo(
            gjelderId = "12345678901",
            gjelderNavn = "Test Testesen",
            oppdragsListe = listOf(oppdrag)
        )

        val oppdragsInfoResponse = OppdragsInfoResponse(listOf(oppdragsInfo))

        coEvery { oppdragsInfoService.sokOppdrag(any(), any()) } returns oppdragsInfoResponse.data

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(OppdragsInfoRequest(gjelderId = "12345678901"))
            .port(PORT)
            .post("$BASE_API_PATH$OPPDRAGSINFO_API_PATH/sokOppdrag")
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.body().`as`(OppdragsInfoResponse::class.java) shouldBe oppdragsInfoResponse
        response.body().`as`(OppdragsInfoResponse::class.java).data[0].gjelderId shouldBe "12345678901"

    }

})

private fun Application.myApplicationModule() {
    commonConfig()
    routing {
        authenticate(false, AUTHENTICATION_NAME) {
            oppdragsInfoApi(oppdragsInfoService)
        }
    }
}

private fun MockOAuth2Server.tokenFromDefaultProvider() =
    issueToken(
        issuerId = "default",
        clientId = "default",
        tokenCallback = DefaultOAuth2TokenCallback(
            claims = mapOf(
                "NAVident" to "Z123456"
            )
        )
    ).serialize()