package no.nav.sokos.oppdragsinfo.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.sql.Connection
import io.ktor.server.application.ApplicationCall
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsInfo
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsListe
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.integration.EregService
import no.nav.sokos.oppdragsinfo.integration.TpService
import no.nav.sokos.oppdragsinfo.integration.pdl.PdlService
import no.nav.sokos.oppdragsinfo.integration.pdl.hentperson.Navn
import no.nav.sokos.oppdragsinfo.integration.pdl.hentperson.Person

val applicationCall = mockk<ApplicationCall>()
val db2DataSource = mockk<Db2DataSource>(relaxed = true)
val connection = mockk<Connection>(relaxed = true)
val pdlService = mockk<PdlService>()
val eregService = mockk<EregService>()
val tpService = mockk<TpService>()
val oppdragsInfoService = OppdragsInfoService(
    db2DataSource = db2DataSource,
    pdlService = pdlService,
    eregService = eregService,
    tpService = tpService
)

internal class OppdragsInfoServiceTest : FunSpec({

    beforeEach {
        mockkObject(OppdragsInfoRepository)
        every { db2DataSource.connection } returns connection
    }

    test("test sokOppdrag") {

        val oppdrag = Oppdrag(
            fagsystemId = "12345678901",
            oppdragsId = 1234567890,
            navnFagGruppe = "NAV Arbeid og ytelser",
            navnFagOmraade = "Arbeidsavklaringspenger",
            kjorIdag = "J",
            typeBilag = "B",
            kodeStatus = "A"
        )

        val oppdragsInfo = OppdragsInfo(
            gjelderId = "12345678901"
        )

        val person = Person(
            listOf(
                Navn(
                    fornavn = "Ola",
                    mellomnavn = "Mellomnavn",
                    etternavn = "Nordmann"
                )
            )
        )


        every { applicationCall.request.headers["Authorization"] } returns MockOAuth2Server().tokenFromDefaultProvider()
        every { connection.hentOppdragsInfo("12345678901") } returns listOf(oppdragsInfo)
        every { connection.hentOppdragsListe("12345678901", "") } returns listOf(oppdrag)
        every { pdlService.getPersonNavn(any()) } returns person


        val result = oppdragsInfoService.sokOppdrag("12345678901", "", applicationCall)

        println(result.toString())

        result.first().gjelderId shouldBe "12345678901"
        result.first().gjelderNavn shouldBe "Ola Mellomnavn Nordmann"
        result.first().oppdragsListe?.shouldHaveSize(1)

    }
})

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