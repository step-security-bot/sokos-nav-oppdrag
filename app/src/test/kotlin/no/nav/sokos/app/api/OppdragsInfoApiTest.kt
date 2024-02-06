package no.nav.sokos.app.api

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
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
import no.nav.sokos.oppdragsinfo.api.SokOppdragRequest
import no.nav.sokos.oppdragsinfo.api.oppdragsInfoApi
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService
import org.hamcrest.Matchers.equalTo


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

    test("sokOppdrag med gyldig gjelderId skal returnere 200 OK") {

        val oppdrag = Oppdrag(
            fagsystemId = "12345678901",
            oppdragsId = 1234556,
            navnFagGruppe = "faggruppeNavn",
            navnFagOmraade = "fagomraadeNavn",
            kjorIdag = "kjorIdag",
            typeBilag = "bilagsType",
            kodeStatus = "PASS",
        )

        val sokOppdragResponse = OppdragsInfo(
            gjelderId = "12345678901",
            gjelderNavn = "Test Testesen",
            oppdragsListe = listOf(oppdrag)
        )

        coEvery { oppdragsInfoService.sokOppdrag(any(), any(), any()) } returns listOf(sokOppdragResponse)

        val response = RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SokOppdragRequest(gjelderId = "12345678901", fagGruppeKode = "ABC"))
            .port(PORT)
            .post("$BASE_API_PATH$OPPDRAGSINFO_API_PATH/oppdrag")
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.OK.value)
            .extract()
            .response()

        response.jsonPath().getList<OppdragsInfo>("gjelderId").first().shouldBe("12345678901")
        response.jsonPath().getList<OppdragsInfo>("gjelderNavn").first().shouldBe("Test Testesen")
        response.jsonPath().getList<Oppdrag>("oppdragsListe").shouldHaveSize(1)
    }

    test("sokOppdrag med ugyldig gjelderId skal returnere 400 Bad Request") {

        RestAssured.given()
            .filter(validationFilter)
            .header(HttpHeaders.ContentType, APPLICATION_JSON)
            .header(HttpHeaders.Authorization, "Bearer ${mockOAuth2Server.tokenFromDefaultProvider()}")
            .body(SokOppdragRequest(gjelderId = "123", fagGruppeKode = ""))
            .port(PORT)
            .post("$BASE_API_PATH$OPPDRAGSINFO_API_PATH/oppdrag")
            .then()
            .assertThat()
            .statusCode(HttpStatusCode.BadRequest.value)
            .body("message", equalTo ("gjelderId er ugyldig. Tillatt format er 9 eller 11 siffer"))
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