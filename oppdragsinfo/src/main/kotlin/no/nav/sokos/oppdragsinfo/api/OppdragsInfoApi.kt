package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import no.nav.sokos.oppdragsinfo.api.model.*
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1"

fun Route.oppdragsInfoApi(
    oppdragsInfoService: OppdragsInfoService = OppdragsInfoService()
) {
    route(BASE_PATH) {
        get("oppdrag/{oppdragsId}") {
            val response = OppdragsInfoResponse(
                oppdragsInfoService.hentOppdrag(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call
                )
            )
            call.respond(response)
        }
        get("oppdrag2/{oppdragsId}") {
            val response = OppdragsInfoResponse2(
                oppdragsInfoService.hentOppdrag2(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call
                )
            )
            call.respond(response)
        }
        get("oppdrag3/{oppdragsId}") {
            val response = OppdragsInfoResponse(
                oppdragsInfoService.hentOppdrag3(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call
                )
            )
            call.respond(response)
        }
        get("oppdrag4/{oppdragsId}") {
            val response = OppdragsInfoResponse(
                oppdragsInfoService.hentOppdrag4(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call
                )
            )
            call.respond(response)
        }
        post("oppdrag") {
            val oppdragsSokRequest: OppdragsSokRequest = call.receive()
            val response = OppdragsSokResponse(
                oppdragsInfoService.sokOppdrag(
                    oppdragsSokRequest,
                    call
                )
            )
            call.respond(response)
        }
    }
    /*
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