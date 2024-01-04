package no.nav.sokos.oppdragsinfo.integration

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.time.ZonedDateTime
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.nav.sokos.oppdragsinfo.config.ApiError
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.integration.model.Organisasjon
import no.nav.sokos.oppdragsinfo.metrics.eregCallCounter
import no.nav.sokos.oppdragsinfo.util.EregException
import no.nav.sokos.oppdragsinfo.util.RetryException
import no.nav.sokos.oppdragsinfo.util.defaultHttpClient
import no.nav.sokos.oppdragsinfo.util.retry
import org.slf4j.MDC

class EregService(
    private val eregHost: String = PropertiesConfig.EksterneHost().eregHost,
    private val httpClient: HttpClient = defaultHttpClient
) {

    suspend fun getOrganisasjonsNavn(organisasjonsNummer: String): Organisasjon =
        retry {
            try {
                logger.info("Henter organisasjonsnavn for $organisasjonsNummer fra Ereg.")
                httpClient.get("$eregHost/v2/organisasjon/$organisasjonsNummer/noekkelinfo") {
                    header("Nav-Call-Id", MDC.get("x-correlation-id"))
                }
            } catch (ex: Exception) {
                logger.error(ex) { "Feil oppstått ved oppslag av $organisasjonsNummer i Ereg." }
                throw RetryException(ex)
            }
        }.let { response ->
            eregCallCounter.labels("${response.status.value}").inc()
            when (response.status.value) {
                200 -> {
                    response.body<Organisasjon>()
                }

                400 -> {
                    throw EregException(
                        ApiError(
                            ZonedDateTime.now(),
                            response.status.value,
                            HttpStatusCode.BadRequest.description,
                            response.errorMessage() ?: "",
                            "${eregHost}/v2/organisasjon/{orgnummer}/noekkelinfo",
                        ),
                        response
                    )
                }

                404 -> {
                    throw EregException(
                        ApiError(
                            ZonedDateTime.now(),
                            response.status.value,
                            HttpStatusCode.NotFound.description,
                            response.errorMessage() ?: "",
                            "${eregHost}/v2/organisasjon/{orgnummer}/noekkelinfo",
                        ),
                        response
                    )
                }

                else -> {
                    throw EregException(
                        ApiError(
                            ZonedDateTime.now(),
                            response.status.value,
                            response.status.description,
                            response.errorMessage() ?: "",
                            "${eregHost}/v2/organisasjon/{orgnummer}/noekkelinfo",
                        ),
                        response
                    )
                }
            }
        }
}

suspend fun HttpResponse.errorMessage() = body<JsonElement>().jsonObject["melding"]?.jsonPrimitive?.content