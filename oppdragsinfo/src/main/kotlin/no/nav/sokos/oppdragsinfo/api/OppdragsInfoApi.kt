package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoResponse
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokResponse
import no.nav.sokos.oppdragsinfo.api.model.OppdragslinjeResponse
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1/oppdragsinfo"

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

        get("oppdrag/{oppdragsId}/{oppdragslinje}") {
            val response = OppdragslinjeResponse(
                oppdragsInfoService.hentOppdragslinje(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call.parameters.get("oppdragslinje").orEmpty(),
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
}