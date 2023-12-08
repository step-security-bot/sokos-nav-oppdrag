package no.nav.sokos.oppdragsinfo.tp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.server.plugins.NotFoundException
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.metrics.tpCallCounter
import no.nav.sokos.oppdragsinfo.tp.model.OrganisasjonsNavn
import no.nav.sokos.oppdragsinfo.util.RetryException
import no.nav.sokos.oppdragsinfo.util.defaultHttpClient
import no.nav.sokos.oppdragsinfo.util.retry
import org.slf4j.MDC

class TpService(
    private val tpHost: String = PropertiesConfig.EksterneHost().tpHost,
    private val httpClient: HttpClient = defaultHttpClient
) {

    suspend fun hentOrganisasjonsNavnByTssId(tssId: String): OrganisasjonsNavn =
        retry {
            try {
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
                    OrganisasjonsNavn(response.body<String>())
                }

                404 -> throw NotFoundException()
                else -> {
                    throw Exception(
                        "Tp returnerte statuskode ${response.status.value} "
                    )
                }
            }
        }
}