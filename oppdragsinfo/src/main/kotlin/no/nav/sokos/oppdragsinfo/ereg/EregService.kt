package no.nav.sokos.oppdragsinfo.ereg

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.ereg.model.Organisasjon
import no.nav.sokos.oppdragsinfo.metrics.eregCallCounter
import no.nav.sokos.oppdragsinfo.util.RetryException
import no.nav.sokos.oppdragsinfo.util.defaultHttpClient
import no.nav.sokos.oppdragsinfo.util.retry
import org.slf4j.MDC

class EregService(
    private val eregHost: String = PropertiesConfig.EksterneHost().eregHost,
    private val httpClient: HttpClient = defaultHttpClient
) {

    suspend fun hentOrganisasjonsNavn(organisasjonsnummer: String): Organisasjon =
        retry {
            try {
                httpClient.get("$eregHost/ereg/api/v1/organisasjon/$organisasjonsnummer/noekkelinfo") {
                    header("Nav-Call-Id", MDC.get("x-correlation-id"))
                }
            } catch (ex: Exception) {
                logger.error(ex) { "Feil oppstått ved oppslag av $organisasjonsnummer i Ereg." }
                throw RetryException(ex)
            }
        }.let { response ->
            eregCallCounter.labels("${response.status.value}").inc()
            when (response.status.value) {
                200 -> {
                    response.body<Organisasjon>()
                }

                400 -> throw BadRequestException(
                    "Ugyldig(e) parameter(e) i forespørsel: ${response.eregFeilmelding()}"
                )

                404 -> throw NotFoundException()
                else -> {
                    throw Exception(
                        "Noe gikk galt! Statuskode: ${response.status.value}, melding: ${response.eregFeilmelding()}"
                    )
                }
            }
        }
}

suspend fun HttpResponse.eregFeilmelding() = body<JsonElement>().jsonObject["melding"]?.toString()