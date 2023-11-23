package no.nav.sokos.oppdragsinfo.api

import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoResponse
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.service.OppdragsinfoService

private val log = KotlinLogging.logger {}

@Resource("/api/v1/oppdrag")
class Oppdrag(val fagGruppeKode: Int? = null, val fagSystemId: Int? = null, val vedtakFom: String? = null)

@Resource("/api/v1/oppdrag/{oppdragId}")
class OppdragId(val oppdragId: String) {
    @Resource("/linje/{linjeId}")
    class Oppdragslinje(val oppdragId: OppdragId, val linjeId: Int)
}

@Resource("/api/v1/oppdragsdetaljer/{oppdragId}")
class Oppdragsdetaljer(val oppdragId: OppdragId)

fun Route.oppdragsInfoApi() {
    val db2DataSource = Db2DataSource(PropertiesConfig.OppdragDatabaseConfig())
    val gdprLogger = AuditLogger()
    val oppdragsinfoService = OppdragsinfoService(db2DataSource, gdprLogger)
    get<Oppdrag> { oppdrag ->
        val response = OppdragsInfoResponse(
                oppdragsinfoService.hentOppdrag(
                        call.request.headers.get("offnr").orEmpty(),
                        oppdrag.fagGruppeKode,
                        oppdrag.fagSystemId,
                        oppdrag.vedtakFom,
                        call
                ))
        call.respond(response)
    }
    get<OppdragId> { oppdragId ->
        val response = OppdragsInfoResponse(
                oppdragsinfoService.hentOppdrag(
                        oppdragId.oppdragId,
                        call
                ))
        call.respond(response)
    }
    get<OppdragId.Oppdragslinje> { oppdragslinje ->
        val response = OppdragsInfoResponse(
                oppdragsinfoService.hentOppdragslinje(
                        oppdragslinje.oppdragId.oppdragId,
                        oppdragslinje.linjeId,
                        call
                ))
        call.respond(response)
    }
    get<Oppdragsdetaljer> { oppdragsdetaljer ->
        val response = OppdragsInfoResponse(
                oppdragsinfoService.hentOppdragsdetaljer(
                        oppdragsdetaljer.oppdragId.oppdragId,
                        call
                ))
        call.respond(response)
    }
}