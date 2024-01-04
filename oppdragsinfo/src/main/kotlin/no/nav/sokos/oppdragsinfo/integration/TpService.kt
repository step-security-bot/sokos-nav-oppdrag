package no.nav.sokos.oppdragsinfo.integration

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import java.time.ZonedDateTime
import no.nav.sokos.oppdragsinfo.config.ApiError
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.integration.model.TssResponse
import no.nav.sokos.oppdragsinfo.metrics.tpCallCounter
import no.nav.sokos.oppdragsinfo.util.RetryException
import no.nav.sokos.oppdragsinfo.util.TpException
import no.nav.sokos.oppdragsinfo.util.defaultHttpClient
import no.nav.sokos.oppdragsinfo.util.retry
import org.slf4j.MDC

class TpService(
    private val tpHost: String = PropertiesConfig.EksterneHost().tpHost,
    private val httpClient: HttpClient = defaultHttpClient
) {

    suspend fun getLeverandorNavn(tssId: String): TssResponse =
        retry {
            try {
                logger.info("Henter leverandørnavn for $tssId fra TP.")
                httpClient.get("$tpHost/api/ordninger/tss/${tssId}") {
                    header("Nav-Call-Id", MDC.get("x-correlation-id"))
                }
            } catch (ex: Exception) {
                logger.error(ex) { "Feil oppstått ved oppslag av $tssId i TP." }
                throw RetryException(ex)
            }
        }.let { response ->
            tpCallCounter.labels("${response.status.value}").inc()
            when (response.status.value) {
                200 -> {
                    TssResponse(response.body<String>())
                }

                404 -> {
                    throw TpException(
                        ApiError(
                            ZonedDateTime.now(),
                            response.status.value,
                            HttpStatusCode.NotFound.description,
                            "Fant ingen leverandørnavn med tssId $tssId",
                            "${tpHost}/api/ordninger/tss/{tssId}",
                        ),
                        response
                    )
                }

                else -> {
                    throw TpException(
                        ApiError(
                            ZonedDateTime.now(),
                            response.status.value,
                            response.status.description,
                            "Noe gikk galt ved oppslag av $tssId i TP",
                            "${tpHost}/api/ordninger/tss/{tssId}",
                        ),
                        response
                    )
                }
            }
        }
}