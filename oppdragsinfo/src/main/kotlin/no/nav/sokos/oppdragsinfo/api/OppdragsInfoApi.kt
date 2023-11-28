package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoRequest
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoResponse
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1"

fun Route.oppdragsInfoApi(
    oppdragsInfoService: OppdragsInfoService = OppdragsInfoService()
) {
    route(BASE_PATH) {
        post("hentOppdrag") {
            val oppdragsInfoRequest: OppdragsInfoRequest = call.receive()
            val response = OppdragsInfoResponse(
                oppdragsInfoService.hentOppdrag(
                    oppdragsInfoRequest.oppdragsId,
                    call
                )
            )
            call.respond(response)
        }
    }

    /*    get<Oppdrag> { oppdrag ->
            val response = OppdragsInfoResponse(
                    oppdragsInfoService.hentOppdrag1(
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
                    oppdragsInfoService.hentOppdrag2(
                            oppdragId.oppdragId,
                            call
                    ))
            call.respond(response)
        }

        get<OppdragId.Oppdragslinje> { oppdragslinje ->
            val response = OppdragsInfoResponse(
                    oppdragsInfoService.hentOppdragslinje(
                            oppdragslinje.oppdragId.oppdragId,
                            oppdragslinje.linjeId,
                            call
                    ))
            call.respond(response)
        }

        get<Oppdragsdetaljer> { oppdragsdetaljer ->
            val response = OppdragsInfoResponse(
                    oppdragsInfoService.hentOppdragsdetaljer(
                            oppdragsdetaljer.oppdragId.oppdragId,
                            call
                    ))
            call.respond(response)
        }*/
}